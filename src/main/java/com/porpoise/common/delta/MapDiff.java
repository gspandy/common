package com.porpoise.common.delta;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

class MapDiff<K, V> extends AbstractDiff<Map<K, V>> {
    private final K key;

    public MapDiff(final K keyValue, final Map<K, V> a, final Map<K, V> b) {
        super(a, b);
        this.key = checkNotNull(keyValue);
    }

    @Override
    public String getPropertyName() {
        return String.format("[%d]", this.key);
    }

}
