package com.dao;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * this object is for saving question in db
 */
@JsonIgnoreProperties({"url","c","cs","as"})
public class RelatedQuestion extends Question {
    public RelatedQuestion(){
        super();
    }
    public RelatedQuestion(String id, String title){
        setId(id);
        setT(title);
    }

    public RelatedQuestion(String id,String title,String summary,String title4url){
        setId(id);
        setT(title);
        setC(summary);
        setTitle4url(title4url);
    }

}

