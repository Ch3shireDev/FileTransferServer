import sockets.DataServerSocket;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = 80;
        DataServerSocket socket = new DataServerSocket(port);
        HttpServer server = new HttpServer(socket);
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

