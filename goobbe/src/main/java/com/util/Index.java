package com.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import com.dao.Question;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class Index {
    private JdbcTemplate jdbcTemplate;
    private Index() {
        jdbcTemplate=JDBCHelper.createMysqlTemplate("po",
                "jdbc:postgresql://123.57.136.60:5432/goobbe",
                "yong", "xixiaoyong123", 80, 120);
    }

    /** Index all text files under a directory. */
    public static void main(String[] args) {
        Index index =new Index();
        String indexPath = "D:\\index";
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocs(writer, index.getQuestionsForIndex(1));

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
                "select * from tb_content where content is not null and id<1500",
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
        ObjectMapper objectMapper=new ObjectMapper();
        Question question = objectMapper.readValue(rs.getString("content"), Question.class);
        question.setUrl(rs.getInt("url"));
        String summery= Jsoup.parse(question.getC().replace("&lt", "<").replace("&gt", ">")).text();
        question.setC(summery);
        question.setId(rs.getString("id"));
        return question;
    }
}
