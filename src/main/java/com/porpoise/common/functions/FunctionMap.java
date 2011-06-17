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

/**
 * 
 */
public class FunctionMap<T> implements Map<Object, T> {
    private final Function<T, ? extends Object> keyFunction;
    private final ConcurrentMap<Object, T> delegate;

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

    /**
     * @param c
     * @return
     */
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
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public T get(final Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public T put(final Object key, final T value) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public T remove(final Object key) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends Object, ? extends T> m) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<Object> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#values()
     */
    @Override
    public Collection<T> values() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<Object, T>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }
}
