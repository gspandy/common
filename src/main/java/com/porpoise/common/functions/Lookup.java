package com.porpoise.common.functions;

import java.util.Map;

import com.google.common.base.Function;

/**
 * 
 */
public interface Lookup {

    public <T> Map<String, Function<T, ?>> propertyLookupForClass(Class<T> c1ass);

}
