package com.porpoise.common.functions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * A set implementation which will use a supplied function to create a unique key
 * 
 * @param <T>
 */
public class FunctionSet<T> implements Set<T> {

	private final Function<T, ? extends Object>	keyFunction;
	private final ConcurrentMap<Object, T>	    delegate;

	/**
	 * @param keyFunction
	 */
	public FunctionSet(final Function<T, ? extends Object> keyFunction) {
		this.keyFunction = keyFunction;
		this.delegate = Maps.newConcurrentMap();
	}

	/**
	 * @param keyFunction
	 * @param first
	 * @param objects
	 */
	public FunctionSet(final Function<T, ? extends Object> keyFunction, final T first, final T... objects) {
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
	public FunctionSet(final Function<T, ? extends Object> keyFunction, final Iterable<T> objects) {
		this(keyFunction);
		addAllInternal(objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		return this.delegate.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object o) {
		return o != null && this.delegate.values().contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.delegate.values().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.delegate.values().toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <K> K[] toArray(final K[] a) {
		return this.delegate.values().toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(final T e) {
		return addInternal(e);
	}

	private boolean addInternal(final T e) {
		final T existing = this.delegate.putIfAbsent(key(e), e);
		return existing == null;
	}

	/**
	 * @param e
	 * @return
	 */
	private Object key(final T e) {
		return e == null ? null : this.keyFunction.apply(e);
	}

	@SuppressWarnings("unchecked")
	private Object keyObj(final Object e) {
		try {
			return key((T) e);
		} catch (final ClassCastException e1) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object o) {
		return this.delegate.remove(keyObj(o)) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.delegate.values().containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return addAllInternal(c);
	}

	/**
	 * @param c
	 * @return
	 */
	private boolean addAllInternal(final Iterable<? extends T> c) {
		boolean changed = false;
		if (c != null) {
			for (final T item : c) {
				final boolean added = addInternal(item);
				changed = changed || added;
			}
		}
		return changed;
	}

	private boolean removeAllKeysInternal(final Set<Object> blackListKeys) {
		boolean changed = false;
		for (final Object key : blackListKeys) {
			final T removed = this.delegate.remove(key);
			final boolean isRemoved = removed != null;
			changed = changed || isRemoved;
		}
		return changed;
	}

	private Set<Object> makeKeys(final Collection<?> c) {
		final Collection<Object> transform = Collections2.transform(c, new Function<Object, Object>() {
			@SuppressWarnings("synthetic-access")
			@Override
			public Object apply(final Object input) {
				return keyObj(input);
			}
		});
		return Sets.newHashSet(Collections2.filter(transform, Predicates.notNull()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		final Set<Object> whiteList = makeKeys(c);
		final SetView<Object> blackList = Sets.difference(this.delegate.keySet(), whiteList);
		return removeAllKeysInternal(blackList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		final Set<Object> blackList = makeKeys(c);
		return removeAllKeysInternal(blackList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		this.delegate.clear();
	}

}
