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

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

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

    public List<Answer> getAs() {
        return as;
    }

    public void setAs(List<Answer> as) {
        this.as = as;
    }
}

