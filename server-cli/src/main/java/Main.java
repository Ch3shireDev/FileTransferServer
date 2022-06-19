import sockets.SocketService;
import tickets.TicketService;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = 80;
        SocketService socketService = new SocketService(port);
        TicketService ticketService = new TicketService();
        HttpServer server = new HttpServer(socketService, ticketService);

        System.out.println("Serwer rozpoczął działanie.");
        while (true) {
            try {
                server.run();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

