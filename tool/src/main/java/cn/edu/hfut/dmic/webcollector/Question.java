package cn.edu.hfut.dmic.webcollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/4/15.
 */
public class Question {
    public Question(String title,String content){
        this.t=title;
        this.c=content;
    }

    /**
     * title
     */
    private String t;
    /**
     * content
     */
    private String c;
    /**
     * comments
     */
    private List<Comment> cs=new ArrayList<Comment>();
    /**
     * answers
     */
    private List<Answer> as=new ArrayList<Answer>();

    public String getTitle() {
        return t;
    }

    public void setTitle(String title) {
        this.t = title;
    }

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

    public List<Answer> getAnswers() {
        return as;
    }

    public void setAnswers(List<Answer> answers) {
        this.as = answers;
    }
}

