package com.service;

import com.dao.Question;
import com.exception.GoobbeException;
import com.util.GetPageByUrlWithProxy;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    @Autowired
    private GetPageByUrlWithProxy getPageByUrlWithProxy;

    public Question getQuestionById(Integer id) throws GoobbeException{
        try {
            Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content where id=?",id);
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

    private Question getQuestionByResultSet(ResultSet rs) throws IOException, SQLException {
        Question question = objectMapper.readValue(rs.getString("content"), Question.class);
        question.setUrl(rs.getString("url"));
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
                        try{
                            return getQuestionByResultSet(rs);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }

    private List<Question> getQuestionsByKeyword(String keyword,int pageNumber) throws GoobbeException{
        try {
            System.out.println("request-->" + keyword + " " + pageNumber);
            ObjectMapper objectMapper1=new ObjectMapper();
            //todo add ip param if need. like this ....&i=random ip;
            String resultJson = Jsoup.connect("http://52.11.54.118/?q="+keyword+"+site%3Astackoverflow.com%2Fquestions%2F&s="+pageNumber).ignoreContentType(true).execute().body();
            JsonNode jsonNode = objectMapper1.readTree(resultJson).get("responseData").get("results");
            List<String> urls=new ArrayList<>();
            for(int i=0;i<jsonNode.size();i++){
                urls.add(jsonNode.get(i).findPath("url").getValueAsText().replace("http://stackoverflow.com/questions/",""));
            }
            // todo "not null" in sql should be removed after format db
            List<Question> questions = namedParameterJdbcTemplate.query(
                "select * from tb_content where url in (:urls) and content is not null",
                Collections.singletonMap("urls",urls),
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                            return getQuestionByResultSet(rs);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            return questions;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new GoobbeException("error");
    }

    public List<Question> getQuestions(String keyword){
        List<Question> questions=new ArrayList<>();
        for(int i=0;i<10;i++){
            for(Question question:getQuestionsByKeyword(keyword,8*i)){
                questions.add(question);
                if(questions.size()>=10){
                    return questions;
                }
            }
        }
        return questions;
    }



    public static void main(String[] args) {
        QuestionService service=new QuestionService();
        List<Question> questions= service.getQuestionsByKeyword("get google result",0);
    }
}
