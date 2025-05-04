package it.renvins.serverpulse.common.logger;

public interface PulseLogger {

    void info(String message);
    void warning(String message);

    void error(String message);
    void error(String message, Throwable throwable);
}
