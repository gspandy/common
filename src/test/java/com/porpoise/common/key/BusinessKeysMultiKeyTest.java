package com.porpoise.common.key;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.porpoise.common.core.Pair;

public class BusinessKeysMultiKeyTest {

    class A extends AbstractBusinessEquality<A> {
        @BusinessKey(type = { "alpha", "both" })
        public String alpha;
        @BusinessKey(type = { "beta", "both" })
        public String beta;

        public A(final String a, final String b) {
            this.alpha = a;
            this.beta = b;
        }

        @Override
        public String toString() {
            return String.format("A[%s,%s]", this.alpha, this.beta);
        }

    }

    class B extends AbstractBusinessEquality<B> {
        @BusinessKey(type = { "alpha" })
        public A parent;
        @BusinessKey(type = { "alpha", "just b" })
        public int property;

        @Override
        public String toString() {
            return String.format("B{%s,%s}", this.parent, this.property);
        }
    }

    class C extends AbstractBusinessEquality<C> {
        @BusinessKey(type = { "alpha", "list" })
        public Collection<A> things = Lists.newArrayList();
    }

    private final BusinessKeys<A> keyForA = BusinessKeys.valueOf(A.class);
    private final BusinessKeys<B> keyForB = BusinessKeys.valueOf(B.class);

    /**
     * Test equality using two different equals types
     */
    @Test
    public void testDifferentEquality() {
        final BusinessKeys<A> key = BusinessKeys.valueOf(A.class);

        A thingOne = new A("ALPHA", "BETA");
        A thingTwo = new A("ALPHA", "GAMMA");
        Assert.assertTrue(key.equals("alpha", thingOne, thingTwo));
        Assert.assertFalse(key.equals("beta", thingOne, thingTwo));

        thingOne = new A("ALPHA", "BETA");
        thingTwo = new A("GAMMA", "BETA");
        Assert.assertFalse(key.equals("alpha", thingOne, thingTwo));
        Assert.assertTrue(key.equals("beta", thingOne, thingTwo));

        Assert.assertFalse(key.equals("both", new A("A1", "B1"), new A("A1", "B2")));
        Assert.assertFalse(key.equals("both", new A("A1", "B1"), new A("A2", "B1")));
        Assert.assertTrue(key.equals("both", new A("A1", "B1"), new A("A1", "B1")));
    }

    /**
     * Test equality using two different equals types
     */
    @Test
    public void testGroup() {
        final BusinessKeys<A> key = BusinessKeys.valueOf(A.class);

        final A thingOne = new A("ALPHA", "BETA");
        final A thingTwo = new A("ALPHA", "GAMMA");
        final A thingThree = new A("DELTA", "BETA");
        final A thingFour = new A("DELTA", "EPSILON");

        {
            // just for sanity, double-check our keys
            final String fail = "Precondition failed: inconsistent keys";
            Assert.assertEquals(fail, key.makeKey("alpha", thingOne), key.makeKey("alpha", thingTwo));
            Assert.assertEquals(fail, key.makeKey("alpha", thingThree), key.makeKey("alpha", thingFour));
            Assert.assertEquals(fail, key.makeKey("beta", thingOne), key.makeKey("beta", thingThree));
            Assert.assertFalse(fail, key.makeKey("beta", thingTwo).equals(key.makeKey("beta", thingFour)));
        }

        final List<A> all = Arrays.asList(thingOne, thingTwo, thingThree, thingFour);

        // assert alphas
        {
            final Map<Object, Collection<A>> byAlpha = key.group("alpha", all);
            Assert.assertEquals(2, byAlpha.size());
            final Collection<A> alphas = byAlpha.get(key.makeKey("alpha", thingOne));
            Assert.assertEquals(2, alphas.size());
            Assert.assertTrue(alphas.contains(thingOne));
            Assert.assertTrue(alphas.contains(thingTwo));

            final Collection<A> deltas = byAlpha.get(key.makeKey("alpha", thingThree));
            Assert.assertEquals(2, deltas.size());
            Assert.assertTrue(deltas.contains(thingThree));
            Assert.assertTrue(deltas.contains(thingFour));
        }

        // assert betas
        {
            final Map<Object, Collection<A>> byBeta = key.group("beta", all);
            Assert.assertEquals(3, byBeta.size());
            final Collection<A> betas = byBeta.get(key.makeKey("beta", thingOne));
            Assert.assertEquals(2, betas.size());
            Assert.assertTrue(betas.contains(thingOne));
            Assert.assertTrue(betas.contains(thingThree));

            final Collection<A> gammas = byBeta.get(key.makeKey("beta", thingTwo));
            Assert.assertEquals(1, gammas.size());
            Assert.assertTrue(gammas.contains(thingTwo));

            final Collection<A> epsilons = byBeta.get(key.makeKey("beta", thingFour));
            Assert.assertEquals(1, epsilons.size());
            Assert.assertTrue(epsilons.contains(thingFour));
        }

        // assert both
        {
            final Map<Object, Collection<A>> byBoth = key.group("both", all);
            Assert.assertEquals(4, byBoth.size());

            final List<A> copy = Lists.newArrayList(all);
            for (final Collection<A> values : byBoth.values()) {
                Assert.assertEquals(1, values.size());
                Assert.assertTrue(copy.removeAll(values));
            }
            Assert.assertTrue(copy.isEmpty());
        }
    }

