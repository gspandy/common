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
     * @param pairOne
     * @param pairTwo
     * @return the visitor result
     */
    <T, P> VisitorResult onProperty(Metadata<P, T> property, Pair<P, T> pairOne, Pair<P, T> pairTwo);

    /**
     * @param <T>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <T, P> VisitorResult beforeMetadataProperty(Metadata<P, T> property, Pair<P, T> thingOne, Pair<P, T> thingTwo);

    /**
     * @param <T>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <T, P> VisitorResult afterMetadataProperty(Metadata<P, T> property, Pair<P, T> thingOne, Pair<P, T> thingTwo);

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
    <P, I extends Iterable<?>> VisitorResult onIterables(Metadata<P, I> property, I thingOne, I thingTwo);

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
    <T, P, I extends Iterable<T>> VisitorResult beforeIterablesWithMetadata(Metadata<P, I> property, I thingOne, I thingTwo);

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
    <T, P, I extends Iterable<T>> VisitorResult afterIterablesWithMetadata(Metadata<P, I> property, I thingOne, I thingTwo);

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
    <T, P, I extends Iterable<T>> VisitorResult onIterableItem(Metadata<P, I> property, int index, Pair<I, T> pairOne, Pair<I, T> pairTwo);

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
    <T, P, I extends Iterable<T>> VisitorResult beforeIterableItemWithMetadata(Metadata<P, I> property, int index, Pair<I, T> pairOne, Pair<I, T> pairTwo);

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
    <T, P, I extends Iterable<T>> VisitorResult afterIterableItemWithMetadata(Metadata<P, I> property, int index, Pair<I, T> pairOne, Pair<I, T> pairTwo);

    /**
     * @param <K>
     * @param <V>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <K, V, P> VisitorResult onMaps(Metadata<P, Map<K, V>> property, Pair<P, Map<K, V>> thingOne, Pair<P, Map<K, V>> thingTwo);

    /**
     * @param <K>
     * @param <V>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <K, V, P> VisitorResult beforeMapsWithMetadata(Metadata<P, Map<K, V>> property, Pair<P, Map<K, V>> thingOne, Pair<P, Map<K, V>> thingTwo);

    /**
     * @param <K>
     * @param <V>
     * @param <P>
     * @param property
     * @param thingOne
     * @param thingTwo
     * @return the visitor result
     */
    <K, V, P> VisitorResult afterMapsWithMetadata(Metadata<P, Map<K, V>> property, Pair<P, Map<K, V>> thingOne, Pair<P, Map<K, V>> thingTwo);

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
    <K, V, P> VisitorResult onMapEntry(Metadata<P, Map<K, V>> property, K key, Pair<Map<K, V>, V> pairOne, Pair<Map<K, V>, V> pairTwo);

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
    <K, V, P> VisitorResult beforeMapEntryWithMetadata(Metadata<P, Map<K, V>> property, K key, Pair<Map<K, V>, V> pairOne, Pair<Map<K, V>, V> pairTwo);

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
    <K, V, P> VisitorResult afterMapEntryWithMetadata(Metadata<P, Map<K, V>> property, K key, Pair<Map<K, V>, V> pairOne, Pair<Map<K, V>, V> pairTwo);

}
