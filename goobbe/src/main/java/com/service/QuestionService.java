package com.service;

import com.dao.Question;
import com.exception.GoobbeException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

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
    private ObjectMapper objectMapper;

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

    public Question getQuestionBysUrl(String sUrl) throws GoobbeException{
        try {
            Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content where url=?",sUrl);
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
            "select * from tb_content where content is not null limit 15",
            new RowMapper<Question>() {
                public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                    try {
                        return getQuestionByResultSet(rs);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        return questions;
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

    public int getQuestionsByKeyword(List<Question> questions,String keyword,int pageNumber) throws GoobbeException{
        try {
            Document doc = Jsoup.connect("http://www.gfsoso.com/?q="+keyword+"+site%3Astackoverflow.com&pn="+pageNumber).get();
            String url=doc.baseUri()+"&t=1";
            int startIndex=doc.toString().indexOf("$.cookie('_GFTOKEN','");
            int endIndex=doc.toString().indexOf("', {expires:720}");
            String cookie=doc.toString().substring(startIndex+21,endIndex);
            doc=Jsoup.connect(url).cookie("_GFTOKEN",cookie).get();
            String targetDiv=doc.toString().substring(doc.toString().indexOf("<ol"),doc.toString().indexOf("ol>")+3);
            Document finalDoc=Jsoup.parse(targetDiv.replace("\\\"","\"").replace("\\","").replace("\t",""));
            for(Element result:finalDoc.select(".g")){
                String resultHref=result.select("a").attr("href").replace(STACK_URL,"");
                if(resultHref.matches("\\d{1,8}/.+")){
                    Question question=new Question(result.select("a").html(),result.select(".s>.st").html(),resultHref);
                    if(questions.size()>=10 || questions.contains(question)){
                        return pageNumber;
                    }
                    questions.add(question);
                }
            }
            if(questions.size()<10){
                return getQuestionsByKeyword(questions,keyword,pageNumber+10);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageNumber;
    }



    public static void main(String[] args) {
        QuestionService service=new QuestionService();
        List<Question> questions=new LinkedList<>();
        int currentPage = service.getQuestionsByKeyword(questions,"we25E",0)/10;
    }
}
