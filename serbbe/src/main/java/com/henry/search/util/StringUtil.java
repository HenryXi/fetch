package com.henry.search.util;

import org.jsoup.Jsoup;

/**
 * Created by yong on 2015/12/7.
 */
public class StringUtil {
    public static String getPlainTextInHTML(String html){
        return Jsoup.parse(html).text() + "\n";
    }
}
