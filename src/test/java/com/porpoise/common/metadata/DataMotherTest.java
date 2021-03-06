package com.porpoise.common.metadata;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * tests for {@link DataMother}
 */
public class DataMotherTest {

    private DataMother dataMother;

    /**
     * internal test class
     */
    static class TestClass {
        public final int    integer;
        public final float  floatValue;
        public final String str;

        public TestClass(final int integer, final float floatValue, final String str) {
            this.integer = integer;
            this.floatValue = floatValue;
            this.str = str;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Float.floatToIntBits(this.floatValue);
            result = prime * result + this.integer;
            result = prime * result + (this.str == null ? 0 : this.str.hashCode());
            return result;
        }

        @SuppressWarnings("boxing")
        @Override
        public boolean equals(final Object obj) {
            final TestClass other = (TestClass) obj;
            return Objects.equal(this.integer, other.integer) && Objects.equal(this.floatValue, other.floatValue)
                    && Objects.equal(this.str, other.str);
        }
    }

    /**
     * prepare the test data
     */
    @Before
    public void setup() {
        this.dataMother = DataMother.withTestValues();
    }

    /**
     * test the datamother produces different values for the primitive types (as per the suppliers for those types)
     */
    @Test
    public void testMotherPrimitiveValues() {
        assertValues(Integer.class);
        assertValues(BigDecimal.class);
        assertValues(String.class);
        assertValues(Float.class);
        assertValues(Byte.class);
        assertValues(Short.class);
        assertValues(Long.class);
    }

    private void assertValues(final Class<?> c) {
        Assert.assertFalse(this.dataMother.get(c).equals(this.dataMother.get(c)));
        Assert.assertFalse(Arrays.equals(this.dataMother.setOf(c).toArray(), this.dataMother.setOf(c).toArray()));
        Assert.assertFalse(Arrays.equals(this.dataMother.collectionOf(c).toArray(), this.dataMother.collectionOf(c)
                .toArray()));
    }

    /**
     * Test {@link DataMother#withConsistentTestValues()} returns the same values for each type
     */
    public void testMotherWithConsistentValues() {
        final DataMother mother = DataMother.withConsistentTestValues();
        for (final Class<?> c : mother.definedClasses()) {
            Assert.assertEquals(mother.get(c), mother.get(c));
        }
    }

    /**
     * test we can construct a datamother
     */
    @Test
    public void testMotherWithNewClass() {
        final Supplier<TestClass> supplier = new Supplier<DataMotherTest.TestClass>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public TestClass get() {
                return new TestClass(//
                        DataMotherTest.this.dataMother.get(Integer.class).intValue(),//
                        DataMotherTest.this.dataMother.get(Float.class).floatValue(), //
                        DataMotherTest.this.dataMother.get(String.class)//
                );
            }
        };
        Assert.assertNull(this.dataMother.get(TestClass.class));
        this.dataMother.addSupplier(TestClass.class, supplier);

        final TestClass first = this.dataMother.get(TestClass.class);
        final TestClass second = this.dataMother.get(TestClass.class);
        Assert.assertNotNull(first);
        Assert.assertTrue(first.equals(first));
        Assert.assertTrue(second.equals(second));
        Assert.assertFalse(first.equals(second));
    }

    /**
     * Test for {@link DataMother#intWithin(int, int)}i
     */
    @SuppressWarnings("boxing")
    @Test
    public void testIntWithin() {
        final Set<Integer> values = Sets.newHashSet();

        // as intWithin is non-deterministic, we test that, over a large set of values,
        // we still only get the two we're looking for
        for (int i = 0; i < 10000; i++) {
            values.add(this.dataMother.intWithin(3, 4));
        }
        Assert.assertTrue(values.remove(Integer.valueOf(3)));
        Assert.assertTrue(values.remove(Integer.valueOf(4)));
        Assert.assertTrue(values.isEmpty());

        // assert consistent values if given a range. In practice calling this method with consistent args explicitly
        // would be foolish, but the method should cope with it
        Assert.assertEquals(0, this.dataMother.intWithin(0, 0));
        Assert.assertEquals(-1, this.dataMother.intWithin(-1, -1));
        Assert.assertEquals(Integer.MIN_VALUE, this.dataMother.intWithin(Integer.MIN_VALUE, Integer.MIN_VALUE));
        Assert.assertEquals(Integer.MAX_VALUE, this.dataMother.intWithin(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
}
