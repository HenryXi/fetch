package com.service;

import com.dao.Question;
import com.exception.GoobbeException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class QuestionService {
    private final String SEARCH_STACK_URL="http://www.gfsoso.com/?q=java+site%3Astackoverflow.com";
    private final String STACK_URL="http://stackoverflow.com/questions/";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private Random random=new Random();

    public Question getQuestionById(Integer id) throws GoobbeException{
        try {
            Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content where id=?",id);
            if(null==record.get("content")){
                throw new GoobbeException("error");
            }
            Question question = objectMapper.readValue(record.get("content").toString(),Question.class);
            question.setId(record.get("id").toString());
            return question;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException("error");
    }

    private Question getQuestionByResultSet(ResultSet rs) throws IOException, SQLException {
        Question question = objectMapper.readValue(rs.getString("content"), Question.class);
        question.setUrl(rs.getInt("url"));
        String summery= Jsoup.parse(question.getC().replace("&lt", "<").replace("&gt", ">")).text();
        if(summery.length()>200){
            summery=summery.substring(0,200);
        }
        question.setC(summery);
        question.setId(rs.getString("id"));
        return question;
    }

    public List<Question> getQuestionsForIndex(Integer page) {
        int startNum=15*page-14; //15*(page-1)+1
        // todo "not null" in sql should be removed after format db
        List<Question> questions = this.jdbcTemplate.query(
                "select * from tb_content where content is not null and id>=? order by id limit 15",
                new Object[]{startNum},
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return getQuestionByResultSet(rs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }

    public List<Question> getQuestionsByKeyword(String keyword) throws GoobbeException{
        try {
            System.out.println("request-->" + keyword);
            String resultJson = Jsoup.connect("http://52.11.54.118:8080/?keyword="+keyword).timeout(10000).ignoreContentType(true).execute().body();
            final List<Question> questionsFromSearch=objectMapper.readValue(resultJson, TypeFactory.defaultInstance().constructCollectionType(List.class, Question.class));
            List<Integer> urls=new ArrayList<>();
            for(Question question:questionsFromSearch){
                urls.add(question.getUrl());
            }
            // todo "not null" in sql should be removed after format db
            namedParameterJdbcTemplate.query(
                "select * from tb_content where url in (:urls) and content is not null",
                Collections.singletonMap("urls",urls),
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                           return setQuestionId(rs, questionsFromSearch);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            Iterator<Question> q = questionsFromSearch.iterator();
            while (q.hasNext()) {
                if(q.next().getId()==null){
                    q.remove();
                }
            }
            return questionsFromSearch;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Question setQuestionId(ResultSet rs, List<Question> questionsFromSearch) throws SQLException, IOException {
        int url=rs.getInt("url");
        for(Question question:questionsFromSearch){
            if(question.getUrl()==url){
                question.setId(rs.getString("id"));
                return question;
            }
        }
        return null;
    }

}
