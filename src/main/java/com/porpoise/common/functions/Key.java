package com.porpoise.common.functions;


/**
 * A Key represents something which is safe to use in a hash or equivalence (tree) set or map. Implementations are
 * expected to be immutable, unless otherwise stated in their javadocs
 * 
 * A key is largely a marker interface, as any java object already supplies a hashCode and equals method when it
 * overrides {@link Object}.
 * 
 * @param <T>
 *            The type represented by this key
 */
public interface Key<T> { // extends Equivalence<T>{

	/**
	 * {@inheritDoc}
	 * 
	 * @return the hash code for this key
	 */
	@Override
	public int hashCode();

	/**
	 * {@inheritDoc}
	 * 
	 * @param other
	 * @return true if this key is equal to the given object
	 */
	@Override
	public boolean equals(Object other);

}
