import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {

    private final int port;
    Integer count = 0;
    Map<Integer, Ticket> Tickets = new HashMap<Integer, Ticket>();

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server start.");

        while (true) {
            try {
                session(serverSocket);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void session(ServerSocket serverSocket) throws IOException {
        Socket clientSocket = serverSocket.accept();

        InputStream inputStream = clientSocket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        HttpHeader header = getHttpHeader(dataInputStream);
        int contentLength = header.getContentLength();
        var contentType = header.getContentType();
        var buffer = new byte[contentLength];

        dataInputStream.read(buffer);

        TicketHttpResponse ticketHttpResponse = createTicketResponse();

        OutputStream outputStream = clientSocket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

//        String body = getTicketAsJson(ticketHttpResponse);
        byte[] body = getFilebytes("a.jpg");
        sendResponseFile(200, "OK", body, "a.jpg", dataOutputStream);

        dataOutputStream.close();
        dataInputStream.close();
        inputStream.close();
        clientSocket.close();
    }

    private byte[] getFilebytes(String filename) throws IOException {
        Path path = Paths.get(filename);
        byte[] body = Files.readAllBytes(path);
        return body;
    }

    private void sendResponseFile(Integer httpCode, String httpResponse, byte[] filebytes, String filename, DataOutputStream bufferedWriter) throws IOException {
        bufferedWriter.write(String.format("HTTP/1.0 %d %s\r\n", httpCode, httpResponse).getBytes(StandardCharsets.UTF_8));
        bufferedWriter.write("Content-Type: application/binary\r\n".getBytes(StandardCharsets.UTF_8));
        bufferedWriter.write(String.format("Content-Length: %d\r\n", filebytes.length).getBytes(StandardCharsets.UTF_8));
        bufferedWriter.write(String.format("Content-Disposition: attachment; filename=\"%s\"\r\n", filename).getBytes(StandardCharsets.UTF_8));
        bufferedWriter.write("\r\n".getBytes(StandardCharsets.UTF_8));
        bufferedWriter.write(filebytes);
    }

    private TicketHttpResponse createTicketResponse() {
        int ticketCount = getNewTicketNumber();
        Ticket ticket = new Ticket(ticketCount);
        long currentTime = System.currentTimeMillis();
        return new TicketHttpResponse(ticket, currentTime);
    }

    private Integer getNewTicketNumber() {
        count++;
        return count;
    }

    private String getTicketAsJson(TicketHttpResponse ticketHttpResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper
                .writeValueAsString(ticketHttpResponse);
        String body = jsonInString;
        return body;
    }

    private HttpHeader getHttpHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) throw new IOException("Błąd pobierania danych.");

        String[] parts = line.split("\s");

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        Map<String, String> values = new HashMap<String, String>();
        Pattern pattern = Pattern.compile("(.*): (.*)");
        while ((line = bufferedReader.readLine()) != null) {
            Matcher result = pattern.matcher(line);
            if (!result.find()) break;
            var key = result.group(1);
            var value = result.group(2);
            values.put(key, value);
            System.out.println(String.format("%s: %s", key, value));
        }

        return new HttpHeader(method, path, version, values);
    }

    private HttpHeader getHttpHeader(DataInputStream dataInputStream) throws IOException {
        String line = dataInputStream.readLine();
        if (line == null) throw new IOException("Błąd pobierania danych.");

        String[] parts = line.split("\s");

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        Map<String, String> values = new HashMap<>();
        Pattern pattern = Pattern.compile("(.*): (.*)");
        while (!(line = dataInputStream.readLine()).isBlank()) {
            Matcher result = pattern.matcher(line);
            if (!result.find()) break;
            var key = result.group(1);
            var value = result.group(2);
            values.put(key, value);
            System.out.println(String.format("%s: %s", key, value));
        }

        return new HttpHeader(method, path, version, values);
    }
}

