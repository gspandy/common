package com.porpoise.common.functions;

import com.google.common.base.Function;

public enum Functions2 {

	; // uninstantiable

	public static <K, V> Function<K, V> orElse(final Function<? super K, ? extends V> f,
	        final Function<? super K, ? extends V> whenNull) {
		return new Function<K, V>() {
			@Override
			public V apply(final K input) {
				final V result = f.apply(input);
				return result == null ? whenNull.apply(input) : result;
			}
		};
	}

}
