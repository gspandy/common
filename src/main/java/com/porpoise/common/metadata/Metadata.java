package com.porpoise.common.metadata;

import com.google.common.base.Function;

/**
 * @param <T>
 */
public interface Metadata<T> {
    /**
     * @return the accessor function
     */
    Function<T, ?> accessor();

    /**
     * @return the property name
     */
    String propertyName();
}
