package com.porpoise.common.metadata;

public class IterableDelta<P> extends Delta<P> {

    private final int index;

    public IterableDelta(final Metadata<?> prop, final int index, final P left, final P right) {
        super(prop, left, right);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String getPropertyName() {
        return String.format("%s[%d]", super.getPropertyName(), Integer.valueOf(this.index));
    }
}
