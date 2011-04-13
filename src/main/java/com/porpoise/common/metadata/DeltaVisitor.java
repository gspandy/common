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
     * 
     */
    public DeltaVisitor() {
        this.workingDelta.push(new Delta<D>());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onProperty(com.porpoise.common.metadata.Metadata, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult onProperty(final Metadata<P> property, final Pair<P, T> pairOne,
            final Pair<P, T> pairTwo) {
        final T thingOne = pairOne.getSecond();
        final T thingTwo = pairTwo.getSecond();
        if (!Objects.equal(thingOne, thingTwo)) {
            delta().addDiff(property.propertyName(), thingOne, thingTwo);
        }
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onIterables(com.porpoise.common.metadata.Metadata,
     * java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult onIterables(final Metadata<P> property, final Iterable<T> thingOne,
            final Iterable<T> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onIterableItem(com.porpoise.common.metadata.Metadata, int,
     * com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <T, P> VisitorResult onIterableItem(final Metadata<P> property, final int index,
            final Pair<? extends Iterable<T>, T> pairOne, final Pair<? extends Iterable<T>, T> pairTwo) {
        @SuppressWarnings("boxing")
        final String propName = String.format("%s[%d]", property.propertyName(), index);
        return processIterableItem(propName, pairOne, pairTwo);
    }

    /**
     * @param <T>
     * @param <P>
     * @param property
     * @param index
     * @param pairOne
     * @param pairTwo
     * @return
     */
    private <T, P> VisitorResult processIterableItem(final String property,
            final Pair<? extends Iterable<T>, T> pairOne, final Pair<? extends Iterable<T>, T> pairTwo) {
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
     * @see com.porpoise.common.metadata.PairVisitor#onMaps(com.porpoise.common.metadata.Metadata,
     * com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMaps(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne,
            final Pair<P, Map<K, V>> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onMapEntry(com.porpoise.common.metadata.Metadata, java.lang.Object,
     * com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMapEntry(final Metadata<P> property, final K key,
            final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        final String prop = String.format("%s[%s]", property.propertyName(), key);
        return processMapEntry(prop, pairOne, pairTwo);
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
    private <V, K, P> VisitorResult processMapEntry(final String prop, final Pair<Map<K, V>, V> pairOne,
            final Pair<Map<K, V>, V> pairTwo) {
        final V thingOne = pairOne.getSecond();
        final V thingTwo = pairTwo.getSecond();
        if (!Objects.equal(thingOne, thingTwo)) {

            delta().addDiff(prop, thingOne, thingTwo);
        }
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeMetadataProperty(com.porpoise.common.metadata.Metadata,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult beforeMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterMetadataProperty(com.porpoise.common.metadata.Metadata,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult afterMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeIterablesWithMetadata(com.porpoise.common.metadata.Metadata,
     * java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult beforeIterablesWithMetadata(final Metadata<P> property, final Iterable<T> thingOne,
            final Iterable<T> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterIterablesWithMetadata(com.porpoise.common.metadata.Metadata,
     * java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P> VisitorResult afterIterablesWithMetadata(final Metadata<P> property, final Iterable<T> thingOne,
            final Iterable<T> thingTwo) {
        // this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.porpoise.common.metadata.PairVisitor#beforeIterableItemWithMetadata(com.porpoise.common.metadata.Metadata,
     * int, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <T, P> VisitorResult beforeIterableItemWithMetadata(final Metadata<P> property, final int index,
            final Pair<? extends Iterable<T>, T> pairOne, final Pair<? extends Iterable<T>, T> pairTwo) {
        @SuppressWarnings("boxing")
        final String propName = String.format("%s[%d]", property.propertyName(), index);

        if (pairOne.getSecond() == null) {
            if (pairTwo.getSecond() != null) {
                processIterableItem(propName, pairOne, pairTwo);
            }
            return VisitorResult.SKIP;
        } else if (pairTwo.getSecond() == null) {
            processIterableItem(propName, pairOne, pairTwo);
            return VisitorResult.SKIP;
        }

        push(propName);

        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.porpoise.common.metadata.PairVisitor#afterIterableItemWithMetadata(com.porpoise.common.metadata.Metadata,
     * int, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <T, P> VisitorResult afterIterableItemWithMetadata(final Metadata<P> property, final int index,
            final Pair<? extends Iterable<T>, T> pairOne, final Pair<? extends Iterable<T>, T> pairTwo) {
        this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeMapsWithMetadata(com.porpoise.common.metadata.Metadata,
     * com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapsWithMetadata(final Metadata<P> property,
            final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        // push(property.propertyName());
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterMapsWithMetadata(com.porpoise.common.metadata.Metadata,
     * com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapsWithMetadata(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne,
            final Pair<P, Map<K, V>> thingTwo) {
        // this.workingDelta.pop();
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeMapEntryWithMetadata(com.porpoise.common.metadata.Metadata,
     * java.lang.Object, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapEntryWithMetadata(final Metadata<P> property, final K key,
            final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        final String propName = String.format("%s[%s]", property.propertyName(), key);

        push(propName);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterMapEntryWithMetadata(com.porpoise.common.metadata.Metadata,
     * java.lang.Object, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapEntryWithMetadata(final Metadata<P> property, final K key,
            final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
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

    private <T> Delta<T> push(final String propertyName) {
        final Delta<T> newDelta = new Delta<T>();
        try {
            delta().addChild(propertyName, newDelta);
        } catch (final AssertionError e) {
            System.err.println(getDelta());
            System.err.println(e);
            throw e;
        }
        this.workingDelta.push(newDelta);
        return newDelta;
    }

}
