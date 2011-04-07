package com.porpoise.common.metadata;

import com.google.common.base.Function;

/**
 */
public interface Metadata<T> {
    /**
     * @return the accessor function
     */
    Function<T, ?> accessor();

    String propertyName();
}
