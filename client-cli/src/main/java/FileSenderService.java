import helpers.HttpHeaderConverter;
import helpers.JsonConverterHelpers;
import models.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class FileSenderService {
    private final String host;
    private final int port;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public FileSenderService(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

    }

    public void open() throws IOException {
        InetAddress address = InetAddress.getByName(host);
        var socket = new Socket(address, port);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
    }

    public void close() throws IOException {
        dataOutputStream.close();
        dataInputStream.close();
    }

    public boolean sendFile(String fileToSend) throws Exception {
        Fileinfo fileinfo = getFileinfo(fileToSend);
        System.out.printf("Wysyłanie pliku %s, rozmiar: %d B%n", fileinfo.getFilename(), fileinfo.getFilesize());
        Ticket ticket = requestTicket(fileinfo);
        System.out.printf("Uzyskano ticket. URL: %s%n", ticket.getUrl());
        return sendFile(fileToSend, ticket);
    }

    private boolean sendFile(String fileToSend, Ticket ticket) throws IOException {
        var url = ticket.getUrl();
        Path path = Paths.get(fileToSend);
        byte[] filebytes = Files.readAllBytes(path);
        HttpRequest request = new HttpRequest("POST", url, filebytes);

        open();
        sendRequest(request, dataOutputStream);
        HttpResponse response = receiveResponse(dataInputStream);
        close();

        return response.getHeader().getCode() == 200;
    }

    public Ticket requestTicket(Fileinfo fileinfo) throws Exception {
        String json = JsonConverterHelpers.getJson(fileinfo);
        HttpRequest request = new HttpRequest("POST", "/tickets", json);

        open();
        sendRequest(request, dataOutputStream);
        HttpResponse response = receiveResponse(dataInputStream);
        close();

        return JsonConverterHelpers.getJsonAsTicket(response.getBody());
    }

    void sendRequest(HttpRequest request, DataOutputStream dataOutputStream) throws IOException {
        var lines = HttpHeaderConverter.toRequestHeaderLines(request.getHeader());
        for (var line : lines) {
            dataOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
        }
        var body = request.getBody();
        if (body.length > 0) {
            dataOutputStream.write(request.getBody());
        }
    }

    private HttpResponse receiveResponse(DataInputStream dataInputStream) throws IOException {
        Collection<String> lines = HttpHeaderConverter.getHeaderLines(dataInputStream);
        HttpResponseHeader responseHeader = HttpHeaderConverter.getResponseHeader(lines);
        var contentLength = responseHeader.getContentLength();
        if (contentLength > 0) {
            byte[] bytes = new byte[contentLength];
            dataInputStream.read(bytes);
            dataInputStream.close();
            return new HttpResponse(responseHeader, bytes);
        }
        return new HttpResponse(responseHeader);
    }

    private Fileinfo getFileinfo(String fileToSend) throws IOException {
        Path path = Paths.get(fileToSend);
        String filename = path.getFileName().toFile().getName();
        long filesize = Files.size(path);
        return new Fileinfo(filename, filesize);
    }


}
