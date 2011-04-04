package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

/**
 * @param <T>
 * @param <V>
 */
public interface MetadataProperty<T> {

    /**
     * @return the property name
     */
    public abstract String name();

    /**
     * @return the property metadata
     */
    public abstract Metadata<T> metadata();

    public abstract boolean isIterable();

    public abstract boolean isMap();

    /**
     * @param input
     * @return the property value
     */
    public abstract Pair<Metadata<?>, ?> valueOf(final T input);

    /**
     * @param input
     * @return the property value
     */
    public <V> Pair<Metadata<V>, Iterable<V>> iterableValueOf(final T input);

    /**
     * @param input
     * @param <KEY>
     * @return the property value
     */
    public abstract <KEY, V> Pair<Metadata<V>, Map<KEY, V>> mappedValueOf(final T input);

}