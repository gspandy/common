package com.porpoise.common.functions;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.core.Pair;

/**
 * A map implementation backed by a function which can convert an object of type T into a key for the map
 */
public class FunctionMap<T> implements Map<Key<T>, T> {
	private final Function<T, Key<T>>	   keyFunction;
	private final ConcurrentMap<Key<T>, T>	delegate;

	/**
	 * create a new function map
	 * 
	 * @param firstFnc
	 * @param fnc
	 * @return a function map
	 */
	public static <T> FunctionMap<T> create(final Function<T, ? extends Object> firstFnc,
	        final Function<T, ? extends Object>... fnc) {
		final Function<T, Key<T>> key = Keys.keyFunction(firstFnc, fnc);
		return new FunctionMap<T>(key);
	}

	/**
	 * @param keyFunction
	 */
	public FunctionMap(final Function<T, Key<T>> keyFunction) {
		this.keyFunction = keyFunction;
		this.delegate = Maps.newConcurrentMap();
	}

	/**
	 * @param keyFunction
	 * @param first
	 * @param objects
	 */
	public FunctionMap(final Function<T, Key<T>> keyFunction, final T first, final T... objects) {
		this(keyFunction, newList(first, objects));
	}

	@SuppressWarnings("unchecked")
	private static <T> Iterable<T> newList(final T first, final T[] objects) {
		final List<T> list = Lists.newArrayList(first);
		list.addAll(Arrays.asList(objects));
		return list;
	}

	/**
	 * @param keyFunction
	 * @param objects
	 */
	public FunctionMap(final Function<T, Key<T>> keyFunction, final Iterable<T> objects) {
		this(keyFunction);
		putAllInternal(objects);
	}

	private boolean putAllInternal(final Iterable<? extends T> c) {
		boolean changed = false;
		if (c != null) {
			for (final T item : c) {
				final boolean added = addInternal(item);
				changed = changed || added;
			}
		}
		return changed;
	}

	private boolean addInternal(final T e) {
		final T existing = this.delegate.putIfAbsent(makeKey(e), e);
		return existing == null;
	}

	@SuppressWarnings("unchecked")
	private Key<T> makeKey(final Object e) {
		try {
			return asKey((T) e);
		} catch (final ClassCastException e1) {
			return null;
		}
	}

	/**
	 * @param input
	 * @return the key for the given input value
	 */
	public Key<T> asKey(final T input) {
		return input == null ? null : this.keyFunction.apply(input);
	}

	@Override
	public int size() {
		return this.delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key) {
		return this.delegate.containsKey(makeKey(key));
	}

	@Override
	public boolean containsValue(final Object value) {
		return this.delegate.containsValue(value);
	}

	@Override
	public T get(final Object key) {
		return this.delegate.get(makeKey(key));
	}

	@Override
	public T put(final Key<T> key, final T value) {
		return putInternal(key, value);
	}

	private T putInternal(final Object key, final T value) {
		return this.delegate.put(makeKey(key), value);
	}

	@Override
	public T remove(final Object key) {
		return this.delegate.remove(makeKey(key));
	}

	@Override
	public void putAll(final Map<? extends Key<T>, ? extends T> m) {
		if (m == null) {
			return;
		}
		for (final Object key : m.keySet()) {
			putInternal(key, m.get(key));
		}
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public Set<Key<T>> keySet() {
		return this.delegate.keySet();
	}

	@Override
	public Collection<T> values() {
		return this.delegate.values();
	}

	@Override
	public Set<java.util.Map.Entry<Key<T>, T>> entrySet() {
		return this.delegate.entrySet();
		// return Sequences.map(, new Function<Map.Entry<Object, T>, Map.Entry<Object, T>>() {
		// @Override
		// public java.util.Map.Entry<Object, T> apply(final java.util.Map.Entry<Object, T> input) {
		// return new java.util.Map.Entry<Object, T>() {
		// @Override
		// public Object getKey() {
		// return makeKey(input.getKey());
		// }
		//
		// @Override
		// public T getValue() {
		// return input.getValue();
		// }
		//
		// @Override
		// public T setValue(final T value) {
		// return input.setValue(value);
		// }
		// };
		// }
		// });
	}

	public Pair<Key<T>, T> put(final T value) {
		final Key<T> key = makeKey(value);
		final T replaced = putInternal(key, value);
		return Pair.valueOf(key, replaced);
	}
}
