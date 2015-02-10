package com.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/7/15.
 */
public class Answer{
    public Answer(String content){
        this.content=content;
    }
    private String content;
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
}
