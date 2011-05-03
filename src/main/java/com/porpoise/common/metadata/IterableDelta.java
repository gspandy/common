package com.porpoise.common.metadata;

/**
 * A delta between two iterables
 * 
 * @param <P>
 */
public class IterableDelta<P> extends Delta<P> {

	private final int	index;

	IterableDelta(final Metadata<?, ?> prop, final int index, final P left, final P right) {
		super(prop, left, right);
		this.index = index;
	}

	/**
	 * @return the index of this iterable delta
	 */
	public int getIndex() {
		return this.index;
	}

	@Override
	public String getPropertyName() {
		return String.format("%s[%d]", super.getPropertyName(), Integer.valueOf(this.index));
	}
}
