package com.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/4/15.
 */
public class Question {
    public Question(){}
    public Question(String title,String content){
        this.title=title;
        this.content=content;
    }
    private String id;
    private String url;
    private String title;
    private String title4url;
    private String content;
    private List<Comment> comments=new ArrayList<Comment>();
    private List<Answer> answers=new ArrayList<Answer>();

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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}

