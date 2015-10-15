package com.henry.threads;

import com.henry.model.Answer;
import com.henry.model.Comment;
import com.henry.model.Question;
import com.henry.util.JDBCHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by henxii on 1/9/15.
 */
public class AddTag4Question extends Thread {
    ObjectMapper objectMapper=new ObjectMapper();
    private Question question;
    protected String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36";
    private List<String> allLinksOfOnePage;
    Long startNumber;
    Long endNumber;
    JdbcTemplate jdbcTemplate = null;
    ConcurrentHashMap<String,Proxy> proxys=new ConcurrentHashMap<String,Proxy>();
    Random random = new Random();
    public Proxy currentProxy;
    private boolean loseConnection;

    public AddTag4Question(Long startNumber, Long endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        jdbcTemplate = JDBCHelper.getJdbcTemplate("po");
        String proxyJson = "[{\"ip_port\":\"111.40.196.68:80\"},{\"ip_port\":\"117.177.243.71:82\"},{\"ip_port\":\"117.177.243.42:81\"},{\"ip_port\":\"117.177.243.43:86\"},{\"ip_port\":\"117.177.246.144:82\"},{\"ip_port\":\"117.177.243.71:85\"},{\"ip_port\":\"117.177.243.71:81\"},{\"ip_port\":\"5.196.227.22:3128\"},{\"ip_port\":\"186.249.177.253:80\"},{\"ip_port\":\"111.40.196.36:80\"},{\"ip_port\":\"222.222.169.68:80\"},{\"ip_port\":\"213.85.92.10:80\"},{\"ip_port\":\"190.203.178.131:8080\"},{\"ip_port\":\"193.87.168.101:3128\"},{\"ip_port\":\"183.221.53.185:8123\"},{\"ip_port\":\"84.26.93.234:80\"},{\"ip_port\":\"117.178.193.163:8123\"},{\"ip_port\":\"201.54.36.228:3128\"},{\"ip_port\":\"186.14.215.139:8080\"},{\"ip_port\":\"186.88.99.153:8080\"},{\"ip_port\":\"218.97.195.36:83\"},{\"ip_port\":\"109.195.210.164:8080\"},{\"ip_port\":\"179.192.31.236:8080\"},{\"ip_port\":\"117.164.168.56:8123\"},{\"ip_port\":\"104.235.29.75:8080\"},{\"ip_port\":\"117.174.196.165:8123\"},{\"ip_port\":\"113.108.230.104:1080\"},{\"ip_port\":\"92.62.130.51:80\"},{\"ip_port\":\"65.255.32.15:8080\"},{\"ip_port\":\"103.4.167.230:8080\"},{\"ip_port\":\"117.177.243.42:86\"},{\"ip_port\":\"183.211.85.156:8123\"},{\"ip_port\":\"112.15.49.47:8123\"},{\"ip_port\":\"39.189.69.169:8123\"},{\"ip_port\":\"39.176.153.64:8123\"},{\"ip_port\":\"223.94.135.154:8123\"},{\"ip_port\":\"202.154.4.18:8080\"},{\"ip_port\":\"117.177.243.43:83\"},{\"ip_port\":\"60.13.74.184:82\"},{\"ip_port\":\"117.177.141.8:8123\"},{\"ip_port\":\"117.177.243.29:85\"},{\"ip_port\":\"117.177.243.42:85\"},{\"ip_port\":\"190.78.27.225:8080\"},{\"ip_port\":\"208.53.215.69:80\"},{\"ip_port\":\"50.203.194.49:80\"},{\"ip_port\":\"186.88.46.209:8080\"},{\"ip_port\":\"190.79.51.28:8080\"},{\"ip_port\":\"139.255.53.90:8080\"},{\"ip_port\":\"39.167.43.42:8123\"},{\"ip_port\":\"103.18.222.58:8080\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(proxyJson);
            String ip;
            int port;
            for (int i = 0; i < jsonNode.size(); i++) {
                ip = jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[0];
                port = Integer.valueOf(jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[1]);
                proxys.put(String.valueOf(i), new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
            }
            for(int i=0;i<10;i++){
                proxys.put(String.valueOf(proxys.size()+1),Proxy.NO_PROXY);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentProxy=Proxy.NO_PROXY;
    }

    private void saveContentAsJsonInDB(Document doc,Integer oldUrl) {
        if(doc==null){
            return;
        }
        question=new Question(doc.select("#question-header>h1>a").html(),doc.select(".postcell>div>.post-text").html());
        for(Element questionComment:doc.select(".question").select(".comment-copy")){
            question.getCs().add(new Comment(questionComment.html()));
        }
        for(Element ans:doc.select(".answer")){
            Answer answer=new Answer(ans.select(".post-text").html());
            for(Element com:ans.select(".comment-copy")){
                answer.getCs().add(new Comment(com.html()));
            }
            question.getAs().add(answer);
        }
        for(Element tag :doc.select(".post-taglist>.post-tag")){
            question.getTs().add(tag.text());
        }
        try {
            if(!doc.baseUri().contains("http://stackoverflow.com/questions/")){
                return;
            }
            int newUrl=Integer.valueOf(doc.baseUri().replace("http://stackoverflow.com/questions/", "").replaceAll("/.+", ""));
            if(newUrl==oldUrl){
                jdbcTemplate.update("update tb_content set content=?::json where url=?",objectMapper.writeValueAsString(question),oldUrl);
            }else{
                //oldUrl have changed 301 code
                jdbcTemplate.update("update tb_content set content=null where url=?",oldUrl);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Document doc = null;
        List<Integer> records=null;
        try{
            records=jdbcTemplate.queryForList("select url from tb_content where id between "+startNumber +" and "+endNumber,Integer.class);
        }catch (DataAccessException e){
            System.out.println("ERROR ---------->" + startNumber + "---" + endNumber + " end----");
            //loseConnection=true;
        }
        for (Integer url:records) {
            try {
                doc = getDoc(url);
                saveContentAsJsonInDB(doc,url);
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println("ERROR ---------->" + startNumber + "---" + endNumber + " end----");
                //loseConnection=true;
            }
        }
        System.out.println("---------->" + startNumber + "---" + endNumber + " end---------");
    }

    private Document getDoc(Integer url) throws MalformedURLException {
        URL website = new URL("http://stackoverflow.com/questions/"+url);
        //System.out.println("proxy -->" +( currentProxy==null?"null":currentProxy.address()));
        HttpURLConnection httpUrlConnetion=new HttpURLConnection(website,currentProxy);
        httpUrlConnetion.setConnectTimeout(1000*40);
        httpUrlConnetion.setReadTimeout(1000 * 20);
        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            if(stateCode!=200){
                System.out.println("state -->"+stateCode+" url --->"+url+" proxy -->" +( currentProxy==null?"null":currentProxy.address()));
                if(stateCode==404 || stateCode==403){
                    if(Proxy.NO_PROXY.equals(currentProxy)){
                        jdbcTemplate.update("update tb_content set content=null where url=?",url);
                        return null;
                    }else{
                        currentProxy=Proxy.NO_PROXY;
                        return getDoc(url);
                    }
                }
                currentProxy = proxys.get(String.valueOf(random.nextInt(proxys.size())));
                return getDoc(url);
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
                currentProxy=proxys.get(String.valueOf(random.nextInt(proxys.size())));
                return getDoc(url);
        }finally {
            httpUrlConnetion.disconnect();
        }
        Document doc= Jsoup.parse(page, String.valueOf(httpUrlConnetion.getURL()));
        return doc;
    }


    public static void main(String[] args) {
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
        AddTag4Question getAllPager = new AddTag4Question(8147230l, 8147230l);
        getAllPager.start();
    }

    public boolean isLoseConnection() {
        return loseConnection;
    }

    public void setLoseConnection(boolean loseConnection) {
        this.loseConnection = loseConnection;
    }
}
