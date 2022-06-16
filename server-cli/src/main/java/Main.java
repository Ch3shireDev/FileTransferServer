import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 80;
        HttpServer server = new HttpServer(port);
        server.start();
    }


}

