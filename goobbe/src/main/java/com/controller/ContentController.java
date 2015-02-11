package com.controller;

import com.dao.Question;
import com.exception.GoobbeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by henxii on 2/9/15.
 */
@Controller
public class ContentController {
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
        try{
            List<Question> list = questionService.getQuestionsForIndex();
            modelMap.put("questions", list);
            return "index";
        }catch (Exception e){
           e.printStackTrace();
        }
        throw new GoobbeException();
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

    @RequestMapping(value = "/questions/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap){
        return loadContent(id,"luck",modelMap);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String loadIndex(@RequestParam("page") String page, ModelMap modelMap){
        List<Question> list = questionService.getQuestionsForIndex();
        //todo Pagination need to be done,but id in db(postgresql) is not continuous!!
        //todo there are many duplicate record in db, need remove them
        return "";
    }
}
