package cn.edu.hfut.dmic.webcollector;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.*;
import java.net.Proxy;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by henxii on 1/8/15.
 */
public class TranslateHelper {
    Random random = new Random();
    Proxy currentProxy;
    private String url="http://translate.google.cn/translate_a/t?client=g&sl=en&tl=zh-CN&hl=zh-CN&q=";
    String userAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:27.0) Gecko/20100101 Firefox/27.0";
    ConcurrentHashMap<String,java.net.Proxy> proxys=new ConcurrentHashMap<String,Proxy>();
    public TranslateHelper(){
        String proxyJson = "[{\"ip_port\":\"183.207.224.50:82\"},{\"ip_port\":\"183.207.224.51:81\"},{\"ip_port\":\"202.102.22.182:80\"},{\"ip_port\":\"163.177.79.5:8101\"},{\"ip_port\":\"212.250.202.217:80\"},{\"ip_port\":\"202.55.23.144:80\"},{\"ip_port\":\"202.55.23.168:80\"},{\"ip_port\":\"111.40.196.68:80\"},{\"ip_port\":\"183.207.224.51:86\"},{\"ip_port\":\"183.207.224.50:84\"},{\"ip_port\":\"183.207.224.50:86\"},{\"ip_port\":\"123.125.104.242:80\"},{\"ip_port\":\"213.85.92.10:80\"},{\"ip_port\":\"54.250.132.57:80\"},{\"ip_port\":\"183.207.228.116:80\"},{\"ip_port\":\"183.207.224.50:85\"},{\"ip_port\":\"183.207.224.51:82\"},{\"ip_port\":\"183.207.224.51:84\"},{\"ip_port\":\"183.207.224.51:80\"},{\"ip_port\":\"183.207.224.44:80\"},{\"ip_port\":\"114.80.182.132:80\"},{\"ip_port\":\"183.207.224.51:85\"},{\"ip_port\":\"183.207.224.50:81\"},{\"ip_port\":\"183.207.224.42:80\"},{\"ip_port\":\"183.207.224.51:83\"},{\"ip_port\":\"183.207.224.43:80\"},{\"ip_port\":\"183.207.224.13:80\"},{\"ip_port\":\"115.231.96.120:80\"},{\"ip_port\":\"101.71.27.120:80\"},{\"ip_port\":\"200.62.212.247:3128\"},{\"ip_port\":\"183.207.237.11:86\"},{\"ip_port\":\"36.250.74.87:8101\"},{\"ip_port\":\"69.10.137.138:8000\"},{\"ip_port\":\"206.17.20.75:3128\"},{\"ip_port\":\"111.12.251.199:80\"},{\"ip_port\":\"111.11.153.19:3128\"},{\"ip_port\":\"111.11.153.19:111\"},{\"ip_port\":\"60.18.147.42:80\"},{\"ip_port\":\"162.208.49.45:7808\"},{\"ip_port\":\"114.215.108.155:9999\"},{\"ip_port\":\"183.203.8.147:8080\"},{\"ip_port\":\"119.4.95.136:80\"},{\"ip_port\":\"58.218.199.124:808\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(proxyJson);
            String ip;
            int port;
            for (int i = 0; i < jsonNode.size(); i++) {
                ip = jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[0];
                port = Integer.valueOf(jsonNode.get(i).get("ip_port").toString().replace("\"", "").split(":")[1]);
                proxys.put(String.valueOf(i), new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentProxy=Proxy.NO_PROXY;
    }
    private void translate(String target) throws MalformedURLException {
        url=url+target;
        URL website = new URL(url);
        System.out.println("proxy -->" +( currentProxy==null?"null":currentProxy.address()));
        sun.net.www.protocol.http.HttpURLConnection httpUrlConnetion=new sun.net.www.protocol.http.HttpURLConnection(website,currentProxy);
        httpUrlConnetion.setDoInput(true);
        httpUrlConnetion.setDoOutput(true);
        httpUrlConnetion.setConnectTimeout(1000);
        httpUrlConnetion.setReadTimeout(1000);
        httpUrlConnetion.setRequestProperty("User-Agent", userAgent);
        httpUrlConnetion.setRequestProperty("Cookie", null);
        String page=null;
        try {
            int stateCode=httpUrlConnetion.getResponseCode();
            switch (stateCode/100) {
                case 5:
                    System.out.println("state -->"+stateCode);
                    currentProxy = proxys.get(String.valueOf(random.nextInt(proxys.size())));
                    translate(target);
                case 4:
                    System.out.println("state -->"+stateCode);
                    currentProxy = proxys.get(String.valueOf(random.nextInt(proxys.size())));
                    translate(target);
            }
            InputStream is=httpUrlConnetion.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            StringBuffer sb = new StringBuffer();
            byte[] read = new byte[4096];
            int length;
            while(-1 != (length = bis.read(read, 0, read.length)))
                sb.append(new String(read, 0, length));

            System.out.println(sb.toString());

            ObjectMapper om = new ObjectMapper();
            TransWrapper transWrapper = om.readValue(sb.toString(), TransWrapper.class);
            //当字符串比较长时，google会进行分段翻译，所以这里得到的是一个数组（注意：不是List）
            Trans[] trans = transWrapper.getSentences();
            String translation = "";

            for(Trans tran : trans)
            {
                translation += tran.getTrans();
            }
            System.out.println(translation);
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException || e instanceof NoRouteToHostException || e instanceof ConnectException) {
                System.out.println("bad proxy change another!");
                currentProxy=proxys.get(String.valueOf(random.nextInt(proxys.size())));
                translate(target);
            } else{
                String other="";
            }
        }finally {
            httpUrlConnetion.disconnect();
        }


    }


    public static void main(String args[]) throws IOException{
//        System.setProperty("http.proxyHost", "183.203.8.147");
//        System.setProperty("http.proxyPort", "8080");
//        System.setProperty("https.proxyHost", "183.203.8.147");
//        System.setProperty("https.proxyPort", "8080");
        Document doc=Jsoup.connect("http://translate.google.cn/translate_a/t?client=g&sl=en&tl=zh-CN&hl=zh-CN&q=hello").userAgent("Mozilla").timeout(5000).get();
        doc.body();
    }


}
class Trans
{
    private String trans;
    private String orig;
    private String translit;
    private String src_translit;

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public String getTranslit() {
        return translit;
    }

    public void setTranslit(String translit) {
        this.translit = translit;
    }

    public String getSrc_translit() {
        return src_translit;
    }

    public void setSrc_translit(String src_translit) {
        this.src_translit = src_translit;
    }
}
class TransWrapper
{
    private Trans[] sentences;
    private String src;
    private String server_time;

    public Trans[] getSentences() {
        return sentences;
    }

    public void setSentences(Trans[] sentences) {
        this.sentences = sentences;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    // setters and getters omitted, 此处略去get，set方法
}
