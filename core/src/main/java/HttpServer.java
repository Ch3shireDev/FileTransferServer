import helpers.JsonConverterHelpers;
import models.Fileinfo;
import models.HttpHeader;
import models.Ticket;
import sockets.IServerSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {

    Integer count = 0;
    Map<Integer, Ticket> Tickets = new HashMap<Integer, Ticket>();

    IServerSocket serverSocket;

    public HttpServer(IServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() throws IOException {

        serverSocket.accept();

        HttpHeader header = getHttpHeader();
        int contentLength = header.getContentLength();

        byte[] buffer = new byte[contentLength];
        serverSocket.readBytes(buffer);
        Fileinfo fileinfo = JsonConverterHelpers.getFileinfoFromJson(buffer);

        Ticket ticketHttpResponse = createTicketResponse(fileinfo);
        String json = JsonConverterHelpers.getTicketAsJson(ticketHttpResponse);

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        sendResponse(201, "Created", body);

        serverSocket.close();
    }

    private void sendResponse(int httpCode, String httpResponse, byte[] body) throws IOException {
        serverSocket.writeLine(String.format("HTTP/1.1 %d %s\r\n", httpCode, httpResponse));
        serverSocket.writeLine("Content-Type: application/binary\r\n");
        serverSocket.writeLine(String.format("Content-Length: %d\r\n", body.length));
        serverSocket.writeLine("\r\n");
        serverSocket.writeBytes(body);
    }

    private HttpHeader getHttpHeader() throws IOException {
        String line = serverSocket.readLine();
        if (line == null) throw new IOException("Błąd pobierania danych.");

        String[] parts = line.split("\s");

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        Map<String, String> values = new HashMap<>();
        Pattern pattern = Pattern.compile("(.*): (.*)");
        while (!(line = serverSocket.readLine()).isBlank()) {
            Matcher result = pattern.matcher(line);
            if (!result.find()) break;
            var key = result.group(1);
            var value = result.group(2);
            values.put(key, value);
            System.out.println(String.format("%s: %s", key, value));
        }

        return new HttpHeader(method, path, version, values);
    }

    private byte[] getFilebytes(String filename) throws IOException {
        Path path = Paths.get(filename);
        byte[] body = Files.readAllBytes(path);
        return body;
    }

    private void sendResponseFile(Integer httpCode, String httpResponse, byte[] filebytes, String filename) throws IOException {
        serverSocket.writeLine(String.format("HTTP/1.1 %d %s\r\n", httpCode, httpResponse));
        serverSocket.writeLine("Content-Type: application/binary\r\n");
        serverSocket.writeLine(String.format("Content-Length: %d\r\n", filebytes.length));
        serverSocket.writeLine(String.format("Content-Disposition: attachment; filename=\"%s\"\r\n", filename));
        serverSocket.writeLine("\r\n");
        serverSocket.writeBytes(filebytes);
    }

    private Ticket createTicketResponse(Fileinfo fileinfo) {
        int ticketCount = getNewTicketNumber();
        String url = String.format("/tickets/%d", ticketCount);
        String filename = fileinfo.getFilename();
        Integer filesize = fileinfo.getFilesize();
        return new Ticket(ticketCount, filename, filesize, url);
    }

    private Integer getNewTicketNumber() {
        count++;
        return count;
    }


}

