package com.porpoise.common.concurrent;

/**
 * Adapter for {@link CallableListener} implementations, allowing subclasses to provide a no-operation implementation
 * for method calls in which they are not interested
 * 
 * @param <K>
 *            the key type
 * 
 * @param <T>
 *            the listener type
 */
public class CallableListenerAdapter<K, T> implements CallableListener<K, T> {
    @Override
    public void onComplete(final K key, final T result) {
        // no-op
    }

    @Override
    public boolean onException(final K key, final Exception exp) {
        return false;
    }
}