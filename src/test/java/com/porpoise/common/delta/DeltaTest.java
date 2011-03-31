package com.porpoise.common.delta;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class DeltaTest {
    class A {
        String name;
        B      bee;
    }

    class B {
        int something;
        A   aye;
    }

    private static final Lookup diffLookup = new Lookup() {
                                               @Override
                                               public <T> Map<String, Function<T, ? extends Object>> propertyLookupForClass(final Class<T> c1ass) {
                                                   final Map<String, Function<T, ? extends Object>> map = Maps.newHashMap();
                                                   if (c1ass == A.class) {
                                                       map.put("name", new Function<T, String>() {
                                                           @Override
                                                           public String apply(final T input) {
                                                               return ((A) input).name;
                                                           }
                                                       });
                                                       map.put("bee", new Function<T, B>() {
                                                           @Override
                                                           public B apply(final T input) {
                                                               return ((A) input).bee;
                                                           }
                                                       });
                                                   } else {
                                                       map.put("something", new Function<T, Integer>() {
                                                           @Override
                                                           @SuppressWarnings("boxing")
                                                           public Integer apply(final T input) {
                                                               return ((B) input).something;
                                                           }
                                                       });
                                                       map.put("bee", new Function<T, A>() {

                                                           @Override
                                                           public A apply(final T input) {
                                                               return ((B) input).aye;
                                                           }
                                                       });
                                                   }
                                                   return map;
                                               }
                                           };
    private A                   left;
    private A                   right;

    @Before
    public void setup() {
        this.left = new A();
        this.left.name = "red";
        this.left.bee = new B();
        this.left.bee.something = 4;
        this.left.bee.aye = new A();
        this.left.bee.aye.name = "purple";

        this.right = new A();
        this.right.name = "green";
        this.right.bee = new B();
        this.right.bee.something = 5;
        this.right.bee.aye = this.right;
    }

    /**
     * 
     */
    @Test
    public void testSimpleDiff() {
        final Delta delta = Delta.valueOf(this.left, this.right, diffLookup);
        System.out.println(delta);
    }
}
