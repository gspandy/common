package com.porpoise.common.delta;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

@SuppressWarnings("synthetic-access")
public class SolarSystem {
    public static Function<SolarSystem, String>       NAME    = new Function<SolarSystem, String>() {
                                                                  @Override
                                                                  public String apply(final SolarSystem input) {
                                                                      return input.name;
                                                                  }
                                                              };
    public static Function<SolarSystem, List<Planet>> PLANETS = new Function<SolarSystem, List<Planet>>() {
                                                                  @Override
                                                                  public List<Planet> apply(final SolarSystem input) {
                                                                      return input.planets;
                                                                  }
                                                              };

    private String                                    name;
    private final List<Planet>                        planets = Lists.newArrayList();

    /**
     * @param nameParam
     * @return the new planet
     */
    public Planet addPlanet(final String nameParam) {
        final Planet p = new Planet(nameParam);
        this.planets.add(p);
        return p;
    }
}
