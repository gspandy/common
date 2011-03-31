package com.porpoise.common.delta;

class IterableDiff<T> extends AbstractDiff<Iterable<T>> {

    private final int index;

    public IterableDiff(final int index, final Iterable<T> a, final Iterable<T> b) {
        super(a, b);
        this.index = index;
    }

    @Override
    @SuppressWarnings("boxing")
    public String getPropertyName() {
        return String.format("[%d]", this.index);
    }

}
