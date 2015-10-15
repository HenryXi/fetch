package com.henry.service;

import com.henry.dao.Question;
import com.henry.exception.GoobbeInternalErrorException;
import com.henry.exception.GoobbeRsNotFoundException;
import com.henry.util.GoobbeLogger;
import com.henry.util.GoobbeTitleUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class QuestionService extends GoobbeLogger {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public Question getQuestionById(Integer id) {

        Map<String, Object> record = jdbcTemplate.queryForMap("select * from tb_content where id=?", id);
        if (null == record.get("content")) {
            warn("content of question [id: " + id + "] is null");
            throw new GoobbeRsNotFoundException();
        }
        try {
            Question question = objectMapper.readValue(record.get("content").toString(), Question.class);
            question.setT(GoobbeTitleUtil.removeQuestionStatus(question.getT()));
            question.setId(record.get("id").toString());
            question.setUrl(Integer.valueOf(record.get("url").toString()));
            question.setTitle4url(GoobbeTitleUtil.generateShortTitle(question.getT()));
            info("get info of " + id);
            return question;
        } catch (Exception e) {
            error(e,"error when get question [id: " + id + "]");
        }
        throw new GoobbeInternalErrorException();
    }

    public Question getQuestionByResultSet(ResultSet rs, boolean isBriefContent, boolean isContainContent) throws IOException, SQLException {
        Question question;
        if(isBriefContent){
            String summery = Jsoup.parse(rs.getString("content").replace("&lt;", "<").replace("&gt;", ">")).text();
            if (summery.length() > 200) {
                summery = summery.substring(0, 200).trim();
            }
            question = new Question(rs.getString("id"), rs.getString("title"), summery);
        }else{
            if(isContainContent){
                question= new Question(rs.getString("id"), rs.getString("title"),rs.getString("content"));
            }else{
                question= new Question(rs.getString("id"), rs.getString("title"), null);
            }

        }
        return question;
    }

    public int getMaxId() {
        info("get max id from db.");
        return jdbcTemplate.queryForInt("select max(id) from tb_content;");
    }

    public List<Question> getQuestionsForRandomPage() {
        int random = (int) (Math.random() * getMaxId() + 1);
        info("generate random id successful, get random questions ... ");
        // todo "not null" in sql should be removed after format db
        List<Question> questions = this.jdbcTemplate.query(
                "select id ,content ->> 't' as title,content ->> 'c' as content" +
                        " from tb_content where content is not null and id>=? order by id limit 15",
                new Object[]{random},
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return getQuestionByResultSet(rs, true, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }
}
