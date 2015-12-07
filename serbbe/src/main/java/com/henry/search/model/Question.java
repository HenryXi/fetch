package com.henry.search.model;

import com.henry.search.util.StringUtil;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public Question(){}
    public Question(int id, String title, String content){
        this.id=id;
        this.title=title;
        this.content=content;
    }
    private int id;
    @JsonProperty("t")
    private String title;
    @JsonProperty("c")
    private String content;
    @JsonProperty("cs")
    private List<Comment> comments = new ArrayList<>();
    @JsonProperty("as")
    private List<Answer> answers = new ArrayList<>();
    @JsonProperty("ts")
    private List<String> tags = new ArrayList<>();

    public String getAllPlaintContentForIndex(){
        StringBuilder sb=new StringBuilder(StringUtil.getPlainTextInHTML(content));
        for(Comment comment:comments){
            sb.append(StringUtil.getPlainTextInHTML(comment.getContent()));
        }
        for(Answer answer:answers){
            sb.append(StringUtil.getPlainTextInHTML(answer.getContent()));
            for(Comment comment:answer.getComments()){
                sb.append(StringUtil.getPlainTextInHTML(comment.getContent()));
            }
        }
        return sb.toString();
    }

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

