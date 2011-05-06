package com.porpoise.common.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Set;

import com.google.common.collect.Sets;
import com.porpoise.common.log.Log;

/**
 * As only one uncaught exception handler is allowed, this handler allows delegate handlers to be registered with it.
 * This way new uncaught exception handlers may be registered without having to replace others.
 * 
 * @author Aaron
 */
public class DelegatingUncaughtHandler implements UncaughtExceptionHandler {
    private final Set<UncaughtExceptionHandler> handlers;

    /**
     * clients of this class should use {@link #initialise()}
     */
    private DelegatingUncaughtHandler() {
        this.handlers = Sets.newHashSet();
    }

    /**
     * @return the DelegatingUncaughtExceptionHandler
     */
    public static DelegatingUncaughtHandler initialise() {
        final UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        // if already initialised, then we don't have to do anything further
        final DelegatingUncaughtHandler delegator;
        if (handler instanceof DelegatingUncaughtHandler) {
            delegator = (DelegatingUncaughtHandler) handler;
        } else {
            delegator = new DelegatingUncaughtHandler();
            Thread.setDefaultUncaughtExceptionHandler(delegator);
            delegator.addHandler(handler);
        }
        return delegator;
    }

    /**
     * add an uncaught exception handler
     * 
     * @param handler
     * @return true if the handler was succesfully registered, false otherwise
     */
    public boolean addHandler(final UncaughtExceptionHandler handler) {
        if (handler == null) {
            return false;
        }
        return this.handlers.add(handler);

    }

    /**
     * @return a new UncaughtExceptionHandler which Log4J the exception
     */
    public static UncaughtExceptionHandler loggingHandler() {
        return new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable error) {
                Log.error("Uncaught exception %s on thread %s", error, thread.getName());
            }
        };
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable error) {
        for (final UncaughtExceptionHandler handler : this.handlers) {
            handler.uncaughtException(thread, error);
        }
    }

}