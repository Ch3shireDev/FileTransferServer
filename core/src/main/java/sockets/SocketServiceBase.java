package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class SocketServiceBase {

    protected DataInputStream dataInputStream;
    protected DataOutputStream dataOutputStream;

    protected String readLine() throws IOException {
        String line = dataInputStream.readLine();
//        System.out.printf("Read line: %s\n", line);
        return line;
    }

    protected Collection<String> readLines() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = readLine()) != null) {
            if (line.isBlank()) break;
            lines.add(line);
        }
        return lines;
    }

    protected void writeLine(String line) throws IOException {
//        System.out.printf("Write line: %s", line);
        dataOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
    }

    protected void writeLines(Collection<String> lines) throws IOException {
        for (String line : lines) {
            writeLine(line);
        }
    }
}
