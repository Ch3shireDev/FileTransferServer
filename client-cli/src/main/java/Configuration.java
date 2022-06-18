import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "FileTransferClient", version = "1.0")
public class Configuration {
    @Option(names = {"--host"}, description = "Adres hosta")
    String host = "http://localhost";
    @Option(names = {"-p", "--port"}, description = "Port")
    int port = 80;
    @Option(names = "--send", description = "Wyślij plik. Wymagana ścieżka do pliku.")
    String fileToSend;
    @Option(names = "--receive", description = "Odbierz plik. Wymagany adres pliku.")
    String receiveUrl;

    public String getReceiveUrl() {
        return receiveUrl;
    }

    public void setReceiveUrl(String receiveUrl) {
        this.receiveUrl = receiveUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFileToSend() {
        return fileToSend;
    }

    public void setFileToSend(String fileToSend) {
        this.fileToSend = fileToSend;
    }

}
