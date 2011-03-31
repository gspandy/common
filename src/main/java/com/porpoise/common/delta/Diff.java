package com.porpoise.common.delta;

import java.util.Collection;

public interface Diff<T> {

    T getFirst();

    T getSecond();

    String getPropertyName();

    Collection<Diff<?>> childDiffs();
}
