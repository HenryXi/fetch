package com.controller;

import com.dao.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.service.QuestionService;

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
        List<Question> list = questionService.getQuestionsForIndex();
        modelMap.put("question", list.get(0));
        return "content";
    }

    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public String loadContent(@RequestParam("id") String id, ModelMap modelMap){
        String question= questionService.getQuestionById(id);
        modelMap.put("question", question);
        return "content";
    }

//    @RequestMapping(params = "method=save")
//    public String save(HttpServletRequest request, ModelMap modelMap){
//
//    }
}
