package com.porpoise.common.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Borrowed from Scala, an Option is a type of "Monad". It serves as a container, and is used to replace potential null
 * values.
 * 
 * For instance, if a method returns a Long which can be null, that intention isn't obvious except perhaps by reading
 * the Javadoc.
 * 
 * If instead, however, it returns an Option<Long>, not only is it clear that that return value might not be a Long, but
 * also it is possible to call methods on the returned option (as the method now returns an object, not a null)
 * 
 * Use {@link Options#none()} and {@link Options#some(Object)} to represent no value (null) or a valid value,
 * respectively.
 * 
 */
public enum Options {
    ;// uninstantiable

    /**
     * The option interface as returned from "some" or "none"
     * 
     * @param <T>
     */
    public static interface Option<T> extends Iterable<T> {
        /**
         * @return the value represented by this option
         * @throws IllegalStateException
         */
        public T get() throws IllegalStateException;

        /**
         * @param defaultValue
         * @return the value represented by this option or the defaultValue parameter if not defined
         * @throws IllegalStateException
         */
        public T getOrElse(T defaultValue);

        /**
         * @return true if this option represents a value
         */
        public boolean isDefined();

        /**
         * @return false if this option is not defined
         */
        public boolean isNotDefined();
    }

    /**
     * @param <T>
     * @return the none option (no value)
     */
    @SuppressWarnings("synthetic-access")
    public static <T> Option<T> none() {
        return new None<T>();
    }

    /**
     * @param <T>
     * @param value
     *            the value to represent
     * @return a "some" value
     */
    public static <T> Option<T> some(final T value) {
        return new Some<T>(Preconditions.checkNotNull(value));
    }

    /**
     * value of
     * 
     * @param <T>
     * @param value
     * @return the option value
     */
    public static <T> Option<T> valueOf(final T value) {
        if (value == null) {
            return none();
        }
        return some(value);
    }

    /**
     * internal representation of none. Two none's are always equal, regardless of the type
     * 
     * @param <T>
     */
    private static final class None<T> implements Option<T> {
        private None() {
        }

        @Override
        public T get() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public int hashCode() {
            return 1234;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof None<?>;
        }

        @Override
        public T getOrElse(final T other) {
            return other;
        }

        @Override
        public boolean isNotDefined() {
            return true;
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<T> iterator() {
            final List<T> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }
    }

    /**
     * A "some" value wrapper
     * 
     * @param <T>
     */
    private static final class Some<T> implements Option<T> {
        private final T value;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (this.value == null ? 0 : this.value.hashCode());
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
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Some<?> other = (Some<?>) obj;
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!this.value.equals(other.value)) {
                return false;
            }
            return true;
        }

        public Some(final T value) {
            this.value = value;
        }

        @Override
        public T get() throws IllegalStateException {
            return this.value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public boolean isNotDefined() {
            return false;
        }

        @Override
        public T getOrElse(final T other) {
            return get() != null ? get() : other;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return this.hasNext;
                }

                @Override
                public T next() {
                    this.hasNext = false;
                    return get();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

}
