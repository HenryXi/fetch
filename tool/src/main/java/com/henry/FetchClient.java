package com.henry;

import com.henry.threads.Fetcher;
import com.henry.threads.GetAllPager;
import com.henry.util.Config;
import com.henry.util.JDBCHelper;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * this client start fetch content from SO
 * 1.get every url in every page
 * 2.get content by url
 */
public class FetchClient {
    public static void main(String[] args) {
        Config.getInstance();
        JDBCHelper.createPostgresqlTemplate("fetcher",
                Config.getString("database.url"),
                Config.getString("database.username"),
                Config.getString("database.pwd"),
                Config.getInt("database.initActive"),
                Config.getInt("database.maxActive"));
        List<Fetcher> runningFetcher = new ArrayList<>();
        List<Fetcher> deadFetcher=new ArrayList<>();
        int index = Config.getInt("total.page");
        do {
            if (runningFetcher.size() == Config.getInt("fetcher.number")) {
                int runningThread=0;
                int usingProxy=0;
                deadFetcher.clear();
                for (Fetcher fetcher : runningFetcher) {
                    if (!fetcher.isAlive()) {
                        deadFetcher.add(fetcher);
                    }
                    if(fetcher.getState()== Thread.State.RUNNABLE){
                        runningThread++;
                    }
                    if(fetcher.getCurrentProxy()!= Proxy.NO_PROXY){
                        usingProxy++;
                    }
                }
                runningFetcher.removeAll(deadFetcher);
                System.out.println(runningThread+" Running, " + index +" pages left, "+usingProxy+" using proxy.");
                try {
                    Thread.sleep(1000*10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Fetcher fetcher = new Fetcher(index);
                runningFetcher.add(fetcher);
                fetcher.start();
                index--;
            }
            if(index==0){
                break;
            }
        } while (true);
    }
}
