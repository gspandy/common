package com.porpoise.common.metadata;

import java.util.Map;
import java.util.Stack;

import com.google.common.base.Objects;
import com.porpoise.common.core.Pair;

/**
 * @param <D>
 *            The delta type produced from this visitor
 */
public class DeltaVisitor<D> extends VisitorAdapter {

	private final Stack<Delta<?>>	workingDelta	= new Stack<Delta<?>>();

	/**
	 * @param left
	 * @param right
	 */
	public DeltaVisitor(final D left, final D right) {
		this.workingDelta.push(Delta.root(left, right));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#onProperty(Metadata, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T, P> VisitorResult onProperty(final Metadata<P, T> property, final Pair<P, T> pairOne,
	        final Pair<P, T> pairTwo) {
		final T thingOne = pairOne.getSecond();
		final T thingTwo = pairTwo.getSecond();
		if (!Objects.equal(thingOne, thingTwo)) {
			delta().addDiff(property, thingOne, thingTwo);
		}
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#onIterables(Metadata, java.lang.Iterable, java.lang.Iterable)
	 */
	@Override
	public <P, I extends Iterable<?>> VisitorResult onIterables(final Metadata<P, I> property, final I thingOne,
	        final I thingTwo) {
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#onIterableItem(Metadata, int, Pair, Pair)
	 */
	@Override
	public <T, P, I extends Iterable<? extends T>> VisitorResult onIterableItem(final Metadata<P, I> property,
	        final int index, final Pair<I, T> pairOne, final Pair<I, T> pairTwo) {
		processIterableItem(property, index, pairOne, pairTwo);
		return result();
	}

	private <T, P, I extends Iterable<? extends T>> Delta<T> processIterableItem(final Metadata<P, I> property,
	        final int index, final Pair<I, T> pairOne, final Pair<I, T> pairTwo) {
		final T thingOne = pairOne.getSecond();
		final T thingTwo = pairTwo.getSecond();
		if (!Objects.equal(thingOne, thingTwo)) {
			return delta().addIterableDiff(property, index, thingOne, thingTwo);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#onMaps(Metadata, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult onMaps(final Metadata<P, Map<K, ? extends V>> property,
	        final Pair<P, Map<K, ? extends V>> thingOne, final Pair<P, Map<K, ? extends V>> thingTwo) {
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#onMapEntry(Metadata, java.lang.Object, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult onMapEntry(final Metadata<P, Map<K, ? extends V>> property, final K key,
	        final Pair<Map<K, ? extends V>, V> pairOne, final Pair<Map<K, ? extends V>, V> pairTwo) {
		return processMapEntry(property, key, pairOne, pairTwo);
	}

	/**
	 * @param <V>
	 * @param <K>
	 * @param <P>
	 * @param property
	 * @param pairOne
	 * @param pairTwo
	 * @return
	 */
	private <V, K, P> VisitorResult processMapEntry(final Metadata<P, Map<K, ? extends V>> property, final K key,
	        final Pair<Map<K, ? extends V>, V> pairOne, final Pair<Map<K, ? extends V>, V> pairTwo) {
		final V thingOne = pairOne.getSecond();
		final V thingTwo = pairTwo.getSecond();
		if (!Objects.equal(thingOne, thingTwo)) {
			delta().addMapDiff(property, key, pairOne.getFirst(), pairTwo.getFirst());
		}
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#beforeMetadataProperty(Metadata, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T, P> VisitorResult beforeMetadataProperty(final Metadata<P, T> property, final Pair<P, T> thingOne,
	        final Pair<P, T> thingTwo) {
		push(property, thingOne, thingTwo);
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#afterMetadataProperty(Metadata, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T, P> VisitorResult afterMetadataProperty(final Metadata<P, T> property, final Pair<P, T> thingOne,
	        final Pair<P, T> thingTwo) {
		this.workingDelta.pop();
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#beforeIterablesWithMetadata(Metadata, java.lang.Iterable, java.lang.Iterable)
	 */
	@Override
	public <T, P, I extends Iterable<? extends T>> VisitorResult beforeIterablesWithMetadata(
	        final Metadata<P, I> property, final I thingOne, final I thingTwo) {
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#afterIterablesWithMetadata(Metadata, java.lang.Iterable, java.lang.Iterable)
	 */
	@Override
	public <T, P, I extends Iterable<? extends T>> VisitorResult afterIterablesWithMetadata(
	        final Metadata<P, I> property, final I thingOne, final I thingTwo) {
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PairVisitor#beforeIterableItemWithMetadata(Metadata, int, Pair, Pair)
	 */
	@Override
	public <T, P, I extends Iterable<? extends T>> VisitorResult beforeIterableItemWithMetadata(
	        final Metadata<P, I> property, final int index, final Pair<I, T> pairOne, final Pair<I, T> pairTwo) {
		VisitorResult result = result();

		if (pairOne.getSecond() == null) {
			if (pairTwo.getSecond() != null) {
				processIterableItem(property, index, pairOne, pairTwo);
			}
			result = VisitorResult.SKIP;
		} else if (pairTwo.getSecond() == null) {
			processIterableItem(property, index, pairOne, pairTwo);
			result = VisitorResult.SKIP;
		}
		push(new IterableDelta<Iterable<? extends T>>(property, index, pairOne.getFirst(), pairTwo.getFirst()));

		return result;
	}

	/**
	 * @see PairVisitor#afterIterableItemWithMetadata(Metadata, int, Pair, Pair)
	 */
	@Override
	public <T, P, I extends Iterable<? extends T>> VisitorResult afterIterableItemWithMetadata(
	        final Metadata<P, I> property, final int index, final Pair<I, T> pairOne, final Pair<I, T> pairTwo) {
		this.workingDelta.pop();
		return result();
	}

	/**
	 * @see PairVisitor#beforeMapsWithMetadata(Metadata, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult beforeMapsWithMetadata(final Metadata<P, Map<K, ? extends V>> property,
	        final Pair<P, Map<K, ? extends V>> thingOne, final Pair<P, Map<K, ? extends V>> thingTwo) {
		return result();
	}

	/**
	 * @see PairVisitor#afterMapsWithMetadata(Metadata, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult afterMapsWithMetadata(final Metadata<P, Map<K, ? extends V>> property,
	        final Pair<P, Map<K, ? extends V>> thingOne, final Pair<P, Map<K, ? extends V>> thingTwo) {
		return result();
	}

	/**
	 * @see PairVisitor#beforeMapEntryWithMetadata(Metadata, java.lang.Object, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult beforeMapEntryWithMetadata(final Metadata<P, Map<K, ? extends V>> property,
	        final K key, final Pair<Map<K, ? extends V>, V> pairOne, final Pair<Map<K, ? extends V>, V> pairTwo) {
		final Map<K, ? extends V> a = pairOne.getFirst();
		final Map<K, ? extends V> b = pairTwo.getFirst();
		push(new MapEntryDelta<K, V>(property, key, a, b));
		return result();
	}

	/**
	 * @see PairVisitor#afterMapEntryWithMetadata(Metadata, java.lang.Object, Pair, Pair)
	 */
	@Override
	public <K, V, P> VisitorResult afterMapEntryWithMetadata(final Metadata<P, Map<K, ? extends V>> property,
	        final K key, final Pair<Map<K, ? extends V>, V> pairOne, final Pair<Map<K, ? extends V>, V> pairTwo) {
		this.workingDelta.pop();
		return result();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDelta().toString();
	}

	/**
	 * @return the delta
	 */
	@SuppressWarnings("unchecked")
	public Delta<D> getDelta() {
		return (Delta<D>) this.workingDelta.get(0);
	}

	private Delta<?> delta() {
		return this.workingDelta.peek();
	}

	private <T, V> Delta<T> push(final Metadata<?, ?> property, final T left, final T right) {
		return push(new Delta<T>(property, left, right));
	}

	private <T> Delta<T> push(final Delta<T> newDelta) {
		delta().addChild(newDelta);
		this.workingDelta.push(newDelta);
		return newDelta;
	}

	protected VisitorResult result() {
		return VisitorResult.CONTINUE;
	}

}
