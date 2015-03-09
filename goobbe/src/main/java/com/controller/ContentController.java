package com.controller;

import com.dao.Question;
import com.exception.GoobbeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by henxii on 2/9/15.
 */
@Controller
public class ContentController {
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
       return loadIndex("1",modelMap);
    }

    @RequestMapping(value = "/questions/{id}/{title4url}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id,@PathVariable("title4url") String title4url, ModelMap modelMap){
        try{
            Question question= questionService.getQuestionById(Integer.valueOf(id));
            if(!question.getTitle4url().equals(title4url)){
                return "redirect:/questions/"+id+"/"+question.getTitle4url();
            }
            modelMap.put("question", question);
            return "content";
        }catch (Exception e){
           e.printStackTrace();
        }
        throw new GoobbeException();
    }
//    @RequestMapping(value = "/question/{sUrl}/{{title4url}}", method= RequestMethod.GET)
//    public String loadContentBySearchResult(@PathVariable("sUrl") String sUrl,@PathVariable("title4url") String title4url, ModelMap modelMap){
//        Question question=questionService.getQuestionBysUrl(sUrl);
//        modelMap.put("question", question);
//        return "redirect:/questions/"+question.getId()+"/"+question.getTitle4url();
//    }

    @RequestMapping(value = "/questions/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap){
        return loadContent(id,"luck",modelMap);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String loadIndex(@RequestParam("page") String page, ModelMap modelMap){
        try{
            int totalPage=128780;
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

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("q") String q, ModelMap modelMap) {
        try {
            //todo: use utf-8 or do not let user input other language just use english
            List<Question> questions = questionService.getQuestions(q);
            modelMap.put("questions", questions);
            modelMap.put("totalPage", -1);
            modelMap.put("keyword",q);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException();
    }
}
