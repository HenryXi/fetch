package cn.edu.hfut.dmic.webcollector.threads;

/**
 * Created by yong on 2015/3/4.
 */
public class ClientGoogleSearch {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        GoogleSearch googleSearch=new GoogleSearch();
        String results="[";
        for(int i = 1; i <= 3;i++){
            results=results+","+googleSearch.getResult("programcreek"+"+site:stackoverflow.com",i);
            System.out.println("get one!");
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime/1000);
        results=results+"]";
        System.out.println(results);
    }
}
