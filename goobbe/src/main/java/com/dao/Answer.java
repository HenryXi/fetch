package com.dao;

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
}
