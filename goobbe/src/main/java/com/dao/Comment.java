package com.dao;

/**
 * Created by henxii on 2/7/15.
 */
public class Comment{
    public Comment(){}
    public Comment(String content){
        this.content=content;
    }
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
