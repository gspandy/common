package com.porpoise.common.metadata;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.Sequences;

public class Delta<T> {
    private final Map<Metadata<?>, Delta<?>> childDeltasByProperty = Maps.newHashMap();

    private final Metadata<?>                property;
    private final T                          left;
    private final T                          right;

    /**
     * @param <R>
     * @param left
     * @param right
     * @return
     */
    public static <R> Delta<R> root(final R left, final R right) {
        return new Delta<R>(null, left, right);
    }

    /**
     * @param propertyName
     * @param left
     * @param right
     */
    public Delta(final Metadata<?> prop, final T left, final T right) {
        this.property = prop;
        this.left = left;
        this.right = right;
    }

    /**
     * @param <P>
     * @param prop
     * @param alpha
     * @param beta
     * @return
     */
    public <P> Delta<T> addDiff(final Metadata<P> prop, final P alpha, final P beta) {
        return addChild(new Delta<P>(prop, alpha, beta));
    }

    /**
     * @param prop
     * @param child
     */
    public <C> Delta<T> addChild(final Delta<C> child) {
        final Delta<?> replaced = this.childDeltasByProperty.put(child.property, child);
        assert replaced == null : String.format("duplicate property %s found in %s", child.property, this.property);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Sequences.toString(paths());
    }

    /**
     * @return
     */
    public Collection<PathElement<?>> paths() {
        return paths(null);
    }

    Collection<PathElement<?>> paths(final PathElement<?> parent) {
        final Collection<PathElement<?>> paths = Lists.newArrayList();
        for (final Entry<Metadata<?>, Delta<?>> entry : this.childDeltasByProperty.entrySet()) {
            @SuppressWarnings("unchecked")
            final PathElement<Object> element = new PathElement<Object>(parent, (Metadata<Object>) entry.getKey(), entry.getValue().left, entry.getValue().right);
            paths.add(element);
        }
        return paths;
    }

    public Metadata<?> getProperty() {
        return this.property;
    }

    public T getLeft() {
        return this.left;
    }

    public T getRight() {
        return this.right;
    }
}
