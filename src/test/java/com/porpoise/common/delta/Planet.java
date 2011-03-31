package com.porpoise.common.delta;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class Planet {
    public static Function<Planet, String>            NAME  = new Function<Planet, String>() {
                                                                @Override
                                                                public String apply(final Planet input) {
                                                                    return input.name;
                                                                }
                                                            };
    public static Function<Planet, Map<String, Moon>> MOONS = new Function<Planet, Map<String, Moon>>() {
                                                                @Override
                                                                public Map<String, Moon> apply(final Planet input) {
                                                                    return input.moonByName;
                                                                }
                                                            };

    public Planet(final String name) {
        this.name = name;
    }

    public String            name;
    public Map<String, Moon> moonByName = Maps.newHashMap();

    /**
     * @param nameParam
     * @param radius
     * @return the new moon
     */
    public Moon addMoon(final String nameParam, final int radius) {
        final Moon m = new Moon(nameParam, radius);
        this.moonByName.put(nameParam, m);
        return m;
    }
}
