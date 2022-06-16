import helpers.JsonConverterHelpers;
import mockups.MockServerSocket;
import models.Ticket;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;


class HttpServerTest {

    HttpServer server;
    MockServerSocket serverSocket;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        serverSocket = new MockServerSocket();
        server = new HttpServer(serverSocket);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    /**
     * Test dla następującego scenariusza:
     * 1. Alicja łączy się z Charliem, aby zgłosić wysyłanie pliku, wysyła dane pliku poprzez zapytanie HTTP `POST /`:
     * {"filename": "a.png","filesize": 213123}
     * W zamian uzyskuje ticket z informacją o parametrach transferu pliku (`201 Created`):
     * {"id": 123,"filename": "a.png","filesize": 213123,"url": "/123"}
     *
     * @throws IOException
     */
    @org.junit.jupiter.api.Test
    void aliceRequestTest() throws IOException {

        serverSocket.sendHeaderLine("POST / HTTP/1.1\r\n");
        serverSocket.sendHeaderLine("Content-Length: 38\r\n");
        serverSocket.sendHeaderLine("Content-Type:application/json\r\n");
        serverSocket.sendHeaderLine("\r\n");
        serverSocket.sendBodyLine("{\"filename\":\"a.png\",\"filesize\":213123}");

        server.run();

        String header = serverSocket.receiveHeaderLine();
        String body = serverSocket.receiveBody();

        Assertions.assertEquals("HTTP/1.1 201 Created\r\n", header);
        Ticket ticket= JsonConverterHelpers.getJsonAsTicket(body);

        Assertions.assertEquals("a.png", ticket.getFilename());
        Assertions.assertEquals(213123, ticket.getFilesize());

    }
}