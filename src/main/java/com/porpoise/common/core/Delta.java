package com.porpoise.common.core;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * representation of the difference between two things
 * 
 * @param <T>
 */
public final class Delta<T> {

    private final static String NEW_LINE = String.format("%n");
    private final ImmutableMap<String, Diff<T, ?>> diffByPropertyName;
    private final ImmutableMap<String, Delta<?>> subDiffByPropertyName;
    private final Function<?, T> accessor;

    static class Diff<T, V> extends Pair<V, V> {
        private final Mutator<T, V> mutator;

        Diff(final V first, final V second, final Mutator<T, V> mutator) {
            super(first, second);
            this.mutator = mutator;
        }

        void update(final T value) {
            this.mutator.update(value, getSecond());
        }
    }

    /**
     * @param <T>
     */
    public static final class Builder<T> {
        private final Map<String, Diff<T, ?>> diffByPropertyName = Maps.newHashMap();
        private final Map<String, Delta.Builder<?>> subDiffs = Maps.newHashMap();
        private Function<?, T> accessor;

        public Delta<T> build() {
            final Map<String, Delta<?>> diffs = Maps.transformValues(this.subDiffs,
                    new Function<Builder<?>, Delta<?>>() {
                        @Override
                        public Delta<?> apply(final Builder<?> input) {
                            return input.build();
                        }
                    });
            return new Delta<T>(this.diffByPropertyName, diffs, this.accessor);
        }

        Builder<T> setAccessor(final Function<?, T> accessor) {
            this.accessor = accessor;
            return this;
        }

        /**
         * 
         * @param <V>
         * @param property
         * @param first
         * @param second
         * @param mutator
         * @return the builder
         */
        public <V> Builder<T> addDiff(final String property, final V first, final V second, final Mutator<T, V> mutator) {
            this.diffByPropertyName.put(property, new Diff<T, V>(first, second, mutator));
            return this;
        }

        public <V> Builder<T> addDiff(final String property, final int first, final int second,
                final Mutator<T, Integer> mutator) {
            return addDiff(property, Integer.valueOf(first), Integer.valueOf(second), mutator);
        }

        public <V> Builder<T> addDiff(final String property, final boolean first, final boolean second,
                final Mutator<T, Boolean> mutator) {
            return addDiff(property, Boolean.valueOf(first), Boolean.valueOf(second), mutator);
        }

        public <V> Builder<T> addDiff(final String property, final long first, final long second,
                final Mutator<T, Long> mutator) {
            return addDiff(property, Long.valueOf(first), Long.valueOf(second), mutator);
        }

        public <B, N> Builder<B> push(final String subProperty, final Function<N, B> accessor) {
            final Builder<B> newBuilder = Delta.newBuilder(accessor);
            this.subDiffs.put(subProperty, newBuilder);
            return newBuilder;
        }

        /**
         * @param string
         * @param gET_BIKES
         * @return
         */
        public <B, N> Builder<B> pushCollection(final String property,
                final Function<N, ? extends Collection<B>> accessor) {
            return null;
        }
    }

    /**
	 */
    Delta(final Map<String, Diff<T, ?>> diffs, final Map<String, Delta<?>> subDiffs,
            final Function<?, T> accessorFunction) {
        this.accessor = accessorFunction;
        this.diffByPropertyName = ImmutableMap.copyOf(diffs);
        this.subDiffByPropertyName = ImmutableMap.copyOf(subDiffs);
    }

    /**
     * @param accessor
     * @return
     */
    public static <B, N> Builder<B> newBuilder(final Function<N, B> accessor) {
        final Builder<B> builder = newBuilder();
        builder.setAccessor(accessor);
        return builder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString("");
    }

    /**
     * @param string
     * @return
     */
    private String toString(final String prefix) {
        final StringBuilder b = new StringBuilder();
        for (final Entry<String, Diff<T, ?>> entry : this.diffByPropertyName.entrySet()) {
            if (!prefix.isEmpty()) {
                b.append(prefix).append(".");
            }
            b.append(toString(entry.getKey(), entry.getValue())).append(NEW_LINE);
        }

        for (final Entry<String, Delta<?>> entry : this.subDiffByPropertyName.entrySet()) {
            final String newPrefix;
            if (!prefix.isEmpty()) {
                newPrefix = String.format("%s.%s", prefix, entry.getKey());
            } else {
                newPrefix = entry.getKey();
            }
            b.append(entry.getValue().toString(newPrefix)).append(NEW_LINE);
        }
        return b.toString();
    }

    /**
     * @param key
     * @param value
     * @return
     */
    private String toString(final String key, final Pair<?, ?> value) {
        return String.format("%s %s", key, value);
    }

    /**
     * @return a new builder
     */
    public static <T> Builder<T> newBuilder() {
        return new Builder<T>();
    }

    /**
     * @param garage
     * @return
     */
    public T update(final T value) {
        if (value == null) {
            return null;
        }
        for (final Diff<T, ?> diff : this.diffByPropertyName.values()) {
            diff.update(value);
        }

        for (final Delta<?> subDelta : this.subDiffByPropertyName.values()) {

        }

        return value;
    }
}
