package com.service;

import com.dao.Answer;
import com.dao.Comment;
import com.dao.Question;
import com.dao.QuestionJson;
import com.exception.GoobbeInternalErrorException;
import com.exception.GoobbeRsNotFoundException;
import com.util.GoobbeLogger;
import com.util.GoobbeTitleUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
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

    public Question getQuestionByResultSet(ResultSet rs, boolean isBriefContent) throws IOException, SQLException {
        Question question;
        if(isBriefContent){
            String summery = Jsoup.parse(rs.getString("content").replace("&lt;", "<").replace("&gt;", ">")).text();
            if (summery.length() > 200) {
                summery = summery.substring(0, 200).trim();
            }
            question = new Question(rs.getString("id"), rs.getString("title"), summery);
        }else{
            question= new Question(rs.getString("id"), rs.getString("title"),rs.getString("content"));
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
                            return getQuestionByResultSet(rs, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }
}
