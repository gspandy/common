package com.porpoise.common.core;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.porpoise.common.delta.Delta;

/**
 * 
 */
public class DeltaTest {

    static class Garage {
        public int sizeInCubicCentimeters;
        public List<Bike> bikes = Lists.newArrayList();
    }

    static class Bike {
        public String name;
        public int weightInKg;
        public int mileage;
    }

    static Mutator<Garage, Integer> SET_SIZE = new Mutator<Garage, Integer>() {
        @Override
        public void update(final Garage object, final Integer value) {
            object.sizeInCubicCentimeters = value.intValue();
        }
    };
    static Mutator<Garage, List<Bike>> SET_BIKES = new Mutator<DeltaTest.Garage, List<Bike>>() {
        @Override
        public void update(final Garage input, final List<Bike> value) {
            input.bikes = value;
        }
    };
    static Function<Garage, List<Bike>> GET_BIKES = new Function<DeltaTest.Garage, List<Bike>>() {
        @Override
        public List<Bike> apply(final Garage input) {
            return input.bikes;
        }
    };
    static Mutator<Bike, Integer> SET_WEIGHT = new Mutator<DeltaTest.Bike, Integer>() {
        @Override
        public void update(final Bike input, final Integer value) {
            input.weightInKg = value;
        }
    };
    static Mutator<Bike, Integer> SET_MILEAGE = new Mutator<DeltaTest.Bike, Integer>() {
        @Override
        public void update(final Bike input, final Integer value) {
            input.mileage = value;
        }
    };
    static Mutator<Bike, String> SET_NAME = new Mutator<DeltaTest.Bike, String>() {
        @Override
        public void update(final Bike input, final String value) {
            input.name = value;
        }
    };
    private Delta<Garage> delta;

    @Before
    public void setup() {
        final Delta.Builder<Garage> garageBuilder = Delta.newBuilder();
        garageBuilder.addDiff("sizeInCubicCentimeters", 1, 2, SET_SIZE);
        final Delta.Builder<Bike> bikeBuilder = garageBuilder.pushCollection("bikes", GET_BIKES);
        bikeBuilder.addDiff("name", "one", "two", SET_NAME).addDiff("weight", 1, 2, SET_WEIGHT);

        this.delta = garageBuilder.build();
    }

    /**
     */
    @Test
    public void testToString() {
        final String string = this.delta.toString();
        Assert.assertTrue(string.contains("sizeInCubicCentimeters [1,2]"));
        Assert.assertTrue(string.contains("bikes.weight [1,2]"));
        Assert.assertTrue(string.contains("bikes.name [one,two]"));
    }

    /**
     */
    @Test
    public void testUpdate() {
        final Garage updated = this.delta.update(new Garage());
    }
}
