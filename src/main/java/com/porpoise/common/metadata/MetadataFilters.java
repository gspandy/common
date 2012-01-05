package com.porpoise.common.metadata;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.porpoise.common.collect.Sequences;

public class MetadataFilters {

	/**
	 * @param properties
	 * @return a predicate which returns true if it is given one of the
	 *         properties
	 */
	public static Predicate<Metadata<?, ?>> asFilter(
			final Metadata<?, ?>... properties) {
		return asFilter(Arrays.asList(properties));
	}

	/**
	 * @param properties
	 * @return a predicate which returns true if it is given one of the
	 *         properties
	 */
	public static Predicate<Metadata<?, ?>> asFilter(
			final Collection<Metadata<?, ?>> properties) {
		return properties == null || properties.isEmpty() ? Predicates
				.<Metadata<?, ?>> alwaysTrue() : Sequences
				.containsPredicate(properties);
	}
}
