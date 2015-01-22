package cn.edu.hfut.dmic.webcollector;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by henxii on 1/8/15.
 */
public class GetPage {
    JdbcTemplate jdbcTemplate = null;
    public GetPage(){
        try {
            jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
                    "jdbc:postgresql://localhost:5432/page",
                    "postgres", "postgres", 5, 30);

            /*创建数据表*/
            jdbcTemplate.execute("CREATE TABLE tb_content\n" +
                    "(\n" +
                    "  id SERIAL,\n" +
                    "  title character varying(1000),\n" +
                    "  url character varying(1000),\n" +
                    "  content text,\n" +
                    "  CONSTRAINT tb_content_pkey PRIMARY KEY (id)\n" +
                    ")");
            System.out.println("成功创建数据表 tb_content");
        } catch (Exception ex) {
            jdbcTemplate = null;
            System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }
    }
    public static void main(String args[]) {
        GetPage getPage=new GetPage();
        getPage.getAllPage();
    }

    private void saveInDB(Document doc){
        int updates=jdbcTemplate.update("insert into tb_content (title,url,content) values (?,?,?)",
                doc.title(), doc.baseUri(), doc.html());
        if(updates==1){
            System.out.println(doc.title()+" ---> insert successful");
        }
    }

    private void getAllPage(){
        for (long i = 1; i < 30000000; i++) {
            try {
                Document doc = Jsoup.connect("http://stackoverflow.com/questions/" + i).get();
                saveInDB(doc);
                System.out.println("http://stackoverflow.com/questions/" + i + " ----> " + doc.title());
            } catch (Exception e) {
                continue;
            }
        }
    }
}
