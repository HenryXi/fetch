package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {

    private Search() {}

    public static void main(String[] args) throws Exception {

        String indexPath="";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        BufferedReader in = null;

        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse("java environment");

        TopDocs results = searcher.search(query, null, 100);

        ScoreDoc[] hits = results.scoreDocs;
        for(int i=0;i<hits.length;i++){
            Document doc = searcher.doc(hits[i].doc);
            System.out.println("id -> "+ doc.get("id")+", title -> "+ doc.get("title"));
        }
    }
}
