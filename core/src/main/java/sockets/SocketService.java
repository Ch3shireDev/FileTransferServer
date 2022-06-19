package sockets;

import helpers.HttpHeaderConverter;
import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketService implements ISocketService {

    private final ServerSocket serverSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public SocketService(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void accept() throws IOException {
        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        this.dataInputStream = new DataInputStream(inputStream);
        OutputStream outputStream = clientSocket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public String readLine() throws IOException {
        String line = dataInputStream.readLine();
        System.out.println(String.format("Read line: %s", line));
        return line;
    }

    @Override
    public void readBytes(byte[] buffer) throws IOException {
        dataInputStream.read(buffer);
    }

    @Override
    public void writeLine(String line) throws IOException {
        System.out.println(String.format("Write line: %s", line));
        dataOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        dataOutputStream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        dataOutputStream.close();
        dataInputStream.close();
    }

    @Override
    public void sendRequestHeader(HttpRequestHeader request) throws IOException {
        var lines = HttpHeaderConverter.toRequestHeaderLines(request);
        for (String line : lines) {
            writeLine(line);
        }
    }

    @Override
    public void sendResponseHeader(HttpResponseHeader header) throws IOException {

    }

    @Override
    public HttpResponseHeader receiveResponseHeader() {
        return null;
    }
}
