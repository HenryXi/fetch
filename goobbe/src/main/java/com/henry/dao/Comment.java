package com.henry.dao;

/**
 * Created by henxii on 2/7/15.
 */
public class Comment{
    public Comment(){}
    public Comment(String content){
        this.c=content;
    }
    private String c;

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }
}
