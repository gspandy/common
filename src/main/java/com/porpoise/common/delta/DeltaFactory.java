package com.porpoise.common.delta;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * 
 */
public enum DeltaFactory {
    ; // unintantiable

    /**
     * @param <T>
     * @param input
     * @param propByName
     * @param diffLookup
     * @return
     */
    public static <T> Delta valueOf(final T left, final T right, final Lookup diffLookup) {
        final Set<Object> visitedSet = Sets.newIdentityHashSet();
        if (left == null) {
            if (right == null) {
                return null;
            }
            final String prop = right.getClass().getSimpleName();
            return valueOf(prop, right, left, diffLookup, visitedSet).makeDelta();
        }
        final String prop = left.getClass().getSimpleName();
        return valueOf(prop, right, left, diffLookup, visitedSet).makeDelta();
    }

    private static <T> DeltaContext valueOf(final String prop, final T left, final T right, final Lookup diffLookup, final Set<Object> visitedSet) {
        final DeltaContext ctxt = new DeltaContext(diffLookup);
        diffRecursive(prop, right, left, ctxt, visitedSet);
        return ctxt;
    }

    private static boolean isIterable(final Object obj) {
        return obj != null && obj.getClass().isAssignableFrom(Iterable.class);
    }

    private static boolean isMap(final Object obj) {
        return obj != null && obj.getClass().isAssignableFrom(Map.class);
    }

    private static <T> DeltaContext diffRecursive(final String prop, final T left, final T right, final DeltaContext ctxt, final Set<Object> visitedSet) {
        if (left == null) {
            if (right != null) {
                ctxt.addDiff(prop, left, right);
            }
            return ctxt;
        }
        if (isIterable(left)) {
            return diffIterable(prop, (Iterable<?>) left, (Iterable<?>) right, ctxt, visitedSet);
        }
        if (isMap(left)) {
            return diffMap(prop, (Map<?, ?>) left, (Map<?, ?>) right, ctxt, visitedSet);
        }
        return diff(prop, left, right, ctxt, visitedSet);
    }

    private static DeltaContext diffIterable(final String prop, final Iterable<?> left, final Iterable<?> right, final DeltaContext ctxt, final Set<Object> visitedSet) {
        return ctxt;
    }

    private static <T> DeltaContext diffMap(final String prop, final Map<?, ?> left, final Map<?, ?> right, final DeltaContext ctxt, final Set<Object> visitedSet) {
        return ctxt;
    }

    private static <T> DeltaContext diff(final String prop, final T left, final T right, final DeltaContext ctxt, final Set<Object> visitedSet) {
        final Class<T> class1 = (Class<T>) left.getClass();
        final Map<String, Function<T, ? extends Object>> propsByName = ctxt.diffLookup.propertyLookupForClass(class1);
        if (propsByName.isEmpty()) {
            ctxt.addDiff(prop, left, right);
        } else {
            for (final Entry<String, Function<T, ? extends Object>> entry : propsByName.entrySet()) {
                final Object a = entry.getValue().apply(left);
                final Object b = entry.getValue().apply(right);
                if (visitedSet.add(a)) {
                    final DeltaContext subDiff = valueOf(entry.getKey(), a, b, ctxt.diffLookup, visitedSet);
                    ctxt.addChild(subDiff);
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
