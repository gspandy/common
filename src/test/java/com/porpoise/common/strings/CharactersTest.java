package com.porpoise.common.strings;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * tests for {@link Characters}
 */
public class CharactersTest {

    /**
     * test for {@link Characters#range(char, char)}
     */
    @Test
    public void test_range() {
        final Iterator<Character> iter = Characters.range('C', 'F').iterator();
        Assert.assertEquals('C', iter.next().charValue());
        Assert.assertEquals('D', iter.next().charValue());
        Assert.assertEquals('E', iter.next().charValue());
        Assert.assertEquals('F', iter.next().charValue());
        Assert.assertFalse(iter.hasNext());

        // no idea what the ascii character values actually are - just make sure we don't blow up or loop forever
        Assert.assertNotNull(Characters.range('1', 'a'));
        Assert.assertNotNull(Characters.range('D', '7'));
        Assert.assertNotNull(Characters.range(';', '_'));
        Assert.assertNotNull(Characters.range('!', ' '));
        Assert.assertNotNull(Characters.range('_', '!'));

    }
}
