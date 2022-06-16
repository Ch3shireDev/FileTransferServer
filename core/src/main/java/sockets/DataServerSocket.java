package sockets;

import sockets.IServerSocket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DataServerSocket implements IServerSocket {

    private final ServerSocket serverSocket;
    private final int port;
    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public DataServerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.port = port;
    }

    @Override
    public void accept() throws IOException {
        this.clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        this.dataInputStream = new DataInputStream(inputStream);
        OutputStream outputStream = clientSocket.getOutputStream();
        this.dataOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public String readLine() throws IOException {
        return dataInputStream.readLine();
    }

    @Override
    public void readBytes(byte[] buffer) throws IOException {
        dataInputStream.read(buffer);
    }

    @Override
    public void writeLine(String line) throws IOException {
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
        serverSocket.close();
    }
}
