package cn.edu.hfut.dmic.webcollector;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by henxii on 2/7/15.
 */
public class CreateIndex {
    public static void main(String[] args) {
        JDBCHelper.createPostgresqlTemplate("po",
                "jdbc:postgresql://localhost:5432/page",
                "postgres", "postgres", 80, 120);
        JdbcTemplate jdbcTemplate=JDBCHelper.getJdbcTemplate("po");
        long totalNum=jdbcTemplate.queryForObject("select count(*) from tb_content2",Long.class);
        do {

        } while (true);
    }
}
