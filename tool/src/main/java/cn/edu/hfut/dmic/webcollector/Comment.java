package cn.edu.hfut.dmic.webcollector;

/**
 * Created by henxii on 2/7/15.
 */
public class Comment{
    public Comment(String content){
        this.c=content;
    }

    /**
     * content
     */
    private String c;

    public String getContent() {
        return c;
    }

    public void setContent(String content) {
        this.c = content;
    }
}
