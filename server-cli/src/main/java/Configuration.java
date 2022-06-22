import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "FileTransferServer", version = "1.0")
public class Configuration {
    @Option(names = {"--port"}, description = "Port.")
    int port = 80;
    @Option(names = {"--verbose", "-v"}, description = "Sporo gada.")
    boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
