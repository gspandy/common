package com.porpoise.common.collect;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link Trie} class
 */
public class TrieTest {

    private Trie<Object> trie;

    /**
     * setup a trie to test
     */
    @Before
    public void setup() {
        this.trie = Trie.valueOf("com.acme.one");
        this.trie.put("com.acme.two");
        this.trie.put("com.acme.three");
    }

    /**
     * test for {@link Trie#leaves()}
     * 
     * test a subnode can return its leaves
     */
    @Test
    public void testLeavesFromSubnode() {

        // let's add a couple more nodes
        this.trie.put("com.acme.two.A");
        this.trie.put("com.acme.two.B");

        // call the method under test
        final Map<String, Object> leaves = this.trie.findClosest("com.acme.two.").leaves();
        Assert.assertEquals(2, leaves.size());
        Assert.assertTrue(leaves.containsKey("com.acme.two.A"));
        Assert.assertTrue(leaves.containsKey("com.acme.two.B"));

        // lets test the 'com.acme.three' key too, just for fun
        Assert.assertEquals(1, this.trie.findClosest("com.acme.th").leaves().size());
        Assert.assertTrue(this.trie.findClosest("com.acme.th").leaves().containsKey("com.acme.three"));
    }

    /**
     * test for {@link Trie#leaves()}
     */
    @SuppressWarnings("boxing")
    @Test
    public void testLeaves() {
        final Trie<Integer> root = Trie.valueOf("eats");
        root.put("shoots", 1);
        root.put("and", 2);
        root.put("leaves", 3);

        root.put("leavesSubstring", 4);
        root.put("andandand", 5);
        root.put("shootsHoops");

        // call the method under test
        final Map<String, Integer> leaves = root.leaves();

        Assert.assertEquals(7, leaves.size());

        Assert.assertTrue(leaves.containsKey("eats"));
        Assert.assertNull(leaves.get("eats"));

        Assert.assertEquals(1, leaves.get("shoots").intValue());
        Assert.assertEquals(2, leaves.get("and").intValue());
        Assert.assertEquals(3, leaves.get("leaves").intValue());
        Assert.assertEquals(4, leaves.get("leavesSubstring").intValue());
        Assert.assertEquals(5, leaves.get("andandand").intValue());

        Assert.assertTrue(leaves.containsKey("shootsHoops"));
        Assert.assertNull(leaves.get("shootsHoops"));
    }

    /**
     * test for {@link Trie#findClosest(String)}
     */
    @Test
    public void testFindClosest() {
        Assert.assertEquals("com.acme.", this.trie.findClosest("com.acme.blah").prefix());
        Assert.assertEquals("com.acme.th", this.trie.findClosest("com.acme.thirteen").prefix());
        Assert.assertEquals("", this.trie.findClosest("dave").prefix());
    }

    /**
     * test for {@link Trie#longestPrefix()}
     */
    @Test
    public void testLongestPrefix() {
        Assert.assertEquals("com.acme.", this.trie.longestPrefix());
        Assert.assertEquals("", Trie.longestPrefix("abc", "def"));
        Assert.assertEquals("abc", Trie.longestPrefix("abc", "abcd"));
        Assert.assertEquals("a", Trie.longestPrefix("abc", "aA"));
        Assert.assertEquals("a", Trie.longestPrefix("a"));
        Assert.assertEquals("", Trie.longestPrefix(""));
        Assert.assertEquals("", Trie.longestPrefix("d", "", "", "d"));
        Assert.assertEquals("a", Trie.longestPrefix("abc", "a1"));
    }
}
