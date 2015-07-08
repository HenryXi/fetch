package cn.edu.hfut.dmic.webcollector.ad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 * Created by henry on 2015/6/26.
 */
public class GenerateAdKeywordAndUrl {
    private JdbcTemplate jdbcTemplate;
    public GenerateAdKeywordAndUrl(){
        jdbcTemplate=JDBCHelper.createSQLiteTemplate("SQLite","jdbc:sqlite:"+System.getProperty("user.home")+"/url.db",
                null, null, 5, 30);
//        jdbcTemplate=JDBCHelper.createPostgresqlTemplate("SQLite","jdbc:postgresql://localhost:15432/page",
//                "kontek", "Passw0rd", 5, 30);
        jdbcTemplate.execute("DROP TABLE IF EXISTS url;");
        jdbcTemplate.execute("create TABLE url(id varchar ,url varchar,title varchar,description varchar);");
//        try {
//            jdbcTemplate.getDataSource().getConnection().setAutoCommit(false);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
    public void generate() {
        try {
            File inputFile = new File(this.getClass().getResource("/marketplace_feed_v2.xml").toURI());
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputFile);

            System.out.println("Root element :"
                    + document.getRootElement().getName());
            List<Node> nodes = document.selectNodes("/Catalog/Category/Site");
            String adId;
            String adUrl;
            String adTitle;
            String adDescription;
            StringBuilder sb=new StringBuilder("INSERT into url (id,url,title,description)VALUES");
            List<String> params=new ArrayList<>();
            for(Node node:nodes){
                adId=node.selectSingleNode("Id").getText();
                System.out.println("Id -> " + adId);
                adUrl="http://goobbe."+adId+".hop.clickbank.net";
                adTitle=node.selectSingleNode("Title").getText();
                adDescription=node.selectSingleNode("Description").getText();
                sb.append("('"+adId+"','"+adUrl+"','"+adTitle+"','"+adDescription+"'),");
//                params.add()
//                jdbcTemplate.update("INSERT into url (id,url,title,description)VALUES (?,?,?,?)",
//                        adId,adUrl,adTitle,adDescription);
            }

            sb.deleteCharAt(sb.lastIndexOf(","));
            jdbcTemplate.queryForList("");
//            Connection conn = null;
//            PreparedStatement stmt  = null;
//            try {
//                conn=jdbcTemplate.getDataSource().getConnection();
//                for (Node node : nodes) {
//                    System.out.println("Id -> " + node.selectSingleNode("Id").getText());
//                    stmt=conn.prepareStatement("INSERT into url (id,url)VALUES ('" + node.selectSingleNode("Id").getText() + "','henry');");
//                    stmt.executeUpdate();
//                }
//                conn.commit();
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        } catch (DocumentException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
