package com.porpoise.common.metadata;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.porpoise.common.collect.Sequences;

public class Delta<T> {
    private final Multimap<String, Delta<?>> childDeltasByProperty = ArrayListMultimap.create();

    private final Metadata<?>                property;
    private final T                          left;
    private final T                          right;

    /**
     * @param <R>
     * @param left
     * @param right
     * @return
     */
    static <R> Delta<R> root(final R left, final R right) {
        return new Delta<R>(null, left, right);
    }

    /**
     * @param propertyName
     * @param left
     * @param right
     */
    Delta(final Metadata<?> prop, final T left, final T right) {
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
    public <P> Delta<P> addDiff(final Metadata<?> prop, final P alpha, final P beta) {
        return addChild(new Delta<P>(prop, alpha, beta));
    }

    /**
     * @param <P>
     * @param prop
     * @param alpha
     * @param beta
     * @return
     */
    public <P> Delta<P> addIterableDiff(final Metadata<?> prop, final int index, final P alpha, final P beta) {
        final Delta<P> diff = new IterableDelta<P>(prop, index, alpha, beta);
        addChild(diff);
        return diff;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Delta<Map<K, V>> addMapDiff(final Metadata<?> prop, final K key, final Map<K, V> alpha, final Map<K, V> beta) {
        return addChild(new MapEntryDelta<K, V>(prop, key, alpha, beta));
    }

    /**
     * @param prop
     * @param child
     */
    public <C> Delta<C> addChild(final Delta<C> child) {
        this.childDeltasByProperty.put(child.getPropertyName(), child);
        // assert replaced == null : String.format("duplicate property '%s' found in %s", child.getPropertyName(), this.property == null ? "root" : this.property.propertyName());
        return child;
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
        for (final Entry<String, Collection<Delta<?>>> entry : this.childDeltasByProperty.asMap().entrySet()) {
            for (final Delta<?> delta : entry.getValue()) {
                @SuppressWarnings("unchecked")
                final PathElement<Object> element = new PathElement<Object>(parent, (Metadata<Object>) delta.property, delta.left, delta.right);
                final Collection<PathElement<?>> childPaths = delta.paths();
                paths.add(element);
                if (!childPaths.isEmpty()) {
                    paths.addAll(childPaths);
                }
            }
        }
        return paths;
    }

    public Metadata<?> getProperty() {
        return this.property;
    }

    public String getPropertyName() {
        return this.property.propertyName();
    }

    public T getLeft() {
        return this.left;
    }

    public T getRight() {
        return this.right;
    }
}
