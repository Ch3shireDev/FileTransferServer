package models;

import java.nio.charset.StandardCharsets;

public class HttpRequest {

    final String version = "HTTP/1.1";
    private HttpRequestHeader header;
    private byte[] body;

    public HttpRequest(String method, String path, byte[] body) {
        this.header = new HttpRequestHeader(method, path, version);
        this.header.setValue("Content-Length", body.length);
        this.header.setValue("Content-Type", "application/binary");
        this.body = body;
    }

    public HttpRequest(String method, String path, String body) {
        this(method, path);
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.header.setValue("Content-Length", this.body.length);
        this.header.setValue("Content-Type", "application/json");
    }

    public HttpRequest(String method, String path, int contentLength) {
        this.header = new HttpRequestHeader(method, path, version);
        this.header.setValue("Content-Length", contentLength);
    }

    public HttpRequest(String method, String path) {
        this.header = new HttpRequestHeader(method, path, version);
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public void setHeader(HttpRequestHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
