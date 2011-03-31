package com.porpoise.common.delta;

import java.util.Map;

import com.google.common.base.Function;

/**
 * 
 */
public interface Lookup {

    public <T> Map<String, Function<T, ? extends Object>> propertyLookupForClass(Class<T> c1ass);

}
