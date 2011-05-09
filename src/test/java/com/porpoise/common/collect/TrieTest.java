package com.porpoise.common.collect;

import org.junit.Assert;
import org.junit.Test;

public class TrieTest {

    @Test
    public void testFindClosest() {
        final Trie<Integer> trie = Trie.valueOf("com.acme.one");
        trie.put("com.acme.two");
        trie.put("com.acme.three");

        System.out.println(trie.toString());

        Assert.assertEquals("com.acme.", trie.findClosest("com.acme.blah").prefix());
    }
}
