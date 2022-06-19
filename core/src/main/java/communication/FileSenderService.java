package communication;

import fileinfo.Fileinfo;
import fileinfo.FileinfoService;
import fileinfo.IFileinfoService;
import helpers.JsonConverterHelpers;
import models.HttpRequest;
import models.HttpResponseHeader;
import sockets.ClientSocketService;
import sockets.IClientSocketService;
import tickets.Ticket;

import java.io.IOException;


public class FileSenderService {
    private final IFileinfoService fileinfoService;
    IClientSocketService clientSocketService;

    public FileSenderService(String host, int port) {
        clientSocketService = new ClientSocketService(host, port);
        fileinfoService = new FileinfoService();
    }

    public FileSenderService(IClientSocketService clientSocketService, IFileinfoService fileinfoService) {
        this.clientSocketService = clientSocketService;
        this.fileinfoService = fileinfoService;
    }

    public boolean sendFile(String fileToSend) throws Exception {
        Fileinfo fileinfo = fileinfoService.getFileinfo(fileToSend);
        if (fileinfo.getFilesize() == 0)
            throw new Exception("Plik ma zerową długosć. Prawdopodobnie plik nie istnieje.");
        System.out.printf("Wysyłanie pliku %s, rozmiar: %d B%n", fileinfo.getFilename(), fileinfo.getFilesize());
        Ticket ticket = requestTicket(fileinfo);
        System.out.printf("Uzyskano ticket. URL: %s%n", ticket.getUrl());
        return sendFile(fileToSend, ticket);
    }

    private Ticket requestTicket(Fileinfo fileinfo) throws Exception {
        String json = JsonConverterHelpers.getJson(fileinfo);
        HttpRequest request = new HttpRequest("POST", "/tickets", json);

        clientSocketService.open();
        clientSocketService.sendRequestHeader(request.getHeader());
        clientSocketService.sendRequestBody(request.getBody());
        HttpResponseHeader response = clientSocketService.receiveResponseHeader();
        int contentLength = response.getContentLength();
        byte[] body = new byte[contentLength];
        clientSocketService.receiveResponseBody(body);
        clientSocketService.close();

        return JsonConverterHelpers.getJsonAsTicket(body);
    }

    private boolean sendFile(String fileToSend, Ticket ticket) throws IOException {
        HttpRequest request = getSendFileRequest(fileToSend, ticket);

        clientSocketService.open();
        clientSocketService.sendRequestHeader(request.getHeader());
        clientSocketService.sendRequestBody(request.getBody());
        HttpResponseHeader responseHeader = clientSocketService.receiveResponseHeader();
        clientSocketService.close();

        return isSuccess(responseHeader);
    }

    private boolean isSuccess(HttpResponseHeader responseHeader) {
        return responseHeader.getCode() == 200;
    }

    private HttpRequest getSendFileRequest(String fileToSend, Ticket ticket) throws IOException {
        return new HttpRequest("POST", ticket.getUrl(), fileinfoService.getFilebytes(fileToSend));
    }


}

