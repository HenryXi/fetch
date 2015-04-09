package cn.edu.hfut.dmic.webcollector.threads;

import cn.edu.hfut.dmic.webcollector.Answer;
import cn.edu.hfut.dmic.webcollector.Comment;
import cn.edu.hfut.dmic.webcollector.Question;
import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by henxii on 1/9/15.
 */
public class GetAllPager2Json extends Thread {
    ObjectMapper objectMapper=new ObjectMapper();
    private Question question;
    protected String userAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 Firefox/27.0";
    private List<String> allLinksOfOnePage;
    Long startNumber;
    Long endNumber;
    JdbcTemplate jdbcTemplate = null;
    ConcurrentHashMap<String,Proxy> proxys=new ConcurrentHashMap<String,Proxy>();
    Random random = new Random();
    public Proxy currentProxy;

    public GetAllPager2Json(Long startNumber, Long endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        jdbcTemplate = JDBCHelper.getJdbcTemplate("po");
        String proxyJson = "[{\"ip_port\":\"54.250.132.57:80\"},{\"ip_port\":\"123.138.184.228:80\"},{\"ip_port\":\"183.223.78.238:8123\"},{\"ip_port\":\"64.186.47.179:3128\"},{\"ip_port\":\"176.31.84.249:8089\"},{\"ip_port\":\"176.31.84.249:7808\"},{\"ip_port\":\"54.93.173.198:3128\"},{\"ip_port\":\"176.31.84.249:3127\"},{\"ip_port\":\"91.229.20.73:80\"},{\"ip_port\":\"124.88.67.33:80\"},{\"ip_port\":\"124.88.67.33:82\"},{\"ip_port\":\"110.173.54.154:80\"},{\"ip_port\":\"88.150.136.180:3128\"},{\"ip_port\":\"88.150.136.179:3128\"},{\"ip_port\":\"183.207.224.50:85\"},{\"ip_port\":\"183.207.224.51:82\"},{\"ip_port\":\"183.207.228.50:80\"},{\"ip_port\":\"183.207.228.115:80\"},{\"ip_port\":\"183.207.228.116:80\"},{\"ip_port\":\"183.207.228.60:80\"},{\"ip_port\":\"183.207.228.58:80\"},{\"ip_port\":\"183.207.228.57:80\"},{\"ip_port\":\"183.207.224.51:80\"},{\"ip_port\":\"183.207.224.44:80\"},{\"ip_port\":\"183.207.224.51:84\"},{\"ip_port\":\"88.150.136.180:3129\"},{\"ip_port\":\"88.150.136.179:3129\"},{\"ip_port\":\"88.150.136.181:3129\"},{\"ip_port\":\"183.207.228.50:83\"},{\"ip_port\":\"119.6.144.74:82\"},{\"ip_port\":\"183.207.232.193:8080\"},{\"ip_port\":\"114.80.182.132:80\"},{\"ip_port\":\"183.207.228.54:84\"},{\"ip_port\":\"183.207.224.51:85\"},{\"ip_port\":\"119.90.127.3:80\"},{\"ip_port\":\"111.1.36.133:80\"},{\"ip_port\":\"111.1.36.6:80\"},{\"ip_port\":\"120.198.243.118:80\"},{\"ip_port\":\"120.198.243.53:80\"},{\"ip_port\":\"120.198.243.130:80\"},{\"ip_port\":\"120.198.243.116:80\"},{\"ip_port\":\"120.198.243.113:80\"},{\"ip_port\":\"120.198.243.82:80\"},{\"ip_port\":\"120.198.243.51:80\"},{\"ip_port\":\"120.198.243.50:80\"},{\"ip_port\":\"120.198.243.114:80\"},{\"ip_port\":\"120.198.243.15:80\"},{\"ip_port\":\"120.198.243.86:80\"},{\"ip_port\":\"120.198.243.52:80\"},{\"ip_port\":\"120.198.243.48:80\"}]";
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
        try {
            if(!doc.baseUri().contains("http://stackoverflow.com/questions/")){
                return;
            }
            jdbcTemplate.update("update tb_content set content=?::json ,url=? where url=?",
                objectMapper.writeValueAsString(question),Integer.valueOf(doc.baseUri().replace("http://stackoverflow.com/questions/", "").replaceAll("/.+", "")),oldUrl);
        }catch (DuplicateKeyException e){
            jdbcTemplate.update("delete from tb_content where url=?",oldUrl);
            return;
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        List<Integer> records=jdbcTemplate.queryForList("select url from tb_content where id between "+startNumber +" and "+endNumber,Integer.class);
        Document doc = null;
        for (Integer url:records) {
            try {
                doc = getDoc(1348569);
                saveContentAsJsonInDB(doc,url);
            } catch (Throwable e) {
                e.printStackTrace();
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
//        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
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
//        JDBCHelper.createMysqlTemplate("po",
//                "jdbc:postgresql://123.57.136.60:5432/goobbe",
//                "yong", "xixiaoyong123", 80, 120);
//        GetAllPager2Json getAllPager = new GetAllPager2Json(1348569l, 300500l);
//        getAllPager.start();
//        do {
//            try {
//                Thread.sleep(1000*10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(getAllPager.isAlive());
//        } while (true);
        System.out.printf(System.getProperty("user.home"));
    }
}
