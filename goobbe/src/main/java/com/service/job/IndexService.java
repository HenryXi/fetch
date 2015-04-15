package com.service.job;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import com.dao.Question;
import com.exception.GoobbeException;
import com.util.GoobbeLogger;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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

@Service
public class IndexService extends GoobbeLogger {
    private JdbcTemplate jdbcTemplate;
    private int INDEX_TITLES_EACH_LOOP=10000;
    private Path indexFolderBak;
    private Path indexFolder;
    private IndexService() {
        jdbcTemplate=JDBCHelper.createMysqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
        indexFolderBak= Paths.get(System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "index_bak");
        indexFolder= Paths.get(System.getProperty("user.home") + FileSystems.getDefault().getSeparator()+"index");
    }

    public static void main(String[] args) {
        IndexService indexService =new IndexService();
        indexService.createIndex();
    }

    public void createIndex(){
        try {
            Directory dir = FSDirectory.open(getIndexPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, iwc);
            int totalNum=jdbcTemplate.queryForInt("select count from tcounter where table_name='tb_content';")/INDEX_TITLES_EACH_LOOP+1;
            for(int i=1;i<totalNum+1;i++){
                indexDocs(writer, getQuestionsForIndex(i * INDEX_TITLES_EACH_LOOP));
                info("indexing... total group: ["+(totalNum+1)+"], ["+INDEX_TITLES_EACH_LOOP+"] items per group, current group: ["+i+"]");
            }
            writer.close();
            FileUtils.deleteDirectory(indexFolder.toFile());
            FileUtils.moveDirectory(indexFolderBak.toFile(),indexFolder.toFile());
            info("finish indexing!");
        } catch (IOException e) {
            error("error occur when indexing!");
        }
    }

    private Path getIndexPath(){
        try {
            FileUtils.deleteDirectory(indexFolderBak.toFile());
            Files.createDirectories(indexFolderBak);
            return indexFolderBak;
        } catch (IOException e) {
            throw new GoobbeException("create index error!");
        }
    }

    private void indexDocs(IndexWriter writer, List<Question> questions)
            throws IOException {
        for(Question question:questions){
            Document doc = new Document();
            doc.add(new StoredField("id",question.getId()));
            doc.add(new TextField("title",question.getT(), Field.Store.YES));
            writer.addDocument(doc);
        }
    }

    private List<Question> getQuestionsForIndex(Integer startNum) {
        List<Question> questions = this.jdbcTemplate.query(
                "select content ->'t' as title,id from tb_content where id between ? and ? and content is not null",
                new Object[]{startNum-9999,startNum},
                new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return getQuestionByResultSet(rs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
        return questions;
    }

    private Question getQuestionByResultSet(ResultSet rs) throws IOException, SQLException {
        Question question = new Question(rs.getInt("id"),rs.getString("title").replace("\"",""));
        return question;
    }
}
