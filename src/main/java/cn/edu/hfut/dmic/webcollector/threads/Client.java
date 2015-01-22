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
        JDBCHelper.createMysqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        List<GetAllPager> getAllPagers = new ArrayList<GetAllPager>();
        long step = 100l;
        long index = 20l;
        do {
            try {
                if (getAllPagers.size() == threadNumber) {
                    int waitingThread=0;
                    int newThread=0;
                    int runningThread=0;
                    int blockThread=0;
                    int timeWaitingThread=0;
                    int terminated=0;
                    List<GetAllPager> deadGap = new ArrayList<GetAllPager>();
                    for (GetAllPager getAllPager : getAllPagers) {
                        if (!getAllPager.isAlive()) {
                            deadGap.add(getAllPager);
                        }
                        switch (getAllPager.getState()){
                            case WAITING:
                                waitingThread++;
                                break;
                            case NEW:
                                newThread++;
                                break;
                            case RUNNABLE:
                                runningThread++;
                                break;
                            case BLOCKED:
                                blockThread++;
                                break;
                            case TERMINATED:
                                terminated++;
                                break;
                            case TIMED_WAITING:
                                timeWaitingThread++;
                                break;
                        }
                    }
                    getAllPagers.removeAll(deadGap);
                    System.out.println(waitingThread+" Waiting, "+runningThread+" Running,"+blockThread+" Block,"+timeWaitingThread+
                            " TimeWaiting. ");
                    if((runningThread!=0 && waitingThread>0)||waitingThread==threadNumber){
                        for (GetAllPager getAllPager : getAllPagers){
                            synchronized (getAllPager){
                                getAllPager.notify();
                                System.out.println("notify "+getAllPager.startNumber);
                                Thread.sleep(1000);
                            }
                        }
                    }
                    Thread.sleep(1000);
                } else {
                    GetAllPager getAllPager = new GetAllPager(1 + (index - 1) * step, index * step);
                    getAllPagers.add(getAllPager);
                    getAllPager.start();
                    Thread.sleep(random.nextInt(1000*5));
                    index++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (index * step >= totalNumber) {
                break;
            }
        } while (true);
    }
}
