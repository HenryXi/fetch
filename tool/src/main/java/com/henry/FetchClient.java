package com.henry;

import com.henry.util.Config;

/**
 * this client start fetch content from SO
 * 1.get every url in every page
 * 2.get content by url
 */
public class FetchClient {
    public static void main(String[] args) {
        System.out.println(Config.getInstance().getPropertyByName("database.url"));
    }
}
