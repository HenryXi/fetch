package cn.edu.hfut.dmic.webcollector.threads;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 1/9/15.
 */
public class ClientAddTag4Question {
    private static final long totalNumber = 8146616;
    public static final int threadNumber = 10;
    public static void main(String[] args) {
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
        List<AddTag4Question> addTag4Questions = new ArrayList<>();
        List<AddTag4Question> deadGap=new ArrayList<>();
        int useProxy=0;
        long step = 100l;
        long index = 59118;
        loseConnection:
        do {
            if (addTag4Questions.size() == threadNumber) {
                int waitingThread=0;
                int runningThread=0;
                int blockThread=0;
                int timeWaitingThread=0;
                deadGap.clear();
                useProxy=0;
                for (AddTag4Question getAllPager : addTag4Questions) {
                    if(getAllPager.isLoseConnection()){
                        System.out.println("Current index -> "+index);
                        break loseConnection;
                    }
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
                    if(!Proxy.NO_PROXY.equals(getAllPager.currentProxy)){
                        useProxy++;
                    }
                }
                addTag4Questions.removeAll(deadGap);
                System.out.println(waitingThread+" Waiting, "+runningThread+" Running,"+blockThread+" Block,"+timeWaitingThread+
                        " TimeWaiting. Current index "+index+", "+useProxy+" use proxy.");
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                AddTag4Question getAllPager = new AddTag4Question(1 + (index - 1) * step, index * step);
                addTag4Questions.add(getAllPager);
                getAllPager.start();
                index++;
            }
            if (index * step >= totalNumber) {
                break;
            }
        } while (true);
    }
}
