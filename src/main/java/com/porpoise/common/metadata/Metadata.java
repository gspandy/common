package com.porpoise.common.metadata;

import com.google.common.base.Function;

/**
 * @param <T>
 */
public interface Metadata<T, V> {
    /**
     * @return the accessor function
     */
    Function<T, V> accessor();

    /**
     * @return the property name
     */
    String propertyName();

    /**
     * This method may not have any effect, or even be supported. If {@link #isMutable()} returns true, then this method
     * will return true if the property has changed as a result of this call.
     * 
     * @param <V>
     * @param input
     *            The field holder -- an entity with a field represented by this metadata
     * @param newValue
     *            the new value
     * @return true if this method had any affect
     */
    boolean update(T input, V newValue);

    /**
     * @return true if the property represented by this field can also update its data
     */
    boolean isMutable();
}
