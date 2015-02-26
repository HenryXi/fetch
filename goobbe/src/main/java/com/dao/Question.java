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

    public String getTitle4url() {
        return url.replaceAll("http://stackoverflow.com/questions/\\d{1,8}/","");
    }

    public void setTitle4url(String title4url) {
        this.title4url = title4url;
    }

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

    public String getTitle() {
        return t;
    }

    public void setTitle(String title) {
        this.t = title;
    }

    public String getContent() {
        return c;
    }

    public void setContent(String content) {
        this.c = content;
    }

    public List<Comment> getComments() {
        return cs;
    }

    public void setComments(List<Comment> comments) {
        this.cs = comments;
    }

    public List<Answer> getAnswers() {
        return as;
    }

    public void setAnswers(List<Answer> answers) {
        this.as = answers;
    }
}

