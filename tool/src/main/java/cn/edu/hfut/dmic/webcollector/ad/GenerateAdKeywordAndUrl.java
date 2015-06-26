package cn.edu.hfut.dmic.webcollector.ad;

import java.io.File;
import java.net.URISyntaxException;
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
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by henry on 2015/6/26.
 */
public class GenerateAdKeywordAndUrl {
    private JdbcTemplate jdbcTemplate;
    public GenerateAdKeywordAndUrl(){
//        jdbcTemplate=JDBCHelper.createSQLiteTemplate("SQLite","jdbc:sqlite:"+System.getProperty("user.home")+"url.db",
//                null, null, 5, 30);
        jdbcTemplate=JDBCHelper.createPostgresqlTemplate("SQLite","jdbc:postgresql://localhost:15432/page",
                "kontek", "Passw0rd", 5, 30);
//        jdbcTemplate.execute("DROP TABLE IF EXISTS url;");
//        jdbcTemplate.execute("create TABLE url(id varchar ,url varchar);");
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
            Document document = reader.read( inputFile );

            System.out.println("Root element :"
                    + document.getRootElement().getName());
            List<Element> categoryElements=document.getRootElement().elements();

            List<Node> nodes = document.selectNodes("/Catalog/Category/Site");
            List<String> sqls=new ArrayList<>();
            for(Node node:nodes){
                sqls.add("INSERT into url (id,url)VALUES ('" + node.selectSingleNode("Id").getText() + "','henry');");
            }
            jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
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
