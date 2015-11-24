package com.henry.search.service;

import com.henry.search.model.Question;
import com.henry.util.Config;
import com.henry.util.JDBCHelper;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yong on 2015/11/17.
 */
@Service
public class IndexService {
    private Logger logger = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private int INDEX_TITLES_EACH_LOOP = 10000;
    private Path indexFolderBak;
    private Path indexFolder;

    private IndexService() {
        indexFolderBak = Paths.get(System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "index_bak");
        indexFolder = Paths.get(System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "index");
    }

    public static void main(String[] args) {
        Config.getInstance("serbbeConfig.properties");
        JDBCHelper.createPostgresqlTemplate("fetcher",
                Config.getString("database.url"),
                Config.getString("database.username"),
                Config.getString("database.pwd"),
                Config.getInt("database.initActive"),
                Config.getInt("database.maxActive"));
        IndexService indexService = new IndexService();
        indexService.setJdbcTemplate(JDBCHelper.getJdbcTemplate("fetcher"));
        indexService.createIndex();
    }

    public void createIndex() {
        try {
            Directory dir = FSDirectory.open(getIndexPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, iwc);
//            int totalNum = jdbcTemplate.queryForObject("select max(id) from tb_content;", Integer.class) / INDEX_TITLES_EACH_LOOP + 1;
            int totalNum = 10;
            for (int i = 1; i <= totalNum; i++) {
                indexDocs(writer, getQuestionsForBuildingIndex(i * INDEX_TITLES_EACH_LOOP));
                logger.info("indexing... total group: [" + (totalNum + 1) + "], [" + INDEX_TITLES_EACH_LOOP + "] items per group, current group: [" + i + "]");
            }
            writer.close();
            FileUtils.deleteDirectory(indexFolder.toFile());
            FileUtils.moveDirectory(indexFolderBak.toFile(), indexFolder.toFile());
            logger.info("finish indexing!");
        } catch (IOException e) {
            logger.error("error occur when indexing!", e);
        }
    }

    private Path getIndexPath() {
        try {
            logger.info("getting index path...");
            logger.info("create index_bak path for indexing.");
            Files.createDirectories(indexFolderBak);
            return indexFolderBak;
        } catch (IOException e) {
            logger.error("create index error!", e);
        }
        return null;
    }

    private void indexDocs(IndexWriter writer, List<Question> questions)
            throws IOException {
        for (Question question : questions) {
            Document doc = new Document();
            doc.add(new StoredField("id", question.getId()));
            doc.add(new TextField("title", question.getTitle(), Field.Store.YES));
            doc.add(new TextField("content", Jsoup.parse(question.getContent()).text(), Field.Store.YES));
            writer.addDocument(doc);
        }
    }

    private List<Question> getQuestionsForBuildingIndex(Integer startNum) {
        List<Question> questions = this.jdbcTemplate.query(
                "select content ->>'t' as title,id,content ->>'c' as content" +
                        " from tb_content where id between ? and ? and content is not null",
                new Object[]{startNum - 9999, startNum},
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return new Question(rs.getInt("id"), rs.getString("title"), rs.getString("content"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
