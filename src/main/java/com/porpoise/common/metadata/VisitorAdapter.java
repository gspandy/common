package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

/**
 * Adapter pattern for the {@link PairVisitor}
 */
public abstract class VisitorAdapter implements PairVisitor {

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

    protected void log(final String format, final Object... args) {
        System.out.println(String.format(format, args));
    }
}
