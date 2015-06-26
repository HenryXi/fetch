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
import org.springframework.dao.DataAccessException;
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
public class AddTag4Question extends Thread {
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
    private boolean loseConnection;

    public AddTag4Question(Long startNumber, Long endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        jdbcTemplate = JDBCHelper.getJdbcTemplate("po");
        String proxyJson = "[{\"ip_port\":\"195.154.170.153:3128\"},{\"ip_port\":\"159.253.133.50:8080\"},{\"ip_port\":\"64.213.148.50:8080\"},{\"ip_port\":\"52.74.218.190:3128\"},{\"ip_port\":\"45.55.227.86:3128\"},{\"ip_port\":\"52.24.43.118:3128\"},{\"ip_port\":\"54.199.12.181:80\"},{\"ip_port\":\"217.168.89.80:8080\"},{\"ip_port\":\"88.132.11.160:8088\"},{\"ip_port\":\"162.208.49.45:7808\"},{\"ip_port\":\"114.215.108.155:9999\"},{\"ip_port\":\"188.165.216.161:3128\"},{\"ip_port\":\"123.150.207.109:80\"},{\"ip_port\":\"124.254.57.150:8118\"},{\"ip_port\":\"211.68.122.171:80\"},{\"ip_port\":\"212.82.126.32:80\"},{\"ip_port\":\"212.250.202.217:80\"},{\"ip_port\":\"111.40.196.68:80\"},{\"ip_port\":\"201.243.159.171:8080\"},{\"ip_port\":\"190.36.147.202:8080\"},{\"ip_port\":\"201.243.165.41:8080\"},{\"ip_port\":\"190.78.150.196:8080\"},{\"ip_port\":\"201.243.128.180:8080\"},{\"ip_port\":\"190.142.43.75:8080\"},{\"ip_port\":\"190.75.47.78:8080\"},{\"ip_port\":\"201.243.125.192:8080\"},{\"ip_port\":\"201.248.20.16:8080\"},{\"ip_port\":\"190.200.154.230:8080\"},{\"ip_port\":\"190.72.132.135:8080\"},{\"ip_port\":\"190.203.253.62:8080\"},{\"ip_port\":\"201.248.96.128:8080\"},{\"ip_port\":\"190.72.122.206:8080\"},{\"ip_port\":\"201.243.186.33:8080\"},{\"ip_port\":\"190.198.145.197:8080\"},{\"ip_port\":\"190.36.144.96:8080\"},{\"ip_port\":\"201.211.170.59:8080\"},{\"ip_port\":\"190.198.231.15:8080\"},{\"ip_port\":\"190.94.201.160:8080\"},{\"ip_port\":\"201.243.145.55:8080\"},{\"ip_port\":\"190.37.57.225:8080\"},{\"ip_port\":\"190.73.138.144:8080\"},{\"ip_port\":\"190.202.209.186:8080\"},{\"ip_port\":\"190.36.94.160:8080\"},{\"ip_port\":\"190.72.129.39:8080\"},{\"ip_port\":\"190.36.11.48:8080\"},{\"ip_port\":\"201.243.174.204:8080\"},{\"ip_port\":\"190.198.16.251:8080\"},{\"ip_port\":\"190.198.150.109:8080\"},{\"ip_port\":\"190.73.119.173:8080\"},{\"ip_port\":\"190.198.22.118:8080\"}]";
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
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
        AddTag4Question getAllPager = new AddTag4Question(1l, 2l);
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

    public boolean isLoseConnection() {
        return loseConnection;
    }

    public void setLoseConnection(boolean loseConnection) {
        this.loseConnection = loseConnection;
    }
}
