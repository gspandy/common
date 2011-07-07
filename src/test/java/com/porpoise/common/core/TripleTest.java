package com.porpoise.common.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.porpoise.common.test.Assertions;

/**
 * 
 */
public class TripleTest {

    private Triple<Integer, Integer, Integer> left;
    private Triple<Integer, Integer, Integer> right;

    /**
     * setup the test
     */
    @Before
    @SuppressWarnings("boxing")
    public void setup() {
        this.left = Triple.valueOf(3, 2, 1);
        this.right = Triple.valueOf(1, 2, 3);
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
        Assertions.assertNonEquality(this.left, Triple.valueOf(3, 2, null));
        Assertions.assertNonEquality(this.left, Triple.valueOf(3, null, 1));
        Assertions.assertNonEquality(this.left, Triple.valueOf(null, 2, 1));
        Assertions.assertNonEquality(this.left, Triple.valueOf(null, null, null));
        Assertions.assertEquality(Triple.valueOf(null, null, null), Triple.valueOf(null, null, null));
    }

}
