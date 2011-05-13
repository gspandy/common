package com.porpoise.common.metadata;

import java.util.Map;

/**
 * a difference between two map entries
 * 
 * @param <K>
 */
public class MapEntryDelta<K> extends Delta<Map<K, ?>> {

    private final K key;

    /**
     * factory method to create a new map entry delta
     * 
     * @param <A>
     * @param prop
     * @param key
     * @param left
     * @param right
     * @return a new map entry delta
     */
    public static <A> MapEntryDelta<A> valueOf(final Metadata<?, ?> prop, final A key, final Map<A, ?> left,
            final Map<A, ?> right) {
        return new MapEntryDelta<A>(prop, key, left, right);
    }

    /**
     * @param prop
     * @param key
     * @param left
     * @param right
     */
    public MapEntryDelta(final Metadata<?, ?> prop, final K key, final Map<K, ?> left, final Map<K, ?> right) {
        super(prop, left, right);
        this.key = key;
    }

    @Override
    public String getPropertyName() {
        return String.format("%s[%s]", super.getPropertyName(), this.key);
    }

    /**
     * @return the map key
     */
    public K getKey() {
        return this.key;
    }

    @Override
    public String getLeftString() {
        if (hasChildren()) {
            return super.getLeftString();
        }
        return mapToString(getLeft());
    }

    @Override
    public String getRightString() {
        if (hasChildren()) {
            return super.getRightString();
        }
        return mapToString(getRight());
    }

    private String mapToString(final Map<K, ?> map) {
        if (map == null) {
            return "null";
        }
        return toStringSafe(map.get(this.key));
    }
}
