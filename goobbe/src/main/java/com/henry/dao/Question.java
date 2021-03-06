package com.henry.dao;

import com.henry.util.GoobbeUtil;

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
        this.title4url= GoobbeUtil.generateShortTitle(t);
    }
    public Question(String id,String title,String content){
        this.id=id;
        this.t=title;
        this.title4url= GoobbeUtil.generateShortTitle(t);
        this.c=content;
    }
    public Question(String title,String content){
        this.t=title;
        this.title4url= GoobbeUtil.generateShortTitle(t);
        this.c=content;
    }
    public Question(String title ,String content,int url){
        this.t=title;
        this.title4url= GoobbeUtil.generateShortTitle(t);
        this.c=content;
        this.url=url;
    }
    private String id;
    private int url;
    private String t;
    private String title4url;
    private String c;
    private List<String> ts=new ArrayList<>();
    private List<Comment> cs=new ArrayList<Comment>();
    private List<Answer> as=new ArrayList<Answer>();

    public List<String> getTs() {
        return ts;
    }

    public void setTs(List<String> ts) {
        this.ts = ts;
    }

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
    }

    public String getTitle4url() {
        return this.title4url;
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

