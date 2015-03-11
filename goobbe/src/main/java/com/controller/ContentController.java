package com.controller;

import com.dao.Question;
import com.exception.GoobbeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.service.QuestionService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.net.URLEncoder;
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
            List<Question> questions = questionService.getQuestions(URLEncoder.encode(q,"UTF-8"));
            modelMap.put("questions", questions);
            modelMap.put("totalPage", -1);
            modelMap.put("keyword",q);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new GoobbeException();
    }
    @ResponseBody
    @RequestMapping(value = "/translate",produces = "text/plain")
    public String  translate(@RequestParam("t") String t,ModelMap modelMap){
        return "{\"responseData\": {\"results\":[{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.thefreedictionary.com/luck\",\"url\":\"http://www.thefreedictionary.com/luck\",\"visibleUrl\":\"www.thefreedictionary.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:4-B9F_KyNXMJ:www.thefreedictionary.com\",\"title\":\"\\u003cb\\u003eluck\\u003c/b\\u003e - definition of \\u003cb\\u003eluck\\u003c/b\\u003e by The Free Dictionary\",\"titleNoFormatting\":\"luck - definition of luck by The Free Dictionary\",\"content\":\"The chance happening of fortunate or adverse events; fortune: They met one day \\nout of pure \\u003cb\\u003eluck\\u003c/b\\u003e. 2. Good fortune or prosperity; success: We wish you \\u003cb\\u003eluck\\u003c/b\\u003e. 3.\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://wiki.guildwars2.com/wiki/Luck\",\"url\":\"http://wiki.guildwars2.com/wiki/Luck\",\"visibleUrl\":\"wiki.guildwars2.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:CxUlBhzWdfAJ:wiki.guildwars2.com\",\"title\":\"\\u003cb\\u003eLuck\\u003c/b\\u003e - Guild Wars 2 Wiki (GW2W)\",\"titleNoFormatting\":\"Luck - Guild Wars 2 Wiki (GW2W)\",\"content\":\"Feb 1, 2015 \\u003cb\\u003e...\\u003c/b\\u003e \\u003cb\\u003eLuck\\u003c/b\\u003e is a game mechanic that increases magic find for all characters on the \\naccount. The amount of \\u003cb\\u003eluck\\u003c/b\\u003e you have can be seen in the ...\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://wiki.guildwars2.com/wiki/Essence_of_Luck\",\"url\":\"http://wiki.guildwars2.com/wiki/Essence_of_Luck\",\"visibleUrl\":\"wiki.guildwars2.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:h2nHSWymHicJ:wiki.guildwars2.com\",\"title\":\"Essence of \\u003cb\\u003eLuck\\u003c/b\\u003e - Guild Wars 2 Wiki (GW2W)\",\"titleNoFormatting\":\"Essence of Luck - Guild Wars 2 Wiki (GW2W)\",\"content\":\"Jan 16, 2015 \\u003cb\\u003e...\\u003c/b\\u003e Essences of \\u003cb\\u003eLuck\\u003c/b\\u003e are consumables that grant \\u003cb\\u003eLuck\\u003c/b\\u003e, an account-wide statistic that \\nincreases Magic Find. There are different rarities which award ...\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://en.wiktionary.org/wiki/luck\",\"url\":\"http://en.wiktionary.org/wiki/luck\",\"visibleUrl\":\"en.wiktionary.org\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:4I9ZuFRUsT4J:en.wiktionary.org\",\"title\":\"\\u003cb\\u003eluck\\u003c/b\\u003e - Wiktionary\",\"titleNoFormatting\":\"luck - Wiktionary\",\"content\":\"From Middle English luk, lukke, related to Old Frisian luk (“\\u003cb\\u003eluck\\u003c/b\\u003e”), West Frisian \\ngelok (“\\u003cb\\u003eluck\\u003c/b\\u003e”), Dutch geluk (“\\u003cb\\u003eluck\\u003c/b\\u003e, happiness”), Low German luk (“\\u003cb\\u003eluck\\u003c/b\\u003e”), German\\n ...\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://espn.go.com/nfl/player/_/id/14874/andrew-luck\",\"url\":\"http://espn.go.com/nfl/player/_/id/14874/andrew-luck\",\"visibleUrl\":\"espn.go.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:Q_NWzZXtZVUJ:espn.go.com\",\"title\":\"Andrew \\u003cb\\u003eLuck\\u003c/b\\u003e Stats, News, Videos, Highlights, Pictures, Bio \\u003cb\\u003e...\\u003c/b\\u003e\",\"titleNoFormatting\":\"Andrew Luck Stats, News, Videos, Highlights, Pictures, Bio ...\",\"content\":\"Get the latest news, stats, videos, highlights and more about Indianapolis Colts \\nquarterback Andrew \\u003cb\\u003eLuck\\u003c/b\\u003e on ESPN.com.\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.lucksd.k12.wi.us/\",\"url\":\"http://www.lucksd.k12.wi.us/\",\"visibleUrl\":\"www.lucksd.k12.wi.us\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:LPK6dUM2RCEJ:www.lucksd.k12.wi.us\",\"title\":\"School District of \\u003cb\\u003eLuck\\u003c/b\\u003e\",\"titleNoFormatting\":\"School District of Luck\",\"content\":\"\\u003cb\\u003eLuck\\u003c/b\\u003e teacher Mrs. Nancy Gill knows how to maximize money for her classroom! \\nWhen a local family donated some $$ to purchase books for her third grade ...\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://fallout.wikia.com/wiki/Luck\",\"url\":\"http://fallout.wikia.com/wiki/Luck\",\"visibleUrl\":\"fallout.wikia.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:2Puq9kfu0IcJ:fallout.wikia.com\",\"title\":\"\\u003cb\\u003eLuck\\u003c/b\\u003e - The Fallout wiki - Fallout: New Vegas and more\",\"titleNoFormatting\":\"Luck - The Fallout wiki - Fallout: New Vegas and more\",\"content\":\"\\u003cb\\u003eLuck\\u003c/b\\u003e is one of the seven primary statistics in the SPECIAL character system. In \\nFallout, \\u003cb\\u003eLuck\\u003c/b\\u003e...\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"https://www.youtube.com/user/TheMasterOfLucky\",\"url\":\"https://www.youtube.com/user/TheMasterOfLucky\",\"visibleUrl\":\"www.youtube.com\",\"cacheUrl\":\"http://www.google.com/search?q\\u003dcache:_x-56S1xZwEJ:www.youtube.com\",\"title\":\"Master of \\u003cb\\u003eLuck\\u003c/b\\u003e - YouTube\",\"titleNoFormatting\":\"Master of Luck - YouTube\",\"content\":\"The Master of \\u003cb\\u003eLuck\\u003c/b\\u003e Face Reveal! Thank you guys so much for 100,000 \\nSubscribers! I love each and every one of you guys and the support you show me\\n, and I ...\"}],\"cursor\":{\"resultCount\":\"45,200,000\",\"pages\":[{\"start\":\"0\",\"label\":1},{\"start\":\"8\",\"label\":2},{\"start\":\"16\",\"label\":3},{\"start\":\"24\",\"label\":4},{\"start\":\"32\",\"label\":5},{\"start\":\"40\",\"label\":6},{\"start\":\"48\",\"label\":7},{\"start\":\"56\",\"label\":8}],\"estimatedResultCount\":\"45200000\",\"currentPageIndex\":1,\"moreResultsUrl\":\"http://www.google.com/search?oe\\u003dutf8\\u0026ie\\u003dutf8\\u0026source\\u003duds\\u0026start\\u003d9\\u0026hl\\u003dzh-CN\\u0026q\\u003dluck\",\"searchResultTime\":\"0.15\"}}, \"responseDetails\": null, \"responseStatus\": 200}";
    }
}
