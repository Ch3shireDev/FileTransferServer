package sockets;

import helpers.HttpHeaderConverter;
import models.HttpRequestHeader;
import models.HttpResponseHeader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketService extends SocketServiceBase implements IServerSocketService {
    Socket clientSocket;
    ServerSocket serverSocket;

    public ServerSocketService(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public ServerSocketService(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    @Override
    public void open() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        this.dataInputStream = new DataInputStream(inputStream);
        OutputStream outputStream = clientSocket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
    }


    @Override
    public HttpRequestHeader receiveRequestHeader() throws IOException {
        var lines = readLines();
        return HttpHeaderConverter.getRequestHeader(lines);
    }

    @Override
    public void receiveRequestBody(byte[] buffer) throws IOException {
        int totalLength = buffer.length;
        int readBytes = 0;
        while (readBytes < totalLength) {
            readBytes += dataInputStream.read(buffer, readBytes, totalLength - readBytes);
        }
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing streams...");
        dataOutputStream.close();
        System.out.println("Closing streams...");
        dataInputStream.close();
        System.out.println("Streams closed.");

    }

    public void end() throws IOException {
        clientSocket.close();
    }

    @Override
    public void sendResponseHeader(HttpResponseHeader header) throws IOException {
        var lines = HttpHeaderConverter.toResponseHeaderLines(header);
        writeLines(lines);
    }

    @Override
    public void sendResponseBody(byte[] bytes) throws IOException {
        dataOutputStream.write(bytes);
    }

}
