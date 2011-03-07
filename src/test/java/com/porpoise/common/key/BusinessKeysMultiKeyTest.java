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
    }

    class B extends AbstractBusinessEquality<B> {
        @BusinessKey(type = { "alpha" })
        public A parent;
        @BusinessKey(type = { "alpha", "just b" })
        public int property;
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
}
