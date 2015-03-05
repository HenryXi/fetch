package com.service;

import com.dao.Question;
import com.exception.GoobbeException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    private final String STACK_URL="http://www.gfsoso.com/?q=java+site%3Astackoverflow.com";
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

    public List<Question> getQuestionsForIndex() {
        List<Question> questions = this.jdbcTemplate.query(
                "select * from tb_content where content is not null limit 15",
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                            return getQuestionByResultSet(rs);
                        }catch (IOException e){
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

    public Question getQuestionByUrlNumber(Integer id) throws GoobbeException{
        Question question=null;
        try {
            Document doc=getDoc(STACK_URL);

//            if(200==conn.getResponseCode()){
//                Map<String,Object> record=jdbcTemplate.queryForMap("select * from tb_content where url=?",
//                                                conn.getURL().toString().replace(STACK_URL,""));
//                if(null==record.get("content")){
//                    return null;
//                }
//                question = objectMapper.readValue(record.get("content").toString(),Question.class);
//                question.setUrl(record.get("url").toString());
//                question.setId(record.get("id").toString());
//            }
//            conn.disconnect();
            return question;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException("error");
    }

    private Document getDoc(String url) throws MalformedURLException {
        URL website = new URL(url);
        //System.out.println("proxy -->" +( currentProxy==null?"null":currentProxy.address()));
        HttpURLConnection httpUrlConnetion=new HttpURLConnection(website, Proxy.NO_PROXY);
        httpUrlConnetion.setConnectTimeout(1000);
//        httpUrlConnetion.setReadTimeout(1000 * 20);
//        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            if(stateCode!=200){
                System.out.println("state -->"+stateCode+" url --->"+url);
                if(stateCode==404){
                    jdbcTemplate.update("update tb_content set content=null where url=?",url);
                    return null;
                }
                //currentProxy = proxys.get(String.valueOf(random.nextInt(proxys.size())));
                //return getDoc(url);
            }
            InputStream is=httpUrlConnetion.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            int sum = 0;
            int maxsize =1000 * 1000;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((read = is.read(buf)) != -1) {
                if (maxsize > 0) {
                    sum = sum + read;
                    if (sum > maxsize) {
                        read = maxsize - (sum - read);
                        bos.write(buf, 0, read);
                        break;
                    }
                }
                bos.write(buf, 0, read);
            }
            is.close();
            byte[] content=bos.toByteArray();
            bos.close();
            page=new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("bad proxy change another!");
            //currentProxy=proxys.get(String.valueOf(random.nextInt(proxys.size())));
            //return getDoc(url);
        }finally {
            httpUrlConnetion.disconnect();
        }
        Document doc= Jsoup.parse(page, String.valueOf(httpUrlConnetion.getURL()));
        return doc;
    }
}
