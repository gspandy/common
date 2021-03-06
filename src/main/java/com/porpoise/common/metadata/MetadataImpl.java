package com.porpoise.common.metadata;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Implementation of the metadata interface
 * 
 * @param <T>
 *            the type which this metadata represents
 * @param <V>
 *            the type of the value returned by the accessor function
 */
public class MetadataImpl<T, V> implements Metadata<T, V> {

    private final String name;
    private final Function<T, V> accessor;
    private final boolean mutable;

    /**
     * Constructor
     * 
     * @param name
     * @param accessor
     */
    public MetadataImpl(final String name, final Function<T, V> accessor) {
        this(name, accessor, false);
    }

    /**
     * Constructor
     * 
     * @param name
     * @param accessor
     * @param mutable
     */
    public MetadataImpl(final String name, final Function<T, V> accessor, final boolean mutable) {
        this.mutable = mutable;
        this.name = Preconditions.checkNotNull(name);
        this.accessor = Preconditions.checkNotNull(accessor);
    }

    /**
     * @return the accessor function
     */
    @Override
    public Function<T, V> accessor() {
        return this.accessor;
    }

    /**
     * @return the property name
     */
    @Override
    public String propertyName() {
        return this.name;
    }

    /**
     * @param input
     * @param newValue
     * @return true if this method had any affect
     */
    @Override
    public boolean update(final T input, final V newValue) {
        return false;
    }

    /**
     * @return true if this metadata can also update its data
     */
    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name;
    }
}
