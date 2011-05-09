package com.porpoise.common.collect;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.porpoise.common.core.Pair;

/**
 * 
 */
public enum Sequences {
    ;//

    public static final Function<Pair<BigDecimal, BigDecimal>, BigDecimal> SUM_DEC;
    public static final Function<Pair<Number, Number>, Number> SUM_NUM;

    static {
        SUM_DEC = new Function<Pair<BigDecimal, BigDecimal>, BigDecimal>() {
            @Override
            public BigDecimal apply(final Pair<BigDecimal, BigDecimal> input) {
                final BigDecimal x = Objects.firstNonNull(input.getFirst(), BigDecimal.ZERO);
                final BigDecimal y = Objects.firstNonNull(input.getSecond(), BigDecimal.ZERO);
                return x.add(y);
            }
        };
        SUM_NUM = new Function<Pair<Number, Number>, Number>() {
            @Override
            public Number apply(final Pair<Number, Number> input) {
                final Number x = Objects.firstNonNull(input.getFirst(), BigDecimal.ZERO);
                final Number y = Objects.firstNonNull(input.getSecond(), BigDecimal.ZERO);
                return Long.valueOf(x.longValue() + y.longValue());
            }
        };
    }

    public static String toString(final Iterable<? extends Object> list) {
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

    public static <KEY, VAL1, VAL2, RES> Map<KEY, RES> mergeMapsIntersection(
            final Function<Pair<VAL1, VAL2>, RES> collate, final Map<KEY, VAL1> map1, final Map<KEY, VAL2> map2) {
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

    public static <T, N> T foldLeft(final T initial, final Iterable<? extends N> things,
            final Function<Pair<T, N>, T> fnc) {
        T value = initial;
        for (final N next : things) {
            value = fnc.apply(Pair.valueOf(value, next));
        }
        return value;
    }

    public static <T, N> T foldRight(final T initial, final Iterable<? extends N> things,
            final Function<Pair<T, N>, T> fnc) {
        return foldLeft(initial, Lists.reverse(Lists.newArrayList(things)), fnc);
    }

    public static BigDecimal foldDec(final Iterable<BigDecimal> things,
            final Function<Pair<BigDecimal, BigDecimal>, BigDecimal> fnc) {
        return foldDec(BigDecimal.ZERO, things, fnc);
    }

    public static BigDecimal foldDec(final BigDecimal initial, final Iterable<BigDecimal> things,
            final Function<Pair<BigDecimal, BigDecimal>, BigDecimal> fnc) {
        return foldLeft(initial, things, fnc);
    }

    public static Number foldNum(final Iterable<? extends Number> things,
            final Function<Pair<Number, Number>, Number> fnc) {
        return foldNum(Integer.valueOf(0), things, fnc);
    }

    public static Number foldNum(final Integer initial, final Iterable<? extends Number> things,
            final Function<Pair<Number, Number>, Number> fnc) {
        return foldLeft(initial, things, fnc);
    }

    public static <K, V> Map<K, V> mergeMaps(final Map<K, V> map1, final Map<K, V> map2,
            final Function<Pair<V, V>, V> collate) {
        return mergeMapsInternal(ImmutableList.of(map1, map2), collate);
    }

    private static <K, V> Map<K, V> mergeMapsInternal(final Iterable<Map<K, V>> mapsToMerge,
            final Function<Pair<V, V>, V> collate) {
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

    private static <C extends Collection<T>, T> C addAll(final C container,
            final Iterable<? extends Iterable<T>> collections) {
        for (final Iterable<T> coll : collections) {
            Iterables.addAll(container, coll);
        }
        return container;
    }

    public static <V, T extends Comparable<V>> int compare(final T left, final T right) {
        final Ordering<T> natural = Ordering.natural();
        return natural.nullsFirst().compare(left, right);
    }

    public static <F, T> Collection<T> flatMap(final Iterable<F> from,
            final Function<? super F, ? extends Iterable<T>> function) {
        final Iterable<Iterable<T>> transformed = Iterables.transform(from, function);
        return flatten(transformed);
    }

    public static <F, T> Set<T> flatMapSet(final Iterable<F> from,
            final Function<? super F, ? extends Iterable<T>> function) {
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

    private static <K, V> Map<K, Collection<V>> groupByInternal(final Iterable<V> collection,
            final Function<? super V, K> mapper) {
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

    /**
     * @param <T>
     * @param size
     * @return a predicate which returns true for all iterables which contain more than 'size' elements
     */
    public static <T> Predicate<Iterable<T>> sizeGreaterThan(final int size) {
        return new Predicate<Iterable<T>>() {
            @Override
            public boolean apply(final Iterable<T> input) {
                return Iterables.size(input) > size;
            }
        };
    }

    /**
     * @param whiteList
     * @return a predicate which returns true when an element is found within the given collection
     */
    public static <T> Predicate<T> containsPredicate(final Collection<T> whiteList) {
        return new Predicate<T>() {
            @Override
            public boolean apply(final T input) {
                return whiteList.contains(input);
            }
        };
    }

    /**
     * Zip together both collections, returning a collection of both elements. Any extra elements in either list will be
     * silently lopped off
     * 
     * @param <A>
     * @param <B>
     * @param first
     * @param second
     * @return a collection of both elements
     */
    public static <A, B> Collection<Pair<A, B>> zip(final Iterable<A> first, final Iterable<B> second) {
        final Collection<Pair<A, B>> zipped = Lists.newArrayList();
        final Iterator<A> iterOne = first.iterator();
        final Iterator<B> iterTwo = second.iterator();
        while (iterOne.hasNext() && iterTwo.hasNext()) {
            zipped.add(Pair.valueOf(iterOne.next(), iterTwo.next()));
        }
        return zipped;
    }

    /**
     * @param all
     *            the elements to unzip
     * @return the first element of the pair
     */
    public static <A, B> Collection<A> unzipFirst(final Collection<Pair<A, B>> all) {
        return Collections2.transform(all, Pair.<A, B> first());
    }

    /**
     * @param all
     *            the elements to unzip
     * @return the first element of the pair
     */
    public static <A, B> Collection<B> unzipSecond(final Collection<Pair<A, B>> all) {
        return Collections2.transform(all, Pair.<A, B> second());
    }

    /**
     * @return a function which will return the last of the elements in a collection
     */
    public static <T> Function<Collection<T>, T> getLast() {
        return new Function<Collection<T>, T>() {
            @Override
            public T apply(final Collection<T> input) {
                return Iterables.getLast(input);
            }
        };
    }

    /**
     * @return a function which will return the first of the elements in a collection
     */
    public static <T> Function<Collection<T>, T> getFirst() {
        return new Function<Collection<T>, T>() {
            @Override
            public T apply(final Collection<T> input) {
                return Iterables.getFirst(input, null);
            }
        };
    }

    /**
     * @param <T>
     * @param iterable
     * @return an iterator for the given iterable. If the iterable is null, an empty iterator is returned
     */
    public static <T, C extends Iterable<T>> Iterator<T> iter(final C iterable) {
        if (iterable == null) {
            return Iterators.emptyIterator();
        }
        return iterable.iterator();
    }

    /**
     * @param <F>
     * @param <T>
     * @param source
     *            the source collection
     * @param function
     *            a function which returns either a valid mapping or a null value
     * @return a collection which contains only non-null values
     */
    public static <F, T> Collection<T> collect(final Collection<? extends F> source,
            final Function<? super F, T> function) {
        final Collection<T> result = Collections2.transform(source, function);
        return Collections2.filter(result, Predicates.notNull());
    }
}
