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

    public static void assertEquality(final Object control, final Object experiment) {
        assertEquality("", control, experiment);
    }

    /**
     * @param control
     * @param experiment
     */
    public static void assertEquality(final String name, final Object control, final Object experiment) {
        if (!nullCheck(control, experiment)) {
            return;
        }
        Assert.assertTrue(name + ": LHS hash code not consistent", control.hashCode() == control.hashCode());
        Assert.assertTrue(name + ": RHS hash code not consistent", experiment.hashCode() == experiment.hashCode());
        Assert.assertTrue(name + ": hash codes not equal", control.hashCode() == experiment.hashCode());

        Assert.assertTrue(name + ": equals is not reflexive", control.equals(control));
        Assert.assertTrue(name + ": equals is not reflexive", experiment.equals(experiment));

        Assert.assertTrue(name + ": not equal", control.equals(experiment));
        Assert.assertTrue(name + ": not symmetric", experiment.equals(control));
    }

    public static void assertNonEquality(final Object control, final Object experiment) {
        assertNonEquality("", control, experiment);
    }

    /**
     * @param control
     * @param experiment
     */
    public static void assertNonEquality(final String name, final Object control, final Object experiment) {
        if (control == null) {
            Assert.assertNotNull("both were null", experiment);
        } else if (experiment != null) {
            Assert.assertFalse(name + ": hash codes ARE equal", control.hashCode() == experiment.hashCode());

            Assert.assertFalse(name + ": both are equal", control.equals(experiment));
            Assert.assertFalse(name + ": equality is not symmetric. LHS != RHS is true but RHS != LHS is false",
                    experiment.equals(control));
        }
    }

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

    /**
     * @param left
     *            the left object
     * @param right
     *            the right object
     * @return true if both are not null
     */
    public static boolean nullCheck(final Object left, final Object right) {
        if (left == null) {
            Assert.assertNull("left side was null, right was not", right);
            return false;
        }
        Assert.assertNotNull("right was null", right);
        return true;
    }

}
