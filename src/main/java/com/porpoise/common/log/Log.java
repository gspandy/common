package com.porpoise.common.log;

import static org.apache.log4j.Logger.getLogger;

import java.util.Formatter;

import org.apache.log4j.Logger;

import com.porpoise.common.concurrent.Threads;

/**
 * Logging utility class. In addition to 'convenience methods' for formatted logging, this class also serves as an RCP
 * log listener
 * 
 * @author Aaron
 */
public class Log {
    private static final Log INSTANCE;

    private final Logger log;

    static {
        INSTANCE = new Log();
    }

    private Log() {
        this.log = getLogger(getClass());
    }

    /**
     * @return the singleton instance
     */
    public static Log getInstance() {
        return INSTANCE;
    }

    /**
     * Convenience method for logging at info level
     * 
     * @param c1ass
     * @param format
     * @param args
     */
    public static final void info(final Class<?> c1ass, final String format, final Object... args) {
        info(getLogger(c1ass), format, args);
    }

    /**
     * Log a message at info level
     * 
     * @param format
     *            a Message which may contain a format string (see {@link Formatter})
     * @param args
     *            the optional format arguments
     */
    public static final void info(final String format, final Object... args) {
        final Logger logger = INSTANCE.log;
        info(logger, format, args);
    }

    private static void info(final Logger logger, final String format, final Object... args) {
        if (logger.isInfoEnabled()) {
            final String message = getMessage(format, args);
            logger.info(message);
        }
    }

    /**
     * log at debug level
     * 
     * @param c1ass
     * @param format
     * @param args
     */
    public static void debug(final Class<?> c1ass, final String format, final Object... args) {
        debug(getLogger(c1ass), format, args);
    }

    /**
     * Log a message at debug level
     * 
     * @param format
     *            the message format string
     * @param args
     *            the format message arguments
     */
    public static void debug(final String format, final Object... args) {
        final Logger logger = INSTANCE.log;
        debug(logger, format, args);
    }

    /**
     * log at trace level
     * 
     * @param c1ass
     * @param format
     * @param args
     */
    public static void trace(final Class<?> c1ass, final String format, final Object... args) {
        trace(getLogger(c1ass), format, args);
    }

    /**
     * Log a message at trace level
     * 
     * @param format
     *            the message format string
     * @param args
     *            the format message arguments
     */
    public static void trace(final String format, final Object... args) {
        final Logger logger = INSTANCE.log;
        trace(logger, format, args);
    }

    /**
     * convenience method for logging at trace level
     * 
     * @param logger
     * @param format
     * @param args
     */
    private static void trace(final Logger logger, final String format, final Object... args) {
        if (logger.isTraceEnabled()) {
            final String message = getMessage(format, args);
            logger.trace(message);
        }
    }

    /**
     * convenience method for logging at debug level
     * 
     * @param logger
     * @param format
     * @param args
     */
    private static void debug(final Logger logger, final String format, final Object... args) {
        if (logger.isDebugEnabled()) {
            final String message = getMessage(format, args);
            logger.debug(message);
        } else {
            // TODO - remove
            trace(logger, format, args);
        }
    }

    /**
     * error
     * 
     * @param exception
     */
    public static void error(final Throwable exception) {
        error(exception.getMessage(), exception);
    }

    /**
     * log the given exception as an error
     * 
     * @param message
     * @param exception
     */
    public static final void error(final String message, final Throwable exception) {
        INSTANCE.log.error(message, exception);
    }

    /**
     * Log a message at error level
     * 
     * @param format
     *            a Message which may contain a format string (see {@link Formatter})
     * @param args
     *            the optional format arguments
     */
    public static final void error(final String format, final Object... args) {
        final String message = getMessage(format, args);
        INSTANCE.log.error(message);
    }

    /**
     * Log a message at warning level
     * 
     * @param format
     *            a Message which may contain a format string (see {@link Formatter})
     * @param args
     *            the optional format arguments
     */
    public static final void warn(final String format, final Object... args) {
        warn(INSTANCE.log, format, args);
    }

    private static void warn(final Logger logger, final String format, final Object... args) {
        final String message = getMessage(format, args);
        logger.warn(message);
    }

    private static String prependCaller(final String messageParam) {
        final String call = Threads.getFirstPriorCallAsString(Log.class);
        return String.format("%s: %s", call, messageParam);
    }

    /**
     * prepare a message for logging.
     * 
     * @param format
     * @param args
     * @return the formatted message
     */
    private static String getMessage(final String format, final Object... args) {
        final String message;
        if (args != null && args.length > 0) {
            message = String.format(format, args);
        } else {
            message = String.format(format);
        }
        return prependCaller(message);
    }

}