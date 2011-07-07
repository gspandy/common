package com.porpoise.common.collect;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.porpoise.common.core.Pair;

/**
 * Tests for {@link Sequences}
 */
public class SequencesTest {
    /** a test function to sum inputs */
    private static final Function<Pair<Integer, Integer>, Integer> SUM;
    /** a test function to convert inputs into an equation string */
    private static final Function<Pair<Integer, BigDecimal>, String> EQUATION;
    /** a test function to multiple inputs */
    private static final Function<Pair<Integer, Integer>, Integer> MULTIPLY;

    /**
     */
    private static final Function<String, Collection<String>> STRING_TO_REVERSE;
    static {
        SUM = new Function<Pair<Integer, Integer>, Integer>() {
            @SuppressWarnings("boxing")
            @Override
            public Integer apply(final Pair<Integer, Integer> input) {
                return input.getFirst().intValue() + input.getSecond().intValue();
            }
        };
        EQUATION = new Function<Pair<Integer, BigDecimal>, String>() {
            @SuppressWarnings("boxing")
            @Override
            public String apply(final Pair<Integer, BigDecimal> input) {
                return String.format("%s X %s = %s", input.getFirst(), input.getSecond(),
                        input.getSecond().multiply(new BigDecimal(input.getFirst()).setScale(1)));
            }
        };
        MULTIPLY = new Function<Pair<Integer, Integer>, Integer>() {
            @SuppressWarnings("boxing")
            @Override
            public Integer apply(final Pair<Integer, Integer> input) {
                return input.getFirst().intValue() * input.getSecond().intValue();
            }
        };

        STRING_TO_REVERSE = new Function<String, Collection<String>>() {
            @Override
            public Collection<String> apply(final String input) {
                return Lists.newArrayList(input, new StringBuilder(input).reverse().toString());
            }
        };
    }

    /**
     * Test the Sequences object can compare lists which contain nulls
     */
    @Test
    public void testCompareWithNulls() {
        // create an unsorted list
        final List<Integer> list = Lists.newArrayList(Integer.valueOf(4), Integer.valueOf(1), null, Integer.valueOf(3),
                Integer.valueOf(2), null);
        final Comparator<Integer> c = new Comparator<Integer>() {
            @Override
            public int compare(final Integer a, final Integer b) {
                // call the method under test
                return Sequences.compare(a, b);
            }
        };
        Collections.sort(list, c);

        // assert we now have a sorted list
        final Iterator<Integer> iter = list.iterator();
        Assert.assertNull(iter.next());
        Assert.assertNull(iter.next());
        Assert.assertEquals(1, iter.next().intValue());
        Assert.assertEquals(2, iter.next().intValue());
        Assert.assertEquals(3, iter.next().intValue());
        Assert.assertEquals(4, iter.next().intValue());
    }

    /**
     * test we can "flatMap" a collection.
     * 
     * To test, we start with a list of strings and a function which returns the original string + the reversed string
     */
    @Test
    public void testFlatMap() {
        final List<String> original = Lists.newArrayList("one", "seven");

        // call the method under test
        final Collection<String> flat = Sequences.flatMap(original, STRING_TO_REVERSE);
        final Iterator<String> iter = flat.iterator();
        Assert.assertEquals("one", iter.next());
        Assert.assertEquals("eno", iter.next());
        Assert.assertEquals("seven", iter.next());
        Assert.assertEquals("neves", iter.next());
        Assert.assertFalse(iter.hasNext());
    }

    /**
     * test we can "flatMap" a collection.
     * 
     * To test, we start with a list of strings and a function which returns the original string + the reversed string
     */
    @Test
    public void testFlatMapSet() {
        final List<String> original = Lists.newArrayList("one", "seven", "one");

        // call the method under test
        final Set<String> flat = Sequences.flatMapSet(original, STRING_TO_REVERSE);
        Assert.assertTrue(flat.contains("one"));
        Assert.assertTrue(flat.contains("eno"));
        Assert.assertTrue(flat.contains("seven"));
        Assert.assertTrue(flat.contains("neves"));
        Assert.assertEquals(4, flat.size());
    }

    /**
     * test we can merge the intersection of two maps of different types
     */
    @SuppressWarnings("boxing")
    @Test
    public void testMergeMapIntersection() {
        final Map<String, Integer> mapOne = ImmutableMap.<String, Integer> builder()//
                .put("alpha", 3) //
                .put("beta", 4) //
                .put("gamma", 5).build();

        final BigDecimal value = new BigDecimal("12.3");
        final Map<String, BigDecimal> mapTwo = ImmutableMap.<String, BigDecimal> builder()//
                .put("delta", value) //
                .put("beta", value) //
                .put("gamma", value).build();

        final Map<String, String> equations = Sequences.mergeMapsIntersection(EQUATION, mapOne, mapTwo);
        Assert.assertEquals(2, equations.size());
        Assert.assertEquals("4 X 12.3 = 49.20", equations.get("beta"));
        Assert.assertEquals("5 X 12.3 = 61.50", equations.get("gamma"));

    }

