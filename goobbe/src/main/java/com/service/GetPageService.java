package com.service;

import com.util.GoobbeLogger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yong on 2015/3/5.
 */
@Service
public class GetPageService extends GoobbeLogger {
    private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36";
    ConcurrentHashMap<String,Proxy> proxys=new ConcurrentHashMap<String,Proxy>();
    Random random = new Random();
    public Proxy currentProxy;
    public GetPageService(){
        String proxyJson = "[{\"ip_port\":\"221.178.99.151:8123\"},{\"ip_port\":\"117.166.127.193:8123\"},{\"ip_port\":\"207.58.234.35:80\"},{\"ip_port\":\"120.206.239.25:8123\"},{\"ip_port\":\"117.164.215.33:8123\"},{\"ip_port\":\"223.83.42.254:8123\"},{\"ip_port\":\"183.216.170.114:8123\"},{\"ip_port\":\"103.10.61.104:8080\"},{\"ip_port\":\"117.165.103.26:8123\"},{\"ip_port\":\"117.178.251.50:8123\"},{\"ip_port\":\"88.150.136.179:3129\"},{\"ip_port\":\"88.150.136.180:3129\"},{\"ip_port\":\"117.169.232.34:8123\"},{\"ip_port\":\"120.206.106.167:8123\"},{\"ip_port\":\"117.162.237.21:8123\"},{\"ip_port\":\"117.162.147.64:8123\"},{\"ip_port\":\"111.12.128.171:8081\"},{\"ip_port\":\"117.168.4.166:8123\"},{\"ip_port\":\"112.15.28.34:8123\"},{\"ip_port\":\"111.192.165.69:8118\"},{\"ip_port\":\"117.162.238.183:8123\"},{\"ip_port\":\"123.138.185.50:80\"},{\"ip_port\":\"117.165.10.189:8123\"},{\"ip_port\":\"117.135.250.51:81\"},{\"ip_port\":\"117.135.250.51:82\"},{\"ip_port\":\"183.207.229.130:83\"},{\"ip_port\":\"117.165.94.155:8123\"},{\"ip_port\":\"111.13.136.58:843\"},{\"ip_port\":\"117.166.170.80:8123\"},{\"ip_port\":\"117.10.37.37:8118\"},{\"ip_port\":\"120.206.186.208:8123\"},{\"ip_port\":\"117.166.170.132:8123\"},{\"ip_port\":\"117.170.12.239:8123\"},{\"ip_port\":\"111.12.128.171:8083\"},{\"ip_port\":\"117.165.36.89:8123\"},{\"ip_port\":\"14.139.172.162:3128\"},{\"ip_port\":\"88.150.136.181:3129\"},{\"ip_port\":\"183.216.189.106:8123\"},{\"ip_port\":\"183.217.204.193:8123\"},{\"ip_port\":\"183.216.250.139:8123\"},{\"ip_port\":\"117.165.170.69:8123\"},{\"ip_port\":\"112.78.1.36:3128\"},{\"ip_port\":\"183.207.229.130:80\"},{\"ip_port\":\"117.171.86.86:8123\"},{\"ip_port\":\"117.170.84.123:8123\"},{\"ip_port\":\"117.163.225.48:8123\"},{\"ip_port\":\"117.166.0.246:8123\"},{\"ip_port\":\"117.165.13.188:8123\"},{\"ip_port\":\"117.166.123.92:8123\"},{\"ip_port\":\"117.164.12.83:8123\"}]";
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

    public Document getDoc(String url) {
        URL website;
        try {
            website = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection httpUrlConnetion=new HttpURLConnection(website,currentProxy);
        httpUrlConnetion.setConnectTimeout(1000 * 40);
        httpUrlConnetion.setReadTimeout(1000 * 20);
        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            if(stateCode!=200){
                if(stateCode==404 || stateCode==403){
                    if(Proxy.NO_PROXY.equals(currentProxy)){
                        return null;
                    }else{
                        currentProxy=Proxy.NO_PROXY;
                        return getDoc(url);
                    }
                }
                warn("http state: [" + stateCode + "]");
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
            warn("bad proxy ["+currentProxy.toString()+"] change another!");
            currentProxy=proxys.get(String.valueOf(random.nextInt(proxys.size())));
            return getDoc(url);
        }finally {
            httpUrlConnetion.disconnect();
        }
        Document doc= Jsoup.parse(page, String.valueOf(httpUrlConnetion.getURL()));
        return doc;
    }
}
