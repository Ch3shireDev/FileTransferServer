package models;

public class HttpResponse {
    private final HttpResponseHeader header;

    public HttpResponse(int code, String message) {
        this.header = new HttpResponseHeader(code, message);
    }

    public HttpResponseHeader getHeader() {
        return this.header;
    }
}
