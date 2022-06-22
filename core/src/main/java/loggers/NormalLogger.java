package loggers;

public class NormalLogger implements ILogger {

    @Override
    public void verbose(String message, Object... args) {
        System.out.printf(message, args);
        System.out.println();
    }

    @Override
    public void info(String message, Object... args) {
        System.out.printf(message, args);
        System.out.println();
    }

    @Override
    public void error(String message, Object... args) {

        System.err.printf(message, args);
        System.err.println();
    }
}
