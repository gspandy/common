package com.porpoise.common.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Lists;

/**
 * A {@link Callable} which delegates to another callable. Listeners can register for notification of the underlying
 * delegate's completion, whether successful or in the event of an exception.
 * 
 * @author Aaron
 * @param <K>
 *            the key type. The 'key' is associated with the event which invoked the callable
 * @param <T>
 *            the value type
 */
class KeyedListenableCallable<K, T> implements Callable<T> {
    private final Collection<ICallableListener<K, T>> listeners;

    private final Callable<T>                         delegate;

    private final K                                   key;

    public KeyedListenableCallable(final K keyParam, final Callable<T> newCallable) {
        this.key = checkNotNull(keyParam);
        this.delegate = checkNotNull(newCallable);
        listeners = new CopyOnWriteArrayList<ICallableListener<K, T>>();
    }

    /**
     * Add the given callback listeners
     * 
     * @param completeCallbacks
     */
    public void addListener(final ICallableListener<K, T> callback) {
        if (callback != null) {
            listeners.add(callback);
        }
    }

    public void addListeners(final Iterable<ICallableListener<K, T>> callbackIterable) {
        if (callbackIterable != null) {
            listeners.addAll(Lists.newArrayList(callbackIterable));
        }
    }

    @Override
    public T call() throws Exception {
        /*
         * Call the delegate callback
         */
        T result = null;
        Exception exception = null;
        try {
            result = this.delegate.call();
        } catch (final Exception e) {
            exception = e;
        }

        /*
         * Notify the listeners of either success or the exception
         */
        if (exception == null) {
            for (final ICallableListener<K, T> callback : listeners) {
                callback.onComplete(key, result);
            }
        } else {
            boolean rethrow = false;
            for (final ICallableListener<K, T> callback : listeners) {
                final boolean doThrow = callback.onException(key, exception);
                rethrow = rethrow || doThrow;
            }
            if (rethrow) {
                throw exception;
            }
        }
        return result;
    }
}