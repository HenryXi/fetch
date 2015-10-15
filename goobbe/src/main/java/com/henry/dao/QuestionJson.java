package com.henry.dao;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * this object is for saving question in db
 */
@JsonIgnoreProperties({"id","url","title4url"})
public class QuestionJson extends Question {
    public QuestionJson(){
        super();
    }
    public QuestionJson(String title ,String content,int url){
        super(title,content,url);
    }

}

