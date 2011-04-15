package com.porpoise.common.metadata;

import com.google.common.base.Function;

/**
 * @param <T>
 */
public interface Metadata<T, V> {
	/**
	 * @return the accessor function
	 */
	Function<T, V> accessor();

	/**
	 * @return the property name
	 */
	String propertyName();

	/**
	 * @param <V>
	 * @param input
	 * @param newValue
	 * @return true if this method had any affect
	 */
	boolean update(T input, V newValue);

	/**
	 * @return true if this metadata can also update its data
	 */
	boolean isMutable();
}
