package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

/**
 */
public interface PairVisitor {

    /**
     * @param <T>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <T, P> VisitorResult onProperty(Metadata<P> property, T thingOne, T thingTwo);

    /**
     * 
     * 
     * @param <T>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <T, P> VisitorResult onIterables(Metadata<P> property, Iterable<T> thingOne, Iterable<T> thingTwo);

    /**
     * 
     * @param <T>
     * @param <P>
     * @param property
     * @param index
     * @param pairOne
     * @param pairTwo
     * @return the visitor result
     */
    <T, P> VisitorResult onIterableItem(Metadata<P> property, int index, Pair<? extends Iterable<T>, T> pairOne,
            Pair<? extends Iterable<T>, T> pairTwo);

    /**
     * @param <K>
     * @param <V>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <K, V, P> VisitorResult onMaps(Metadata<P> property, Pair<P, Map<K, V>> thingOne, Pair<P, Map<K, V>> thingTwo);

    /**
     * @param <K>
     * @param <V>
     * @param <P>
     * @param property
     * @param key
     * @param pairOne
     * @param pairTwo
     * @return the visitor result
     */
    <K, V, P> VisitorResult onMapEntry(Metadata<P> property, K key, Pair<Map<K, V>, V> pairOne,
            Pair<Map<K, V>, V> pairTwo);

}
