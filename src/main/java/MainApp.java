import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class MainApp {

    private static SparkSession spark = null;

    private static void init(){
        SparkConf conf = new SparkConf()
                .setAppName(MainApp.class.getName())
                .setIfMissing("spark.master", "local[*]");

        spark = SparkSession.builder().config(conf).getOrCreate();
    }

    private static Dataset<Row> loadCsv(String path){
        Dataset<Row> movies = spark
                .read()
                .option("header", true)
                .option("inferSchema", true)
                .csv(path);

        return movies;
    }



    public static void main(String[] agrs) throws Exception{

        init();
        Dataset<Row> movies = loadCsv("/data/ml-latest-small/movies.csv");
        movies.foreachPartition(rows -> {

            String urlString = "http://localhost:8983/solr/movielens";

            ConcurrentUpdateSolrClient solr = new ConcurrentUpdateSolrClient
                    .Builder(urlString)
                    .withQueueSize(1000)
                    .withThreadCount(20)
                    .build();

            for(int i = 0; rows.hasNext(); ++i){
                Row row = rows.next();
                System.out.println(String.valueOf(i) + row.toString());
                SolrInputDocument document = new SolrInputDocument();
                document.addField("movieid", row.getInt(0));
                document.addField("title", row.getString(1));
                document.addField("genre", row.getString(2));
                UpdateResponse response = solr.add(document);

            }
            solr.commit();

        });



        spark.close();
    }
}
