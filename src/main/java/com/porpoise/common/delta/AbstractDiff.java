package com.porpoise.common.delta;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.porpoise.common.collect.Sequences;
import com.porpoise.common.core.Pair;

abstract class AbstractDiff<T> extends Pair<T, T> implements Diff<T> {

    public AbstractDiff(final T a, final T b) {
        super(a, b);
    }

    private final Collection<Diff<?>> children = Lists.newArrayList();

    protected <D> Diff<D> addChild(final Diff<D> child) {
        this.children.add(child);
        return child;
    }

    @Override
    public Collection<Diff<?>> childDiffs() {
        return this.children;
    }

    @Override
    public String toString() {
        final String thisDiff = String.format("%s %s", getPropertyName(), super.toString());
        final Function<Diff<?>, String> function = new Function<Diff<?>, String>() {

            @Override
            public String apply(final Diff<?> input) {
                return String.format("%s.%s", getPropertyName(), input.toString());
            }
        };
        final Iterable<String> toString = Iterables.transform(this.children, function);
        return String.format("%s%n%s", thisDiff, Sequences.toString(toString));
    }
}
