package cn.edu.hfut.dmic.webcollector.threads;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by henxii on 3/11/15.
 */
public class RemoveShortTitle {
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper=new ObjectMapper();
    public RemoveShortTitle(){
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        jdbcTemplate = JDBCHelper.getJdbcTemplate("po");
    }

    public void remove(){
        long startNumber=90000000000l;
        int total=0;
        stop:
        do{
            List<Q> questions = this.jdbcTemplate.query(
                    "select * from tb_content where id >=? and content is not null order by id limit 15",
                    new Object[]{startNumber},
                    new RowMapper<Q>() {
                        public Q mapRow(ResultSet rs, int rowNum) throws SQLException {
                            try {
                                return new Q(objectMapper.readTree(rs.getString("content")).get("title").getValueAsText(),rs.getString("url"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
            if(questions.size()==0){
                break ;
            }
            for(Q q:questions){
                String correctTitle=q.getUrl().replace("http://stackoverflow.com/questions/","").replaceAll("\\d{1,8}/", "");
                String myTitle=handleTitle(q.getTitle());
                if(!correctTitle.contains(myTitle) && !myTitle.contains(correctTitle)){
                    total++;
//                    System.out.println("startNumber->"+startNumber);
//                    System.out.println("correct  --> "+correctTitle);
//                    System.out.println("my title --> "+myTitle);
//                    System.out.println("original --> "+q.getTitle());
                    System.out.println();
                   // break stop;
                }
            }
            startNumber+=15;
        }while (true);

    }

    private String handleTitle(String title){
        title=title.replace("[duplicate]","");
        title=title.replace("[closed]","");

        title=title.toLowerCase();
        title=title.replace("c# ", "c sharp ");
//        title=title.replaceAll("\\.|,|’|-|/|_|…|\\\\|="," ");
//        title=title.replaceAll("&.{1,4};|[^0-9a-zA-Z\\s]","").trim();
//        title=title.replaceAll("\\s+","-");
        try {
            title= StringEscapeUtils.unescapeHtml4(title).replaceAll("[^0-9a-zA-Z\\\\s]"," ").trim();
            title = URLEncoder.encode(title, "UTF-8");
            title= title.replaceAll("%.{1,2}", "").replaceAll("[^0-9a-zA-Z]","+");
            title=title.replaceAll("\\++","-");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(title.length()>80){
            title=title.substring(0,80);
            //title=title.substring(0,title.lastIndexOf(" "));
        }
        return title;
    }





    public static void main(String[] args) {
        RemoveShortTitle removeShortTitle=new RemoveShortTitle();
        removeShortTitle.remove();

    }
}
class Q{
    public Q(){}
    public Q(String title,String url){
        this.title=title;
        this.url=url;
    }
    private String title;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
