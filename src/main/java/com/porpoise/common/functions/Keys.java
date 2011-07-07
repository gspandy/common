package com.porpoise.common.functions;

import java.util.Arrays;

import com.google.common.base.Function;

/**
 * Utility to create a
 */
public enum Keys {
    ;// uninstantiable

    /**
     * @param <T>
     * @param functions
     * @return a function which can create keys based on the given functions
     */
    public static <T> Function<T, Object> keyFunction(final Function<T, ?>... functions) {
        final Iterable<Function<T, ?>> functionCollection = Arrays.asList(functions);
        return keyFunction(functionCollection);
    }

    /**
     * @param functionCollection
     * @return a function which can create keys based on the given functions
     */
    public static <T> Function<T, Object> keyFunction(final Iterable<Function<T, ?>> functionCollection) {
        return new Function<T, Object>() {
            @Override
            public Object apply(final T input) {
                return new FixedHashKey<T>(functionCollection, input);
            }
        };
    }

}
