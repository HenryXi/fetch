package com.controller;

import com.dao.Question;
import com.dao.RelatedQuestion;
import com.exception.GoobbeException;
import com.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
       return loadIndex("1",modelMap);
    }

    @RequestMapping(value = "/questions/{id}/{title4url}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id,@PathVariable("title4url") String title4url, ModelMap modelMap){
        try{
            Question question= questionService.getQuestionById(Integer.valueOf(id));
            modelMap.put("question", question);
            List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
            modelMap.put("relatedQuestions",relatedQuestions);
            return "content";
        }catch (Exception e){
           e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/questions/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap){
        return loadContent(id,"luck",modelMap);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String loadIndex(@RequestParam("page") String page, ModelMap modelMap){
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
            return "index";
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/question/{url}/{title4url}",method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, @PathVariable("title4url") String title4url, ModelMap modelMap){
        try{
            Question question= questionService.getQuestionByUrl(Integer.valueOf(url));
            modelMap.put("question", question);
            List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
            modelMap.put("relatedQuestions",relatedQuestions);
            return "content";
        }catch (Exception e){
            e.printStackTrace();
        }
        throw new GoobbeException();
    }

    @RequestMapping(value = "/question/{url}", method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, ModelMap modelMap){
        return showSearchResult(url,"luck",modelMap);
    }

    @RequestMapping(value = "/getRelated", method = RequestMethod.GET)
    @ResponseBody
    public List<RelatedQuestion> getRelatedQuestion(@RequestParam("title") String title){
        return searchService.getLocalSearchResult(title);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody String search(@RequestParam("q") String q, ModelMap modelMap, HttpServletResponse response) {
        if("".equals(q)){
            return "";
        }
        try {
            return questionService.getQuestionsByKeyword(q);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GoobbeException();
        }
    }
    @ResponseBody
    @RequestMapping(value = "/translate", method = RequestMethod.GET)
    public String  translate(@RequestParam("t") String t,ModelMap modelMap, HttpServletRequest request){
        return "";
    }
}
