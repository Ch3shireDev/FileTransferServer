package sockets;

import models.HttpRequestHeader;
import models.HttpResponse;
import models.HttpResponseHeader;

import java.io.IOException;

public interface ISocketService {
    public void accept() throws IOException;

    public String readLine() throws IOException;

    public void readBytes(byte[] buffer) throws IOException;

    public void writeLine(String line) throws IOException;

    public void writeBytes(byte[] bytes) throws IOException;

    public void close() throws IOException;

    void sendRequestHeader(HttpRequestHeader header) throws IOException;
    void sendResponseHeader(HttpResponseHeader header) throws IOException;

    HttpResponseHeader receiveResponseHeader();
}
