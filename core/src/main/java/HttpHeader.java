import java.util.Map;

public class HttpHeader {
    private final Map<String, String> values;
    String method;
    String path;
    String version;

    public HttpHeader(String method, String path, String version, Map<String, String> values) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.values = values;
    }

    public int getContentLength() {
        String contentLength = values.getOrDefault("Content-Length", "0");
        return Integer.parseInt(contentLength);
    }

    public ContentTypeEnum getContentType() {
        String contentType = values.getOrDefault("Content-Type", "None");


        if (contentType.equals("text/plain")) return ContentTypeEnum.TEXT_PLAIN;
        return ContentTypeEnum.UNDEFINED;
    }
}
