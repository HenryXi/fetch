package com.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by henry on 2015/4/14.
 */
public class HandleTitle {
    public static String generateShortTitle(String title){
        title=title.replace("[duplicate]","");
        title=title.replace("[closed]","");
        title=title.replace("<b>","").replace("</b>","");
        title=title.toLowerCase();
        title=title.replace("c# ", "c sharp ");
        title=title.replace(" c#", " c sharp");
        try {
            title= StringEscapeUtils.unescapeHtml4(title).replaceAll("[^0-9a-zA-Z\\\\s]"," ").trim();
            title = URLEncoder.encode(title, "UTF-8");
            title= title.replaceAll("%.{1,2}", "").replaceAll("[^0-9a-zA-Z]", "+");
            title=title.replaceAll("\\++", "-");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(title.length()>80){
            title=title.substring(0,80);
        }
        if(title.length()==title.lastIndexOf("-")){
            title=title.substring(0,title.lastIndexOf("-"));
        }
        return title;
    }
}
