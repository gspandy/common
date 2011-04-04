package com.porpoise.common.delta;

import com.porpoise.common.core.Pair;

abstract class AbstractDiff<T> extends Pair<T, T> implements Diff<T> {

    public AbstractDiff(final T a, final T b) {
        super(a, b);
    }
}
