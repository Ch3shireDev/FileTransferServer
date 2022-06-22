package loggers;

public interface ILogger {
    void verbose(String message, Object... args);

    void info(String message, Object... args);

    void error(String message, Object... args);
}

