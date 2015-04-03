package com.util;

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import com.dao.Question;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class Index {
    private JdbcTemplate jdbcTemplate;
    private Index() {
        jdbcTemplate=JDBCHelper.createMysqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
    }

    public static void main(String[] args) {
        Index index =new Index();
        String indexPath = "D:\\index";
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(dir, iwc);
            for(int i=1;i<816;i++){
                indexDocs(writer, index.getQuestionsForIndex(i*10000));
                System.out.println("current index -> " + i);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }


    static void indexDocs(IndexWriter writer, List<Question> questions)
            throws IOException {
        for(Question question:questions){
            Document doc = new Document();
            doc.add(new StoredField("id",question.getId()));
            doc.add(new TextField("title",question.getT(), Field.Store.YES));
            writer.addDocument(doc);
        }

    }

    public List<Question> getQuestionsForIndex(Integer startNum) {
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
