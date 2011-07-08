package com.porpoise.common.collect;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.porpoise.common.functions.FunctionSet;

/**
 * 
 */
public class FunctionSetTest {

    private FunctionSet<String> functionSet;
    private Function<String, Integer> lengthFunction;

    /**
	 */
    @Before
    public void setup() {
        // create a set which is keyed on string length
        this.lengthFunction = new Function<String, Integer>() {
            @Override
            public Integer apply(final String input) {
                return Integer.valueOf(input.length());
            }
        };
        this.functionSet = new FunctionSet<String>(this.lengthFunction);
    }

    /**
	 */
    @Test
    public void testAdd() {
        Assert.assertTrue(this.functionSet.add("one"));
        Assert.assertFalse(this.functionSet.add("two"));
        Assert.assertEquals("one", Iterables.getOnlyElement(this.functionSet));
    }

    /**
	 */
    @Test
    public void testAddAll() {
        Assert.assertTrue(this.functionSet.addAll(ImmutableList.of("one", "two", "three")));
        Assert.assertFalse(this.functionSet.addAll(ImmutableList.of("one", "two", "three")));
        Assert.assertEquals(2, this.functionSet.size());
        Assert.assertTrue(this.functionSet.contains("one"));
        Assert.assertTrue(this.functionSet.contains("three"));
    }

    /**
	 */
    @Test
    public void testRetainAll() {
        Assert.assertTrue(this.functionSet.addAll(ImmutableList.of("one", "two", "three")));
        final ImmutableList<String> items = ImmutableList.of("not in the list", "three");
        Assert.assertTrue(this.functionSet.retainAll(items));
        Assert.assertFalse(this.functionSet.retainAll(items));
        Assert.assertEquals(1, this.functionSet.size());
        Assert.assertTrue(this.functionSet.contains("three"));
    }

    /**
	 */
    @Test
    public void testClear() {
        Assert.assertTrue(this.functionSet.addAll(ImmutableList.of("one", "two", "three")));
        this.functionSet.clear();
        Assert.assertTrue(this.functionSet.isEmpty());
        Assert.assertEquals(0, this.functionSet.size());
        Assert.assertFalse(this.functionSet.contains("one"));
        Assert.assertFalse(this.functionSet.contains("two"));
        Assert.assertFalse(this.functionSet.contains("three"));
    }

    /**
	 */
    @Test
    public void testRemove() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        Assert.assertTrue(set.remove("a"));
        Assert.assertFalse(set.remove("a"));

        Assert.assertFalse(set.remove("b"));

        Assert.assertTrue(set.remove("see"));
        Assert.assertFalse(set.remove("see"));

        Assert.assertTrue(set.isEmpty());
    }

    /**
	 */
    @Test
    public void testRemoveAll() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        Assert.assertTrue(set.removeAll(ImmutableList.of("a", "see")));
        Assert.assertFalse(set.removeAll(ImmutableList.of("a", "see")));
        Assert.assertFalse(set.removeAll(ImmutableList.of("b")));
    }

    /**
	 */
    @Test
    public void testContains() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains("a"));
        Assert.assertFalse(set.contains("b"));
        Assert.assertTrue(set.contains("see"));
        Assert.assertFalse(set.contains(Integer.valueOf(1)));
        Assert.assertFalse(set.contains(null));
    }

    /**
	 */
    @Test
    public void testContainsAll() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.containsAll(ImmutableList.of("a", "see")));
        Assert.assertFalse(set.containsAll(ImmutableList.of("a", "b", "see")));
        Assert.assertFalse(set.containsAll(ImmutableList.of(Integer.valueOf(123))));
    }

    /**
	 */
    @Test
    public void testToArray() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        final Object[] array = set.toArray();
        Assert.assertArrayEquals(new Object[] { "a", "see" }, array);
    }

    /**
	 */
    @Test
    public void testToArray2() {
        final Set<String> set = new FunctionSet<String>(this.lengthFunction, "a", "b", "see");
        final Object[] array = set.toArray(new Object[0]);
        Assert.assertArrayEquals(new Object[] { "a", "see" }, array);
    }

    /**
     */
    @Test
    public void testCompositeFunction() {
        class Bean {
            private String str = "";
            private Integer value = 1;
            private Boolean test = true;

            public Bean(final String s, final int v, final boolean b) {
                this.str = s;
                this.value = v;
                this.test = b;
            }
        }
        final Function<Bean, String> str = new Function<Bean, String>() {
            @Override
            public String apply(final Bean input) {
                return input.str;
            }
        };
        final Function<Bean, Integer> value = new Function<Bean, Integer>() {
            @Override
            public Integer apply(final Bean input) {
                return input.value;
            }
        };
        final Function<Bean, Boolean> test = new Function<Bean, Boolean>() {
            @Override
            public Boolean apply(final Bean input) {
                return input.test;
            }
        };
        @SuppressWarnings("unchecked")
        final FunctionSet<Bean> set = FunctionSet.create(str, value, test);

        Assert.assertTrue(set.add(new Bean("A", 1, true)));
        Assert.assertFalse(set.add(new Bean("A", 1, true)));

        Assert.assertTrue(set.add(new Bean("A", 1, false)));
        Assert.assertFalse(set.add(new Bean("A", 1, false)));

        Assert.assertTrue(set.add(new Bean("A", 2, false)));
        Assert.assertFalse(set.add(new Bean("A", 2, false)));

        Assert.assertTrue(set.add(new Bean("B", 2, false)));
        Assert.assertFalse(set.add(new Bean("B", 2, false)));
    }
}
