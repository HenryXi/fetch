package com.controller;

import com.dao.Question;
import com.dao.RelatedQuestion;
import com.exception.GoobbeInternalErrorException;
import com.exception.GoobbeRsNotFoundException;
import com.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

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
    public String loadIndex(ModelMap modelMap,HttpServletRequest request){
        modelMap.put("host",request.getHeader("host").replace("www.","").replace(".com", ""));
        List<Question> questions=questionService.getRandomQuestions();
        if(questions.contains(null)) throw new GoobbeInternalErrorException();
        modelMap.put("questions", questions);
        modelMap.put("currentPage",-1);
        return "index";
    }

    @RequestMapping(value = "/questions/{id}/{title4url}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id,@PathVariable("title4url") String title4url,
                              ModelMap modelMap,Locale locale,HttpServletRequest request){
        modelMap.put("host",request.getHeader("host").replace("www.", "").replace(".com", ""));
        Question question= questionService.getQuestionById(Integer.valueOf(id));
        if(question==null||question.getT().equals("")) throw new GoobbeRsNotFoundException();
        modelMap.put("question", question);
        List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
        modelMap.put("relatedQuestions", relatedQuestions);
        modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
        return "content";
    }

    @RequestMapping(value = "/questions/{id}", method = RequestMethod.GET)
    public String loadContent(@PathVariable("id") String id, ModelMap modelMap,Locale locale,HttpServletRequest request){
        return loadContent(id,"luck",modelMap,locale,request);
    }

    @RequestMapping(value = "/questions", method = RequestMethod.GET)
    public String loadIndex(@RequestParam("page") String page, ModelMap modelMap,Locale locale,HttpServletRequest request){
        modelMap.put("host",request.getHeader("host").replace("www.","").replace(".com", ""));
        int totalPage=1+questionService.getMaxId()/number_of_questions_per_page;
        int pageNum=Integer.valueOf(page);
        if(pageNum<=0){
            pageNum=1;
        }
        if(pageNum>totalPage){
            pageNum=totalPage;
        }
        List<Question> questions = questionService.getQuestionsForIndex(pageNum);
        if(questions.contains(null)) throw new GoobbeInternalErrorException();
        modelMap.put("questions", questions);
        modelMap.put("currentPage",pageNum);
        modelMap.put("totalPage", totalPage);
        modelMap.put("tl",messageSource.getMessage("target.language", null, "EnglishToEnglish", locale));
        return "index";
    }

    @RequestMapping(value = "/question/{url}/{title4url}",method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, @PathVariable("title4url") String title4url,
                                   ModelMap modelMap,Locale locale,HttpServletRequest request){
        modelMap.put("host",request.getHeader("host").replace("www.", "").replace(".com", ""));
        Question question= questionService.getQuestionByUrl(url);
        if(question==null||question.getT().equals("")) throw new GoobbeRsNotFoundException();
        modelMap.put("question", question);
        List<RelatedQuestion> relatedQuestions=searchService.getLocalSearchResult(question.getT());
        modelMap.put("relatedQuestions", relatedQuestions);
        modelMap.put("tl",messageSource.getMessage("target.language",null,"EnglishToEnglish",locale));
        return "content";
    }

    @RequestMapping(value = "/question/{url}", method = RequestMethod.GET)
    public String showSearchResult(@PathVariable("url") String url, ModelMap modelMap,Locale locale,HttpServletRequest request){
        return showSearchResult(url,"luck",modelMap,locale,request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public @ResponseBody String search(@RequestParam("q") String q, ModelMap modelMap) {
        return questionService.getQuestionsByKeyword(q);
    }
}
