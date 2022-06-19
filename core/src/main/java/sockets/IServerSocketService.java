package sockets;

import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.io.IOException;

public interface IServerSocketService extends ISocketService {

    void sendResponseHeader(HttpResponseHeader header) throws IOException;

    HttpRequestHeader receiveRequestHeader() throws IOException;

    public void sendResponseBody(byte[] bytes) throws IOException;

    public void receiveRequestBody(byte[] buffer) throws IOException;

}
