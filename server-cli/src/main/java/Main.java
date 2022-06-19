import communication.HttpServerService;
import sockets.IServerSocketService;
import sockets.ServerSocketService;
import tickets.TicketService;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = 80;
        IServerSocketService socketService = new ServerSocketService(port);
        TicketService ticketService = new TicketService();
        HttpServerService server = new HttpServerService(socketService, ticketService);

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

