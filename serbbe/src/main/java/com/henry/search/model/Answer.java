package com.henry.search.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yong on 2015/11/24.
 */
public class Answer {
    @JsonProperty("c")
    private String content;
    @JsonProperty("cs")
    private List<Comment> comments=new ArrayList<Comment>();

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
    protected String getPlainContentForIndexing(){
        return Jsoup.parse(this.content).text();
    }
}
