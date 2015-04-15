package com.service;

import com.dao.Answer;
import com.dao.Comment;
import com.dao.Question;
import com.dao.QuestionJson;
import com.exception.GoobbeException;
import com.util.GoobbeLogger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class QuestionService extends GoobbeLogger {
    private final String STACK_URL="http://stackoverflow.com/questions/";
    private final String STACK_OVERFLOW=" - Stack Overflow";
    private final String STACK_=" - Stack";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GetPageService getPageService;

    public Question getQuestionById(Integer id) throws GoobbeException{
        try {
            Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content where id=?",id);
            if(null==record.get("content")){
                error("content of question [id: "+id+"] is null");
                throw new GoobbeException("error");
            }
            Question question = objectMapper.readValue(record.get("content").toString(),Question.class);
            question.setId(record.get("id").toString());
            return question;
        } catch (Exception e) {
            error("error when get question [id: "+id+"]");
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

    public String getQuestionsByKeyword(String keyword) throws GoobbeException{
        try {
            info("user search by keyword:[" + keyword+"]");
            keyword= URLEncoder.encode(keyword,"UTF-8");
            String returnJson=Jsoup.connect("http://52.11.54.118:8080/google?keyword="+keyword).timeout(10000).ignoreContentType(true).execute().body();
            return returnJson.replace(STACK_OVERFLOW,"").replace(STACK_,"").replaceAll("<br.{0,2}>","");
        } catch (IOException e) {
            error("error when user search by keyword:["+keyword+"]");
        }
        return null;
    }

    public Question getQuestionByUrl(Integer url) {
        try {
            List<Question> questions=jdbcTemplate.query("select * from tb_content where url=?",
                    new Object[]{url},
                    new RowMapper<Question>() {
                        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                            try {
                                if (rs.getString("content") == null) {
                                    return null;
                                }
                                Question question = objectMapper.readValue(rs.getString("content").toString(), Question.class);
                                question.setId(rs.getString("id"));
                                return question;
                            } catch (IOException e) {
                                return null;
                            }
                        }
                    });
            if(questions.size()==0 || questions.get(0)==null){
                Document docFromSearch=getPageService.getDoc(STACK_URL+url);
                QuestionJson questionJson=getQuestionByDoc(docFromSearch,url);
                saveSearchResultInDB(questionJson);
                return questionJson;
            }else{
                return questions.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException("error");
    }

    private QuestionJson getQuestionByDoc(Document doc,int url) {
        if(doc==null){
            //todo handle this
            return null;
        }
        String questionTitle=doc.select("#question-header>h1>a").html();
        String questionContent=doc.select(".postcell>div>.post-text").html();
        QuestionJson questionJson=new QuestionJson(questionTitle,questionContent,url);
        for(Element questionComment:doc.select(".question").select(".comment-copy")){
            questionJson.getCs().add(new Comment(questionComment.html()));
        }
        for(Element ans:doc.select(".answer")){
            Answer answer=new Answer(ans.select(".post-text").html());
            for(Element com:ans.select(".comment-copy")){
                answer.getCs().add(new Comment(com.html()));
            }
            questionJson.getAs().add(answer);
        }
        return questionJson;
    }

    private void saveSearchResultInDB(QuestionJson questionJson) {
        if(questionJson==null){
            return ;
        }
        try {
            int updateRow=jdbcTemplate.update("update tb_content set content=?::json where url=?",
                objectMapper.writeValueAsString(questionJson), questionJson.getUrl());
            if(updateRow==0){
                String id=jdbcTemplate.queryForObject("insert into tb_content (content,url) VALUES (?::json,?) RETURNING id", String.class,
                        objectMapper.writeValueAsString(questionJson), questionJson.getUrl());
                questionJson.setId(id);
            }
        }catch (DuplicateKeyException e){
            return;
        }catch (IOException e) {
            error("error occur when save search result in db. question url["+questionJson.getUrl()+"]");
        }
    }

    public int getTotalNumQuestions(){
        return jdbcTemplate.queryForInt("select count from tcounter where table_name='tb_content';");
    }
}
