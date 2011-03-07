package com.porpoise.common.key;

import org.junit.Assert;
import org.junit.Test;

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
            return String.format("A[%s,%s]", alpha, beta);
        }

    }

    class B extends AbstractBusinessEquality<B> {
        @BusinessKey(type = { "alpha" })
        public A   parent;
        @BusinessKey(type = { "alpha", "just b" })
        public int property;

        @Override
        public String toString() {
            return String.format("B{%s,%s}", parent, property);
        }
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
