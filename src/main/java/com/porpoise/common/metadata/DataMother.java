package com.porpoise.common.metadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.porpoise.common.date.Dates;

/**
 * Data "Mother"
 * 
 * "Mother" classes are just factories, but specifically for test data. The idea of calling someting XXX "Mother" is to
 * distinguish them from otherwise potentially valid Factory classes.
 * 
 * For example, given a "Person" class, someone discovering a "PersonFactory" class might legitimately assume it should
 * be used to create Person instances (and wonder why the Person class warrents a "Factory"). Upon seeing a
 * "PersonMother", however, it hopefully is more clear that it is for creating Person instances for test data.
 * 
 * Using DataMothers can also be useful in slightly objects deep in an object graph. Consider (ignoring bad LoD):
 * 
 * <pre>
 * Order order = DataMother.get(Order.class);
 * Description d = order.getLine(0).getItem(0).getDescription()
 * 
 * -- here we just want a different descriptions
 * DataMother.add(Description.class, new Supplier<Description>() { ... });
 * Order order2 = DataMother.get(Order.class);
 * 
 * -- order and order2 should now only differ by their line item descriptions
 * 
 * <pre/>
 * 
 * 
 * @see <a href="http://martinfowler.com/bliki/ObjectMother.html">ObjectMother</a>
 * 
 */
public class DataMother {

    private final Map<Class<?>, Supplier<?>> providerByClass;

    private final Random                     random = new Random(System.currentTimeMillis());

    /**
     * @return a DataMother which will cycle through different values for the wrapped primitive classes (Integer, Long,
     *         etc), as well as Date, BigInteger and BigDecimal
     */
    @SuppressWarnings("boxing")
    public static DataMother withTestValues() {
        final DataMother mother = new DataMother();

        mother.add(String.class, "Alpha", " beta ", "Gamma", "DELTA");
        mother.add(Integer.class, Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0, 1, 1234);
        mother.add(Long.class, Long.valueOf(Long.MAX_VALUE), Long.valueOf(Long.MIN_VALUE), Long.valueOf(-1),
                Long.valueOf(0), Long.valueOf(1), Long.valueOf(1234));
        mother.add(Byte.class, Byte.valueOf(Byte.MAX_VALUE), Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf((byte) -1),
                Byte.valueOf((byte) 0), Byte.valueOf((byte) 1), Byte.valueOf((byte) 1234));
        mother.add(Short.class, Short.valueOf(Short.MAX_VALUE), Short.valueOf(Short.MIN_VALUE),
                Short.valueOf((short) -1), Short.valueOf((short) 0), Short.valueOf((short) 1),
                Short.valueOf((short) 1234));
        mother.add(Float.class, Float.valueOf(Float.MAX_VALUE), Float.valueOf(Float.MIN_VALUE), Float.valueOf(-1),
                Float.valueOf(0), Float.valueOf(1), Float.valueOf(12.34f));
        mother.add(Double.class, Double.valueOf(Double.MAX_VALUE), Double.valueOf(Double.MIN_VALUE),
                Double.valueOf(-1), Double.valueOf(0), Double.valueOf(1), Double.valueOf(12.34d));
        mother.add(BigDecimal.class, new BigDecimal("-0.01"), new BigDecimal("12.345678"), BigDecimal.ZERO,
                BigDecimal.ONE, BigDecimal.TEN);
        mother.add(BigInteger.class, new BigInteger("-1"), new BigInteger("12345678987654321"), BigInteger.ZERO,
                BigInteger.ONE, BigInteger.TEN);
        mother.add(Date.class, Dates.yearMonthDay(2000, 1, 1), Dates.now().getTime(), Dates.yearMonthDay(1977, 7, 8));

        return mother;
    }

    /**
     * @return a data mother which will return consistent values for the classes covered in {@link #withTestValues()}
     */
    public static DataMother withConsistentTestValues() {
        final DataMother mother = new DataMother();
        final DataMother variableValues = withTestValues();
        for (final Class<?> c : variableValues.definedClasses()) {
            final Supplier<?> instance = Suppliers.ofInstance(variableValues.get(c));
            mother.providerByClass.put(c, instance);
        }
        return mother;
    }

    /**
     * @return the supported the classes
     */
    public Set<Class<?>> definedClasses() {
        return this.providerByClass.keySet();
    }

    /**
     * Default Constructor
     */
    public DataMother() {
        // consider MutableClassToInstanceMap
        this(Maps.<Class<?>, Supplier<?>> newHashMap());
    }

    /**
     * @param <T>
     *            the value type
     * @param class1
     *            the class type to add
     * @param values
     *            the values to add for the given class type
     * @return the datamother instance
     */
    public <T> DataMother add(final Class<? extends T> class1, final T... values) {
        return addSupplier(class1, new ValuesSupplier<T>(values));
    }

    /**
     * @param <T>
     *            the value type
     * @param type
     *            the class type for which values will be supplied
     * @param supplier
     *            the supplier instance
     * @return the datamother instance
     */
    public <T> DataMother addSupplier(final Class<? extends T> type, final Supplier<T> supplier) {
        this.providerByClass.put(type, supplier);
        return this;
    }

    DataMother(final Map<Class<?>, Supplier<?>> impl) {
        this.providerByClass = Preconditions.checkNotNull(impl);
    }

    /**
     * @param <T>
     * @param c1ass
     * @return a value for the given class as determined by a supplier registered against the given class
     */
    public <T> T get(final Class<T> c1ass) {
        @SuppressWarnings("unchecked")
        final Supplier<T> supplier = (Supplier<T>) this.providerByClass.get(c1ass);
        return supplier == null ? null : supplier.get();
    }

    /**
     * @param <T>
     * @param c1ass
     * @return a non-null instance of the given class type or throws a {@link NullPointerException}
     */
    public <T> T getNonNull(final Class<T> c1ass) {
        return Preconditions.checkNotNull(get(c1ass), "No supplier was defined for %s", c1ass);
    }

    /**
     * @param <T>
     * @param c1ass
     *            the data type for which instances are to be returned
     * @return a collection of data of the given type
     */
    public <T> Collection<T> collectionOf(final Class<T> c1ass) {
        return listOf(c1ass);
    }

    /**
     * @param <T>
     * @param c1ass
     *            the data type for which instances are to be returned
     * @return a list of data of the given type
     */
    public <T> List<T> listOf(final Class<T> c1ass) {
        return ImmutableList.of(getNonNull(c1ass), getNonNull(c1ass), getNonNull(c1ass));
    }

    /**
     * @param <T>
     * @param c1ass
     *            the data type for which instances are to be returned
     * @return a set of data of the given type
     */
    public <T> Set<T> setOf(final Class<T> c1ass) {
        return ImmutableSet.of(get(c1ass), get(c1ass), get(c1ass));
    }

    /**
     * @param min
     *            the minimum number to include
     * @param max
     *            the maximum number to include
     * @return an integer value within the given range, inclusive
     */
    public int intWithin(final int min, final int max) {
        if (max == min) {
            return max;
        }
        if (max < min) {
            return intWithin(max, min);
        }
        final int rangeExclusive = max - min;
        final int rangeInclusive = rangeExclusive + 1;
        return min + this.random.nextInt(rangeInclusive);
    }
}
