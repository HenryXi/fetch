package com.henry.threads;

import com.henry.util.JDBCHelper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.*;

import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by henxii on 1/9/15.
 */
public class GetAllPager extends Thread {
    protected String userAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 Firefox/27.0";
    public static int proxyNumber = 0;
    Long startNumber;
    Long endNumber;
    JdbcTemplate jdbcTemplate = null;
    ConcurrentHashMap<String,Proxy> proxys=new ConcurrentHashMap<String,Proxy>();
    Random random = new Random();
    Proxy currentProxy;
    public GetAllPager(){}
    public GetAllPager(Long startNumber, Long endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        try {
            jdbcTemplate = JDBCHelper.getJdbcTemplate("po");
            List targets = jdbcTemplate.queryForList("select index from tb_content where index=" + endNumber);
            if (targets.size() != 0) {
                this.startNumber = endNumber;
                System.out.println("skip-->" + startNumber);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            jdbcTemplate = null;
        }
        String proxyJson = "[{\"ip_port\":\"117.170.243.47:8123\"},{\"ip_port\":\"124.202.217.134:8118\"},{\"ip_port\":\"110.34.39.229:8080\"},{\"ip_port\":\"117.162.241.210:8123\"},{\"ip_port\":\"117.164.26.109:8123\"},{\"ip_port\":\"167.114.71.58:3128\"},{\"ip_port\":\"186.91.240.93:8080\"},{\"ip_port\":\"45.64.9.126:8080\"},{\"ip_port\":\"183.249.13.193:8123\"},{\"ip_port\":\"193.93.238.95:8080\"},{\"ip_port\":\"39.188.161.63:8123\"},{\"ip_port\":\"79.119.192.77:8080\"},{\"ip_port\":\"41.35.195.202:8080\"},{\"ip_port\":\"120.199.255.150:8123\"},{\"ip_port\":\"39.182.234.248:8123\"},{\"ip_port\":\"183.245.218.68:8123\"},{\"ip_port\":\"86.107.110.73:8089\"},{\"ip_port\":\"95.211.6.190:3128\"},{\"ip_port\":\"182.191.74.128:3128\"},{\"ip_port\":\"117.170.178.116:8123\"},{\"ip_port\":\"195.206.54.158:3128\"},{\"ip_port\":\"117.170.227.183:8123\"},{\"ip_port\":\"117.162.86.183:8123\"},{\"ip_port\":\"117.165.229.119:8123\"},{\"ip_port\":\"117.162.4.200:8123\"},{\"ip_port\":\"117.178.125.9:8123\"},{\"ip_port\":\"120.206.145.128:8123\"},{\"ip_port\":\"177.184.135.13:8080\"},{\"ip_port\":\"117.164.42.235:8123\"},{\"ip_port\":\"117.166.57.59:8123\"},{\"ip_port\":\"117.165.35.135:8123\"},{\"ip_port\":\"117.165.202.194:8123\"},{\"ip_port\":\"117.168.28.177:8123\"},{\"ip_port\":\"117.167.69.163:8123\"},{\"ip_port\":\"117.166.81.219:8123\"},{\"ip_port\":\"117.169.163.223:8123\"},{\"ip_port\":\"117.166.65.112:8123\"},{\"ip_port\":\"117.171.17.68:8123\"},{\"ip_port\":\"117.170.252.70:8123\"},{\"ip_port\":\"117.169.186.250:8123\"},{\"ip_port\":\"117.166.22.250:8123\"},{\"ip_port\":\"117.164.44.184:8123\"},{\"ip_port\":\"117.178.142.1:8123\"},{\"ip_port\":\"117.162.98.42:8123\"},{\"ip_port\":\"117.178.215.170:8123\"},{\"ip_port\":\"117.178.51.100:8123\"},{\"ip_port\":\"117.163.231.206:8123\"},{\"ip_port\":\"117.178.234.13:8123\"},{\"ip_port\":\"120.206.104.135:8123\"},{\"ip_port\":\"117.164.131.179:8123\"}]";
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

    private void saveInDB(Document doc, long index) {
        int updates = jdbcTemplate.update("insert into tb_content (title,url,content,index) values (?,?,?,?)",
                doc.title(), doc.baseUri(), doc.html(), index);
        if (updates == 1) {
            System.out.println(startNumber + " ---> " + doc.title() + " ---> insert successful");
        }
    }

    private void saveInDBFor503(String url) {
        int updates = jdbcTemplate.update("INSERT INTO tb_503content(url) VALUES (?)", url);
        if (updates == 1) {
            System.out.println("not fetch this url --> " + url);
        }
    }

    public void run() {
        for (long i = startNumber; i <= endNumber; i++) {
            Document doc = null;
            try {
//                doc = Jsoup.connect("http://stackoverflow.com/questions/" + i).timeout(30*1000).get();
                doc = getDoc("http://stackoverflow.com/questions/" + i);
                if(null==doc || doc.title().equals("")){
                    String url=doc==null?""+i:doc.baseUri();
                    System.out.println("get one 404 url-->" + url);
                    continue;
                }
                if (doc.baseUri().contains(i + "#" + i)) {
                    System.out.println("skip one url-->" + i);
                    continue;
                }
                saveInDB(doc, i);
            } catch (DuplicateKeyException e) {
                Object[] params = {i, doc.title()};
                int[] types = {Types.BIGINT, Types.VARCHAR};
                jdbcTemplate.update("update tb_content set index=? where title=?", params, types);
                System.out.printf("update index("+i+") successful!");
                continue;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        System.out.println("---------->" + startNumber + "---" + endNumber + " end---------");
    }

    public Document getDoc(String url) throws MalformedURLException {
        URL website = new URL(url);
        System.out.println("proxy -->" +( currentProxy==null?"null":currentProxy.address()));
        HttpURLConnection httpUrlConnetion=new HttpURLConnection(website,currentProxy);
        httpUrlConnetion.setConnectTimeout(1000*40);
        httpUrlConnetion.setReadTimeout(1000 * 20);
//        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            switch (stateCode/100) {
                case 5:
                    System.out.println("state -->"+stateCode);
                    currentProxy = proxys.get(String.valueOf(random.nextInt(proxys.size())));
                    return getDoc(url);
                case 4:
                    System.out.println("state -->"+stateCode);
                    return null;
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
            if (e instanceof SocketTimeoutException || e instanceof NoRouteToHostException || e instanceof ConnectException) {
                System.out.println("bad proxy change another!");
                currentProxy=proxys.get(String.valueOf(random.nextInt(proxys.size())));
                return getDoc(url);
            } else if(e.getMessage().contains("Server returned HTTP response code: 500")){
                saveInDBFor503(url);
            } else{
                String other="";
            }
        }finally {
            httpUrlConnetion.disconnect();
        }

        Document doc= Jsoup.parse(page, String.valueOf(httpUrlConnetion.getURL()));
        if(doc.title().equals("")){
            String test="";
        }
        return doc;
    }


    public static void main(String[] args) {
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        GetAllPager getAllPager = new GetAllPager();
        try {
            Document document=getAllPager.getDoc("http://goobbe.com/questions/11/is-it-possible-to-distribute-an-iphone-app-in-a-specific-group-of-people");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
