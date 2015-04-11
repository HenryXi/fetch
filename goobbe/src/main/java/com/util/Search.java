package com.util;

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

import java.io.BufferedReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Date;

public class Search {

    private Search() {}

    public static void main(String[] args) throws Exception {
        Date begin=new Date();
        String indexPath = System.getProperty("user.home") + FileSystems.getDefault().getSeparator()+"index";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse("PHP and the million array baby");
        TopDocs results = searcher.search(query, null, 10);
        ScoreDoc[] hits = results.scoreDocs;
        Date end=new Date();
        System.out.println("used: "+(end.getTime()-begin.getTime())+" ms");
        for(int i=0;i<hits.length;i++){
            Document doc = searcher.doc(hits[i].doc);
            System.out.println("id -> "+ doc.get("id")+", title -> "+ doc.get("title"));
        }

    }
}
