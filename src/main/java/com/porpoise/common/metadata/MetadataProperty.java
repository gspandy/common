package com.porpoise.common.metadata;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.porpoise.common.core.Pair;

/**
 * @param <T>
 */
public class MetadataProperty<T, V> {
    private final String propertyName;
    private final Metadata<T> metadata;
    private final Function<T, Pair<Metadata<V>, ?>> valueFunction;

    /**
     * @param md
     * @param prop
     * @param value
     */
    public MetadataProperty(final Metadata<T> md, final String prop, final Function<T, Pair<Metadata<V>, ?>> value) {
        this.propertyName = Preconditions.checkNotNull(prop);
        this.metadata = Preconditions.checkNotNull(md);
        this.valueFunction = value;
    }

    /**
     * @return the property name
     */
    public String name() {
        return this.propertyName;
    }

    /**
     * @return the property metadata
     */
    public Metadata<T> metadata() {
        return this.metadata;
    }

    public boolean isIterable() {
        return false;
    }

    public boolean isMap() {
        return false;
    }

    /**
     * @return the property value
     */
    public Pair<Metadata<V>, V> valueOf(final T input) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the property value
     */
    public Pair<Metadata<V>, Iterable<V>> iterableValueOf(final T input) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the property value
     */
    public <KEY> Pair<Metadata<V>, Map<KEY, V>> mappedValueOf(final T input) {
        throw new UnsupportedOperationException();
    }
}
