package com.porpoise.common.metadata;

import java.util.Map;

import com.google.common.base.Function;

/**
 */
public interface Metadata<T> {

    Map<String, Function<T, ? extends Object>> valuesByName();

    Iterable<MetadataProperty<T>> simpleProperties();

}
