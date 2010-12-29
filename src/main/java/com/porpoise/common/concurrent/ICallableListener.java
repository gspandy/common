package com.porpoise.common.concurrent;

import java.util.concurrent.Callable;

/**
 * Listener interface to provide notification when a {@link Callable} completes
 * 
 * @author Aaron
 * @param <K>
 *            the key type
 * @param <T>
 *            the value type
 */
public interface ICallableListener<K, T> {
    /**
     * The callable has completed successfully, returning the given value
     * 
     * @param result
     *            the callback result
     */
    public void onComplete(K key, T result);

    /**
     * The callable threw an exception. If any registered listeners return true, then the exception will be rethrown.
     * Otherwise the callable will return null;
     * 
     * All listeners will always be called, regardless of return value.
     * 
     * @param exp
     * @return true if the exception should be propagated (rethrown), false otherwise
     */
    public boolean onException(K key, Exception exp);
}