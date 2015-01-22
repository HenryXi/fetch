package cn.edu.hfut.dmic.webcollector;

/**
 * Created by henxii on 1/19/15.
 */
public class Proxy {
    public Proxy(String url,String port){
        this.url=url;
        this.port=port;
    }
    private String url;
    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
