package com.henry.search.model;

import java.util.ArrayList;
import java.util.List;

public class Question {

    public Question(int id, String title, String content){
        this.id=id;
        this.title=title;
        this.content=content;
    }

    private int id;
    private String title;
    private String content;
    private List<Comment> comments = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

