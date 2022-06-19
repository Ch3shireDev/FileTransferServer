import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "FileTransferClient", version = "1.0")
public class Configuration {
    @Option(names = {"--host"}, description = "Adres hosta.")
    String host = "localhost";
    @Option(names = {"--port"}, description = "Port.")
    int port = 80;
    @Option(names = "--send", description = "Tryb wysylania pliku. Wymagana sciezka do pliku.")
    String fileToSend = "";
    @Option(names = "--receive", description = "Tryb odbierania pliku. Wymagany adres pliku.")
    String receiveUrl = "";

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

    public boolean isSendFile() {
        return !fileToSend.isBlank() && receiveUrl.isBlank();
    }

    public boolean isReceiveFile() {
        return fileToSend.isBlank() && !receiveUrl.isBlank();
    }
}
