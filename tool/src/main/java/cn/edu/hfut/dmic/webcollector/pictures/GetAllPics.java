package cn.edu.hfut.dmic.webcollector.pictures;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequester;
import cn.edu.hfut.dmic.webcollector.net.HttpRequesterImpl;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;

/**
 * Created by henxii on 1/13/15.
 */
public class GetAllPics extends Thread {
    HttpRequester httpRequester = new HttpRequesterImpl();
    private long totalPages;
    private String baseUrl;

    public GetAllPics(long totalPages, String baseUrl) {
        this.totalPages = totalPages;
        this.baseUrl = baseUrl;
    }

    public void run() {
        try {
            for (long i = 1; i <=totalPages; i++) {
                HttpResponse response = null;
                response = httpRequester.getResponse("http://qiumeimei.com/page/" + i);
                Page page = new Page();
                page.setUrl("http://stackoverflow.com/questions/" + i);
                page.setResponse(response);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
