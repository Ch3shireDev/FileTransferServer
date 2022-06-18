package sockets;

import java.io.IOException;

public interface ISocketService {
    public void accept() throws IOException;

    public String readLine() throws IOException;

    public void readBytes(byte[] buffer) throws IOException;

    public void writeLine(String line) throws IOException;

    public void writeBytes(byte[] bytes) throws IOException;

    public void close() throws IOException;
}
