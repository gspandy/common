package com.porpoise.common.delta;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.porpoise.common.metadata.Metadata;
import com.porpoise.common.metadata.MetadataProperty;

/**
 * 
 */
public enum DeltaFactory {
    ; // unintantiable

    public static <T> Delta valueOf(final T left, final T right, final Metadata diffLookup) {
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

    private static <T> Delta valueOf(final String prop, final T left, final T right, final Metadata<T> diffLookup,
            final Set<Object> visitedSet) {
        final Delta base = new Delta();
        diffRecursive(prop, right, left, base, diffLookup, visitedSet);
        return base;
    }

    private static <T> Delta diffRecursive(final String prop, final T left, final T right, final Delta ctxt,
            final Metadata<T> diffLookup, final Set<Object> visitedSet) {
        if (left == null) {
            if (right != null) {
                ctxt.addDiff(prop, left, right);
            }
            return ctxt;
        } else if (right == null) {
            ctxt.addDiff(prop, left, right);
            return ctxt;
        }
        if (!visitedSet.add(left)) {
            return ctxt;
        }

        for (final MetadataProperty<T, ?> simpleProperty : diffLookup.simpleProperties()) {
            diffSimple(ctxt, simpleProperty, left, right);
        }
        return ctxt;
    }

    private static <T, P> void diffSimple(final Delta delta, final MetadataProperty<T, P> prop, final T left,
            final T right) {

        final Object a = prop.valueOf(left);
        final Object b = prop.valueOf(right);

        // diffRecursive(simpleProperty.name(), v1, v2, ctxt, diffLookup, visitedSet
    }

    private static <T> Delta diffIterable(final String prop, final Iterable<?> left, final Iterable<?> right,
            final Delta ctxt, final Metadata diffLookup, final Set<Object> visitedSet) {
        return ctxt;
    }

    private static <T> Delta diffMap(final String prop, final Map<?, ?> left, final Map<?, ?> right, final Delta ctxt,
            final Metadata diffLookup, final Set<Object> visitedSet) {
        return ctxt;
    }

    private static <T> Delta diff(final String prop, final T left, final T right, final Delta ctxt,
            final Metadata diffLookup, final Set<Object> visitedSet) {

        final Class<T> class1 = (Class<T>) left.getClass();
        final Map<String, Function<T, ? extends Object>> propsByName = diffLookup.propertyLookupForClass(class1);
        if (propsByName.isEmpty()) {
            ctxt.addDiff(prop, left, right);
        } else {
            for (final Entry<String, Function<T, ? extends Object>> entry : propsByName.entrySet()) {
                final Object a = entry.getValue().apply(left);
                final Object b = entry.getValue().apply(right);
                if (visitedSet.add(a)) {
                    // final Delta subDiff = valueOf(entry.getKey(), a, b, ctxt.diffLookup, visitedSet);
                    // ctxt.addChild(subDiff);
                } else {
                    log("Stopping at circular reference of %s", a);
                }
            }
        }
        return ctxt;
    }

    private static void log(final String string, final Object... args) {
        System.out.println(String.format(string, args));
    }
}
