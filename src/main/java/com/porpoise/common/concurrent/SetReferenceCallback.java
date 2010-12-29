package com.porpoise.common.concurrent;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;
import com.porpoise.common.Log4J;

/**
 * Callback which will update an atomic reference once complete
 * 
 * @author Aaron
 * @param <T>
 */
class SetReferenceCallback<K, T> extends CallableListenerAdapter<K, T> {
    private final AtomicReference<T> reference;

    private final T                  originalValue;

    public SetReferenceCallback(final AtomicReference<T> ref) {
        this.reference = Preconditions.checkNotNull(ref);
        originalValue = ref.get();
    }

    @Override
    public void onComplete(final K key, final T result) {
        final boolean success = reference.compareAndSet(originalValue, result);
        if (!success) {
            Log4J.debug("Couldn not set '%s' reference to '%s' as the value had been altered since instantiation", key,
                    result);
        }
    }

}