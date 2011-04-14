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

    private final Stack<Delta<?>> workingDelta = new Stack<Delta<?>>();

    /**
     * @param name
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
    public <T, P> VisitorResult onProperty(final Metadata<P> property, final Pair<P, T> pairOne, final Pair<P, T> pairTwo) {
        final T thingOne = pairOne.getSecond();
        final T thingTwo = pairTwo.getSecond();
        if (!Objects.equal(thingOne, thingTwo)) {
            delta().addDiff(property, thingOne, thingTwo);
        }
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#onIterables(Metadata, java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult onIterables(final Metadata<P> property, final Iterable<T> thingOne, final Iterable<T> thingTwo) {
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#onIterableItem(Metadata, int, Pair, Pair)
     */
    @Override
    public <T, P> VisitorResult onIterableItem(final Metadata<P> property, final int index, final Pair<? extends Iterable<T>, T> pairOne,
            final Pair<? extends Iterable<T>, T> pairTwo) {
        return processIterableItem(property, index, pairOne, pairTwo);
    }

    private <T, P> VisitorResult processIterableItem(final Metadata<P> property, final int index, final Pair<? extends Iterable<T>, T> pairOne,
            final Pair<? extends Iterable<T>, T> pairTwo) {
        final T thingOne = pairOne.getSecond();
        final T thingTwo = pairTwo.getSecond();
        if (!Objects.equal(thingOne, thingTwo)) {
            delta().addIterableDiff(property, index, thingOne, thingTwo);
        }
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#onMaps(Metadata, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMaps(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#onMapEntry(Metadata, java.lang.Object, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMapEntry(final Metadata<P> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
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
    private <V, K, P> VisitorResult processMapEntry(final Metadata<P> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        final V thingOne = pairOne.getSecond();
        final V thingTwo = pairTwo.getSecond();
        if (!Objects.equal(thingOne, thingTwo)) {
            delta().addMapDiff(property, key, pairOne.getFirst(), pairTwo.getFirst());
        }
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#beforeMetadataProperty(Metadata, java.lang.Object, java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult beforeMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        push(property, thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#afterMetadataProperty(Metadata, java.lang.Object, java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult afterMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#beforeIterablesWithMetadata(Metadata, java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult beforeIterablesWithMetadata(final Metadata<P> property, final Iterable<T> thingOne, final Iterable<T> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#afterIterablesWithMetadata(Metadata, java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult afterIterablesWithMetadata(final Metadata<P> property, final Iterable<T> thingOne, final Iterable<T> thingTwo) {
        // this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see PairVisitor#beforeIterableItemWithMetadata(Metadata, int, Pair, Pair)
     */
    @Override
    public <T, P> VisitorResult beforeIterableItemWithMetadata(final Metadata<P> property, final int index, final Pair<? extends Iterable<T>, T> pairOne,
            final Pair<? extends Iterable<T>, T> pairTwo) {
        VisitorResult result = VisitorResult.CONTINUE;

        if (pairOne.getSecond() == null) {
            if (pairTwo.getSecond() != null) {
                processIterableItem(property, index, pairOne, pairTwo);
            }
            result = VisitorResult.SKIP;
        } else if (pairTwo.getSecond() == null) {
            processIterableItem(property, index, pairOne, pairTwo);
            result = VisitorResult.SKIP;
        }

        push(property, pairOne.getFirst(), pairTwo.getFirst());

        return result;
    }

    /**
     * @see PairVisitor#afterIterableItemWithMetadata(Metadata, int, Pair, Pair)
     */
    @Override
    public <T, P> VisitorResult afterIterableItemWithMetadata(final Metadata<P> property, final int index, final Pair<? extends Iterable<T>, T> pairOne,
            final Pair<? extends Iterable<T>, T> pairTwo) {
        this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /**
     * @see PairVisitor#beforeMapsWithMetadata(Metadata, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapsWithMetadata(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /**
     * @see PairVisitor#afterMapsWithMetadata(Metadata, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapsWithMetadata(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        return VisitorResult.CONTINUE;
    }

    /**
     * @see PairVisitor#beforeMapEntryWithMetadata(Metadata, java.lang.Object, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapEntryWithMetadata(final Metadata<P> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        push(property, pairOne.getFirst(), pairTwo.getFirst());
        return VisitorResult.CONTINUE;
    }

    /**
     * @see PairVisitor#afterMapEntryWithMetadata(Metadata, java.lang.Object, Pair, Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapEntryWithMetadata(final Metadata<P> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        this.workingDelta.pop();
        return VisitorResult.CONTINUE;
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

    private <T> Delta<T> push(final Metadata<?> property, final T left, final T right) {
        final Delta<T> newDelta = new Delta<T>(property, left, right);
        delta().addChild(newDelta);
        this.workingDelta.push(newDelta);
        return newDelta;
    }

}
