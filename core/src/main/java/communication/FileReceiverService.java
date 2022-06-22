package communication;

import fileinfo.Filedata;
import models.HttpRequest;
import models.HttpResponse;
import models.HttpResponseHeader;
import sockets.IClientSocketService;

import java.io.IOException;

public class FileReceiverService {
    private final IClientSocketService clientSocketService;


    public FileReceiverService(IClientSocketService clientSocketService) {
        this.clientSocketService = clientSocketService;
    }

    public Filedata receiveFile(String url) throws Exception {
        HttpRequest request = new HttpRequest("GET", url);
        HttpResponse response = getResponse(request);
        if (!isSuccess(response)) throw new Exception("Błąd pobierania.");
        Filedata filedata = getFile(response);
        return filedata;
    }

    private Filedata getFile(HttpResponse response) throws Exception {
        var filename = response.getHeader().getFilename();
        var filebytes = response.getBody();
        return new Filedata(filename, filebytes);
    }

    private boolean isSuccess(HttpResponse response) {
        return response.getHeader().getCode() == 200;
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        clientSocketService.open();
        clientSocketService.sendRequestHeader(request.getHeader());
        HttpResponseHeader responseHeader = clientSocketService.receiveResponseHeader();
        int contentLength = responseHeader.getContentLength();
        byte[] responseBody = new byte[contentLength];
        clientSocketService.receiveResponseBody(responseBody);
        System.out.println("Zamykanie socketa klienta.");
        clientSocketService.close();
        System.out.println("Socket klienta zamknięty.");
        return new HttpResponse(responseHeader, responseBody);
    }
}

