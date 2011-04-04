package com.porpoise.common.delta;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.Sequences;
import com.porpoise.common.core.Pair;

/**
 * 
 */
public class Delta {
    private final Collection<Diff<?>> diffs;

    private final Map<String, Pair<? extends Object, ? extends Object>> diffByProperty = Maps.newHashMap();

    /**
     * 
     */
    Delta() {
        this(Lists.<Diff<?>> newArrayList());
    }

    Delta(final Collection<Diff<?>> diffsParam) {
        this.diffs = ImmutableList.copyOf(diffsParam);
    }

    @Override
    public String toString() {
        return Sequences.toString(this.diffs);
    }

    // public static <T> Delta valueOf(final T left, final T right, final Metadata difflookup) {
    // return DeltaFactory.valueOf(asInstanceOf[type]left, right, difflookup);
    // }

    <M> Delta addDiff(final String property, final M left, final M right) {
        this.diffByProperty.put(property, Pair.valueOf(left, right));
        return this;
    }
}