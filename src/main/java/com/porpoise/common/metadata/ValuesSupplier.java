package com.porpoise.common.metadata;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;

class ValuesSupplier<T> implements Supplier<T> {

    private final Iterator<T> cycle;

    public ValuesSupplier(final T... values) {
        this(Arrays.asList(values));
    }

    public ValuesSupplier(final Iterable<T> iterable) {
        this.cycle = Iterators.cycle(iterable);
    }

    @Override
    public T get() {
        return this.cycle.next();
    }
}
