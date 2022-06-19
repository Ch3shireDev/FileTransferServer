package models;

public class HttpResponse {
    private final HttpResponseHeader header;
    private final byte[] body;

    public HttpResponse(int code, String message) {
        this.header = new HttpResponseHeader(code, message);
        this.body = new byte[0];
    }

    public HttpResponse(int code, String message, byte[] body) {
        this.header = new HttpResponseHeader(code, message);
        this.header.values.put("Content-Length", String.valueOf(body.length));
        this.body = body;
    }

    public HttpResponse(HttpResponseHeader responseHeader, byte[] body) {
        this.header = responseHeader;
        this.body = body;
    }

    public HttpResponse(HttpResponseHeader responseHeader) {
        this.header = responseHeader;
        this.body = new byte[0];
    }

    public HttpResponseHeader getHeader() {
        return this.header;
    }

    public byte[] getBody() {
        return body;
    }
}
