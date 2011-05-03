package com.porpoise.common.core;

/**
 * @param <T>
 *            the type to update
 * @param <V>
 *            The type of the value to update
 * 
 */
public interface Mutator<T, V> {

	/**
	 * @param object
	 *            the object to update
	 * @param value
	 *            the value to set on the object
	 */
	public void update(T object, V value);
}
