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
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private List<Question> getQuestionsByKeyword(String keyword,int pageNumber) throws GoobbeException{
        try {
            System.out.println("request-->" + keyword + " " + pageNumber);
            String resultJson = Jsoup.connect("http://52.11.54.118/?q="+keyword+"+site%3Astackoverflow.com%2Fquestions%2F&s="+pageNumber+getRandomIp()).ignoreContentType(true).execute().body();
            final JsonNode jsonNode = objectMapper.readTree(resultJson).get("responseData").get("results");
            if(jsonNode==null){
                System.out.println(resultJson);
                return null;
            }
            List<Integer> urls=new ArrayList<>();
            Pattern p = Pattern.compile("http://stackoverflow.com/questions/\\d{1,8}/.+");
            for(int i=0;i<jsonNode.size();i++){
                String url=jsonNode.get(i).findPath("url").getValueAsText();
                if(p.matcher(url).find()){
                    urls.add(Integer.valueOf(url.replace("http://stackoverflow.com/questions/", "").replaceAll("/.+", "")));
                }
            }
            if(urls.size()==0){
                return null;
            }
            // todo "not null" in sql should be removed after format db
            List<Question> questions = namedParameterJdbcTemplate.query(
                "select * from tb_content where url in (:urls) and content is not null",
                Collections.singletonMap("urls",urls),
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try{
                            return getQuestionForSearchResult(rs, jsonNode);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            return questions;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Question getQuestionForSearchResult(ResultSet rs, JsonNode jsonNode) throws SQLException, IOException {
        Iterator<JsonNode> iterator=jsonNode.getElements();
        while (iterator.hasNext()){
            JsonNode resultJsonNode=iterator.next();
            String url=rs.getString("url");
            if(resultJsonNode.findValue("unescapedUrl").getValueAsText().contains(url)){
                String title= resultJsonNode.get("title").getValueAsText();
                return new Question(rs.getString("id"),title.replace("- Stack Overflow","")
                        ,resultJsonNode.findValue("content").getValueAsText(),url);
            }
        }
        return null;
    }

    private String getRandomIp() throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getByName(random.nextInt(255)+"."+random.nextInt(255)+"."+random.nextInt(255)+"."+random.nextInt(255));
        if(inetAddress.isSiteLocalAddress()){
            return "";
        }
        return "&i="+inetAddress.getHostAddress();
    }

    public List<Question> getQuestions(String keyword){
        List<Question> questions=new ArrayList<>();
        for(int i=0;i<10;i++){
            List<Question> questionsFromSearch=getQuestionsByKeyword(keyword,8*i);
            if(questionsFromSearch==null){
                return questions;
            }
            for(Question question:questionsFromSearch){
                if(question!=null && !questions.contains(question)){
                    questions.add(question);
                }
                if(questions.size()>=10){
                    return questions;
                }
            }
        }
        return questions;
    }



    public static void main(String[] args) {
        String json="{\n" +
                "    \"responseData\": {\n" +
                "        \"results\": [\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/27378945/replace-whole-word-using-ms-sql-server-replace\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/27378945/replace-whole-word-using-ms-sql-server-replace\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:kUbHOz-m3CsJ:stackoverflow.com\",\n" +
                "                \"title\": \"Replace whole word using ms sql server &quot;replace&quot; - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"Replace whole word using ms sql server &quot;replace&quot; - Stack Overflow\",\n" +
                "                \"content\": \"declare @str varchar(50)=&#39;<b>GoodLuck</b> Markand&#39; declare @replacedString varchar(\\n50) set @replacedString = replace(@str,&#39;Good&#39;,&#39;Better&#39;) print ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28163172/trying-to-set-text-of-a-textfield-with-no-luck\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28163172/trying-to-set-text-of-a-textfield-with-no-luck\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:ZJpRxVZzGGYJ:stackoverflow.com\",\n" +
                "                \"title\": \"javafx - Trying to set text of a textField with no <b>luck</b> - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"javafx - Trying to set text of a textField with no luck - Stack Overflow\",\n" +
                "                \"content\": \"Trying to set text of a textField with no luck .... No problem! It&#39;s a little bit of a \\nstrange notation until you get used to it :) <b>Good luck</b>! – WillBD Jan ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28822651/calling-external-function-in-awk\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28822651/calling-external-function-in-awk\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:JDmY5cKGexAJ:stackoverflow.com\",\n" +
                "                \"title\": \"bash - Calling external function in awk - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"bash - Calling external function in awk - Stack Overflow\",\n" +
                "                \"content\": \"@barmar, yes I&#39;m aware of that. I just think is it more legible in these smallish \\ncomments. <b>Good luck</b> to all! – shellter Mar 3 at 2:59 ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28309396/bind-shell-with-linux\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28309396/bind-shell-with-linux\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:SIAbsXLrZTAJ:stackoverflow.com\",\n" +
                "                \"title\": \"netcat - Bind shell with linux - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"netcat - Bind shell with linux - Stack Overflow\",\n" +
                "                \"content\": \"Feb 3, 2015 <b>...</b> <b>Good luck</b>. – shellter Feb 3 at 21:56 ... <b>Good luck</b>! – shellter Feb 3 at 23:29 ... Is &quot;\\npassword knocking&quot; a good idea? Calculate new value based ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/24083989/string-split-in-c-with-strtok-function\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/24083989/string-split-in-c-with-strtok-function\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:DdvUEqsSzJIJ:stackoverflow.com\",\n" +
                "                \"title\": \"String split in C with strtok function - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"String split in C with strtok function - Stack Overflow\",\n" +
                "                \"content\": \"char *pch; char str[] = &quot;hello \\\\&quot;Stack Overflow\\\\&quot; <b>good luck</b>!&quot;; pch = strtok(str,&quot; &quot;); \\nwhile (pch != NULL) { printf (&quot;%s\\\\n&quot;,pch); pch = strtok(NULL, &quot; &quot;); }.\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28419500/suggest-a-good-crm-system-for-f2p-game\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28419500/suggest-a-good-crm-system-for-f2p-game\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:xbf2RRbYickJ:stackoverflow.com\",\n" +
                "                \"title\": \"Suggest a <b>good</b> CRM system for F2P game - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"Suggest a good CRM system for F2P game - Stack Overflow\",\n" +
                "                \"content\": \"<b>Good luck</b>. Microsoft Dynamics 30 day free trial http://www.microsoft.com/\\ndynamics/en/asia/crm-free-trial-overview.aspx. salesforce 30 day free ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28414882/no-rule-to-make-traget-install-kubuntu-konsole\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28414882/no-rule-to-make-traget-install-kubuntu-konsole\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:h0F6XGPQYYsJ:stackoverflow.com\",\n" +
                "                \"title\": \"sh - No rule to make traget Install (Kubuntu Konsole) - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"sh - No rule to make traget Install (Kubuntu Konsole) - Stack Overflow\",\n" +
                "                \"content\": \"most software that uses make also requires that you use ./configure before that. \\n<b>Good luck</b>. – shellter Feb 9 at 17:15 ...\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"GsearchResultClass\": \"GwebSearch\",\n" +
                "                \"unescapedUrl\": \"http://stackoverflow.com/questions/28373600/structuring-if-statements-properly\",\n" +
                "                \"url\": \"http://stackoverflow.com/questions/28373600/structuring-if-statements-properly\",\n" +
                "                \"visibleUrl\": \"stackoverflow.com\",\n" +
                "                \"cacheUrl\": \"http://www.google.com/search?q=cache:8XGWR4wPuP0J:stackoverflow.com\",\n" +
                "                \"title\": \"c - Structuring if statements properly - Stack Overflow\",\n" +
                "                \"titleNoFormatting\": \"c - Structuring if statements properly - Stack Overflow\",\n" +
                "                \"content\": \"After outputting the appropriate message from above, don&#39;t forget to wish Nick \\n<b>good luck</b> with his new business: <b>Good luck</b>! This is my code so ...\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"cursor\": {\n" +
                "            \"resultCount\": \"84,300\",\n" +
                "            \"pages\": [\n" +
                "                {\n" +
                "                    \"start\": \"0\",\n" +
                "                    \"label\": 1\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"8\",\n" +
                "                    \"label\": 2\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"16\",\n" +
                "                    \"label\": 3\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"24\",\n" +
                "                    \"label\": 4\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"32\",\n" +
                "                    \"label\": 5\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"40\",\n" +
                "                    \"label\": 6\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"48\",\n" +
                "                    \"label\": 7\n" +
                "                },\n" +
                "                {\n" +
                "                    \"start\": \"56\",\n" +
                "                    \"label\": 8\n" +
                "                }\n" +
                "            ],\n" +
                "            \"estimatedResultCount\": \"84300\",\n" +
                "            \"currentPageIndex\": 0,\n" +
                "            \"moreResultsUrl\": \"http://www.google.com/search?oe=utf8&ie=utf8&source=uds&start=0&hl=en&q=good+luck+site:stackoverflow.com/questions/\",\n" +
                "            \"searchResultTime\": \"0.39\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"responseDetails\": null,\n" +
                "    \"responseStatus\": 200\n" +
                "}";
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            JsonNode jsonNode=objectMapper.readTree(json).get("responseData").get("results");
            jsonNode.findParent("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
