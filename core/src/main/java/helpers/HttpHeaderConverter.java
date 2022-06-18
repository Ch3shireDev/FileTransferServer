package helpers;

import models.HttpHeader;
import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeaderConverter {

    public static Collection<String> toRequestHeaderLines(HttpRequestHeader header) {
        var firstLine = String.format("%s %s %s\r\n", header.getMethod(), header.getPath(), header.getVersion());
        return getHeaderLines(firstLine, header);
    }

    public static Collection<String> toResponseHeaderLines(HttpResponseHeader header) {
        var firstLine = String.format("%s %d %s\r\n", header.getVersion(), header.getCode(), header.getMessage());
        return getHeaderLines(firstLine, header);
    }

    static Collection<String> getHeaderLines(String firstLine, HttpHeader header) {
        List<String> lines = new ArrayList<>();
        lines.add(firstLine);
        lines.addAll(getHeaderDictionaryLines(header));
        lines.add("\r\n");
        return lines;
    }

    static Collection<String> getHeaderDictionaryLines(HttpHeader header) {

        List<String> lines = new ArrayList<>();
        for (var key : header.getKeys()) {
            var value = header.getValue(key);
            lines.add(String.format("%s: %s\r\n", key, value));
        }

        return lines;
    }


    public static HttpRequestHeader getHttpHeader(List<String> lines) {
        String firstLine = lines.stream().findFirst().get();
        String[] parts = firstLine.split("\\s");

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        Map<String, String> values = new HashMap<>();
        Pattern pattern = Pattern.compile("(.*): (.*)");
        for (String line : lines.stream().skip(1).toArray(String[]::new)) {
            Matcher result = pattern.matcher(line);
            if (!result.find()) break;
            var key = result.group(1);
            var value = result.group(2);
            values.put(key, value);
        }

        return new HttpRequestHeader(method, path, version, values);
    }

    public static HttpResponseHeader getResponseHeader(Collection<String> lines) {

        String line = lines.stream().findFirst().get();

        String[] parts = line.split("\\s");

        String version = parts[0];
        String code = parts[1];
        String message = parts[2];

        Map<String, String> values = new HashMap<>();
        Pattern pattern = Pattern.compile("(.*): (.*)");

        for (String line2 : lines.stream().skip(1).toArray(String[]::new)) {
            if (line2.isBlank()) break;
            Matcher result = pattern.matcher(line);
            if (!result.find()) break;
            var key = result.group(1);
            var value = result.group(2);
            values.put(key, value);
        }

        return new HttpResponseHeader(version, code, message, values);
    }

}
