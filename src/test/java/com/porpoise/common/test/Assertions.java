package com.porpoise.common.test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.porpoise.common.collect.Sequences;

public enum Assertions {
    ; // uninstantiable

    /**
     * assert two big decimals are equal
     */
    public static void assertEquals(final BigDecimal left, final BigDecimal right) {
        final String doesntMatch = String.format("%s != %s", left, right);
        Assert.assertFalse(doesntMatch, left == null ^ right == null);
        if (left != null) {
            Assert.assertTrue(doesntMatch, left.compareTo(right) == 0);
        }
    }

    /**
     * utility for finding diffs between iterables
     */
    public static <T> Collection<String> differences(final Iterable<? extends T> first,
            final Iterable<? extends T> second, final Function<T, Predicate<? super T>> matchMaker) {
        if (first == null) {
            if (second == null) {
                return Collections.emptySet();
            }
            return Collections.singleton("right side was null, left side was non-null");
        } else if (second == null) {
            return Collections.singleton("left side was null, right was non-null");
        }
        final Collection<String> errors = Lists.newArrayList();

        final List<T> copy = Lists.newArrayList(second);
        for (final T control : first) {
            final int index = Iterables.indexOf(copy, matchMaker.apply(control));
            if (index < 0) {
                errors.add(String.format("Couldn't find %s in %s", second, Sequences.toString(first)));
            } else {
                copy.remove(index);
            }
        }
        if (!copy.isEmpty()) {
            errors.add(String.format("the left collection contained extra elements: %s", Sequences.toString(copy)));
        }
        return errors;
    }

}
