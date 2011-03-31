package com.porpoise.common.delta;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.porpoise.common.collect.Sequences;

/**
 * 
 */
public class Delta {
    private final Collection<Diff<?>> diffs;

    Delta(final Collection<Diff<?>> diffsParam) {
        this.diffs = ImmutableList.copyOf(diffsParam);
    }

    @Override
    public String toString() {
        return Sequences.toString(this.diffs);
    }

    public static <T> Delta valueOf(final T left, final T right, final Lookup difflookup) {
        return DeltaFactory.valueOf(left, right, difflookup);
    }
}
