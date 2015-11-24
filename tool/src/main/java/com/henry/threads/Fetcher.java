package com.henry.threads;

import com.henry.model.Answer;
import com.henry.model.Comment;
import com.henry.model.Question;
import com.henry.util.Config;
import com.henry.util.JDBCHelper;
import com.henry.util.ProxyUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fetcher extends Thread {
    private static int NOT_FOUND = 404;
    private static int FORBIDDEN = 403;
    private static int OK = 200;
    private ObjectMapper objectMapper = new ObjectMapper();
    private JdbcTemplate jdbcTemplate;
    private int pageNumber;
    private Random random = new Random();
    private Proxy currentProxy = Proxy.NO_PROXY;

    private static final class Lock {
    }

    private final Object lock = new Lock();

    public Fetcher(int pageNumber) {
        this.pageNumber = pageNumber;
        jdbcTemplate = JDBCHelper.getJdbcTemplate("fetcher");
        ProxyUtil.getInstance();
    }

    public void run() {
        synchronized (lock) {
            do {
                System.out.println("begin fetch --> " + pageNumber);
                Document pageDoc = getDoc("http://stackoverflow.com/questions?pagesize=50&sort=newest&page=" + pageNumber);
                if (pageDoc == null) {
                    return;
                }
                List<Integer> questionsId = getQuestionsIdByDoc(pageDoc);
                for (Integer questionId : questionsId) {
                    if (!isQuestionInDb(questionId)) {
                        Document contentDoc = getDoc("http://stackoverflow.com/questions/" + questionId);
                        saveContentAsJsonInDB(contentDoc);
                    }
                }
                System.out.println("---------->" + pageNumber + " end---------");

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } while (pageNumber != -1);
        }
    }

    private boolean isQuestionInDb(Integer questionId) {
        return jdbcTemplate.queryForObject("Select count(*) from tb_content where id = ?", new Object[]{questionId}, Integer.class) == 1;
    }

    private List<Integer> getQuestionsIdByDoc(Document doc) {
        List<Integer> questionsId = new ArrayList<>();
        Elements elements = doc.select(".question-hyperlink");
        for (Element element : elements) {
            if (element.attr("href").contains("/questions/")) {
                questionsId.add(Integer.valueOf(element.attr("href").replace("/questions/", "").replaceAll("/.+", "")));
            }
        }
        return questionsId;
    }

    private Document getDoc(String url) {
        URL website = null;
        try {
            website = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
        HttpURLConnection httpUrlConnetion = new HttpURLConnection(website, currentProxy);
        httpUrlConnetion.setConnectTimeout(1000 * 40);
        httpUrlConnetion.setReadTimeout(1000 * 20);
        httpUrlConnetion.setRequestProperty("User-Agent", Config.getString("user.agent"));
        String page = null;
        try {
            int stateCode = httpUrlConnetion.getResponseCode();
            if (stateCode != OK) {
                if (stateCode == NOT_FOUND || stateCode == FORBIDDEN) {
                    if (Proxy.NO_PROXY.equals(currentProxy)) {
                        return null;
                    } else {
                        currentProxy = Proxy.NO_PROXY;
                        return getDoc(url);
                    }
                }
                currentProxy = ProxyUtil.getProxys().get(String.valueOf(random.nextInt(ProxyUtil.getProxys().size())));
                return getDoc(url);
            }
            InputStream is = httpUrlConnetion.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            int sum = 0;
            int maxsize = 1000 * 1000;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((read = is.read(buf)) != -1) {
                if (maxsize > 0) {
                    sum = sum + read;
                    if (sum > maxsize) {
                        read = maxsize - (sum - read);
                        bos.write(buf, 0, read);
                        break;
                    }
                }
                bos.write(buf, 0, read);
            }
            is.close();
            byte[] content = bos.toByteArray();
            bos.close();
            page = new String(content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("bad proxy change another!");
            currentProxy = ProxyUtil.getProxys().get(String.valueOf(random.nextInt(ProxyUtil.getProxys().size())));
            return getDoc(url);
        } finally {
            httpUrlConnetion.disconnect();
        }
        Document doc = Jsoup.parse(page, String.valueOf(httpUrlConnetion.getURL()));
        return doc;
    }

    private void saveContentAsJsonInDB(Document doc) {
        if (doc == null || !doc.baseUri().contains("http://stackoverflow.com/questions/")) {
            return;
        }
        Question question = new Question(doc.select("#question-header>h1>a").html(), doc.select(".postcell>div>.post-text").html());
        for (Element questionComment : doc.select(".question").select(".comment-copy")) {
            question.getCs().add(new Comment(questionComment.html()));
        }
        for (Element ans : doc.select(".answer")) {
            Answer answer = new Answer(ans.select(".post-text").html());
            for (Element com : ans.select(".comment-copy")) {
                answer.getCs().add(new Comment(com.html()));
            }
            question.getAs().add(answer);
        }
        for (Element tag : doc.select(".post-taglist>.post-tag")) {
            question.getTs().add(tag.text());
        }
        try {
            //todo check the content change or not by date, if not change don't update
            /**
             * Jquery:
             * $("span[class*='relativetime'")
             * Java:
             * DateFormat format = new SimpleDateFormat("MMM d ''yy 'at' HH:mm", Locale.ENGLISH);
             * format.parse("Jul 5 '12 at 17:53");
             */
            String conent = objectMapper.writeValueAsString(question);
            int url = Integer.valueOf(doc.baseUri().replace("http://stackoverflow.com/questions/", "").replaceAll("/.+", ""));
            jdbcTemplate.update("INSERT INTO tb_content (content,id) VALUES (?::json,?)", conent, url);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
        }
    }

    public Proxy getCurrentProxy() {
        return currentProxy;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        synchronized (lock) {
            this.pageNumber = pageNumber;
            this.currentProxy = Proxy.NO_PROXY;
            lock.notify();
        }
    }

    public static void main(String[] args) {
        Config.getInstance("fetch.properties");
        JDBCHelper.createPostgresqlTemplate("fetcher",
        Config.getString("database.url"),
                Config.getString("database.username"),
                Config.getString("database.pwd"),
                Config.getInt("database.initActive"),
                Config.getInt("database.maxActive"));
        Fetcher fetcher = new Fetcher(10000);
        fetcher.start();
    }

}
