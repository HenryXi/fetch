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

}

