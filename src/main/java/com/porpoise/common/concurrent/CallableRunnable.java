package com.porpoise.common.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @param <T>
 *            the callable type
 */
public final class CallableRunnable<T> implements Runnable {
    /** The callable being run */
    private final Callable<T> callable;

    /** The Future which will hold the return value */
    private final SettableFuture<T> future = SettableFuture.create();

    /**
     * @return the future which will be populated once run
     */
    public ListenableFuture<T> getFuture() {
        return this.future;
    }

    /**
     * Access restricted - use {@link Runnables#asRunnable(Callable)}
     * 
     * @param callableValue
     */
    CallableRunnable(final Callable<T> callableValue) {
        this.callable = checkNotNull(callableValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            final T result = this.callable.call();
            this.future.set(result);
        } catch (final Throwable e) {
            this.future.setException(e);
        }
    }

}