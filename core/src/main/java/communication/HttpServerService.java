package communication;

import fileinfo.Fileinfo;
import helpers.JsonConverterHelpers;
import models.HttpRequestHeader;
import models.HttpResponse;
import sockets.IServerSocketService;
import sockets.ServerSocketService;
import tickets.ITicketService;
import tickets.Ticket;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServerService extends Thread {

    IServerSocketService serverSocket;
    ITicketService ticketService;

    public HttpServerService(IServerSocketService serverSocket, ITicketService ticketService) {
        this.serverSocket = serverSocket;
        this.ticketService = ticketService;
    }

    public HttpServerService(Socket socket, ITicketService ticketService){
        this.serverSocket = new ServerSocketService(socket);
        this.ticketService = ticketService;
    }

    public void run() {
        try {
            serverSocket.open();
            System.out.println("Receiving header");
            HttpRequestHeader header = serverSocket.receiveRequestHeader();
            System.out.printf("Creating response for %s\n", header.getPath());
            HttpResponse response = getResponse(header);
            System.out.printf("Sending response header for %s\n",header.getPath());
            serverSocket.sendResponseHeader(response.getHeader());
            System.out.printf("Sending response body for %s\n",header.getPath());
            serverSocket.sendResponseBody(response.getBody());
            System.out.printf("Connection end for %s\n",header.getPath());
            serverSocket.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
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
                System.err.printf("Niezrozumiale polecenie: %s , %s%n", header.getMethod(), header.getPath());
                return new HttpResponse(500, "Internal server error");
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return new HttpResponse(500, "Internal server error");
        }
    }

    private HttpResponse receiveData(HttpRequestHeader header) {
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
        Ticket ticketHttpResponse = getTicket(fileinfo);
        String json = JsonConverterHelpers.getTicketAsJson(ticketHttpResponse);
        System.out.printf("Wysyłanie ticketa: %s%n", json);
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        return new HttpResponse(201, "Created", body);
    }

    private Ticket getTicket(Fileinfo fileinfo) {
        Ticket ticketHttpResponse;

        synchronized (ticketService) {
            ticketHttpResponse = ticketService.createTicketResponse(fileinfo);
        }
        return ticketHttpResponse;
    }

    private HttpResponse sendData(HttpRequestHeader header) throws Exception {
        System.out.printf("Send data start for %s.\n", header.getPath());
        int contentLength = header.getContentLength();
        byte[] buffer = new byte[contentLength];
        //sleep(10, 1000, "Sending... %d "+header.getPath()+"\n");
        System.out.printf("Receiving request body for %s.\n",header.getPath());
        serverSocket.receiveRequestBody(buffer);
        System.out.printf("Request body received for %s.\n", header.getPath());
        synchronized (ticketService) {
            System.out.printf("Sending data to ticket: %s\n", header.getPath());
            ticketService.sendDataToTicket(header.getPath(), buffer);
            System.out.printf("Sending data to ticket %s succeeded\n", header.getPath());
        }
        System.out.printf("Send data end for %s\n", header.getPath());
        return new HttpResponse(200, "OK");
    }

    private void sleep(int count, int intervals, String message) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            Thread.sleep(intervals);
            System.out.printf(message, i);
        }
    }
}

