package com.porpoise.common.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.strings.Trim;

/**
 * Represents a delta between two objects of type T
 * 
 * @param <T>
 *            the root object type for the delta
 */
public class Delta<T> {
    private final Map<String, Delta<?>> childDeltasByProperty = Maps.newHashMap();

    private final Metadata<?> property;
    private final T left;
    private final T right;

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
     * @return the added delta
     */
    <P> Delta<P> addDiff(final Metadata<?> prop, final P alpha, final P beta) {
        return addChild(new Delta<P>(prop, alpha, beta));
    }

    /**
     * @param <P>
     * @param prop
     * @param alpha
     * @param beta
     * @return the newly added diff
     */
    <P> Delta<P> addIterableDiff(final Metadata<?> prop, final int index, final P alpha, final P beta) {
        final Delta<P> diff = new IterableDelta<P>(prop, index, alpha, beta);
        addChild(diff);
        return diff;
    }

    /**
     * @param <K>
     * @param <V>
     * @param prop
     * @param key
     * @param alpha
     * @param beta
     * @return the newly added delta
     */
    <K, V> Delta<Map<K, V>> addMapDiff(final Metadata<?> prop, final K key, final Map<K, V> alpha, final Map<K, V> beta) {
        return addChild(new MapEntryDelta<K, V>(prop, key, alpha, beta));
    }

    /**
     * @param <C>
     * @param child
     *            the child delta
     * @return the new delta
     */
    <C> Delta<C> addChild(final Delta<C> child) {
        this.childDeltasByProperty.put(child.getPropertyName(), child);
        // assert replaced == null : String.format("duplicate property '%s' found in %s", child.getPropertyName(),
        // this.property == null ? "root" : this.property.propertyName());
        return child;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(null);
    }

    private String mkName() {
        if (this.property == null) {
            return "";
        }
        return String.format("%s{%s,%s}", getPropertyName(), Trim.right(getLeft(), 10), Trim.right(getRight(), 10));
    }

    private String toString(final String prefixParam) {
        final StringBuilder b = new StringBuilder();
        final String prefix;
        if (prefixParam == null) {
            prefix = mkName();
        } else {
            prefix = String.format("%s => %s", prefixParam, mkName());
            b.append(prefix).append(String.format("%n"));
        }
        for (final Delta<?> child : this.childDeltasByProperty.values()) {
            b.append(child.toString(prefix));
        }

        return b.toString();
    }

    /**
     * @return all the path elements for this delta
     */
    public Collection<PathElement<?>> paths() {
        return paths(null);
    }

    /**
     * @param parentPath
     * @return
     */
    @SuppressWarnings("unchecked")
    private PathElement<T> makePath(final PathElement<?> parentPath) {
        if (getProperty() == null) {
            return (PathElement<T>) parentPath;
        }
        return new PathElement<T>(parentPath, this, getLeft(), getRight());
    }

    private Collection<PathElement<?>> paths(final PathElement<?> parentParam) {
        final Collection<PathElement<?>> paths;

        final PathElement<?> newParent = makePath(parentParam);

        final boolean isLeaf = this.childDeltasByProperty.isEmpty();
        if (isLeaf) {
            if (newParent != null) {
                paths = ImmutableList.<PathElement<?>> of(newParent);
            } else {
                paths = Collections.emptyList();
            }
        } else {
            paths = Lists.newArrayList();
            for (final Delta<?> child : this.childDeltasByProperty.values()) {
                paths.addAll(child.paths(newParent));
            }
        }
        return paths;
    }

    /**
     * @return the metadata property
     */
    public Metadata<?> getProperty() {
        return this.property;
    }

    /**
     * This method should be overridden in subclasses to reflect, for instance, collection indices (e.g.
     * "someListProperty[3]")
     * 
     * @return the property name
     */
    public String getPropertyName() {
        return this.property == null ? "" : this.property.propertyName();
    }

    /**
     * @return the left side value
     */
    public T getLeft() {
        return this.left;
    }

    /**
     * @return the right side value
     */
    public T getRight() {
        return this.right;
    }
}
