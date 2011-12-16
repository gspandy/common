package com.porpoise.common.functions;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Object which can be used as the key in a map based on the given functions and input type
 */
final class FixedHashKey<T> implements Key<T>, KeyFactory<T> {

	/** Any null values returned from key functions will be replaced with this null representation */
	private static class NullValue {
	}

	private static final NullValue	                      NULL	= new NullValue();

	private final T	                                      input;
	private final int	                                  cachedHashCode;
	private final Collection<Object>	                  precomputedValues;
	private final Iterable<Function<T, ? extends Object>>	functions;

	FixedHashKey(final Iterable<Function<T, ? extends Object>> functions, final T inputParam) {
		this.functions = Preconditions.checkNotNull(functions);
		this.input = Preconditions.checkNotNull(inputParam);

		// pre-compute our hashCode and values
		final List<Object> values = Lists.newArrayList();
		int hashCode = 31;
		for (final Function<T, ?> fnc : functions) {
			final Object object = Objects.firstNonNull(fnc.apply(inputParam), NULL);
			values.add(object);
			hashCode = 17 * hashCode + ((object == null) ? 0 : object.hashCode());
		}
		this.cachedHashCode = hashCode;
		this.precomputedValues = ImmutableList.copyOf(values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FixedHashKey<T> other = (FixedHashKey<T>) obj;
		if (other.input.getClass().isAssignableFrom(this.input.getClass())) {
			/*
			 * Here we assume the hash keys are the same -- that the functions they represent are identical
			 */
			if (this.precomputedValues.size() != other.precomputedValues.size()) {
				throw new IllegalStateException("Two different hash key types are being used together");
			}
			final Iterator<Object> iterOne = this.precomputedValues.iterator();
			for (final Object second : other.precomputedValues) {
				final Object first = iterOne.next();
				if (!Objects.equal(first, second)) {
					return false;
				}
			}
			assert !iterOne.hasNext();
		}
		return true;
	}

	@Override
	public boolean equivalent(final T a, final T b) {
		final Key<T> first = Keys.keyFunction(this.functions).apply(a);
		final Key<T> second = Keys.keyFunction(this.functions).apply(b);
		return first.equals(second);
	}

	@Override
	public int hash(final T t) {
		return makeKeyFor(t).hashCode();
	}

	@Override
	public Key<T> makeKeyFor(final T input) {
		return Keys.keyFunction(this.functions).apply(input);
	}
}
