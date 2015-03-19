package com.dao;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

