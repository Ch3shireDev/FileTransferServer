package mockups;

import helpers.HttpHeaderConverter;
import models.HttpRequestHeader;
import models.HttpResponseHeader;
import sockets.ISocketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockSocketService implements ISocketService {

    LinkedList<String> requestHeaderLines = new LinkedList<>();
    LinkedList<String> responseHeaderLines = new LinkedList<>();

    LinkedList<Byte> requestBodyBytes = new LinkedList<Byte>();
    LinkedList<Byte> responseBodyBytes = new LinkedList<Byte>();

    public MockSocketService() {
    }

    public void sendHeaderLine(String line) {
        requestHeaderLines.add(line);
    }

    @Override
    public void writeLine(String line) throws IOException {
        responseHeaderLines.add(line);
    }


    private String receiveHeaderLine() {
        return responseHeaderLines.pop();
    }

    @Override
    public void accept() throws IOException {

    }

    @Override
    public String readLine() throws IOException {
        return requestHeaderLines.pop();
    }

    @Override
    public void readBytes(byte[] buffer) throws IOException {
        for (var i = 0; i < buffer.length; i++) {
            buffer[i] = requestBodyBytes.pop();
        }
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        for (var i = 0; i < bytes.length; i++) {
            responseBodyBytes.add(bytes[i]);
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void sendRequestHeader(HttpRequestHeader header) {
        var lines = HttpHeaderConverter.toRequestHeaderLines(header);

        for (var line : lines) {
            sendHeaderLine(line);
        }

    }

    @Override
    public void sendResponseHeader(HttpResponseHeader header) throws IOException {
        var lines = HttpHeaderConverter.toResponseHeaderLines(header);
        for (var line : lines) {
            writeLine(line);
        }
    }

    @Override
    public HttpResponseHeader receiveResponseHeader() {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = receiveHeaderLine()) != null) {
            if (line.isBlank()) break;
            headerLines.add(line);
        }

        return HttpHeaderConverter.getResponseHeader(headerLines);
    }

    public void sendBody(String line) {
        sendBody(line.getBytes(StandardCharsets.UTF_8));
    }

    public void sendBody(byte[] bytes) {
        for (var b : bytes) {
            requestBodyBytes.add(b);
        }
    }

    public String receiveBodyString() {
        byte[] array = receiveResponseBodyBytes();
        return new String(array);
    }

    public byte[] receiveResponseBodyBytes() {
        Byte[] bytes = responseBodyBytes.toArray(Byte[]::new);
        if (bytes.length == 0) return new byte[0];
        byte[] array = new byte[bytes.length];
        for (var i = 0; i < bytes.length; i++) array[i] = bytes[i];
        responseBodyBytes.clear();
        return array;
    }
}
