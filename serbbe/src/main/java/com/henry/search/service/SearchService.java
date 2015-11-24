package com.henry.search.service;

import com.henry.search.model.Question;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yong on 2015/11/24.
 */
@Service
public class SearchService {

    private Logger logger = LoggerFactory.getLogger(SearchService.class);

    public static void main(String[] args) {
        SearchService searchService = new SearchService();
        searchService.getRelatedQuestionsId("java annotation");
    }

    public List<Question> getRelatedQuestionsId(String searchKey) {
        List<Question> relatedQuestions = new ArrayList<>();
        String indexPath = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "index";
        IndexReader reader = null;
        try {
            FSDirectory fs = FSDirectory.open(Paths.get(indexPath));
            reader = DirectoryReader.open(fs);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                    new String[]{"bodytext", "title"},
                    analyzer);

            TopDocs results = searcher.search(queryParser.parse(searchKey), 27);
            ScoreDoc[] hits = results.scoreDocs;
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                relatedQuestions.add(new Question(Integer.valueOf(doc.get("id")), doc.get("title"), doc.get("content")));
            }
            fs.close();
            reader.close();
        } catch (IOException e) {
            logger.error("read directory [" + indexPath + "] error!", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relatedQuestions;
    }
}
