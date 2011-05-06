package com.porpoise.common.concurrent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

/**
 * Simple cache implementation which can use an optional expiration time and weak key/value references.
 * 
 * Should an object not exist in the cache, then its value will be computed via a call to {@link #createValue(Object)}
 * 
 * @param <K>
 *            the cache key type
 * @param <T>
 *            the cache value type
 * @author Aaron
 */
abstract class AbstractCache<K, T> {
    static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    static final int DEFAULT_TIMEOUT = 10;

    private final Map<K, T> cache;

    /**
     * Default constructor, specifying a default key expiration
     */
    public AbstractCache() {
        this(DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    /**
     * @param expiration
     *            the expiration used for map keys if the 'expirationTimeUnit' parameter is not null
     * @param expirationTimeUnit
     *            the time unit used for the key expiration. If null, no expiration will be set
     */
    public AbstractCache(final int expiration, final TimeUnit expirationTimeUnit) {
        this(newMapBuilder(expiration, expirationTimeUnit));
    }

    static MapMaker newMapBuilder(final int expiration, final TimeUnit expirationTimeUnit) {
        final MapMaker builder = new MapMaker().concurrencyLevel(15).softKeys().weakValues();
        if (expirationTimeUnit != null) {
            builder.expireAfterWrite(expiration, expirationTimeUnit);
        }
        return builder;
    }

    public AbstractCache(final MapMaker builder) {
        this.cache = builder.makeComputingMap(new Function<K, T>() {
            @Override
            public T apply(final K key) {
                return createValue(key);
            }
        });
    }

    /**
     * @param key
     *            a referenced key for which no value was given
     * @return a value for the given key
     */
    protected abstract T createValue(K key);

    /**
     * @param key
     *            the key to an item in the cache
     * @return the value for a given key
     */
    public T get(final K key) {
        return this.cache.get(key);
    }

    /**
     * @return a copy of the current cache as a map
     */
    public Map<K, T> asMap() {
        return Maps.newHashMap(this.cache);
    }

}