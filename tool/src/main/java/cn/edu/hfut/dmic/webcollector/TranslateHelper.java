package cn.edu.hfut.dmic.webcollector;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

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
        String proxyJson = "[{\"ip_port\":\"177.55.251.26:8080\"},{\"ip_port\":\"202.103.149.187:9999\"},{\"ip_port\":\"186.94.55.151:8080\"},{\"ip_port\":\"190.204.64.199:9064\"},{\"ip_port\":\"201.20.187.35:8080\"},{\"ip_port\":\"186.94.241.7:9064\"},{\"ip_port\":\"95.211.99.112:8080\"},{\"ip_port\":\"201.20.182.28:8080\"},{\"ip_port\":\"111.119.198.182:3128\"},{\"ip_port\":\"177.184.138.98:8080\"},{\"ip_port\":\"91.121.108.174:3128\"},{\"ip_port\":\"84.10.28.77:8080\"},{\"ip_port\":\"103.9.185.57:8080\"},{\"ip_port\":\"110.77.248.105:3128\"},{\"ip_port\":\"190.78.176.39:8080\"},{\"ip_port\":\"190.202.160.129:8080\"},{\"ip_port\":\"190.206.205.32:9064\"},{\"ip_port\":\"128.199.221.102:443\"},{\"ip_port\":\"218.248.47.189:3128\"},{\"ip_port\":\"197.40.94.189:8080\"},{\"ip_port\":\"104.155.213.223:80\"},{\"ip_port\":\"208.109.223.119:8800\"},{\"ip_port\":\"197.33.26.184:8080\"},{\"ip_port\":\"190.207.13.108:8080\"},{\"ip_port\":\"190.202.219.188:8080\"},{\"ip_port\":\"190.38.177.181:8080\"},{\"ip_port\":\"220.242.29.20:80\"},{\"ip_port\":\"200.8.224.171:8080\"},{\"ip_port\":\"181.92.6.135:8080\"},{\"ip_port\":\"115.133.232.128:8080\"},{\"ip_port\":\"125.230.103.85:3128\"},{\"ip_port\":\"41.34.135.238:8080\"},{\"ip_port\":\"190.78.121.178:8080\"},{\"ip_port\":\"190.203.166.15:8080\"},{\"ip_port\":\"190.0.50.35:3128\"},{\"ip_port\":\"200.84.149.117:8080\"},{\"ip_port\":\"54.148.103.250:80\"},{\"ip_port\":\"190.207.95.141:8080\"},{\"ip_port\":\"110.77.214.244:8080\"},{\"ip_port\":\"120.194.237.95:80\"},{\"ip_port\":\"106.81.43.108:8118\"},{\"ip_port\":\"41.205.6.88:8080\"},{\"ip_port\":\"213.136.183.98:8080\"},{\"ip_port\":\"117.166.110.79:8123\"},{\"ip_port\":\"36.80.5.240:8080\"},{\"ip_port\":\"117.171.253.168:8123\"},{\"ip_port\":\"117.175.59.35:8123\"},{\"ip_port\":\"183.136.135.153:8080\"},{\"ip_port\":\"183.220.158.175:8123\"},{\"ip_port\":\"117.168.159.84:8123\"}]";
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
        TranslateHelper translateHelper=new TranslateHelper();
//        do{
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//            String s = br.readLine();
//            translateHelper.translate(s);
//        }while (true);
        translateHelper.translate("hello,everyone");
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
