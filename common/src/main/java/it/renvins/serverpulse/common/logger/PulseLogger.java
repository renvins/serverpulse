package it.renvins.serverpulse.common.logger;

/* This interface defines a simple logging mechanism for the ServerPulse modules.
 * It provides methods for logging informational messages, warnings, and errors.
 */
public interface PulseLogger {

    void info(String message);
    void warning(String message);

    void error(String message);
    void error(String message, Throwable throwable);
}
