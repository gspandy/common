package com.porpoise.common.metadata;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.core.Pair;

public class Delta<T> {
    private final Map<String, Delta<?>> childDeltasByProperty = Maps.newHashMap();
    private final Map<String, Pair<?, ?>> diffsByProperty = Maps.newHashMap();

    /**
     * @param <P>
     * @param property
     * @param alpha
     * @param beta
     * @return
     */
    public <P> Delta<T> addDiff(final String property, final P alpha, final P beta) {
        final Pair<?, ?> replaced = this.diffsByProperty.put(property, Pair.valueOf(alpha, beta));
        assert replaced == null;
        return this;
    }

    /**
     * @param propertyName
     * @param child
     */
    public <C> void addChild(final String propertyName, final Delta<C> child) {
        final Delta<?> replaced = this.childDeltasByProperty.put(propertyName, child);
        assert replaced == null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString("");
    }

    /**
     * @param prefix
     * @return
     */
    private String toString(final String prefixParam) {
        String prefix = prefixParam;
        if (Strings.isNullOrEmpty(prefix)) {
            prefix = "";
        } else {
            prefix = prefixParam + ".";
        }

        final String newLine = String.format("%n");

        final StringBuilder b = new StringBuilder();
        final List<String> names = Lists.newArrayList(this.diffsByProperty.keySet());
        names.addAll(this.childDeltasByProperty.keySet());
        Collections.sort(names);
        for (final String property : names) {
            final Pair<?, ?> value = this.diffsByProperty.get(property);
            if (value != null) {
                b.append(prefix).append(property).append(" ").append(value).append(newLine);
            } else {
                final Delta<?> delta = this.childDeltasByProperty.get(property);
                final String newPrefix = prefix + property;
                b.append(delta.toString(newPrefix));
            }
        }
        return b.toString();
    }
}
