package com.example;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/*

Set env variable for Zookeeper.
$ export SOLR_ZK_ENSEMBLE=localhost:2181/solr

Create template
$ solrctl instancedir --generate winereview

Update the schema in Solr as below.

 <fields>
   <field name="id" type="string" indexed="true" stored="true" required="true" multiValued="false" />
   <field name="_version_" type="long" indexed="true" stored="true"/>
   <field name="country" type="text_general" indexed="true" stored="true"/>
   <field name="description" type="text_general" indexed="true" stored="true"/>
   <field name="designation" type="text_general" indexed="true" stored="true"/>
   <field name="points" type="tint" indexed="true" stored="true"/>
   <field name="price" type="tdouble" indexed="true" stored="true"/>
   <field name="province" type="text_general" indexed="true" stored="true"/>
   <field name="region_1" type="text_general" indexed="true" stored="true"/>
   <field name="region_2" type="text_general" indexed="true" stored="true"/>
   <field name="taster_name" type="text_general" indexed="true" stored="true"/>
   <field name="taster_twitter_handle" type="text_general" indexed="true" stored="true"/>
   <field name="title" type="text_general" indexed="true" stored="true"/>
   <field name="variety" type="text_general" indexed="true" stored="true"/>
   <field name="winery" type="text_general" indexed="true" stored="true"/>
   <field name="text" type="text_general" indexed="true" stored="false" multiValued="true"/>
</fields>

<uniqueKey>id</uniqueKey>

<copyField source="country" dest="text"/>
<copyField source="description" dest="text"/>
<copyField source="designation" dest="text"/>
<copyField source="province" dest="text"/>
<copyField source="region_1" dest="text"/>
<copyField source="region_2" dest="text"/>
<copyField source="taster_name" dest="text"/>
<copyField source="title" dest="text"/>
<copyField source="variety" dest="text"/>
<copyField source="winery" dest="text"/>
<copyField source="points" dest="text"/>
<copyField source="price" dest="text"/>

Upload collection configuration to Zookeeper
$ solrctl instancedir --update winereview winereview

Launch the collection to solr cloud.
$ solrctl collection --create winereview


* */

public class SparkLoader {
    private static SparkSession spark = null;
    private static final Logger logger = LoggerFactory.getLogger(SparkLoader.class);

    public static void main(final String[] args) {


        final String path = args[0];

        final SparkConf conf = new SparkConf().setAppName(SparkLoader.class.getName())
                .setIfMissing("spark.master", "local[*]")
                .setIfMissing("spark.sql.shuffle.partitions", "2");

        spark = SparkSession.builder().
                config(conf).
                getOrCreate();

        final Dataset<ReviewBean> raw = spark.read()
                .option("header", true)
                .option("inferSchema", true)
                .csv(path)
                .withColumn("points", functions.col("points").cast("int"))
                .withColumn("price", functions.col("price").cast("double"))
                .as(Encoders.bean(ReviewBean.class));


        raw.printSchema();
        logger.info("Total number of records: " + String.valueOf(raw.count()));
        logger.info("Total number of partitions: " + String.valueOf(raw.rdd().getNumPartitions()));

        final String defaultCollection = "winereview";

        raw.coalesce(1).foreachPartition((Iterator<ReviewBean> partitions) -> {

            final CloudSolrServer solr = new CloudSolrServer("localhost:2181/solr");
            solr.setParallelUpdates(true);
            solr.setDefaultCollection(defaultCollection);
            ReviewBean r;
            SolrInputDocument doc;
            int count = 0;

            while (partitions.hasNext()) {
                r = partitions.next();
                doc = new SolrInputDocument();
                try {
                    doc.setField("id", r.getId());
                    doc.setField("country", r.getCountry());
                    doc.setField("description", r.getDescription());
                    doc.setField("designation", r.getDesignation());
                    doc.setField("points", r.getPoints());
                    doc.setField("price", r.getPrice());
                    doc.setField("province", r.getProvince());
                    doc.setField("region_1", r.getRegion_1());
                    doc.setField("region_2", r.getRegion_2());
                    doc.setField("taster_name", r.getTaster_name());
                    doc.setField("taster_twitter_handle", r.getTaster_twitter_handle());
                    doc.setField("title", r.getTitle());
                    doc.setField("variety", r.getVariety());
                    doc.setField("winery", r.getWinery());
                    solr.add(doc);
                } catch (final Exception ex) {
                    logger.error("Error in inserting to Solr, id: ", r.getId());
                    ex.printStackTrace();
                }

                ++count;
            }
            logger.info("Number of records in partition: " + String.valueOf(count));

            solr.commit();
            solr.shutdown();

        });

        spark.close();


    }
}
