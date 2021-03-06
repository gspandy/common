package com.porpoise.common.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
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
    static final String                 NULL_STRING           = "null";

    private final Map<String, Delta<?>> childDeltasByProperty = Maps.newHashMap();

    private final Metadata<?, ?>        property;
    private final T                     left;
    private final T                     right;

    /**
     * @param <R>
     * @param left
     * @param right
     * @return a new delta node
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
     * @param index
     * @param alpha
     * @param beta
     * @return the newly added delta
     */
    public <P> Delta<P> addIterableDiff(final Metadata<?, ?> prop, final int index, final P alpha, final P beta) {
        final Delta<P> diff = new IterableDelta<P>(prop, index, alpha, beta);
        addChild(diff);
        return diff;
    }

    /**
     * @param prop
     * @param key
     * @param alpha
     * @param beta
     * @return the newly added delta
     */
    public <K> Delta<Map<? extends K, ?>> addMapDiff(final Metadata<?, ?> prop, final K key, final Map<? extends K, ?> alpha,
            final Map<? extends K, ?> beta) {
        final MapEntryDelta<K> child = new MapEntryDelta<K>(prop, key, alpha, beta);
        return addChild(child);
    }

    /**
     * @param <C>
     * @param child
     *            the child delta
     * @return the new delta
     */
    public <C, D extends Delta<C>> D addChild(final D child) {
        if (child == this) {
            throw new IllegalArgumentException("Cannot add a delta to itself");
        }
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
        final List<String> pathStrings = Lists.newArrayList(Collections2.transform(paths(), Functions.toStringFunction()));
        Collections.sort(pathStrings);
        return Sequences.toString(pathStrings);
    }

    /**
     * @return all the path elements for this delta
     */
    public Collection<PathElement<?, ?>> paths() {
        return paths(null);
    }

    /**
     * @param property
     * @return all paths which contain the given property
     */
    public Collection<PathElement<?, ?>> pathsWithProperty(final Metadata<?, ?> property) {
        final Predicate<PathElement<?, ?>> filter = pathContainsPredicate(property);
        return filterPaths(filter);
    }

    public Collection<PathElement<?, ?>> filterPaths(final Predicate<PathElement<?, ?>> filter) {
        final Collection<PathElement<?, ?>> allPaths = paths();
        return ImmutableList.copyOf(Collections2.filter(allPaths, filter));
    }

    /**
     * @param property
     * @return a predicate which returns true if the path element contains the given property
     */
    public Predicate<PathElement<?, ?>> pathContainsPredicate(final Metadata<?, ?> property) {
        return new Predicate<PathElement<?, ?>>() {
            @Override
            public boolean apply(final PathElement<?, ?> path) {
                return path.contains(property);
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected PathElement<T, ?> makePath(final PathElement<?, ?> parentPath) {
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
                final Collection<PathElement<?, ?>> childPaths = child.paths(newParent);
                paths.addAll(childPaths);
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
     * This method should be overridden in subclasses to reflect, for instance, collection indices (e.g. "someListProperty[3]")
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

    public boolean isEmpty() {
        return this.property == null && isLeaf();
    }
}
