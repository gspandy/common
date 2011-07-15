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
import com.porpoise.common.collect.Sequences;

/**
 * A map implementation backed by a function which can convert an object of type T into a key for the map
 */
public class FunctionMap<T> implements Map<Object, T> {
    private final Function<T, ? extends Object> keyFunction;
    private final ConcurrentMap<Object, T> delegate;

    /**
     * create a new function map
     * @param firstFnc
     * @param fnc
     * @return a function map
     */
	public static <T> FunctionMap<T> create(Function<T, ? extends Object> firstFnc,
			Function<T, ? extends Object> ... fnc) {
		final Function<T, Object> key = Keys.keyFunction(firstFnc, fnc);
		return new FunctionMap<T>(key);
	}

	
    /**
     * @param keyFunction
     */
    public FunctionMap(final Function<T, ? extends Object> keyFunction) {
        this.keyFunction = keyFunction;
        this.delegate = Maps.newConcurrentMap();
    }

    /**
     * @param keyFunction
     * @param first
     * @param objects
     */
    public FunctionMap(final Function<T, ? extends Object> keyFunction, final T first, final T... objects) {
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
    public FunctionMap(final Function<T, ? extends Object> keyFunction, final Iterable<T> objects) {
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
    private Object makeKey(final Object e) {
        try {
        	Object key = e == null ? null : this.keyFunction.apply((T)e);
            return key;
        } catch (final ClassCastException e1) {
            return e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(makeKey(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(final Object key) {
        return delegate.get(makeKey(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(final Object key, final T value) {
        return putInternal(key, value);
    }

	private T putInternal(final Object key, final T value) {
		return delegate.put(makeKey(key), value);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public T remove(final Object key) {
        return delegate.remove(makeKey(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(final Map<? extends Object, ? extends T> m) {
    	if (m == null)
    	{
    		return;
    	}
    	for (Object key : m.keySet()) {
    		putInternal(key, m.get(key));
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
    	delegate.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<Object> keySet() {
        return Sequences.map(delegate.keySet(), new Function<Object, Object> () {
			@Override
			public Object apply(Object input) {
				return makeKey(input);
			}});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> values() {
        return delegate.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<java.util.Map.Entry<Object, T>> entrySet() {
		return Sequences.map(delegate.entrySet(), new Function<Map.Entry<Object,T>, Map.Entry<Object,T>>() {
			@Override
			public java.util.Map.Entry<Object, T> apply(final java.util.Map.Entry<Object, T> input) {
				return new java.util.Map.Entry<Object, T>() {
					@Override
					public Object getKey() {
						return makeKey(input.getKey());
					}

					@Override
					public T getValue() {
						return input.getValue();
					}
					@Override
					public T setValue(T value) {
						return input.setValue(value);
					}};
			}
		});
    }
}
