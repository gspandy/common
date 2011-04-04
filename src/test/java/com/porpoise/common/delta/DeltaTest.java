package com.porpoise.common.delta;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.core.Pair;
import com.porpoise.common.metadata.BaseMetadataProperty;
import com.porpoise.common.metadata.Metadata;
import com.porpoise.common.metadata.MetadataProperty;

public class DeltaTest {
    class A {
        String name;
        B      bee;
    }

    class B {
        int something;
        A   aye;
    }

    private A left;
    private A right;

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
        final Metadata<A> metadataA = mda();
        final Delta delta = DeltaFactory.valueOf(this.left, this.right, metadataA);
        System.out.println(delta);
    }

    public Metadata<DeltaTest.B> mdb() {
        final Metadata<DeltaTest.B> b = new Metadata<DeltaTest.B>() {

            @Override
            public Map<String, Function<B, ? extends Object>> valuesByName() {
                final Map<String, Function<B, ? extends Object>> map = Maps.newHashMap();
                final Function<B, Integer> fnc = new Function<DeltaTest.B, Integer>() {

                    @SuppressWarnings("boxing")
                    @Override
                    public Integer apply(final B input) {
                        return input.something;
                    }
                };
                map.put("something", fnc);
                return map;
            }

            @Override
            public Iterable<MetadataProperty<B>> simpleProperties() {
                final List<MetadataProperty<B>> list = Lists.newArrayList();
                final Function<B, Pair<Metadata<A>, A>> value = new Function<DeltaTest.B, Pair<Metadata<A>, A>>() {
                    @Override
                    public Pair<Metadata<A>, A> apply(final B input) {
                        return Pair.valueOf(mda(), input.aye);
                    }
                };
                list.add(new BaseMetadataProperty(this, "aye", value));
                return list;
            }
        };
        return b;
    }

    public Metadata<A> mda() {
        final Metadata<A> metadataA = new Metadata<DeltaTest.A>() {

            @Override
            public Map<String, Function<A, ? extends Object>> valuesByName() {
                final Map<String, Function<A, ? extends Object>> map = Maps.newHashMap();
                final Function<A, String> fnc = new Function<DeltaTest.A, String>() {

                    @Override
                    public String apply(final A input) {
                        return input.name;
                    }
                };
                map.put("name", fnc);
                return map;
            }

            @Override
            public Iterable<MetadataProperty<A>> simpleProperties() {
                final List<MetadataProperty<A>> list = Lists.newArrayList();
                final Function<A, Pair<Metadata<B>, B>> value = new Function<DeltaTest.A, Pair<Metadata<B>, B>>() {
                    @Override
                    public Pair<Metadata<B>, B> apply(final A input) {
                        return Pair.valueOf(mdb(), input.bee);
                    }
                };
                list.add(new BaseMetadataProperty(this, "bee", value));
                return list;
            }
        };
        return metadataA;
    }
}
