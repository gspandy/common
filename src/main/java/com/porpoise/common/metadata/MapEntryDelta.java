package com.porpoise.common.metadata;

import java.util.Map;

public class MapEntryDelta<K, V> extends Delta<Map<K, V>> {

    private final K key;

    public MapEntryDelta(final Metadata<?> prop, final K key, final Map<K, V> left, final Map<K, V> right) {
        super(prop, left, right);
        this.key = key;
    }

    @Override
    public String getPropertyName() {
        return String.format("%s[%d]", super.getPropertyName(), this.key);
    }

    public K getKey() {
        return this.key;
    }

}
