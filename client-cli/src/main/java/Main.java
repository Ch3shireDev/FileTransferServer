import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        CommandLine commandLine = new CommandLine(configuration);
        commandLine.parseArgs(args);
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }
    }
}

