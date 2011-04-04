package com.porpoise.common.delta;

import static com.google.common.base.Preconditions.checkNotNull;

class SimpleDiff<T> extends AbstractDiff<T> {

    private final String propertyName;

    public SimpleDiff(final String name, final T left, final T right) {
        super(left, right);
        this.propertyName = checkNotNull(name);
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }
}
