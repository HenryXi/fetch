package com.henry.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/7/15.
 */
public class Answer{
    public Answer(){}
    public Answer(String content){
        this.c=content;
    }
    private String c;
    private List<Comment> cs=new ArrayList<Comment>();

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
}
