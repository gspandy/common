package com.porpoise.common.delta;

import java.util.Collection;

import com.google.common.collect.Lists;

class DeltaContext {
    public DeltaContext(final Lookup lookup) {
        this.diffLookup = lookup;
    }

    final Lookup                           diffLookup;
    private final Collection<Diff<?>>      diffs    = Lists.newArrayList();
    private final Collection<DeltaContext> children = Lists.newArrayList();

    /**
     * @return
     */
    public Delta makeDelta() {
        return null;
    }

    public <T> DeltaContext addDiff(final String p, final T left, final T right) {
        this.diffs.add(new SimpleDiff<T>(p, left, right));
        return this;
    }

    public void addChild(final DeltaContext subDiff) {
        this.children.add(subDiff);
    }

}