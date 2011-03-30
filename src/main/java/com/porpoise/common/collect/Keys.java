package com.porpoise.common.collect;

import java.util.Arrays;

import com.google.common.base.Function;

/**
 * 
 */
public enum Keys {
    ;// uninstantiable

    /**
     * @param <T>
     * @param functions
     * @return a function which can create keys based on the given functions
     */
    public static <T> Function<T, Object> keyFunction(final Function<T, ?>... functions) {
        return new Function<T, Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object apply(final T input) {
                return new FixedHashKey<T>(Arrays.asList(functions), input);
            }
        };
    }
}
