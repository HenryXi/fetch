package transfer.dao;

public class Question {
    private int url;
    private String t;
    private String c;

    public Question() {
    }

    public Question(String title, String content, int url) {
        this.t = title;
        this.c = content;
        this.url = url;
    }

    public int getUrl() {
        return this.url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public String getT() {
        return this.t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getC() {
        return this.c;
    }

    public void setC(String c) {
        this.c = c;
    }
}