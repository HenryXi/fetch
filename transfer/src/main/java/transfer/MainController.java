package transfer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import transfer.dao.Question;

@Controller
public class MainController {
    private static final String GOOGLE_ADDRESS = "www.google.com";
    private static String google_ip;

    public MainController() {
    }

    @RequestMapping(
            value = {"/"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public List<Question> getResult(@RequestParam("keyword") String keyword) throws UnsupportedEncodingException {
        List questions = new ArrayList();
        keyword = URLEncoder.encode(keyword, "UTF-8");
        if (google_ip == null) {
            google_ip = this.getGoogleIp();
        }

        String url = "http://" + google_ip + "/search?q=" + keyword + "+site:stackoverflow.com%2Fquestions%2F&num=50";

        try {
            long e = System.currentTimeMillis();
            Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5000).get();
            long endTime = System.currentTimeMillis();
            long used = endTime - e;
            System.out.println("used -> " + used);
            if (used > 2000L) {
                google_ip = this.getGoogleIp();
            }

            questions = this.getQuestionsByDoc(doc);
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return questions;
    }

    private List<Question> getQuestionsByDoc(Document doc) {
        ArrayList questions = new ArrayList();
        Elements els = doc.select("li.g");
        Iterator i$ = els.iterator();

        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            String urlStr = StringUtils.substringBetween(el.select("a[href~=.*http://stackoverflow.com/questions/\\d+]").attr("href"),"/questions/","/");
            if (null!=urlStr && urlStr.matches("\\d+")) {
                int urlNumber = Integer.valueOf(urlStr);
                Question question = new Question(el.select("h3 > a").html(), el.select(".st").html(), urlNumber);
                questions.add(question);
            }
        }

        return questions;
    }

    public String getGoogleIp() {
        try {
            InetAddress e = InetAddress.getByName((new URL("http://www.google.com")).getHost());
            System.out.println("ip -> " + e.getHostAddress());
            return e.getHostAddress();
        } catch (UnknownHostException var2) {
            var2.printStackTrace();
        } catch (MalformedURLException var3) {
            var3.printStackTrace();
        }

        return "www.google.com";
    }

    @RequestMapping(
            value = {"/google"},
            method = {RequestMethod.GET}
    )
    @ResponseBody
    public List<Question> getResultByUrl(@RequestParam("keyword") String keyword) throws UnsupportedEncodingException {
        List questions = new ArrayList();
        keyword = URLEncoder.encode(keyword, "UTF-8");
        String url = "http://www.google.com/search?q=" + keyword + "+site:stackoverflow.com%2Fquestions%2F&num=50";
//        System.setProperty("http.proxyHost", "10.10.8.1");
//        System.setProperty("http.proxyPort", "3128");
//        System.setProperty("https.proxyHost", "10.10.8.1");
//        System.setProperty("https.proxyPort", "3128");
        try {
            long e = System.currentTimeMillis();
            Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(5000).get();
            long endTime = System.currentTimeMillis();
            long used = endTime - e;
            System.out.println("used -> " + used);
            questions = this.getQuestionsByDoc(doc);
        } catch (IOException var11) {
            var11.printStackTrace();
        }

        return questions;
    }

    public static void main(String[] args) {
        MainController controller = new MainController();
        try {
            controller.getResultByUrl("java");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}