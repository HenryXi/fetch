package com.service;

import com.dao.RelatedQuestion;
import com.util.GoobbeLogger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
@Service
public class SearchService extends GoobbeLogger {

    private SearchService() {}

    public static void main(String[] args) throws Exception {
        SearchService searchService=new SearchService();
        List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult("java thread priority");

    }

    public List<RelatedQuestion> getLocalSearchResult(String target){
        List<RelatedQuestion> relatedQuestions=new ArrayList<>();
        String indexPath = System.getProperty("user.home") + FileSystems.getDefault().getSeparator()+"index";
        IndexReader reader = null;
        try {
            FSDirectory fs=FSDirectory.open(Paths.get(indexPath));
            reader = DirectoryReader.open(fs);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("title", analyzer);
            Query query = parser.parse(QueryParser.escape(target.toLowerCase()));
            TopDocs results = searcher.search(query, null, 16);
            ScoreDoc[] hits = results.scoreDocs;
            for(int i=1;i<hits.length;i++){
                Document doc = searcher.doc(hits[i].doc);
                relatedQuestions.add(new RelatedQuestion(doc.get("id"), doc.get("title")));
            }
            fs.close();
            reader.close();
        } catch (IOException e) {
            error(e,"read directory ["+indexPath+"] error!");
        } catch (ParseException e){
            error(e,"parse search keyword ["+target+"] error!");
        }
        return relatedQuestions;
    }
}
