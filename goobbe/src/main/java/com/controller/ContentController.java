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
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String index(Model model) {
//        return "content";
//    }
//
//    @RequestMapping(value = "/springmvc/helloworld", method = RequestMethod.GET)
//    public String helloworld(Model model) {
//        model.addAttribute("msg", "springmvc helloworld例子演示成功啦...");
//        return "helloworld";
//    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loadIndex(ModelMap modelMap){
        List<Question> list = questionService.getQuestionsForIndex();
        modelMap.put("list", list);
        return "index";
    }

    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public String loadContent(@RequestParam("id") String id, ModelMap modelMap){
        Question question= questionService.getQuestionById(id);
        modelMap.put("question", question);
        return "content";
    }

//    @RequestMapping(params = "method=save")
//    public String save(HttpServletRequest request, ModelMap modelMap){
//
//    }
}
