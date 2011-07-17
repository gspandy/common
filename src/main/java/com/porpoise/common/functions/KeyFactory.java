package com.porpoise.common.functions;

public interface KeyFactory<T> {

	/**
	 * create a key for the given object
	 * @param input
	 * @return a key for the given input object
	 */
	Key<T> makeKeyFor(T input);
}
