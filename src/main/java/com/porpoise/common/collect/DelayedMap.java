package com.porpoise.common.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A delayed map is a type of delegating map which will keep local changes (puts, removes clears) until it is "flushed" to its underlying map.
 * </p>
 * <p>
 * This may be useful to keep track of and control the changes of a map provided as a method argument or constructor, or for use a tiered cache, where a local scratch-pad might be
 * used and then submitted to a session cache, which in turn submits to an application-wide cache.
 * </p>
 * <p>
 * NOTE: no synchronization or resolution is provided with underlying maps. If a similar change is made both to a DelayedMap and to the underlying map delegate, the underlying
 * changes may be lost when the map is flushed. External synchronisation/coordination is thus required in order to prevent data loss.
 * </p>
 * 
 * @param <K>
 *            The key type
 * @param <V>
 *            The value type
 */
public class DelayedMap<K, V> implements Map<K, V> {
    private final Map<K, V>      delegate;

    private final Map<K, V>      updateMap;
    private final Set<K>         newSet;
    private final Set<K>         deleteSet;

    //
    // This flag may be used in order to track "new" entries, though
    // it may also be achieved by other means.
    //
    private static final boolean TRACK_NEW_ENTITIES = false;

    /**
     * @param underlyingMap
     */
    public DelayedMap(final Map<K, V> underlyingMap) {
        this.delegate = underlyingMap;
        if (this.delegate == null) {
            throw new NullPointerException("Delegate cannot be null");
        }
        this.updateMap = new HashMap<K, V>();
        this.deleteSet = new HashSet<K>();
        this.newSet = new HashSet<K>();
    }

    /**
     * flush the delayed map's updates/removals to the underlying maps
     */
    public void flush() {
        for (final K key : this.deleteSet) {
            this.delegate.remove(key);
        }
        this.delegate.putAll(this.updateMap);
        reset();
    }

    /**
     * reset any work done on the delay set
     */
    public void reset() {
        this.updateMap.clear();
        this.deleteSet.clear();
        if (trackNewEntities()) {
            this.newSet.clear();
        }
    }

    /**
     * @see Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        return this.updateMap.containsKey(key) || delegateContainsKey(key);
    }

    private boolean delegateContainsKey(final Object key) {
        return !this.deleteSet.contains(key) && this.delegate.containsKey(key);
    }

    /**
     * @see Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        return this.updateMap.containsValue(value) || this.delegate.containsValue(value);
    }

    /**
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        final Map<K, V> workingMap = new HashMap<K, V>(this.delegate);
        workingMap.putAll(this.updateMap);
        for (final K key : this.deleteSet) {
            workingMap.remove(key);
        }

        return workingMap.entrySet();
    }

    /**
     * @see Map#get(java.lang.Object)
     */
    @Override
    public V get(final Object key) {
        V value = this.updateMap.get(key);
        if (value == null) {
            if (!this.deleteSet.contains(key)) {
                value = this.delegate.get(key);
            }
        }

        return value;
    }

    /**
     * @see Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        // this.updateMap.isEmpty() && this.delegate.isEmpty()
        return keySet().isEmpty();
    }

    /**
     * @see Map#keySet()
     */
    @Override
    public Set<K> keySet() {
        final Set<K> keySet = new HashSet<K>(this.updateMap.keySet());
        keySet.addAll(this.delegate.keySet());
        keySet.removeAll(this.deleteSet);
        return keySet;
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(final K key, final V value) {
        V oldValue = putInternal(key, value);
        if (oldValue == null) {
            if (!this.deleteSet.contains(key)) {
                oldValue = this.delegate.get(key);
            }
        }

        return oldValue;
    }

    private V putInternal(final K key, final V value) {
        final V oldValue = this.updateMap.put(key, value);
        if (trackNewEntities()) {
            if (!this.delegate.containsKey(key)) {
                this.newSet.add(key);
            }
        }
        return oldValue;
    }

    /**
     * @return
     */
    private boolean trackNewEntities() {
        return TRACK_NEW_ENTITIES;
    }

    /**
     * @see Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            putInternal(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @see Map#remove(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public V remove(final Object key) {
        V removed = this.updateMap.remove(key);
        if (trackNewEntities()) {
            this.newSet.remove(key);
        }
        if (this.delegate.containsKey(key)) {
            this.deleteSet.add((K) key);
            if (removed == null) {
                removed = this.delegate.get(key);
            }
        }
        return removed;
    }

    /**
     * @see Map#size()
     */
    @Override
    public int size() {
        return this.updateMap.size() + this.delegate.size() - this.deleteSet.size();
    }

    /**
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values() {
        final Set<Entry<K, V>> entrySet = entrySet();
        final Collection<V> values = new ArrayList<V>(entrySet.size());
        for (final Entry<K, V> entry : entrySet) {
            values.add(entry.getValue());
        }
        return values;
    }

    /**
     * @see Map#clear()
     */
    @Override
    public void clear() {
        reset();
        this.deleteSet.addAll(this.delegate.keySet());
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("DelayedMap {");

        for (final Entry<K, V> entry : entrySet()) {
            buffer.append(String.format("%10s => %s%n", entry.getKey(), entry.getValue()));
        }
        buffer.append("}");

        return buffer.toString();
    }

    /**
     * @return the delegate map
     */
    public Map<K, V> getDelegate() {
        return this.delegate;
    }
}
