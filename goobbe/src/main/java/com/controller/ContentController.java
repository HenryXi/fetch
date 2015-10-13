package com.controller;

import com.dao.Question;
import com.dao.RelatedQuestion;
import com.exception.GoobbeInternalErrorException;
import com.exception.GoobbeRsNotFoundException;
import com.service.SearchLocalService;
import com.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Controller
public class ContentController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SearchLocalService searchLocalService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IndexService indexService;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
        List<Question> questions=questionService.getQuestionsForRandomPage();
        if(questions.contains(null)) throw new GoobbeInternalErrorException();
        modelMap.put("questions", questions);
        modelMap.put("currentPage",-1);
        return "index";
    }

    @RequestMapping(value = "/issue/{id}/{title4url}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id,@PathVariable("title4url") String title4url,
                              ModelMap modelMap,Locale locale,HttpServletRequest request){
        Question question= questionService.getQuestionById(Integer.valueOf(id));
        if(question==null||question.getT().equals("")) throw new GoobbeRsNotFoundException();
        modelMap.put("question", question);
        List<RelatedQuestion> relatedQuestions= searchLocalService.getLocalSearchResult(question);
        modelMap.put("relatedQuestions", relatedQuestions);
        modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
        return "content";
    }

    @RequestMapping(value = "/issue/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap,Locale locale,HttpServletRequest request){
        return loadContent(id,"luck",modelMap,locale,request);
    }

    @RequestMapping(value = "/{command}/{password}", method = RequestMethod.GET)
    public String executeCommand(@PathVariable("command") String command,
                                 @PathVariable("password") String password,
                                 ModelMap modelMap){
        if(command.equals("index") && password.equals("makemoney")){
            indexService.createIndex();
        }
        return loadIndex(modelMap);
    }
}
