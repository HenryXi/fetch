package com.henry.search.controller;

import com.henry.search.model.Question;
import com.henry.search.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by yong on 2015/11/17.
 */
@Controller
public class SearchController {
    private Logger dataLogger = LoggerFactory.getLogger("dataLogger");
    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/s")
    public String page(@RequestParam(value = "q") String keyword, ModelMap modelMap,
                       HttpServletResponse response) {
        List<Question> resultQuestions = searchService.getRelatedQuestions(keyword);
        modelMap.addAttribute("questions", resultQuestions);
        modelMap.addAttribute("keyword", keyword);
        return "result";
    }

    @RequestMapping(value = "/g/{questionId}")
    public String redirect(HttpServletRequest request, ModelMap modelMap, @PathVariable("questionId") String questionId,
                           @RequestParam("kw") String keyword) {
        StringBuilder sb = new StringBuilder(request.getSession().getId().substring(24));
        sb.append(" ").append(request.getRemoteAddr()).append(" ").append(keyword).append(" ").append(questionId);
        dataLogger.info(sb.toString());
        return "redirect:http://stackoverflow.com/questions/" + questionId;
    }
}
