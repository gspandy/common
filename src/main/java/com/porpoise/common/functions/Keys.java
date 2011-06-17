package com.porpoise.common.functions;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
     * Convenience method for creating key functions from at least two functions
     * 
     * @param <T>
     * @param first
     * @param second
     * @param functions
     * @return
     */
    public static <T> Function<T, Object> keyFunction(final Function<T, ?> first, final Function<T, ?> second,
            final Function<T, ?>... functions) {
        final Collection<Function<T, ? extends Object>> all = Lists.newArrayList();
        all.add(first);
        all.add(second);
        all.addAll(Arrays.asList(functions));
        return keyFunction(all);
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
