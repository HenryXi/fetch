package com.dao;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
/**
 * this object is for getting question from db
 */
//@JsonIgnoreProperties({"id","url","title4url"})
public class Question {
    public Question(){}
    public Question(int id,String title){
        this.id=String.valueOf(id);
        this.t=title;
    }
    public Question(String title,String content){
        this.t=title;
        this.c=content;
    }
    public Question(String title ,String content,int url){
        this.t=title;
        this.title4url=handleTitle(t);
        this.c=content;
        this.url=url;
    }
    private String id;
    private int url;
    private String t;
    private String title4url;
    private String c;
    private List<Comment> cs=new ArrayList<Comment>();
    private List<Answer> as=new ArrayList<Answer>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
        this.title4url=handleTitle(t);
    }

    public String getTitle4url() {
        return this.title4url;
    }
    private String handleTitle(String title){
        title=title.replace("[duplicate]","");
        title=title.replace("[closed]","");
        title=title.replace("<b>","").replace("</b>","");
        title=title.toLowerCase();
        title=title.replace("c# ", "c sharp ");
        title=title.replace(" c#", " c sharp");
        try {
            title= StringEscapeUtils.unescapeHtml4(title).replaceAll("[^0-9a-zA-Z\\\\s]"," ").trim();
            title = URLEncoder.encode(title, "UTF-8");
            title= title.replaceAll("%.{1,2}", "").replaceAll("[^0-9a-zA-Z]", "+");
            title=title.replaceAll("\\++", "-");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(title.length()>80){
            title=title.substring(0,80);
        }
        if(title.length()==title.lastIndexOf("-")){
            title=title.substring(0,title.lastIndexOf("-"));
        }
        return title;
    }
    public void setTitle4url(String title4url) {
        this.title4url = title4url;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<Comment> getCs() {
        return cs;
    }

    public void setCs(List<Comment> cs) {
        this.cs = cs;
    }

    public List<Answer> getAs() {
        return as;
    }

    public void setAs(List<Answer> as) {
        this.as = as;
    }
    @Override
    public boolean equals(Object question){
        if(question instanceof Question){
            Question anotherQuestion=(Question)question;
            if(this.id.equals(anotherQuestion.getId())){
                return true;
            }
        }
        return false;
    }
}

