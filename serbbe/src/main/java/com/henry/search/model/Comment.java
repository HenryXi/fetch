package com.henry.search.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

/**
 * Created by yong on 2015/11/24.
 */
public class Comment {
    @JsonProperty("c")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    protected String getPlainContentForIndexing(){
        return Jsoup.parse(this.content).text();
    }
}
