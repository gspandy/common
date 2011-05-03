package com.porpoise.common.metadata;

import java.util.Map;

/**
 * a diff between two map entries
 * 
 * @param <K>
 * @param <V>
 */
public class MapEntryDelta<K, V> extends Delta<Map<K, ? extends V>> {

	private final K	key;

	MapEntryDelta(final Metadata<?, ?> prop, final K key, final Map<K, ? extends V> left,
	        final Map<K, ? extends V> right) {
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
}
