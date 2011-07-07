package com.porpoise.common.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.util.concurrent.Callables;

/**
 * The DelayedCache is an implementation of {@code AbstractCache}. The implementation of {@code #createValue(Object)}
 * does not compute missing values directly, but rather delegates their computation to a worker (a {@link Callable}
 * instance) and returns a default value immediately. Once the worker is complete, registered listeners are notified so
 * they can update their values.
 * 
 * @param <K>
 *            the key type (request type) for the cached value
 * @param <T>
 *            the value type
 */
public abstract class DelayedCache<K, T> extends AbstractCache<K, AtomicReference<T>> {
    /** collection of listeners who are notified when values are calculated */
    private final Collection<CallableListener<K, T>> listeners;

    /** The executor service used to start computation threads */
    private ExecutorService pool;

    /** once disposed, we should disallow use */
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    /** The default return value used when a value is not found in the cache */
    private T defaultValue = null;

    /**
     * @return the defaultValue
     */
    public T getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * @param value
     *            the defaultValue to set
     */
    public void setDefaultValue(final T value) {
        this.defaultValue = value;
    }

    /**
     * Default constructor
     */
    public DelayedCache() {
        this(Executors.newFixedThreadPool(1));
    }

    /**
     * @param threadPool
     *            the {@link ExecutorService} used to start worker threads
     */
    public DelayedCache(final ExecutorService threadPool) {
        this(10, TimeUnit.MINUTES, threadPool);
    }

    /**
     * @param timeout
     *            the cache value timeout
     * @param timeoutTimeUnit
     *            the time unit to apply to the timeout
     * @param threadPool
     */
    public DelayedCache(final int timeout, final TimeUnit timeoutTimeUnit, final ExecutorService threadPool) {
        this(newMapBuilder(timeout, timeoutTimeUnit), threadPool);
    }

    /**
     * create the cache using the given map maker
     * 
     * @param builder
     * @param threadPool
     */
    protected DelayedCache(final MapMaker builder, final ExecutorService threadPool) {
        super(builder);
        this.pool = Preconditions.checkNotNull(threadPool);
        this.listeners = new CopyOnWriteArraySet<CallableListener<K, T>>();
    }

    /**
     * Registers a callable listener.
     * 
     * @param listener
     *            the listener to register
     * @return true if the listener was added successfully
     */
    public boolean addListener(final CallableListener<K, T> listener) {
        checkDisposed();

        if (listener == null) {
            return false;
        }
        return this.listeners.add(listener);
    }

    private void checkDisposed() {
        if (isDisposed()) {
            throw new IllegalStateException("cache had been disposed");
        }
    }

    private boolean isDisposed() {
        return this.disposed.get();
    }

    /**
     * Unregisters a callable listener
     * 
     * @param listener
     *            the listener to register
     * @return true if the listener was added successfully
     */
    public boolean removeListener(final CallableListener<K, T> listener) {
        return this.listeners.remove(listener);
    }

    /**
     * 
     */
    public void dispose() {
        if (this.disposed.compareAndSet(false, true)) {
            this.listeners.clear();
            this.pool.shutdown();
        }
    }

    /**
     * The value for the given key was not found in the cache. Submit a background task to compute the value.
     * 
     * Once the task completes, a reference will be updated with the value and all listeners will be notified.
     * 
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.concurrent.AbstractCache#createValue(java.lang.Object)
     */
    @Override
    protected final AtomicReference<T> createValue(final K key) {
        checkDisposed();

        // create the reference placeholder. This will be updated by the worker task once complete
        final AtomicReference<T> reference = new AtomicReference<T>(getDefaultValue());

        // create a callback to notify any interested listeners once the value is set
        final KeyedListenableCallable<K, T> logic = new KeyedListenableCallable<K, T>(key, newCallable(key));

        // add OUR listener callback (the one which will update the reference)
        logic.addListener(new SetReferenceCallback<K, T>(reference));

        // also append any general listeners who have registered with the cache
        logic.addListeners(this.listeners);

        // finally submit our task to the thread pool.
        this.pool.submit(logic);

        // return the reference which later will be updated by the worker
        return reference;
    }

    /**
     * Create a worker task (Callable). The default implementation is simply to ask subclasses for the value directly
     * (effectively making the cache a Callable themselves).
     * 
     * subclasses may override this method to create a worker (callable) implementation to compute the value for the
     * given key
     * 
     * @param key
     *            the map key for which to compute a value
     * @return a callable which can compute the value for the given key
     */
    protected Callable<T> newCallable(final K key) {
        final boolean calculateInMainThread = false;
        if (calculateInMainThread) {
            return Callables.returning(computeValue(key));
        }
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                return computeValue(key);
            }
        };
    }

    /**
     * compute the value for the given key
     * 
     * @param key
     * @return the value for the given key
     */
    protected abstract T computeValue(final K key);

}