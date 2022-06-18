import helpers.JsonConverterHelpers;
import mockups.MockSocketService;
import mockups.MockTicketService;
import models.Fileinfo;
import models.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class HttpServerTest {

    HttpServer server;
    MockSocketService serverSocket;
    MockTicketService ticketService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        serverSocket = new MockSocketService();
        ticketService = new MockTicketService();
        server = new HttpServer(serverSocket, ticketService);
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
    @Test
    void requestTicketTest() throws IOException {

        Assertions.assertEquals(0, ticketService.tickets.size());

        serverSocket.sendHeaderLine("POST /tickets HTTP/1.1\r\n");
        serverSocket.sendHeaderLine("Content-Length: 38\r\n");
        serverSocket.sendHeaderLine("Content-Type: application/json\r\n");
        serverSocket.sendHeaderLine("\r\n");
        serverSocket.sendBodyLine("{\"filename\":\"a.png\",\"filesize\":213123}");

        server.run();

        String header = serverSocket.receiveHeaderLine();
        String body = serverSocket.receiveBodyString();

        Assertions.assertEquals("HTTP/1.1 201 Created\r\n", header);
        Ticket ticket1 = JsonConverterHelpers.getJsonAsTicket(body);

        Assertions.assertEquals("a.png", ticket1.getFilename());
        Assertions.assertEquals(213123, ticket1.getFilesize());

        Assertions.assertEquals(1, ticketService.tickets.size());
        Ticket ticket2 = ticketService.tickets.stream().findFirst().get();

        Assertions.assertEquals("a.png", ticket2.getFilename());
        Assertions.assertEquals(213123, ticket2.getFilesize());

    }

    /**
     * 2. Alicja łączy się z Charliem, by wysłać plik poprzez zapytanie HTTP `POST /123` (url pliku z ticketa).
     * W ciele zapytania HTTP umieszcza binaria pliku. Połączenie będzie trwało tak długo, aż nie zostanie
     * przesłany cały plik. Na koniec Alicja dostanie odpowiedź z serwera. Jeśli wszystko poszło ok,
     * będzie to `201 Created`. Jeśli coś poszło nie tak, będzie to `400 Bad Request`.
     *
     * @throws IOException
     */
    @Test
    public void sendFileTest() throws IOException {
        ticketService.tickets.add(new Ticket("/123", new Fileinfo("a.png", 8)));

        serverSocket.sendHeaderLine("POST /tickets/123 HTTP/1.1\r\n");
        serverSocket.sendHeaderLine("Content-Length: 8\r\n");
        serverSocket.sendHeaderLine("Content-Type: application/json\r\n");
        serverSocket.sendHeaderLine("\r\n");

        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

        serverSocket.sendBodyLine(bytes);

        server.run();

        String header = serverSocket.receiveHeaderLine();
        byte[] body = serverSocket.receiveBodyBytes();

        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", header);
        Assertions.assertEquals(0, body.length);
    }

    /**
     * 1. Bob łączy się z Charliem, by odebrać od niego plik wskazany przez Alicję. Wysyła do Charliego
     * zapytanie HTTP `GET /123`. Jeśli faktycznie istnieje oczekujący na niego plik od Alicji,
     * otrzymuje wiadomość zwrotną `200 OK` wraz z nagłówkiem informującym o nazwie pliku oraz binariami tego pliku.
     * Jeśli coś pójdzie nie tak, Bob otrzymuje wiadomość `400 Bad Request`.
     *
     * @throws IOException
     */
    @Test
    public void receiveFileTest() throws IOException {
        Ticket ticket = new Ticket("/tickets/123", new Fileinfo("a.png", 8));
        ticketService.tickets.add(ticket);

        var bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

        ticketService.sendDataToTicket("/tickets/123", bytes);
        serverSocket.sendHeaderLine("GET /tickets/123 HTTP/1.1\r\n");
        serverSocket.sendHeaderLine("\r\n");

        server.run();

        String header = serverSocket.receiveHeaderLine();
        byte[] body = serverSocket.receiveBodyBytes();

        Assertions.assertEquals("HTTP/1.1 200 OK\r\n", header);
        Assertions.assertEquals(8, body.length);
        Assertions.assertArrayEquals(bytes, body);

    }

}