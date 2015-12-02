package com.henry.search.controller;

import com.henry.search.model.Question;
import com.henry.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by yong on 2015/11/17.
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/s")
    public String page(@RequestParam(value = "q") String keyWord, ModelMap modelMap) {
        List<Question> resultQuestions = searchService.getRelatedQuestions(keyWord);
        modelMap.addAttribute("questions", resultQuestions);
        modelMap.addAttribute("keyWord", keyWord);
        return "result";
    }
}
