package com.porpoise.common.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.porpoise.common.Pair;

/**
 * 
 */
public enum Sequences {
    ;//

    public static String toString(final Collection<? extends Object> list) {
        if (list == null) {
            return "";
        }
        return Joiner.on(String.format(",%n")).join(list);
    }

    /**
     * @param <K>
     * @return a function which will return the keys for a set
     */
    public static final <K> Function<Map<K, ?>, Set<K>> getKeys() {
        return new Function<Map<K, ?>, Set<K>>() {
            @Override
            public Set<K> apply(final Map<K, ?> input) {
                return input.keySet();
            }
        };
    }

    public static <KEY, VAL1, VAL2, RES> Map<KEY, RES> mergeMapsIntersection(final Function<Pair<VAL1, VAL2>, RES> collate, final Map<KEY, VAL1> map1, final Map<KEY, VAL2> map2) {
        final Map<KEY, RES> intersection = Maps.newHashMap();
        for (final KEY key : Sets.intersection(map1.keySet(), map2.keySet())) {
            final VAL1 value1 = map1.get(key);
            final VAL2 value2 = map2.get(key);
            if (value1 != null && value2 != null) {
                final RES result = collate.apply(Pair.valueOf(value1, value2));
                intersection.put(key, result);
            }
        }
        return intersection;
    }

    public static <K, V> Map<K, V> mergeMaps(final Map<K, V> map1, final Map<K, V> map2, final Function<Pair<V, V>, V> collate) {
        return mergeMapsInternal(ImmutableList.of(map1, map2), collate);
    }

    private static <K, V> Map<K, V> mergeMapsInternal(final Iterable<Map<K, V>> mapsToMerge, final Function<Pair<V, V>, V> collate) {
        final Function<Map<K, ?>, Set<K>> getKeys = getKeys();
        final Map<K, V> merged = Maps.newHashMap();
        for (final K key : flatMapSet(mapsToMerge, getKeys)) {
            V mergedValue = null;
            for (final Map<K, V> map : mapsToMerge) {
                final V value = map.get(key);
                if (value != null) {
                    if (mergedValue == null) {
                        mergedValue = value;
                    } else {
                        mergedValue = collate.apply(Pair.valueOf(mergedValue, value));
                    }
                }
            }
            merged.put(key, mergedValue);
        }
        return merged;
    }

    public static <T> Collection<T> flatten(final Iterable<T>... all) {
        return flatten(Arrays.asList(all));
    }

    public static <T> Collection<T> flatten(final Iterable<? extends Iterable<T>> all) {
        final Collection<T> flat = Lists.newArrayList();
        return addAll(flat, all);
    }

    private static <C extends Collection<T>, T> C addAll(final C container, final Iterable<? extends Iterable<T>> collections) {
        for (final Iterable<T> coll : collections) {
            Iterables.addAll(container, coll);
        }
        return container;
    }

    public static <V, T extends Comparable<V>> int compare(final T left, final T right) {
        final Ordering<T> natural = Ordering.natural();
        return natural.nullsFirst().compare(left, right);
    }

    public static <F, T> Collection<T> flatMap(final Iterable<F> from, final Function<? super F, ? extends Iterable<T>> function) {
        final Iterable<Iterable<T>> transformed = Iterables.transform(from, function);
        return flatten(transformed);
    }

    public static <F, T> Set<T> flatMapSet(final Iterable<F> from, final Function<? super F, ? extends Iterable<T>> function) {
        final Iterable<Iterable<T>> all = Iterables.transform(from, function);
        final Set<T> flat = Sets.newHashSet();
        return addAll(flat, all);
    }

    public static <K, V> Map<K, V> groupByUnique(final Iterable<V> collection, final Function<? super V, K> mapper) {
        final Map<K, Collection<V>> map = groupBy(collection, mapper);
        final Function<Iterable<V>, V> onlyElm = new Function<Iterable<V>, V>() {
            @Override
            public V apply(final Iterable<V> input) {
                return Iterables.getOnlyElement(input);
            }
        };
        // do a copy here - we want to fail fast to guarantee the uniqueness
        return ImmutableMap.copyOf(Maps.transformValues(map, onlyElm));
    }

    public static <K, V> Map<K, Collection<V>> groupBy(final Iterable<V> collection, final Function<? super V, K> mapper) {
        return groupByInternal(collection, mapper);
    }

    private static <K, V> Map<K, Collection<V>> groupByInternal(final Iterable<V> collection, final Function<? super V, K> mapper) {
        final Map<K, Collection<V>> mapped = Maps.newConcurrentMap();
        for (final V value : collection) {
            final K key = mapper.apply(value);
            Collection<V> values = mapped.get(key);
            if (values == null) {
                values = Lists.newArrayList();
                mapped.put(key, values);
            }
            values.add(value);
        }
        return mapped;
    }

}
