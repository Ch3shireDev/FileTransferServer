import helpers.HttpHeaderConverter;
import helpers.JsonConverterHelpers;
import models.Fileinfo;
import models.HttpRequestHeader;
import models.HttpResponse;
import models.Ticket;
import sockets.ISocketService;
import tickets.ITicketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        HttpRequestHeader header = getHttpHeader();
        sendResponse(header);
        serverSocket.close();
    }

    HttpRequestHeader getHttpHeader() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!(line = serverSocket.readLine()).isBlank()) {
            lines.add(line);
        }
        return HttpHeaderConverter.getHttpHeader(lines);
    }

    private void sendResponse(HttpRequestHeader header) throws IOException {
        try {
            if (header.getMethod().equals("POST") && header.getPath().equals("/tickets")) {
                ticketRequest(header);
            }
            else if (header.getMethod().equals("POST") && header.getPath().matches("/tickets/[\\w\\d]+")) {
                sendData(header);
            }
            else if (header.getMethod().equals("GET") && header.getPath().matches("/tickets/[\\w\\d]+")) {
                receiveData(header);
            }
        }
        catch (Exception e) {
            sendResponse(500, "Internal server error");
        }
    }

    private void receiveData(HttpRequestHeader header) throws IOException {
        var ticketJson = header.getValue("Ticket");
        var url = header.getPath();
        Ticket ticket = ticketService.getTicket(url);
        byte[] body = ticketService.getDataFromTicket(url);
        sendResponseFile(200, "OK", body, ticket.getFilename());
    }

    private void ticketRequest(HttpRequestHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.readBytes(buffer);
        Fileinfo fileinfo = JsonConverterHelpers.getFileinfoFromJson(buffer);
        Ticket ticketHttpResponse = ticketService.createTicketResponse(fileinfo);
        String json = JsonConverterHelpers.getTicketAsJson(ticketHttpResponse);

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        sendResponse(201, "Created", body);
    }

    private void sendData(HttpRequestHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.readBytes(buffer);
        ticketService.sendDataToTicket(header.getPath(), buffer);
        sendResponse(200, "OK");
    }

    private void sendResponse(int httpCode, String httpResponse) throws IOException {
        var response = new HttpResponse(httpCode, httpResponse);
        serverSocket.sendResponseHeader(response.getHeader());
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

