package com.henry;

import com.henry.threads.Fetcher;
import com.henry.threads.GetAllPager;
import com.henry.util.Config;
import com.henry.util.JDBCHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * this client start fetch content from SO
 * 1.get every url in every page
 * 2.get content by url
 */
public class FetchClient {
    public FetchClient(){
        Config.getInstance();
    }
    public static void main(String[] args) {

        JDBCHelper.createPostgresqlTemplate("po",
                Config.getString("database.url"),
                Config.getString("database.username"),
                Config.getString("database.pwd"),
                Config.getInt("database.initActive"),
                Config.getInt("database.maxActive"));
        List<Fetcher> runningFetcher = new ArrayList<>();
        List<Fetcher> deadFetcher=new ArrayList<>();
        long step = 100l;
        long startIndex=3907l;
        long index = 3907l;
        long startTime= Calendar.getInstance().getTime().getTime();
        do {
            if (runningFetcher.size() == 3) {
                int waitingThread=0;
                int runningThread=0;
                int blockThread=0;
                int timeWaitingThread=0;
                deadFetcher.clear();
                for (GetAllPager getAllPager : runningFetcher) {
                    if (!getAllPager.isAlive()) {
                        deadFetcher.add(getAllPager);
                    }
                    switch (getAllPager.getState()){
                        case WAITING:
                            waitingThread++;
                            break;
                        case RUNNABLE:
                            runningThread++;
                            break;
                        case BLOCKED:
                            blockThread++;
                            break;

                        case TIMED_WAITING:
                            timeWaitingThread++;
                            break;
                    }
                }
                runningFetcher.removeAll(deadFetcher);
                long endTime=Calendar.getInstance().getTime().getTime();
                int pagePerSecond= (int) ((index-startIndex-1)*100/(endTime-startTime)/1000);
                System.out.println(waitingThread+" Waiting, "+runningThread+" Running,"+blockThread+" Block,"+timeWaitingThread+
                        " TimeWaiting. Current index "+index+". Current speed: "+pagePerSecond+" pages per second.");
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                GetAllPager getAllPager = new GetAllPager(1 + (index - 1) * step, index * step);
                runningFetcher.add(getAllPager);
                getAllPager.start();
                index++;
            }
            if (index * step >= 10) {
                break;
            }
        } while (true);
    }
}
