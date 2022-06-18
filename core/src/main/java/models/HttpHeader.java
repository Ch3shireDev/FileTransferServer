package models;

import java.util.Map;

public class HttpHeader {
    private final Map<String, String> values;
    private final String method;
    private final String path;
    private final String version;

    public HttpHeader(String method, String path, String version, Map<String, String> values) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public HttpMethod getMethod() throws Exception {
        switch (method) {
            case "GET":
                return HttpMethod.GET;
            case "POST":
                return HttpMethod.POST;
            default:
                throw new Exception(String.format("Method not known: %s", method));
        }
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public int getContentLength() {
        String contentLength = values.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength);
    }

    public String getValue(String key) {
        if (!values.containsKey(key)) return "";
        return values.get(key);
    }
}

