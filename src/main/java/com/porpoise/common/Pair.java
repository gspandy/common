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

}
