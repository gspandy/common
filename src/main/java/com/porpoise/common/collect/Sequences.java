package com.porpoise.common.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    /**
     * Utility for merging the intersection of two maps.
     * 
     * @param <KEY>
     *            The common key type
     * @param <VAL1>
     *            The value type of the first map
     * @param <VAL2>
     *            The value type of the second map
     * @param <RES>
     *            The value type of the resulting map
     * @param collate
     *            a function which can combine the values of map1 (type VAL1) and map2 (type VAL2) and produce a result (type RES)
     * @param map1
     *            the first map to merge
     * @param map2
     *            the second map to merge
     * @return a map containing merged values of all the given maps
     */
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

    /**
     * Merges two maps together.
     * 
     * @see #mergeMaps(Function, Map, Map, Map, Map...)
     * 
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @param collate
     *            the value merging function
     * @param map1
     *            the first map to merge with the seoncd
     * @param map2
     *            the second map to merge with the first
     * @return a map containing merged values of all the given maps
     */
    public static <K, V> Map<K, V> mergeMaps(final Function<Pair<V, V>, V> collate, final Map<K, V> map1, final Map<K, V> map2) {
        return mergeMapsInternal(collate, ImmutableList.of(map1, map2));
    }

    /**
     * Merges three or more maps. This signature is such so as to differentiate between one which takes two maps.
     * 
     * Any keys which appear in multiple maps will have their values merged via the merging function.
     * 
     * As the values of the maps may be mutable, be sure to check the function's javadoc to see if any underlying objects will be changed.
     * 
     * @param <K>
     *            The key type
     * @param <V>
     *            The value type
     * @param collate
     *            A function which can combine map values
     * @param map1
     *            the first map to merge
     * @param map2
     *            the second map to merge
     * @param map3
     *            the third map to merge
     * @param maps
     *            the remaining maps to merge the maps to merge
     * @return a map containing merged values of all the given maps
     */
    public static <K, V> Map<K, V> mergeMaps(final Function<Pair<V, V>, V> collate, final Map<K, V> map1, final Map<K, V> map2, final Map<K, V> map3, final Map<K, V>... maps) {
        final List<Map<K, V>> lists = Lists.newArrayList();
        lists.add(map1);
        lists.add(map2);
        lists.add(map3);
        lists.addAll(Arrays.asList(maps));
        return mergeMapsInternal(collate, lists);
    }

    private static <K, V> Map<K, V> mergeMapsInternal(final Function<Pair<V, V>, V> collate, final List<Map<K, V>> mapsToMerge) {
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

    /**
     * flatten all the collections
     * 
     * @param <T>
     *            the collection type
     * @param all
     *            a var-arg array of iterables
     * @return all the collections as one big collection
     */
    public static <T> Collection<T> flatten(final Iterable<T>... all) {
        return flatten(Arrays.asList(all));
    }

    /**
     * Convert a matrix (iterable of iterables) into a flattened collection
     * 
     * @param <T>
     *            the collection type
     * @param all
     *            the iterable elements
     * @return the flattened collection
     */
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

    /**
     * @param <V>
     *            the value type
     * @param <T>
     *            the comparable type
     * @param left
     *            the first object to compare
     * @param right
     *            the second object to compare
     * @return the result after comparing the two values
     */
    public static <V, T extends Comparable<V>> int compare(final T left, final T right) {
        final Ordering<T> natural = Ordering.natural();
        return natural.nullsFirst().compare(left, right);
    }

    /**
     * @param <F>
     *            the source type
     * @param <T>
     *            the target type
     * @param from
     *            the source collection
     * @param function
     *            a function which provides a one-to-many transformation from the source type to many target types
     * @return a collection of the target type
     */
    public static <F, T> Collection<T> flatMap(final Iterable<F> from, final Function<? super F, ? extends Iterable<T>> function) {
        final Iterable<Iterable<T>> transformed = Iterables.transform(from, function);
        return flatten(transformed);
    }

    /**
     * @param <F>
     *            the source type
     * @param <T>
     *            the target type
     * @param from
     *            the source collection
     * @param function
     *            a function which provides a one-to-many transformation from the source type to many target types
     * @return a set of the target type
     */
    public static <F, T> Set<T> flatMapSet(final Iterable<F> from, final Function<? super F, ? extends Iterable<T>> function) {
        final Iterable<Iterable<T>> all = Iterables.transform(from, function);
        final Set<T> flat = Sets.newHashSet();
        return addAll(flat, all);
    }

    /**
     * If certain that your function will produce only unique values, then this function will return a map for the given collection. If multiple values are returned, then an
     * {@link IllegalArgumentException} will be thrown
     * 
     * see also DomainFunctions.mapById
     * 
     * @param collection
     *            the collection of things to group
     * @param mapper
     *            the mapping function
     * 
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     * @return a map of values by the key as determined by the function
     */
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

    /**
     * convert the collection into a map which will group the values by the key as returned from the function.
     * 
     * @param collection
     * @param mapper
     * @param <K>
     * @param <V>
     * @return a map of the values by their key
     */
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
