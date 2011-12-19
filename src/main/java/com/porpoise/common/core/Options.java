package com.porpoise.common.core;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 */
public enum Options {
	;// uninstantiable

	/**
	 * @param values
	 * @return the collection as an optional, or exception if the iterable has multiple elements
	 */
	public <T> Optional<T> fromIterable(final Iterable<T> values) {
		return Optional.<T> fromNullable(Iterables.<T> getOnlyElement(values, null));
	}
}
