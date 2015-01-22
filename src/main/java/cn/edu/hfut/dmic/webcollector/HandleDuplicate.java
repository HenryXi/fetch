package cn.edu.hfut.dmic.webcollector;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by henxii on 1/19/15.
 */
public class HandleDuplicate {
    private JdbcTemplate jdbcTemplate;
    public HandleDuplicate(){
        jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 5, 30);
    }

    public static void main(String[] args) {
        HandleDuplicate handleDuplicate=new HandleDuplicate();
        handleDuplicate.handle();
    }

    private void handle(){
        Map<String ,String> map=new HashMap<String, String>();
        List<Map<String,Object>> record=jdbcTemplate.queryForList("select title ,url from tb_content");
        Iterator it=record.iterator();
        while(it.hasNext()){
            Map urlMap = (Map) it.next();
            String url =map.get((String)urlMap.get("title"));



//            if(urlMap.get("title")==null){
//                map.put(title,url);
//            }else if(url.length()<((String)urlMap.get("title")).length()){
//                map.put(title,(String)urlMap.get("url"));
//            }
        }
        String a="";
    }
}
