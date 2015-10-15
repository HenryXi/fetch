package com.henry.threads;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yong on 2015/3/4.
 */
public class GoogleSearch {
    private List<Proxy> proxyArray=new ArrayList<>();
    private Random random = new Random();
    public Proxy currentProxy;
    public GoogleSearch(){
        String proxyJson = "[{\"ip_port\":\"103.27.24.114:80\"},{\"ip_port\":\"103.27.24.113:8080\"},{\"ip_port\":\"103.27.24.113:80\"},{\"ip_port\":\"103.27.24.113:3128\"},{\"ip_port\":\"42.236.33.180:80\"},{\"ip_port\":\"42.236.33.179:80\"},{\"ip_port\":\"42.236.33.177:80\"},{\"ip_port\":\"182.92.64.30:8080\"},{\"ip_port\":\"36.250.74.88:8103\"},{\"ip_port\":\"185.72.156.19:3127\"},{\"ip_port\":\"36.250.74.87:8103\"},{\"ip_port\":\"101.4.136.34:9999\"},{\"ip_port\":\"117.135.252.14:80\"},{\"ip_port\":\"191.7.196.174:8080\"},{\"ip_port\":\"122.225.117.26:80\"},{\"ip_port\":\"120.197.234.166:80\"},{\"ip_port\":\"222.87.129.218:80\"},{\"ip_port\":\"61.232.6.164:8081\"},{\"ip_port\":\"69.197.148.18:3127\"},{\"ip_port\":\"94.23.23.60:80\"},{\"ip_port\":\"60.18.147.42:80\"},{\"ip_port\":\"222.35.185.129:8080\"},{\"ip_port\":\"162.208.49.45:7808\"},{\"ip_port\":\"67.158.229.171:8080\"},{\"ip_port\":\"186.89.161.6:8080\"},{\"ip_port\":\"189.16.13.90:8080\"},{\"ip_port\":\"190.78.236.216:8080\"},{\"ip_port\":\"190.60.61.240:8080\"},{\"ip_port\":\"117.168.229.224:8123\"},{\"ip_port\":\"36.250.74.88:8104\"},{\"ip_port\":\"173.208.248.221:7808\"},{\"ip_port\":\"104.238.200.211:8089\"},{\"ip_port\":\"204.44.116.114:7808\"},{\"ip_port\":\"41.89.96.6:3128\"},{\"ip_port\":\"14.139.59.51:3128\"},{\"ip_port\":\"188.234.139.89:80\"},{\"ip_port\":\"111.206.50.177:80\"},{\"ip_port\":\"182.239.127.140:82\"},{\"ip_port\":\"202.171.253.74:81\"},{\"ip_port\":\"202.171.253.84:83\"},{\"ip_port\":\"182.239.127.139:82\"},{\"ip_port\":\"111.7.128.171:80\"},{\"ip_port\":\"183.207.228.52:86\"},{\"ip_port\":\"183.207.228.54:83\"},{\"ip_port\":\"202.171.253.74:86\"},{\"ip_port\":\"183.207.228.50:82\"},{\"ip_port\":\"183.207.228.54:82\"},{\"ip_port\":\"183.207.228.52:82\"},{\"ip_port\":\"183.207.228.50:84\"},{\"ip_port\":\"183.207.228.50:86\"}]";
//        String proxyJson="[\n" +
//            "    {\n" +
//            "        \"ip_port\": \"127.0.0.1:8087\"\n" +
//            "    }\n" +
//            "]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(proxyJson);
            String ip;
            int port;
            for (int i = 0; i < jsonNode.size(); i++) {
                ip = jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[0];
                port = Integer.valueOf(jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[1]);
                proxyArray.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
            }
            currentProxy=proxyArray.get(random.nextInt(proxyArray.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getResult(String target,int i){
        System.out.println(currentProxy.toString()+" "+proxyArray.size());
        String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start="+i+"&q=";
//        String query = "programcreek";
        String charset = "UTF-8";
        try{
            URL website = new URL(address + URLEncoder.encode(target, charset)+"");
            HttpURLConnection httpUrlConnetion=new HttpURLConnection(website,proxyArray.get(random.nextInt(proxyArray.size())));
//            httpUrlConnetion.setConnectTimeout(1000*60);
//            httpUrlConnetion.setReadTimeout(1000*60);
            if(200!=httpUrlConnetion.getResponseCode()){
                System.out.println("state->"+httpUrlConnetion.getResponseCode());
//                proxyArray.remove(currentProxy);
                currentProxy=proxyArray.get(random.nextInt(proxyArray.size()));
                return getResult(target,i);
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
            httpUrlConnetion.disconnect();
            String result=new String(content, StandardCharsets.UTF_8);
            if(!result.contains("\"responseStatus\": 200")){
                System.out.println("bad result");
//                proxyArray.remove(currentProxy);
                currentProxy=proxyArray.get(random.nextInt(proxyArray.size()));
                return getResult(target,i);
            }
            return new String(content, StandardCharsets.UTF_8);
        }catch (SocketTimeoutException e){
            System.out.println("timeout");
        }catch (Exception e){
            System.out.println(e.getClass()+currentProxy.toString()+" "+proxyArray.size());
            //e.printStackTrace();
        }
//        proxyArray.remove(currentProxy);
        currentProxy=proxyArray.get(random.nextInt(proxyArray.size()));
        return getResult(target,i);
    }
}

