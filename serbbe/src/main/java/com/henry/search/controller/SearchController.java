package com.henry.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by yong on 2015/11/17.
 */
@Controller
public class SearchController {
    @RequestMapping(value = "/")
    public String index(){
        return "index";
    }
}
