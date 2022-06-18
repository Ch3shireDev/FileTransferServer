package models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class HttpHeader {

    private final String version;

    protected Map<String, String> values;

    protected HttpHeader() {
        this.values = new HashMap<>();
        this.version = "HTTP/1.1";
    }

    protected HttpHeader(Map<String, String> values) {
        this.values = values;
        this.version = "HTTP/1.1";
    }

    protected HttpHeader(Map<String, String> values, String version) {
        this.values = values;
        this.version = version;
    }

    protected HttpHeader(String version) {
        this.version = version;
        this.values = new HashMap<>();
    }

    public int getContentLength() {
        String contentLength = values.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength);
    }

    public String getValue(String key) {
        if (!values.containsKey(key)) return "";
        return values.get(key);
    }

    public void setValue(String key, int value) {
        setValue(key, String.valueOf(value));
    }

    public void setValue(String key, String value) {
        if (values.containsKey(key)) values.replace(key, value);
        else values.put(key, value);
    }

    public Set<String> getKeys() {
        return values.keySet();
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getVersion() {
        return version;
    }
}
