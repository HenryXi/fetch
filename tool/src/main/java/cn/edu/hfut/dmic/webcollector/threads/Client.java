package cn.edu.hfut.dmic.webcollector.threads;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;

import java.util.*;

/**
 * Created by henxii on 1/9/15.
 */
public class Client {
    private static final long totalNumber = 30000000;
    private static final long startNumber = 1;
    public static final int threadNumber = 3;
    private static Random random=new Random();
    public static void main(String[] args) {
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        List<GetAllPager> getAllPagers = new ArrayList<GetAllPager>();
        List<GetAllPager> deadGap=new ArrayList<GetAllPager>();
        long step = 100l;
        long startIndex=3907l;
        long index = 3907l;
        long startTime=Calendar.getInstance().getTime().getTime();
        do {
            if (getAllPagers.size() == threadNumber) {
                int waitingThread=0;
                int runningThread=0;
                int blockThread=0;
                int timeWaitingThread=0;
                deadGap.clear();
                for (GetAllPager getAllPager : getAllPagers) {
                    if (!getAllPager.isAlive()) {
                        deadGap.add(getAllPager);
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
                getAllPagers.removeAll(deadGap);
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
                getAllPagers.add(getAllPager);
                getAllPager.start();
                index++;
            }
            if (index * step >= totalNumber) {
                break;
            }
        } while (true);
    }
}
