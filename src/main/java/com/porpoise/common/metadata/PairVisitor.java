package com.porpoise.common.metadata;

import java.util.Map;

import com.porpoise.common.core.Pair;

public interface PairVisitor {

    <T> VisitorResult onProperty(Metadata<?> property, T thingOne, T thingTwo);

    <T> VisitorResult onIterables(Metadata<?> property, Iterable<T> alpha, Iterable<T> beta);

    <T> VisitorResult onIterableItem(Metadata<?> property, int index, Pair<Iterable<T>, T> pairOne, Pair<Iterable<T>, T> pairTwo);

    <K, V> VisitorResult onMaps(Metadata<?> property, Map<K, V> thingOne, Map<K, V> thingTwo);

    <K, V> VisitorResult onMapEntry(Metadata<?> property, K key, Pair<Map<K, V>, V> first, Pair<Map<K, V>, V> second);

}
