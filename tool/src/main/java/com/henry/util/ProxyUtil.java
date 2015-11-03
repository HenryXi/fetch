package com.henry.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyUtil {
    private static ProxyUtil proxyUtil;
    private static Map<String,Proxy> proxys=new ConcurrentHashMap<>();
    private ProxyUtil(){
        Config.getInstance("fetch.properties");
        String proxyJson = Config.getString("proxy.ips");
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
            for(int i=0;i<Config.getInt("no.proxy.number");i++){
                proxys.put(String.valueOf(proxys.size()+1),Proxy.NO_PROXY);
            }
            for(int i=0;i<Config.getInt("usable.proxy.number");i++){
                proxys.put(String.valueOf(proxys.size()+1),
                        new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)));
                proxys.put(String.valueOf(proxys.size()+1),
                        new Proxy(Proxy.Type.HTTP, new InetSocketAddress("123.57.136.60", 1080)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProxyUtil getInstance(){
        if(proxyUtil==null){
            return new ProxyUtil();
        }
        return proxyUtil;
    }

    public static Map<String, Proxy> getProxys() {
        return proxys;
    }

    public static void setProxys(Map<String, Proxy> proxys) {
        ProxyUtil.proxys = proxys;
    }
}
