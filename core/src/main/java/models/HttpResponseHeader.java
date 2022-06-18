package models;

import java.util.Map;

public class HttpResponseHeader extends HttpHeader {
    private final int code;
    private final String message;

    public HttpResponseHeader(String version, String code, String message, Map<String, String> values) {
        super(values, version);
        this.code = Integer.parseInt(code);
        this.message = message;
    }

    public HttpResponseHeader(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
