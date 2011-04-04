package com.porpoise.common.delta;


public interface Diff<T> {

    T getFirst();

    T getSecond();

    String getPropertyName();
}
