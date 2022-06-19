package communication;

import fileinfo.IFileinfoService;
import models.HttpRequest;
import models.HttpResponse;
import models.HttpResponseHeader;
import sockets.IClientSocketService;

import java.io.IOException;

public class FileReceiverService {
    private final IClientSocketService clientSocketService;
    private final IFileinfoService fileinfoService;


    public FileReceiverService(IClientSocketService clientSocketService, IFileinfoService fileinfoService) {
        this.clientSocketService = clientSocketService;
        this.fileinfoService = fileinfoService;
    }

    public boolean receiveFile(String url) throws Exception {
        HttpRequest request = new HttpRequest("GET", url);
        HttpResponse response = getResponse(request);
        if (!isSuccess(response)) return false;
        System.out.println("Zapisywanie pliku.");
        saveFile(response);
        System.out.println("Plik zapisany poprawnie.");
        return true;
    }

    private void saveFile(HttpResponse response) throws Exception {
        var filename = response.getHeader().getFilename();
        var filebytes = response.getBody();
        fileinfoService.writeFile(filename, filebytes);
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
