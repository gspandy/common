package com.porpoise.common;

import org.junit.Assert;
import org.junit.Test;

public class LevenshteinTest {

    @Test
    public void testSameStringsAreEqual() {
        Assert.assertEquals(0, Levenshtein.distance("a", "a"));
        Assert.assertEquals(0, Levenshtein.distance("something", "something"));
        Assert.assertEquals(0, Levenshtein.distance("", ""));
    }

    @Test
    public void testOneAway() {
        Assert.assertEquals(1, Levenshtein.distance("abc", "abcd"));
        Assert.assertEquals(1, Levenshtein.distance("abce", "abcd"));
        Assert.assertEquals(1, Levenshtein.distance("Abcd", "abcd"));
        Assert.assertEquals(1, Levenshtein.distance("Abcd", "ABcd"));
        Assert.assertEquals(1, Levenshtein.distance("ABCd", "ABcd"));
        Assert.assertEquals(1, Levenshtein.distance("1", "12"));
        Assert.assertEquals(1, Levenshtein.distance("2", "12"));
        Assert.assertEquals(1, Levenshtein.distance("12", "1"));
        Assert.assertEquals(1, Levenshtein.distance("12", "2"));
    }

    @Test
    public void testTwoAway() {
        Assert.assertEquals(2, Levenshtein.distance("abdc", "abcd"));
        Assert.assertEquals(2, Levenshtein.distance("ABcd", "abcd"));
        Assert.assertEquals(2, Levenshtein.distance("abcd", "abCD"));
    }

    @Test
    public void testBestMatch() {
        final String result = Levenshtein.match("jungle").pickBestfrom("juggle", "jiggle", "jello", "alphabit");
        Assert.assertEquals("juggle", result);
    }

    @Test
    public void testBestMatchHarder() {
        final String result = Levenshtein.match("alpha").pickBestfrom("omega", "beta", "gamma", "alph oh, wait a sec, this doesn't match");
        Assert.assertEquals("omega", result);
    }

    @Test
    public void testToString() {
        System.out.println(Levenshtein.toString("alpha", "omega"));
        System.err.println(Levenshtein.toString("alpha123", "abcdefghijklmnopqrstuvwxyz"));
        System.out.println(Levenshtein.toString("abcdefghijklmnopqrstuvwxyz", "alpha123"));
    }

}
