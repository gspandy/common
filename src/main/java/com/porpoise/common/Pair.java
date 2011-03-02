package com.porpoise.common;


public class Pair<T1, T2> {

    private final T1 first;
    private final T2 second;

    public Pair(final T1 a, final T2 b) {
        first = a;
        second = b;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public static <J, K> Pair<J, K> valueOf(final J thingOne, final K thingTwo) {
        return new Pair<J, K>(thingOne, thingTwo);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (first == null ? 0 : first.hashCode());
        result = prime * result + (second == null ? 0 : second.hashCode());
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
        final Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }

}
