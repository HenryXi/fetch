package cn.edu.hfut.dmic.webcollector.threads;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

/**
 * Created by henxii on 1/14/15.
 */
public class Handle503Pages extends Thread{
    private boolean handle503Urls=true;
    JdbcTemplate jdbcTemplate = null;
    public Handle503Pages(List<String> urls){
        try {
            jdbcTemplate = JDBCHelper.createPostgresqlTemplate("mysql1",
                    "jdbc:postgresql://localhost:5432/page",
                    "postgres", "postgres", 5, 30);
            System.out.println("connect db successful!");
        } catch (Exception ex) {
            jdbcTemplate = null;
            System.out.println("connect db fail!");
        }
    }


    private void saveInDBFor503(String url){
        int updates=jdbcTemplate.update("INSERT INTO tb_503content(url) VALUES (?)",url);
        if(updates==1){
            System.out.println("503 url --> "+url+"insert successful!");
        }
    }

    public void run(){
        if(handle503Urls){
            List rows=jdbcTemplate.queryForList("select * from tb_503content");
            Iterator it = rows.iterator();
            while(it.hasNext()) {
                Map urlMap = (Map) it.next();
//                Document doc = Jsoup.connect((String)urlMap.get("url")).timeout(10*1000).get();
//                saveInDB(doc);
//                System.out.println((String)urlMap.get("url") + " ----> " + doc.title());
            }
        }
        try{

        }catch (Exception e){
            handle503Urls=true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        List<String> urls=new ArrayList<String>();
        urls.add("http://stackoverflow.com/questions/167617/where-do-you-get-your-application-sounds-from/167648#167648");
        Handle503Pages handle503Pages=new Handle503Pages(urls);
        handle503Pages.run();
    }
}
