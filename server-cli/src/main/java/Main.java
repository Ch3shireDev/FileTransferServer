import communication.HttpServerService;
import picocli.CommandLine;
import tickets.TicketService;

import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws Exception {

        Configuration configuration = getConfiguration(args);
        int port = configuration.getPort();

        var socket = new ServerSocket(port);

        TicketService ticketService = new TicketService();

        System.out.printf("Serwer rozpoczął działanie. Port: %d\n", port);
        {
            try {
                while (true) {
                    new HttpServerService(socket.accept(), ticketService).start();
                }
            }
            finally {
                socket.close();
            }
        }
    }


    private static Configuration getConfiguration(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        CommandLine commandLine = new CommandLine(configuration);

        commandLine.parseArgs(args);
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            throw new Exception("Program zakończył działanie.");
        }
        else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            throw new Exception("Program zakończył działanie.");
        }

        return configuration;
    }
}

