package com.porpoise.common.collect;

import com.google.common.base.Function;

/**
 * FruitBowlAccessors
 */
public enum FruitBowlAccessors {
	; // uninstantiable

	/**
	 * ACCESSOR FOR GET_CREATED
	 */
	public static final Function<FruitBowl, java.util.Date>	       GET_CREATED;

	/**
	 * ACCESSOR FOR GET_GRAPES
	 */
	public static final Function<FruitBowl, java.util.List<Grape>>	GET_GRAPES;

	static {
		/**
		 * GET_CREATED
		 */
		GET_CREATED = new Function<FruitBowl, java.util.Date>() {
			@Override
			public java.util.Date apply(final FruitBowl input) {
				return input.getCreated();
			}
		};

		/**
		 * GET_GRAPES
		 */
		GET_GRAPES = new Function<FruitBowl, java.util.List<Grape>>() {
			@Override
			public java.util.List<Grape> apply(final FruitBowl input) {
				return input.getGrapes();
			}
		};

	}
}