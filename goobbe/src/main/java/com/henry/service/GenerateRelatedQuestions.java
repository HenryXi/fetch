package com.henry.service;


import com.henry.dao.Question;
import com.henry.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class GenerateRelatedQuestions {
    private static final String BEGIN = ".begin";
    private static final String END = ".end";

    @Autowired
    private SearchLocalService searchLocalService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Question> getRelatedQuestions(String siteName, Question question) {
        siteName=siteName.replace("www.", "");
        Config.getInstance("config.properties");
        if (Config.getString(siteName + BEGIN)==null) {
            return searchLocalService.getLocalSearchResult(question);
        }
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startId", Integer.valueOf(question.getId()) + Config.getInt(siteName + BEGIN));
        parameters.addValue("endId", Integer.valueOf(question.getId()) + Config.getInt(siteName + END));
        List<Question> questions = namedParameterJdbcTemplate.query("select id ,content ->> 't' as title,content ->> 'c' as content" +
                        " from tb_content where id BETWEEN :startId AND :endId and content is not null",
                parameters, new RowMapper<Question>() {
                    public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return questionService.getQuestionByResultSet(rs, false, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

        return questions;
    }
}
