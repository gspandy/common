package com.porpoise.common.metadata;

import com.google.common.collect.Iterables;

/**
 * A delta between two iterables
 * 
 * @param <P>
 */
public class IterableDelta<P> extends Delta<P> {

    private final int index;

    IterableDelta(final Metadata<?, ?> prop, final int index, final P left, final P right) {
        super(prop, left, right);
        this.index = index;
    }

    /**
     * @return the index of this iterable delta
     */
    public int getIndex() {
        return this.index;
    }

    @Override
    public String getPropertyName() {
        return String.format("%s[%d]", super.getPropertyName(), Integer.valueOf(this.index));
    }

    @Override
    public String getLeftString() {
        if (hasChildren()) {
            return super.getLeftString();
        }
        return listString(getLeft());
    }

    @Override
    public String getRightString() {
        if (hasChildren()) {
            return super.getRightString();
        }
        return listString(getRight());
    }

    private String listString(final P left) {
        if (left instanceof Iterable<?>) {
            final Iterable<?> iterable = (Iterable<?>) left;
            if (Iterables.size(iterable) > this.index) {
                return toStringSafe(Iterables.get(iterable, this.index));
            }
            return NULL_STRING;
        }
        return toStringSafe(left);
    }
}
