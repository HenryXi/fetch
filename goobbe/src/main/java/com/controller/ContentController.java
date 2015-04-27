package com.controller;

import com.dao.Question;
import com.dao.RelatedQuestion;
import com.exception.GoobbeException;
import com.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ContentController {
    private final int number_of_questions_per_page=15;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private MessageSource messageSource;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
        List<Question> list=questionService.getRandomQuestions();
        modelMap.put("questions", list);
        modelMap.put("currentPage",-1);
        return "index";
    }

    @RequestMapping(value = "/questions/{id}/{title4url}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id,@PathVariable("title4url") String title4url, ModelMap modelMap,Locale locale){
        try{
            Question question= questionService.getQuestionById(Integer.valueOf(id));
            modelMap.put("question", question);
            List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
            modelMap.put("relatedQuestions",relatedQuestions);
            modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
            return "content";
        }catch (Exception e){
           e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/questions/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap,Locale locale){
        return loadContent(id,"luck",modelMap,locale);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String loadIndex(@RequestParam("page") String page, ModelMap modelMap,Locale locale){
        try{
            int totalPage=1+questionService.getMaxId()/number_of_questions_per_page;
            int pageNum=Integer.valueOf(page);
            if(pageNum<=0){
                pageNum=1;
            }
            if(pageNum>totalPage){
                pageNum=totalPage;
            }
            List<Question> list = questionService.getQuestionsForIndex(pageNum);
            modelMap.put("questions", list);
            modelMap.put("currentPage",pageNum);
            modelMap.put("totalPage",totalPage);
            modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
            return "index";
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/question/{url}/{title4url}",method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, @PathVariable("title4url") String title4url, ModelMap modelMap,Locale locale){
        try{
            Question question= questionService.getQuestionByUrl(Integer.valueOf(url));
            modelMap.put("question", question);
            List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
            modelMap.put("relatedQuestions",relatedQuestions);
            modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
            return "content";
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/question/{url}", method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, ModelMap modelMap,Locale locale){
        return showSearchResult(url,"luck",modelMap,locale);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody String search(@RequestParam("q") String q, ModelMap modelMap, HttpServletResponse response) {
        try {
            return questionService.getQuestionsByKeyword(q);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GoobbeException();
        }
    }
}
