package com.service;

import com.dao.Question;
import com.exception.GoobbeException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    public Question getQuestionById(Integer id) throws GoobbeException{
        try {
            Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content2 where id=?",id);
            if(null==record.get("content")){
                throw new GoobbeException("error");
            }
            Question question = objectMapper.readValue(record.get("content").toString(),Question.class);
            question.setUrl(record.get("url").toString());
            question.setId(record.get("id").toString());
            return question;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException("error");
    }

    public List<Question> getQuestionsForIndex() {
        List<Question> questions = this.jdbcTemplate.query(
                "select * from tb_content2 where content is not null limit 15",
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                            Question question = objectMapper.readValue(rs.getString("content"), Question.class);
                            question.setUrl(rs.getString("url"));
                            question.setContent(Jsoup.parse(question.getContent().replace("&lt", "<").replace("&gt", ">")).text().substring(0, 200));
                            question.setId(rs.getString("id"));
                            return question;
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }

    public List<Question> getQuestionsForIndex(String page) {
        List<Question> questions = this.jdbcTemplate.query(
                "select * from tb_content2 where content is not null limit 15",
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                            Question question = objectMapper.readValue(rs.getString("content"), Question.class);
                            question.setUrl(rs.getString("url"));
                            question.setContent(Jsoup.parse(question.getContent().replace("&lt", "<").replace("&gt", ">")).text().substring(0, 200));
                            question.setId(rs.getString("id"));
                            return question;
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }
}
