package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

/**
 * 
 */
public class DeltaVisitor implements PairVisitor {

    /**
     * 
     */
    public DeltaVisitor() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.metadata.PairVisitor#onProperty(com.porpoise.common.metadata.Metadata, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public <T, P> VisitorResult onProperty(final Metadata<P> property, final T thingOne, final T thingTwo) {
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return VisitorResult.CONTINUE;
    }

}
