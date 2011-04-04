package com.porpoise.common.delta;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.porpoise.common.core.Pair;
import com.porpoise.common.metadata.Metadata;
import com.porpoise.common.metadata.MetadataProperty;

/**
 * 
 */
public enum DeltaFactory {
    ; // unintantiable

    public static <T> Delta valueOf(final T left, final T right, final Metadata<T> diffLookup) {
        final Set<Object> visitedSet = Sets.newIdentityHashSet();
        if (left == null) {
            if (right == null) {
                return null;
            }
            final String prop = right.getClass().getSimpleName();
            return valueOf(prop, right, left, diffLookup, visitedSet);
        }
        final String prop = left.getClass().getSimpleName();
        return valueOf(prop, right, left, diffLookup, visitedSet);
    }

    private static <T> Delta valueOf(final String prop, final T left, final T right, final Metadata<T> diffLookup, final Set<Object> visitedSet) {
        return diffRecursive(prop, right, left, diffLookup, visitedSet);
    }

    private static <T> Delta diffRecursive(final String prop, final Object left, final Object right, final Metadata<T> diffLookup, final Set<Object> visitedSet) {
        final Delta delta = new Delta();
        if (left == null) {
            if (right != null) {
                delta.addDiff(prop, left, right);
            }
            return delta;
        } else if (right == null) {
            delta.addDiff(prop, left, right);
            return delta;
        }
        if (!visitedSet.add(left)) {
            return delta;
        }

        for (final Entry<String, Function<T, ? extends Object>> entry : diffLookup.valuesByName().entrySet()) {
            Function<T, ? extends Object> fnc = entry.getValue();
            final Object a = fnc.apply(left);
            final Object b = fnc.apply(right);
            if (!Objects.equal(a, b)) {
                delta.addDiff(entry.getKey(), a, b);
            }
        }

        for (final MetadataProperty<T> simpleProperty : diffLookup.simpleProperties()) {
            final String propName = simpleProperty.name();

            if (simpleProperty.isIterable()) {
                final Pair<Metadata<Object>, Iterable<Object>> a = simpleProperty.iterableValueOf(left);
                final Pair<Metadata<Object>, Iterable<Object>> b = simpleProperty.iterableValueOf(right);

                int index = 0;
                final Iterator<Object> iteratorOne = iter(a.getSecond());
                final Iterator<Object> iteratorTwo = iter(b.getSecond());
                while (iteratorOne.hasNext() || iteratorTwo.hasNext()) {
                    final Object alpha = next(iteratorOne);
                    final Object beta = next(iteratorTwo);
                    if (!Objects.equal(alpha, beta)) {

                        final Delta kid = diffRecursive(String.format("%s[%s]", propName, index), alpha, beta, a.getFirst(), visitedSet);
                        delta.addChild(kid);
                    }
                    index++;
                }
            } else if (simpleProperty.isMap()) {
                final Pair<Metadata<Object>, Map<Object, Object>> a = simpleProperty.mappedValueOf(left);
                final Pair<Metadata<Object>, Map<Object, Object>> b = simpleProperty.mappedValueOf(right);

                // TODO - diff maps
            } else {
                final Pair<Metadata<?>, ?> a = simpleProperty.valueOf(left);
                final Pair<Metadata<?>, ?> b = simpleProperty.valueOf(right);

                final Delta kid = diffRecursive(propName, a.getSecond(), b.getSecond(), a.getFirst(), visitedSet);
                delta.addChild(kid);
            }
        }
        return delta;
    }

    private static <T> T next(final Iterator<T> iter) {
        return iter.hasNext() ? iter.next() : null;
    }

    private static <T> Iterator<T> iter(final Iterable<T> iter) {
        if (iter == null) {
            return Iterators.emptyIterator();
        }
        return iter.iterator();
    }
}
