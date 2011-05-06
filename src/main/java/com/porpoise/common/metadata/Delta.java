package com.porpoise.common.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.Sequences;

/**
 * Represents a delta between two objects of type T
 * 
 * @param <T>
 *            the root object type for the delta
 */
public class Delta<T> {
    static final String NULL_STRING = "null";

    private final Map<String, Delta<?>> childDeltasByProperty = Maps.newHashMap();

    private final Metadata<?, ?> property;
    private final T left;
    private final T right;

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
    Delta(final Metadata<?, ?> prop, final T left, final T right) {
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
    public <P> Delta<P> addDiff(final Metadata<?, ?> prop, final P alpha, final P beta) {
        return addChild(new Delta<P>(prop, alpha, beta));
    }

    /**
     * @param <P>
     * @param prop
     * @param alpha
     * @param beta
     * @return the newly added diff
     */
    public <P> Delta<P> addIterableDiff(final Metadata<?, ?> prop, final int index, final P alpha, final P beta) {
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
    public <K, V> Delta<Map<K, ? extends V>> addMapDiff(final Metadata<?, Map<K, ? extends V>> prop, final K key,
            final Map<K, ? extends V> alpha, final Map<K, ? extends V> beta) {
        return addChild(new MapEntryDelta<K, V>(prop, key, alpha, beta));
    }

    /**
     * @param <C>
     * @param child
     *            the child delta
     * @return the new delta
     */
    public <C, D extends Delta<C>> D addChild(final D child) {
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
        final List<String> pathStrings = Lists.newArrayList(Collections2.transform(paths(),
                Functions.toStringFunction()));
        Collections.sort(pathStrings);
        return Sequences.toString(pathStrings);
    }

    //
    // private String mkName() {
    // if (this.property == null) {
    // return "";
    // }
    // return String.format("%s{%s,%s}", getPropertyName(), Trim.right(getLeft(), 10), Trim.right(getRight(), 10));
    // }

    // private String toString(final String prefixParam) {
    // final StringBuilder b = new StringBuilder();
    // final String prefix;
    // if (prefixParam == null) {
    // prefix = mkName();
    // } else {
    // prefix = String.format("%s => %s", prefixParam, mkName());
    // b.append(prefix).append(String.format("%n"));
    // }
    // for (final Delta<?> child : this.childDeltasByProperty.values()) {
    // b.append(child.toString(prefix));
    // }
    //
    // return b.toString();
    // }

    /**
     * @return all the path elements for this delta
     */
    public Collection<PathElement<?, ?>> paths() {
        return paths(null);
    }

    /**
     * @param parentPath
     * @return
     */
    @SuppressWarnings("unchecked")
    private PathElement<T, ?> makePath(final PathElement<?, ?> parentPath) {
        if (getProperty() == null) {
            return (PathElement<T, ?>) parentPath;
        }
        return new PathElement<T, Object>((PathElement<?, T>) parentPath, this);
    }

    private Collection<PathElement<?, ?>> paths(final PathElement<?, ?> parentParam) {
        final Collection<PathElement<?, ?>> paths;

        final PathElement<?, ?> newParent = makePath(parentParam);

        if (isLeaf()) {
            if (newParent != null) {
                paths = ImmutableList.<PathElement<?, ?>> of(newParent);
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

    protected boolean hasChildren() {
        return !this.childDeltasByProperty.isEmpty();
    }

    protected boolean isLeaf() {
        return !hasChildren();
    }

    /**
     * @return the metadata property
     */
    public Metadata<?, ?> getProperty() {
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
     * @return the left side string
     */
    public String getLeftString() {
        return toStringSafe(getLeft());
    }

    /**
     * @return the right side string
     */
    public String getRightString() {
        return toStringSafe(getRight());
    }

    protected static <V> String toStringSafe(final V value) {
        return Objects.firstNonNull(value, NULL_STRING).toString();
    }

    /**
     * @return the right side value
     */
    public T getRight() {
        return this.right;
    }
}
