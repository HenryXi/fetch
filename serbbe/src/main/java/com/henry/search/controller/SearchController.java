package com.henry.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yong on 2015/11/17.
 */
@Controller
public class SearchController {
    @RequestMapping(value = "/{page}")
    public String index(@PathVariable(value = "page") String page){
        return page;
    }
}
