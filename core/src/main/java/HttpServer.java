import helpers.JsonConverterHelpers;
import models.Fileinfo;
import models.HttpHeader;
import models.HttpMethod;
import models.Ticket;
import sockets.ISocketService;
import tickets.ITicketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {

    Integer count = 0;
    Map<Integer, Ticket> Tickets = new HashMap<Integer, Ticket>();

    ISocketService serverSocket;
    ITicketService ticketService;

    public HttpServer(ISocketService serverSocket, ITicketService ticketService) {
        this.serverSocket = serverSocket;
        this.ticketService = ticketService;
    }

    public void run() throws IOException {
        serverSocket.accept();
        HttpHeader header = getHttpHeader();
        sendResponse(header);
        serverSocket.close();
    }

    private void sendResponse(HttpHeader header) throws IOException {
        try {
            if (header.getMethod() == HttpMethod.POST && Objects.equals(header.getPath(), "/tickets")) {
                ticketRequest(header);
            }
            else if (header.getMethod() == HttpMethod.POST && header.getPath().matches("/tickets/[\\w\\d]+")) {
                sendData(header);
            }
            else if (header.getMethod() == HttpMethod.GET && header.getPath().matches("/tickets/[\\w\\d]+")) {
                receiveData(header);
            }
        }
        catch (Exception e) {
            sendResponse(500, "Internal server error");
        }
    }

    private void receiveData(HttpHeader header) throws IOException {
        var ticketJson = header.getValue("Ticket");
        var url = header.getPath();
        Ticket ticket = ticketService.getTicket(url);
        byte[] body = ticketService.getDataFromTicket(url);
        sendResponseFile(200, "OK", body, ticket.getFilename());
    }

    private void ticketRequest(HttpHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.readBytes(buffer);
        Fileinfo fileinfo = JsonConverterHelpers.getFileinfoFromJson(buffer);
        Ticket ticketHttpResponse = ticketService.createTicketResponse(fileinfo);
        String json = JsonConverterHelpers.getTicketAsJson(ticketHttpResponse);

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        sendResponse(201, "Created", body);
    }

    private void sendData(HttpHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.readBytes(buffer);
        ticketService.sendDataToTicket(header.getPath(), buffer);
        sendResponse(200, "OK");
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
        }

        return new HttpHeader(method, path, version, values);
    }

    private void sendResponse(int httpCode, String httpResponse) throws IOException {
        sendResponse(httpCode, httpResponse, new byte[0]);
    }

    private void sendResponse(int httpCode, String httpResponse, byte[] body) throws IOException {
        serverSocket.writeLine(String.format("HTTP/1.1 %d %s\r\n", httpCode, httpResponse));
        serverSocket.writeLine("Content-Type: application/binary\r\n");
        if (body.length > 0) serverSocket.writeLine(String.format("Content-Length: %d\r\n", body.length));
        serverSocket.writeLine("\r\n");
        serverSocket.writeBytes(body);
    }

    private void sendResponseFile(Integer httpCode, String httpResponse, byte[] filebytes, String filename) throws IOException {
        serverSocket.writeLine(String.format("HTTP/1.1 %d %s\r\n", httpCode, httpResponse));
        serverSocket.writeLine("Content-Type: application/binary\r\n");
        serverSocket.writeLine(String.format("Content-Length: %d\r\n", filebytes.length));
        serverSocket.writeLine(String.format("Content-Disposition: attachment; filename=\"%s\"\r\n", filename));
        serverSocket.writeLine("\r\n");
        serverSocket.writeBytes(filebytes);
    }


}

