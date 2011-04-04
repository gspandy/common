package com.porpoise.common.delta;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.Sequences;
import com.porpoise.common.core.Pair;

/**
 * 
 */
public class Delta {
    private final Map<String, Pair<? extends Object, ? extends Object>> diffByProperty = Maps.newHashMap();

    private final Collection<Delta>                                     children       = Lists.newArrayList();

    @Override
    public String toString() {
        final String ds = Joiner.on(",%n").withKeyValueSeparator("=>").join(this.diffByProperty);
        final String kids = Sequences.toString(this.children);
        return String.format("%s%n%s", ds, kids);
    }

    // public static <T> Delta valueOf(final T left, final T right, final Metadata difflookup) {
    // return DeltaFactory.valueOf(asInstanceOf[type]left, right, difflookup);
    // }

    <M> Delta addDiff(final String property, final M left, final M right) {
        this.diffByProperty.put(property, Pair.valueOf(left, right));
        return this;
    }

    public void addChild(final Delta delta) {
        this.children.add(delta);
    }

}