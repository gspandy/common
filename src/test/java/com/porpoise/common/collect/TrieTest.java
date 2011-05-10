package com.porpoise.common.collect;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TrieTest {

    private Trie<Object> trie;

    @Before
    public void setup() {
        this.trie = Trie.valueOf("com.acme.one");
        this.trie.put("com.acme.two");
        this.trie.put("com.acme.three");
    }

    @Test
    public void testFindClosest() {
        Assert.assertEquals("com.acme.", this.trie.findClosest("com.acme.blah").prefix());
        Assert.assertEquals("com.acme.th", this.trie.findClosest("com.acme.thirteen").prefix());
        Assert.assertNull(this.trie.findClosest("dave"));
    }

    @Test
    public void testLongestPrefix() {
        Assert.assertEquals("com.acme.", this.trie.longestPrefix());
    }
}