    /**
     * Test equality using two different equals types
     */
    @Test
    public void testGroupUnique() {
        final BusinessKeys<A> key = BusinessKeys.valueOf(A.class);

        final A thingOne = new A("ALPHA", "BETA");
        final A thingTwo = new A("ALPHA", "GAMMA");
        final A thingThree = new A("DELTA", "BETA");
        final A thingFour = new A("DELTA", "EPSILON");

        final List<A> all = Arrays.asList(thingOne, thingTwo, thingThree, thingFour);

        // assert both
        {
            final Map<Object, A> byBoth = key.groupUnique("both", all);
            Assert.assertEquals(4, byBoth.size());

            final List<A> copy = Lists.newArrayList(all);
            for (final A value : byBoth.values()) {
                Assert.assertTrue(copy.remove(value));
            }
            Assert.assertTrue(copy.isEmpty());
        }
    }

    @Test
    public void testDifference() {
        final BusinessKeys<B> key = BusinessKeys.valueOf(B.class);

        final A first = new A("A1", "B1");
        final B child = new B();
        child.property = 123;
        child.parent = first;

        final A second = new A("A2", "B2");
        final B child2 = new B();
        child2.property = 456;
        child2.parent = second;

        final Map<String, Pair<Object, Object>> diff = key.differences("alpha", child, child2);
        Assert.assertEquals(2, diff.size());
        Assert.assertEquals(first, diff.get("parent").getFirst());
        Assert.assertEquals(second, diff.get("parent").getSecond());
        Assert.assertEquals(123, diff.get("property").getFirst());
        Assert.assertEquals(456, diff.get("property").getSecond());
    }

    @Test
    public void testGetPropertyFunction() {
        final BusinessKeys<B> key = BusinessKeys.valueOf(B.class);

        final A first = new A("A1", "B1");
        final B child = new B();
        child.property = 123;
        child.parent = first;

        final A second = new A("A2", "B2");
        final B child2 = new B();
        child2.property = 456;
        child2.parent = second;

        final Function<B, Integer> betas = key.getPropertyFunction("property");
        final Collection<Integer> byProperty = Collections2.transform(ImmutableList.of(child, child2), betas);
        Assert.assertEquals(2, byProperty.size());
        Assert.assertTrue(byProperty.contains(123));
        Assert.assertTrue(byProperty.contains(456));
    }

    /**
     * Test equality using two different equals types
     */
    @Test
    public void testToString() {
        final A first = new A("A1", "B1");
        final B child = new B();
        child.property = 123;
        child.parent = first;
        final String string = this.keyForB.toString("alpha", child);
        Assert.assertEquals("B{parent*=A{alpha*=A1}, property*=123}", string);
    }

    /**
     * Test equality using two different equals types
     */
    @Test
    public void testHashCode() {
        final Object key1;
        {
            final B child = new B();
            child.property = 123;
            child.parent = new A("A1", "B1");
            key1 = this.keyForB.makeKey("alpha", child);
        }

        final Object key2;
        {
            final B child2 = new B();
            child2.property = 123;
            child2.parent = new A("A1", "B differs");
            key2 = this.keyForB.makeKey("alpha", child2);
        }

        final Object keyDiffers;
        {
            final B child3 = new B();
            child3.property = 123;
            child3.parent = new A("Alpha property differs", "B1");
            keyDiffers = this.keyForB.makeKey("alpha", child3);
        }

        Assert.assertEquals(key1, key2);
        Assert.assertTrue(key1.equals(key2));
        Assert.assertTrue(key1.hashCode() == key2.hashCode());

        Assert.assertFalse(key1.equals(keyDiffers));
        Assert.assertFalse(keyDiffers.equals(key1));
        Assert.assertFalse(key1.hashCode() == keyDiffers.hashCode());
    }
}