    /**
     * test {@link Sequences#groupByUnique(Iterable, Function)} throws an exception if in fact the keys produced are NOT
     * unique
     */
    @Test
    public void testGroupByUniqueThrowsExceptionIfNotIndeedUnique() {
        // create a list to convert to a map. Our converting function will NOT produce unique keys, however
        final List<String> animals = ImmutableList.of("cat", "dog");

        // let's make a function which will return NON-unique results for our input set, e.g. the string length
        // of "cat" and "dog" are both 3
        final Function<String, Integer> stringByLength = new Function<String, Integer>() {
            @Override
            public Integer apply(final String input) {
                return Integer.valueOf(input.length());
            }
        };
        IllegalArgumentException exception = null;
        try {
            // call the method under test
            Sequences.groupByUnique(animals, stringByLength);
            Assert.fail("Our function did NOT return unique keys - an exception was exepected");
        } catch (final IllegalArgumentException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    /**
     * Test we can merge maps
     */
    @Test
    @SuppressWarnings({ "boxing" })
    public void testMergeMaps() {
        final Map<String, Integer> mapOne = ImmutableMap.<String, Integer> builder()//
                .put("alpha", 3) //
                .put("beta", 4) //
                .put("gamma", 5).build();

        final Map<String, Integer> mapTwo = ImmutableMap.<String, Integer> builder()//
                .put("delta", 10) //
                .put("beta", 40) //
                .put("gamma", 50).build();

        // call the method under test
        final Map<String, Integer> added = Sequences.mergeMaps(mapOne, mapTwo, SUM);
        Assert.assertEquals(4, added.size());
        Assert.assertEquals(3, added.get("alpha").intValue());
        Assert.assertEquals(44, added.get("beta").intValue());
        Assert.assertEquals(55, added.get("gamma").intValue());
        Assert.assertEquals(10, added.get("delta").intValue());

        // just double check - this time multiple the map contents
        final Map<String, Integer> multiplied = Sequences.mergeMaps(mapOne, mapTwo, MULTIPLY);
        Assert.assertEquals(4, multiplied.size());
        Assert.assertEquals(3, multiplied.get("alpha").intValue());
        Assert.assertEquals(160, multiplied.get("beta").intValue());
        Assert.assertEquals(250, multiplied.get("gamma").intValue());
        Assert.assertEquals(10, multiplied.get("delta").intValue());
    }

    /**
     * test for {@link Sequences#SUM_DEC}
     */
    @Test
    public void testSum() {
        final ImmutableList<BigDecimal> decs = ImmutableList.of(new BigDecimal(2), new BigDecimal(3));
        final BigDecimal sum = Sequences.foldDec(decs, Sequences.SUM_DEC);
        Assert.assertTrue(sum.compareTo(new BigDecimal(5)) == 0);
    }

    /**
     * Test for {@link Sequences#collect(Collection, Function)}
     */
    @Test
    public void testCollect() {
        final List<Integer> decs = Ints.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final Collection<String> divByThreeAsString = Sequences.collect(decs, new Function<Integer, String>() {
            @Override
            public String apply(final Integer value) {
                return value.intValue() % 3 == 0 ? value.toString() : null;
            }
        });
        final Iterator<String> actual = divByThreeAsString.iterator();
        Assert.assertEquals("3", actual.next());
        Assert.assertEquals("6", actual.next());
        Assert.assertEquals("9", actual.next());
        Assert.assertFalse(actual.hasNext());

    }

    /**
     * test for {@link Sequences#flatten(Iterable...)}
     */
    @Test
    public void testFlatten() {
        @SuppressWarnings({ "unchecked", "boxing" })
        // call the method under test
        final Collection<Integer> flattened = Sequences.flatten(ImmutableList.of(1, 2), ImmutableList.of(3, 4));
        final Iterator<Integer> iter = flattened.iterator();
        Assert.assertEquals(1, iter.next().intValue());
        Assert.assertEquals(2, iter.next().intValue());
        Assert.assertEquals(3, iter.next().intValue());
        Assert.assertEquals(4, iter.next().intValue());
        Assert.assertFalse(iter.hasNext());

    }

    /**
     * test for {@link Sequences#zip(Iterable, Iterable)}
     */
    @SuppressWarnings("boxing")
    @Test
    public void testZip() {
        final Collection<Pair<String, Integer>> zipped = Sequences.zip(ImmutableList.of("A", "B", "C"),
                ImmutableList.of(1, 2));
        final Iterator<Pair<String, Integer>> iter = zipped.iterator();

        Pair<String, Integer> pair = iter.next();
        Assert.assertEquals("A", pair.getFirst());
        Assert.assertEquals(1, pair.getSecond().intValue());

        pair = iter.next();
        Assert.assertEquals("B", pair.getFirst());
        Assert.assertEquals(2, pair.getSecond().intValue());

        pair = iter.next();
        Assert.assertEquals("C", pair.getFirst());
        Assert.assertNull(pair.getSecond());

        Assert.assertFalse(iter.hasNext());
    }

    /**
     * test for {@link Sequences#unzipFirst(Collection)} and {@link Sequences#unzipSecond(Collection)}
     */
    @SuppressWarnings("boxing")
    @Test
    public void testUnzip() {
        final ImmutableList<String> expectedFirst = ImmutableList.of("A", "B", "C");
        final List<Integer> expectedSecond = Lists.newArrayList(1, 2);
        final Collection<Pair<String, Integer>> zipped = Sequences.zip(expectedFirst, expectedSecond);

        final Collection<String> first = Sequences.unzipFirst(zipped);
        final Collection<Integer> second = Sequences.unzipSecond(zipped);

        Assert.assertArrayEquals(expectedFirst.toArray(), first.toArray());

        expectedSecond.add(null);
        Assert.assertArrayEquals(expectedSecond.toArray(), second.toArray());
    }

    /**
     * Test for {@link Sequences#foldNum(Integer, Iterable, Function)}
     */
    @Test
    @SuppressWarnings("boxing")
    public void testFoldNum() {
        final int result = Sequences.foldNum(1, ImmutableList.of(1, 2, 3),
                new Function<Pair<Number, Number>, Number>() {
                    @Override
                    public Number apply(final Pair<Number, Number> input) {
                        return input.getFirst().intValue() * input.getSecond().intValue();
                    }
                }).intValue();
        Assert.assertEquals(1 * 2 * 3, result);
    }
}
