package com.porpoise.common.concurrent;

/**
 * Adapter for {@link ICallableListener} implementations, allowing subclasses to provide a no-operation implementation
 * for method calls in which they are not interested
 * 
 * @param <T>
 */
public class CallableListenerAdapter<K, T> implements ICallableListener<K, T> {
    @Override
    public void onComplete(final K key, final T result) {
        // no-op
    }

    @Override
    public boolean onException(final K key, final Exception exp) {
        return false;
    }
}