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
    public <T, P> VisitorResult onProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        log("onProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult onIterables(final Metadata<P> property, final Iterable<T> thingOne,
            final Iterable<T> thingTwo) {
        log("onIterables(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult onIterableItem(final Metadata<P> property, final int index,
            final Pair<? extends Iterable<T>, T> pairOne, final Pair<? extends Iterable<T>, T> pairTwo) {
        log("onIterableItem(%s, %s, %s, %s)", property.propertyName(), index, pairOne.getSecond(), pairTwo.getSecond());
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onMaps(com.porpoise.common.metadata.Metadata,
     *      com.porpoise.common.core.Pair, com.porpoise.common.core.Pair)
     */
    @Override
    public <K, V, P> VisitorResult onMaps(final Metadata<P> property, final Pair<P, Map<K, V>> thingOne,
            final Pair<P, Map<K, V>> thingTwo) {
        log("onMaps(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K, V, P> VisitorResult onMapEntry(final Metadata<P> property, final K key, final Pair<Map<K, V>, V> first,
            final Pair<Map<K, V>, V> second) {
        log("onMapEntry(%s, %s, %s, %s)", property.propertyName(), key, first.getSecond(), second.getSecond());
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult beforeMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        log("beforeMetadataProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
        return VisitorResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, P> VisitorResult afterMetadataProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
        log("afterMetadataProperty(%s, %s, %s)", property.propertyName(), thingOne, thingTwo);
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
        log("beforeIterablesWithMetadata(%s,  %s, %s)", property.propertyName(), thingOne, thingTwo);
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
        log("afterIterablesWithMetadata(%s,  %s, %s)", property.propertyName(), thingOne, thingTwo);
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
        log("beforeIterableItemWithMetadata(%s, %s, %s, %s)", property.propertyName(), Integer.valueOf(index), pairOne,
                pairTwo);
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
        log("afterIterableItemWithMetadata(%s, %s, %s, %s)", property.propertyName(), Integer.valueOf(index), pairOne,
                pairTwo);
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
        log("beforeMapsWithMetadata(%s, %s, %s, %s)", property.propertyName(), thingOne, thingTwo);
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
        log("afterMapsWithMetadata(%s, %s, %s, %s)", property.propertyName(), thingOne, thingTwo);
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
        log("beforeMapEntryWithMetadata(%s, %s, %s, %s)", property.propertyName(), key, pairOne, pairTwo);
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
        log("afterMapEntryWithMetadata(%s, %s, %s, %s)", property.propertyName(), key, pairOne, pairTwo);
        return VisitorResult.CONTINUE;
    }

    protected void log(final String format, final Object... args) {
        System.out.println(String.format(format, args));
    }

}
