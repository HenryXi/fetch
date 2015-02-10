package com.service;

import com.dao.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 2/10/15.
 */
@Service
public class QuestionService {
    public Question getQuestionById(String index){
        return new Question("","");
    }

    public List<Question> getQuestionsForIndex(){
        List<Question> questions= new ArrayList<>();
        return questions;
    }
}
