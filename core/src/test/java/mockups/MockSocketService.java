package mockups;

import communication.HttpServerService;
import helpers.HttpHeaderConverter;
import models.HttpRequestHeader;
import models.HttpResponseHeader;
import sockets.IClientSocketService;
import sockets.IServerSocketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockSocketService implements IClientSocketService, IServerSocketService {

    LinkedList<String> requestHeaderLines = new LinkedList<>();
    LinkedList<String> responseHeaderLines = new LinkedList<>();
    LinkedList<Byte> requestBodyBytes = new LinkedList<>();
    LinkedList<Byte> responseBodyBytes = new LinkedList<>();
    private HttpServerService serverService;

    public MockSocketService() {
    }


    private String receiveHeaderLine() {
        return requestHeaderLines.pop();
    }

    @Override
    public void open() throws IOException {

    }


    @Override
    public void sendRequestBody(byte[] bytes) throws IOException {
        for (var i = 0; i < bytes.length; i++) {
            requestBodyBytes.add(bytes[i]);
        }
    }

    @Override
    public void receiveResponseBody(byte[] buffer) throws IOException {
        for (var i = 0; i < buffer.length; i++) {
            buffer[i] = responseBodyBytes.pop();
        }
    }

    @Override
    public void sendResponseBody(byte[] bytes) throws IOException {
        for (var i = 0; i < bytes.length; i++) {
            responseBodyBytes.add(bytes[i]);
        }
    }

    @Override
    public void receiveRequestBody(byte[] buffer) throws IOException {
        for (var i = 0; i < buffer.length; i++) {
            buffer[i] = requestBodyBytes.pop();
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void sendRequestHeader(HttpRequestHeader header) {
        var lines = HttpHeaderConverter.toRequestHeaderLines(header);
        requestHeaderLines.addAll(lines);
    }

    @Override
    public void sendResponseHeader(HttpResponseHeader header) throws IOException {
        var lines = HttpHeaderConverter.toResponseHeaderLines(header);
        responseHeaderLines.addAll(lines);
    }

    @Override
    public HttpRequestHeader receiveRequestHeader() throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = requestHeaderLines.pop()) != null) {
            if (line.isBlank()) break;
            headerLines.add(line);
        }

        return HttpHeaderConverter.getRequestHeader(headerLines);
    }

    @Override
    public HttpResponseHeader receiveResponseHeader() {
        runServer();

        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = responseHeaderLines.pop()) != null) {
            if (line.isBlank()) break;
            headerLines.add(line);
        }

        return HttpHeaderConverter.getResponseHeader(headerLines);
    }

    private void runServer() {
        if (serverService == null) return;
        try {
            serverService.run();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
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

    public void setServer(HttpServerService serverService) {
        this.serverService = serverService;
    }
}
