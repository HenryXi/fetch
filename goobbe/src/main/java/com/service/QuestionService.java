package com.service;

import com.dao.Question;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    public String getQuestionById(String index){
        List<String> records=jdbcTemplate.queryForList("select content from tb_content2 where content is not null limit 30",String.class);
        return "";
    }

    public List<Question> getQuestionsForIndex(){
        List<Question> questions= new ArrayList<>();
        List<String> records=jdbcTemplate.queryForList("select content from tb_content2 where content is not null limit 30",String.class);
        for(String content:records){
            try {
                Question question=objectMapper.readValue(content, Question.class);
                questions.add(question);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return questions;
    }
}
