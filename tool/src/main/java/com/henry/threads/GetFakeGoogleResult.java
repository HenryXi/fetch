package com.henry.threads;



import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by henxii on 3/5/15.
 */
public class GetFakeGoogleResult {
    public static void main(String[] args) {
        String url="http://www.upol.cn/rms/model/course.php?cnum=";
        ObjectMapper objectMapper=new ObjectMapper();

        for(int i=1;i<1000;i++){
            try {
                Document doc=Jsoup.connect(url+i).get();
                if(doc.title().equals("") || doc.title().equals("服务器安全狗防护验证页面")){
                    continue;
                }
                System.out.println("Tutorial title: "+doc.title()+" Tutorial url: "+url+i);
                String json=doc.html().substring(doc.html().indexOf("var zNodes =") + 15, doc.html().indexOf("var rMenu;") - 4);
                Tutorial[] tutorials=objectMapper.readValue(json, Tutorial[].class);
                for(Tutorial tutorial:tutorials){
                    if(!tutorial.getLenovo_url().equals("")){
                        System.out.println("http://sdzx.cdn.lenovows.com/whaty/"+i+"/"+tutorial.getLenovo_url());
                    }
                }
            } catch (IOException e) {
                continue;
            }
        }

    }
}
class Tutorial{
    private String name;
    private String lenovo_url;
    private String id;
    private String pid;
    private String type;
    private String cid;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLenovo_url() {
        return lenovo_url;
    }

    public void setLenovo_url(String lenovo_url) {
        this.lenovo_url = lenovo_url;
    }
}
