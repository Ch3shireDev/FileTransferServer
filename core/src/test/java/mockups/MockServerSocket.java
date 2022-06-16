package mockups;

import sockets.IServerSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class MockServerSocket implements IServerSocket {

    LinkedList<String> requestHeaderLines = new LinkedList<>();
    LinkedList<String> responseHeaderLines = new LinkedList<>();

    LinkedList<Byte> requestBodyBytes = new LinkedList<Byte>();
    LinkedList<Byte> responseBodyBytes = new LinkedList<Byte>();

    public MockServerSocket() {
    }

    public void sendHeaderLine(String line) {
        requestHeaderLines.add(line);
    }

    public String receiveHeaderLine() {
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
    public void writeLine(String line) throws IOException {
        responseHeaderLines.add(line);
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

    public void sendBodyLine(String line) {
        var bytes = line.getBytes(StandardCharsets.UTF_8);
        for (var b : bytes) {
            requestBodyBytes.add(b);
        }
    }

    public String receiveBody() {
        Byte[] bytes = responseBodyBytes.toArray(Byte[]::new);
        byte[] array = new byte[bytes.length];
        for (var i = 0; i < bytes.length; i++) array[i] = bytes[i];
        return new String(array);
    }
}
