import communication.FileReceiverService;
import communication.FileSenderService;
import fileinfo.FileinfoService;
import fileinfo.IFileinfoService;
import picocli.CommandLine;
import sockets.ClientSocketService;
import sockets.IClientSocketService;


public class Main {

    public static void main(String[] args) {
        try {

            Configuration configuration = getConfiguration(args);

            if (configuration.isSendFile()) {
                String fileToSend = configuration.getFileToSend();
                System.out.printf("Otwieranie połączenia. Host: %s, port: %d%n", configuration.getHost(), configuration.getPort());
                var result = new FileSenderService(configuration.getHost(), configuration.getPort()).sendFile(fileToSend);
                if (result) System.out.println("Wysyłanie pliku zakończone sukcesem.");
                else System.err.println("Błąd podczas wysyłania pliku.");

            }
            else if (configuration.isReceiveFile()) {
                IClientSocketService clientSocketService = new ClientSocketService(configuration.getHost(), configuration.getPort());
                IFileinfoService fileinfoService = new FileinfoService();
                var result = new FileReceiverService(clientSocketService, fileinfoService).receiveFile(configuration.receiveUrl);
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static Configuration getConfiguration(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        CommandLine commandLine = new CommandLine(configuration);

        if (args.length == 0) {
            commandLine.usage(System.out);
            throw new Exception("Program zakończył działanie.");
        }

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

