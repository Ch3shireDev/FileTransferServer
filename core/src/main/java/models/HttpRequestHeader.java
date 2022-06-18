package models;

import java.util.Map;


public class HttpRequestHeader extends HttpHeader {
    private final String method;
    private final String path;

    public HttpRequestHeader(String method, String path, String version, Map<String, String> values) {
        super(values, version);
        this.method = method;
        this.path = path;
        this.values = values;
    }

    public HttpRequestHeader(String method, String path, String version) {
        super(version);
        this.method = method;
        this.path = path;
    }

    public HttpRequestHeader(String method, String path){
        super();
        this.method=method;
        this.path=path;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }


}

