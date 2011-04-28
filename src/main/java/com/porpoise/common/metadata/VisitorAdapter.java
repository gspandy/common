package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

/**
 * Adapter pattern for the {@link PairVisitor}
 */
public class VisitorAdapter implements PairVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult onProperty(final Metadata<P, T> property, final Pair<P, T> pairOne, final Pair<P, T> pairTwo) {
        final T thingOne = pairOne.getSecond();
        final T thingTwo = pairTwo.getSecond();
        log("onProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <P, I extends Iterable<?>> VisitorResult onIterables(final Metadata<P, I> property, final I thingOne, final I thingTwo) {
        log("onIterables(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P, I extends Iterable<? extends T>> VisitorResult onIterableItem(final Metadata<P, I> property, final int index, final Pair<I, T> pairOne, final Pair<I, T> pairTwo) {
        log("onIterableItem(%s, %s, %s, %s)", property.propertyName(), Integer.valueOf(index), pairOne.getSecond(), pairTwo.getSecond());
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onMaps(com.porpoise.common.metadata.Metadata, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMaps(final Metadata<P, Map<K, V>> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        log("onMaps(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V, P> VisitorResult onMapEntry(final Metadata<P, Map<K, V>> property, final K key, final Pair<Map<K, V>, V> first, final Pair<Map<K, V>, V> second) {
        log("onMapEntry(%s, %s, %s, %s)", property.propertyName(), key, first.getSecond(), second.getSecond());
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult beforeMetadataProperty(final Metadata<P, T> property, final Pair<P, T> thingOne, final Pair<P, T> thingTwo) {
        log("beforeMetadataProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult afterMetadataProperty(final Metadata<P, T> property, final Pair<P, T> thingOne, final Pair<P, T> thingTwo) {
        log("afterMetadataProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeIterablesWithMetadata(com.porpoise.common.metadata.Metadata, java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P, I extends Iterable<T>> VisitorResult beforeIterablesWithMetadata(final Metadata<P, I> property, final I thingOne, final I thingTwo) {
        log("beforeIterablesWithMetadata(%s,  %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterIterablesWithMetadata(com.porpoise.common.metadata.Metadata, java.lang.Iterable, java.lang.Iterable)
     */
    @Override
    public <T, P, I extends Iterable<T>> VisitorResult afterIterablesWithMetadata(final Metadata<P, I> property, final I thingOne, final I thingTwo) {
        log("afterIterablesWithMetadata(%s,  %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeIterableItemWithMetadata(com.porpoise.common.metadata.Metadata, int, com.porpoise.common.core.Pair,
     * com.porpoise.common.core.Pair)
     */
    @Override
    public <T, P, I extends Iterable<T>> VisitorResult beforeIterableItemWithMetadata(final Metadata<P, I> property, final int index, final Pair<I, T> pairOne,
            final Pair<I, T> pairTwo) {
        log("beforeIterableItemWithMetadata(%s, %s, %s, %s)", property.propertyName(), Integer.valueOf(index), pairOne, pairTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterIterableItemWithMetadata(com.porpoise.common.metadata.Metadata, int, com.porpoise.common.core.Pair,
     * com.porpoise.common.core.Pair)
     */
    @Override
    public <T, P, I extends Iterable<T>> VisitorResult afterIterableItemWithMetadata(final Metadata<P, I> property, final int index, final Pair<I, T> pairOne,
            final Pair<I, T> pairTwo) {
        log("afterIterableItemWithMetadata(%s, %s, %s, %s)", property.propertyName(), Integer.valueOf(index), pairOne, pairTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeMapsWithMetadata(com.porpoise.common.metadata.Metadata, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapsWithMetadata(final Metadata<P, Map<K, V>> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        log("beforeMapsWithMetadata(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterMapsWithMetadata(com.porpoise.common.metadata.Metadata, com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapsWithMetadata(final Metadata<P, Map<K, V>> property, final Pair<P, Map<K, V>> thingOne, final Pair<P, Map<K, V>> thingTwo) {
        log("afterMapsWithMetadata(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#beforeMapEntryWithMetadata(com.porpoise.common.metadata.Metadata, java.lang.Object, com.porpoise.common.core.Pair,
     * com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult beforeMapEntryWithMetadata(final Metadata<P, Map<K, V>> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        log("beforeMapEntryWithMetadata(%s, %s, %s, %s)", property.propertyName(), key, pairOne, pairTwo);
        return VisitorResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#afterMapEntryWithMetadata(com.porpoise.common.metadata.Metadata, java.lang.Object, com.porpoise.common.core.Pair,
     * com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult afterMapEntryWithMetadata(final Metadata<P, Map<K, V>> property, final K key, final Pair<Map<K, V>, V> pairOne, final Pair<Map<K, V>, V> pairTwo) {
        log("afterMapEntryWithMetadata(%s, %s, %s, %s)", property.propertyName(), key, pairOne, pairTwo);
        return VisitorResult.CONTINUE;
    }

    protected void log(final String format, final Object... args) {
        System.out.println(String.format(format, args));
    }

}
