package com.service;

import com.dao.Question;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SearchLocalService extends GoobbeLogger {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private QuestionService questionService;
    private SearchLocalService() {}

    public static void main(String[] args) throws Exception {
        SearchLocalService searchLocalService =new SearchLocalService();

    }

    public List<Question> getLocalSearchResult(Question question){
        List<Integer> relatedQuestionsId = getRelatedQuestionsId(question);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("relatedQuestionsId", relatedQuestionsId);
        List<Question> questions=namedParameterJdbcTemplate.query("select id ,content ->> 't' as title,content ->> 'c' as content" +
                                                  " from tb_content where id in (:relatedQuestionsId)",
                    parameters, new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return questionService.getQuestionByResultSet(rs, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

        return questions;
    }

    public List<Integer> getRelatedQuestionsId(Question question){
        List<Integer> relatedQuestionsId=new ArrayList<>();
        String indexPath = System.getProperty("user.home") + FileSystems.getDefault().getSeparator()+"index";
        IndexReader reader = null;
        try {
            FSDirectory fs=FSDirectory.open(Paths.get(indexPath));
            reader = DirectoryReader.open(fs);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("title", analyzer);
            Query query = parser.parse(QueryParser.escape(question.getT().toLowerCase()));
            TopDocs results = searcher.search(query, null, question.getAs().size()<=3?3:question.getAs().size());
            ScoreDoc[] hits = results.scoreDocs;
            for(int i=0;i<hits.length;i++){
                Document doc = searcher.doc(hits[i].doc);
                if(doc.get("id").equals(question.getId())){
                    continue;
                }
                relatedQuestionsId.add(Integer.valueOf(doc.get("id")));
            }
            fs.close();
            reader.close();
        } catch (IOException e) {
            error(e,"read directory ["+indexPath+"] error!");
        } catch (ParseException e){
            error(e,"parse search keyword ["+question.getT()+"] error!");
        }
        return relatedQuestionsId;
    }
}
