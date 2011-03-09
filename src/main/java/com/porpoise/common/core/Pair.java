package com.porpoise.common.core;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class Pair<T1, T2> {

    /**
     * @param <A>
     *            the first type
     * @param <B>
     *            the second type
     * @return a predicate which returns true if the pair contains values which differ according to
     *         {@link Object#equals(Object)}
     */
    public static final <A, B> Predicate<Pair<A, B>> different() {
        final Predicate<Pair<A, B>> same = same();
        return Predicates.not(same);
    }

    /**
     * @param <A>
     *            the first type
     * @param <B>
     *            the second type
     * @return a predicate which returns true if the pair contains values which are the same according to
     *         {@link Object#equals(Object)}
     */
    public static final <A, B> Predicate<Pair<A, B>> same() {
        return new Predicate<Pair<A, B>>() {
            @Override
            public boolean apply(final Pair<A, B> input) {
                return Objects.equal(input.getFirst(), input.getSecond());
            }
        };
    }

    private final T1 first;
    private final T2 second;

    public Pair(final T1 a, final T2 b) {
        this.first = a;
        this.second = b;
    }

    public T1 getFirst() {
        return this.first;
    }

    public T2 getSecond() {
        return this.second;
    }

    public static <J, K> Pair<J, K> valueOf(final J thingOne, final K thingTwo) {
        return new Pair<J, K>(thingOne, thingTwo);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.first == null ? 0 : this.first.hashCode());
        result = prime * result + (this.second == null ? 0 : this.second.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (this.first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!this.first.equals(other.first)) {
            return false;
        }
        if (this.second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!this.second.equals(other.second)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("[%s,%s]", getFirst(), getSecond());
    }
}
