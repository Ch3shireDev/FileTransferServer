package sockets;

import helpers.HttpHeaderConverter;
import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;

public class ClientSocketService extends SocketServiceBase implements IClientSocketService {
    private final String host;
    private final int port;

    public ClientSocketService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void open() throws IOException {
        InetAddress address = InetAddress.getByName(host);
        var socket = new Socket(address, port);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void sendRequestBody(byte[] buffer) throws IOException {
        dataOutputStream.write(buffer);
    }

    @Override
    public void receiveResponseBody(byte[] bytes) throws IOException {
        dataInputStream.read(bytes);
    }

    @Override
    public void close() throws IOException {

        dataOutputStream.close();
        dataInputStream.close();
    }

    @Override
    public void sendRequestHeader(HttpRequestHeader header) throws IOException {
        var lines = HttpHeaderConverter.toRequestHeaderLines(header);
        writeLines(lines);
    }

    @Override
    public HttpResponseHeader receiveResponseHeader() throws IOException {
        Collection<String> lines = readLines();
        return HttpHeaderConverter.getResponseHeader(lines);
    }
}
