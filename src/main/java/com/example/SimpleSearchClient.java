package com.example;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public class SimpleSearchClient {

    public static void main(final String[] args) throws Exception {
        final String defaultCollection = "employees_collection";
        final String queryString = "lastname:Penrose*";

        final CloudSolrServer solr = new CloudSolrServer("localhost:2181/solr");
        solr.setParallelUpdates(true);
        solr.setParser(new XMLResponseParser());
        solr.setDefaultCollection(defaultCollection);
        final SolrPingResponse ping = solr.ping();
        System.out.println(ping);


        // Add a new document
        final SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", "1000");
        doc.setField("firstname", "Roger");
        doc.setField("lastname", "Penrose");
        solr.add(doc);

        solr.commit();

        // Search query
        final SolrQuery query = new SolrQuery();
        query.setRequestHandler("/select");

        query.setQuery(queryString);

        final QueryResponse result = solr.query(query);
        for (final SolrDocument d : result.getResults()) {
            System.out.println(d.toString());
        }

        //Delete document
        solr.deleteById(String.valueOf(1000));
        solr.commit();

        solr.shutdown();


    }
}
