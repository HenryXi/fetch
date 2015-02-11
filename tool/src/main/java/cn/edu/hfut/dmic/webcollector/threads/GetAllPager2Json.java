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
        String proxyJson = "[{\"ip_port\":\"103.27.24.114:80\"},{\"ip_port\":\"117.166.4.104:8123\"},{\"ip_port\":\"36.250.74.88:8102\"},{\"ip_port\":\"103.27.24.113:80\"},{\"ip_port\":\"103.27.24.113:3128\"},{\"ip_port\":\"103.27.24.113:8080\"},{\"ip_port\":\"117.165.37.114:8123\"},{\"ip_port\":\"117.166.108.111:8123\"},{\"ip_port\":\"117.163.215.173:8123\"},{\"ip_port\":\"117.165.14.184:8123\"},{\"ip_port\":\"120.203.174.244:8123\"},{\"ip_port\":\"183.217.118.44:8123\"},{\"ip_port\":\"117.165.42.102:8123\"},{\"ip_port\":\"183.217.206.95:8123\"},{\"ip_port\":\"190.238.239.25:8080\"},{\"ip_port\":\"146.166.142.3:8080\"},{\"ip_port\":\"117.163.226.164:8123\"},{\"ip_port\":\"117.163.243.3:8123\"},{\"ip_port\":\"117.165.196.39:8123\"},{\"ip_port\":\"117.164.33.75:8123\"},{\"ip_port\":\"117.163.192.133:8123\"},{\"ip_port\":\"117.163.22.161:8123\"},{\"ip_port\":\"117.165.196.96:8123\"},{\"ip_port\":\"117.163.123.188:8123\"},{\"ip_port\":\"117.163.229.43:8123\"},{\"ip_port\":\"117.165.244.244:8123\"},{\"ip_port\":\"117.170.232.71:8123\"},{\"ip_port\":\"117.168.248.68:8123\"},{\"ip_port\":\"117.167.25.133:8123\"},{\"ip_port\":\"117.167.130.216:8123\"},{\"ip_port\":\"117.168.207.89:8123\"},{\"ip_port\":\"117.149.243.82:8123\"},{\"ip_port\":\"117.163.168.35:8123\"},{\"ip_port\":\"120.206.186.246:8123\"},{\"ip_port\":\"117.178.173.233:8123\"},{\"ip_port\":\"117.163.2.148:8123\"},{\"ip_port\":\"183.216.162.135:8123\"},{\"ip_port\":\"117.178.232.126:8123\"},{\"ip_port\":\"120.132.71.232:80\"},{\"ip_port\":\"120.206.195.222:8123\"},{\"ip_port\":\"120.206.136.76:8123\"},{\"ip_port\":\"183.217.194.118:8123\"},{\"ip_port\":\"183.217.197.108:8123\"},{\"ip_port\":\"183.218.97.16:8123\"},{\"ip_port\":\"183.217.64.65:8123\"},{\"ip_port\":\"183.220.198.208:8123\"},{\"ip_port\":\"60.248.109.228:8080\"},{\"ip_port\":\"194.94.2.26:3128\"},{\"ip_port\":\"199.217.117.89:3128\"},{\"ip_port\":\"218.23.27.18:9797\"}]";
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
            proxys.put(String.valueOf(jsonNode.size()+1),Proxy.NO_PROXY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentProxy=Proxy.NO_PROXY;
    }

    private void saveContentAsJsonInDB(Document doc) {
        if(doc==null){
            return;
        }
        question=new Question(doc.select("#question-header>h1>a").html(),doc.select(".postcell>div>.post-text").html());
        for(Element questionComment:doc.select(".question").select(".comment-copy")){
            question.getComments().add(new Comment(questionComment.html()));
        }
        for(Element ans:doc.select(".answer")){
            Answer answer=new Answer(ans.select(".post-text").html());
            for(Element com:ans.select(".comment-copy")){
                answer.getComments().add(new Comment(com.html()));
            }
            question.getAnswers().add(answer);
        }
        try {
            int i=jdbcTemplate.update("update tb_content2 set content= ?::json where url=?",objectMapper.writeValueAsString(question),doc.baseUri());
            if(i==0){
                jdbcTemplate.update("insert into tb_content2 (url,content) values (?,?::json)",doc.baseUri(),objectMapper.writeValueAsString(question));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        List<String> records=jdbcTemplate.queryForList("select url from tb_content2 where id between "+startNumber +" and "+endNumber,String.class);
        Document doc = null;
        for (String url:records) {
            try {
                doc = getDoc(url);
                saveContentAsJsonInDB(doc);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        System.out.println("---------->" + startNumber + "---" + endNumber + " end---------");
    }

    private Document getDoc(String url) throws MalformedURLException {
        URL website = new URL(url);
        //System.out.println("proxy -->" +( currentProxy==null?"null":currentProxy.address()));
        HttpURLConnection httpUrlConnetion=new HttpURLConnection(website,currentProxy);
        httpUrlConnetion.setConnectTimeout(1000*40);
        httpUrlConnetion.setReadTimeout(1000 * 20);
//        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            if(stateCode!=200){
                System.out.println("state -->"+stateCode+" url --->"+url);
                if(stateCode==404){
                    jdbcTemplate.update("update tb_content2 set content=null where url=?",url);
                    return null;
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
        JDBCHelper.createMysqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        GetAllPager2Json getAllPager = new GetAllPager2Json(2l, 100l);
        getAllPager.start();
        do {
            try {
                Thread.sleep(1000*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getAllPager.isAlive());
        } while (true);
    }
}
