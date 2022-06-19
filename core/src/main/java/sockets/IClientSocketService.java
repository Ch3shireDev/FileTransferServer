package sockets;

import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.io.IOException;

public interface IClientSocketService extends ISocketService {
    void sendRequestHeader(HttpRequestHeader header) throws IOException;

    HttpResponseHeader receiveResponseHeader() throws IOException;

    public void sendRequestBody(byte[] buffer) throws IOException;

    public void receiveResponseBody(byte[] bytes) throws IOException;
}
