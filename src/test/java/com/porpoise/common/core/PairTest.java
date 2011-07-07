package com.porpoise.common.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.porpoise.common.test.Assertions;

/**
 * 
 */
public class PairTest {

    private Pair<Integer, Integer> left;
    private Pair<Integer, Integer> right;

    /**
     * setup the test
     */
    @Before
    @SuppressWarnings("boxing")
    public void setup() {
        this.left = Pair.valueOf(2, 1);
        this.right = Pair.valueOf(1, 2);
    }

    /**
     * Test for equals and hash code for triples
     */
    @SuppressWarnings("boxing")
    @Test
    public void testEqualsAndHashCode() {
        Assert.assertTrue(this.left.hashCode() == this.left.hashCode());
        Assert.assertTrue(this.right.hashCode() == this.right.hashCode());

        Assertions.assertEquality(this.left, this.left);
        Assertions.assertEquality(this.right, this.right);
        Assertions.assertNonEquality(this.left, this.right);
        Assertions.assertNonEquality(this.left, Pair.valueOf(2, null));
        Assertions.assertNonEquality(this.left, Pair.valueOf(1, null));
        Assertions.assertNonEquality(this.left, Pair.valueOf(null, null));
        Assertions.assertEquality(Triple.valueOf(null, null, null), Triple.valueOf(null, null, null));
    }

}
