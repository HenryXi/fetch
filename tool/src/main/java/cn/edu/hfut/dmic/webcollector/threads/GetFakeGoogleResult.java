package cn.edu.hfut.dmic.webcollector.threads;



import cn.edu.hfut.dmic.webcollector.Question;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by henxii on 3/5/15.
 */
public class GetFakeGoogleResult {
    public static void main(String[] args) {
        try {
            Document doc=Jsoup.connect("http://www.gfsoso.com/?q=command+browser+site%3Astackoverflow.com").get();
            String url=doc.baseUri()+"&t=1";
            int startIndex=doc.toString().indexOf("$.cookie('_GFTOKEN','");
            int endIndex=doc.toString().indexOf("', {expires:720}");
            String cookie=doc.toString().substring(startIndex+21,endIndex);
            doc=Jsoup.connect(url).cookie("_GFTOKEN",cookie).get();
            String targetDiv=doc.toString().substring(doc.toString().indexOf("<ol"),doc.toString().indexOf("ol>")+3);
            Document finalDoc=Jsoup.parse(targetDiv.replace("\\\"","\"").replace("\\","").replace("\t",""));
            finalDoc.select(".r");
            List<Question> questions=new ArrayList<>();
            for(Element element:finalDoc.select(".g")){
                Question question=new Question("","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
