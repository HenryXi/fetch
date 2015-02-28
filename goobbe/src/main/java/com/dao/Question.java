package com.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/4/15.
 */
public class Question {
    public Question(){}
    public Question(String title,String content){
        this.t=title;
        this.c=content;
    }
    private String id;
    private String url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getTitle4url() {
        return url.replaceAll("\\d{1,8}/","");
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

}

