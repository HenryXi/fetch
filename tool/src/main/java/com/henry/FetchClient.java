package com.henry;

import com.henry.threads.Fetcher;
import com.henry.util.Config;
import com.henry.util.JDBCHelper;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * this client start fetch content from SO
 * 1.get every url in every page
 * 2.get content by url
 */
public class FetchClient {
    public static void main(String[] args) {
        Config.getInstance("fetch.properties");
        JDBCHelper.createPostgresqlTemplate("fetcher",
                Config.getString("database.url"),
                Config.getString("database.username"),
                Config.getString("database.pwd"),
                Config.getInt("database.initActive"),
                Config.getInt("database.maxActive"));
        List<Fetcher> runningFetcher = new ArrayList<>();
        List<Fetcher> waitingFetcher=new ArrayList<>();
        int index = Config.getInt("total.page");
        do {
            if (runningFetcher.size() == Config.getInt("fetcher.number")) {
                int runningThread=0;
                int usingProxy=0;
                int usableProxy=0;
                for (Fetcher fetcher : runningFetcher) {
                    if (fetcher.getState()== Thread.State.WAITING) {
                        waitingFetcher.add(fetcher);
                    }
                    if(fetcher.getState()== Thread.State.RUNNABLE){
                        runningThread++;
                    }
                    if(fetcher.getCurrentProxy()!= Proxy.NO_PROXY){
                        usingProxy++;
                    }
                    if(fetcher.getCurrentProxy()!=null &&
                            fetcher.getCurrentProxy().toString().contains("1080")){
                        usableProxy++;
                    }
                }
                runningFetcher.removeAll(waitingFetcher);
                System.out.println(runningThread+" Running, " + index +" pages left, "+usingProxy+" using proxy("
                        +usableProxy+" usableProxy)");
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                if(waitingFetcher.size()+runningFetcher.size()<Config.getInt("fetcher.number")){
                    Fetcher fetcher=new Fetcher(index);
                    fetcher.start();
                    runningFetcher.add(fetcher);
                    index--;
                }else{
                    for(Fetcher fetcher:waitingFetcher){
                        fetcher.setPageNumber(index);
                        runningFetcher.add(fetcher);
                        index--;
                    }
                    waitingFetcher.removeAll(runningFetcher);
                }
            }
            if(index==0){
                break;
            }
        } while (true);
    }
}
