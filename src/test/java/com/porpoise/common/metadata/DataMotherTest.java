package com.porpoise.common.metadata;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;

public class DataMotherTest {

    private DataMother dataMother;

    public static class TestClass {
        public final int integer;
        public final float floatValue;
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
        this.dataMother.add(TestClass.class, supplier);

        final TestClass first = this.dataMother.get(TestClass.class);
        final TestClass second = this.dataMother.get(TestClass.class);
        Assert.assertNotNull(first);
        Assert.assertTrue(first.equals(first));
        Assert.assertTrue(second.equals(second));
        Assert.assertFalse(first.equals(second));
    }

}
