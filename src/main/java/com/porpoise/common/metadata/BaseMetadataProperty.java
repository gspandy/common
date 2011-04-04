package com.porpoise.common.metadata;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.porpoise.common.core.Pair;

/**
 * @param <T>
 */
public class BaseMetadataProperty<T> implements MetadataProperty<T> {
    private final String                            propertyName;
    private final Metadata<T>                       metadata;
    private final Function<T, Pair<Metadata<?>, ?>> valueFunction;

    /**
     * @param md
     * @param prop
     * @param value
     */
    public BaseMetadataProperty(final Metadata<T> md, final String prop, final Function<T, Pair<Metadata<?>, ?>> value) {
        this.propertyName = Preconditions.checkNotNull(prop);
        this.metadata = Preconditions.checkNotNull(md);
        this.valueFunction = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#name()
     */
    @Override
    public String name() {
        return this.propertyName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#metadata()
     */
    @Override
    public Metadata<T> metadata() {
        return this.metadata;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#isIterable()
     */
    @Override
    public boolean isIterable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#isMap()
     */
    @Override
    public boolean isMap() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#valueOf(T)
     */
    @Override
    public Pair<Metadata<?>, ?> valueOf(final T input) {
        // throw new UnsupportedOperationException();
        return this.valueFunction.apply(input);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.MetadataProperty#iterableValueOf(T)
     */
    @Override
    public <V> Pair<Metadata<V>, Iterable<V>> iterableValueOf(final T input) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <KEY, V> Pair<Metadata<V>, Map<KEY, V>> mappedValueOf(final T input) {
        throw new UnsupportedOperationException();
    }

}
