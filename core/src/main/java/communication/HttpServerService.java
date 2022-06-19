package communication;

import fileinfo.Fileinfo;
import helpers.JsonConverterHelpers;
import models.HttpRequestHeader;
import models.HttpResponse;
import sockets.IServerSocketService;
import tickets.ITicketService;
import tickets.Ticket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpServerService {

    Integer count = 0;
    Map<Integer, Ticket> Tickets = new HashMap<Integer, Ticket>();

    IServerSocketService serverSocket;
    ITicketService ticketService;

    public HttpServerService(IServerSocketService serverSocket, ITicketService ticketService) {
        this.serverSocket = serverSocket;
        this.ticketService = ticketService;
    }

    public void run() throws IOException {
        serverSocket.open();
        HttpRequestHeader header = serverSocket.receiveRequestHeader();
        HttpResponse response = getResponse(header);
        serverSocket.sendResponseHeader(response.getHeader());
        serverSocket.sendResponseBody(response.getBody());
        serverSocket.close();
    }

    private HttpResponse getResponse(HttpRequestHeader header) throws IOException {
        try {
            if (header.getMethod().equals("POST") && header.getPath().equals("/tickets")) {
                System.out.println("Ticket request.");
                return ticketRequest(header);
            }
            else if (header.getMethod().equals("POST") && header.getPath().matches("/tickets/[\\w\\d]+")) {
                System.out.println("Send data.");
                return sendData(header);
            }
            else if (header.getMethod().equals("GET") && header.getPath().matches("/tickets/[\\w\\d]+")) {
                System.out.println("Receive data.");
                return receiveData(header);
            }
            else {
                System.out.println(String.format("Niezrozumiale polecenie: %s , %s", header.getMethod(), header.getPath()));
                return new HttpResponse(500, "Internal server error");
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return new HttpResponse(500, "Internal server error");
        }
    }

    private HttpResponse receiveData(HttpRequestHeader header) throws IOException {
        var url = header.getPath();
        Ticket ticket = ticketService.getTicket(url);
        if (ticket == null) return new HttpResponse(404, "Not found");
        byte[] body = ticketService.getDataFromTicket(url);
        var response = new HttpResponse(200, "OK", body);
        var filename = ticket.getFilename();
        response.getHeader().setValue("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
        return response;
    }

    private HttpResponse ticketRequest(HttpRequestHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.receiveRequestBody(buffer);
        Fileinfo fileinfo = JsonConverterHelpers.getFileinfoFromJson(buffer);
        Ticket ticketHttpResponse = ticketService.createTicketResponse(fileinfo);
        String json = JsonConverterHelpers.getTicketAsJson(ticketHttpResponse);
        System.out.printf("Wysyłanie ticketa: %s%n", json);
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        return new HttpResponse(201, "Created", body);
    }

    private HttpResponse sendData(HttpRequestHeader header) throws IOException {
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        serverSocket.receiveRequestBody(buffer);
        ticketService.sendDataToTicket(header.getPath(), buffer);
        return new HttpResponse(200, "OK");
    }
}

