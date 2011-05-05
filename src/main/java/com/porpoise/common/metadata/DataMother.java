package com.porpoise.common.metadata;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.porpoise.common.date.Dates;

public class DataMother {

	private final Map<Class<?>, Supplier<?>>	providerByClass;

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
		mother.add(Date.class, Dates.yearMonthDay(2000, 1, 1), Dates.now().getTime(), Dates.yearMonthDay(1977, 7, 8));

		return mother;
	}

	public <T> DataMother add(final Class<T> class1, final T... values) {
		return add(class1, new ValuesSupplier<T>(values));
	}

	public DataMother() {
		this(Maps.<Class<?>, Supplier<?>> newHashMap());
	}

	public <T> DataMother add(final Class<T> type, final Supplier<T> supplier) {
		this.providerByClass.put(type, supplier);
		return this;
	}

	DataMother(final Map<Class<?>, Supplier<?>> impl) {
		this.providerByClass = Preconditions.checkNotNull(impl);
	}

	public <T> T get(final Class<T> c1ass) {
		@SuppressWarnings("unchecked")
		final Supplier<T> supplier = (Supplier<T>) this.providerByClass.get(c1ass);
		return supplier == null ? null : supplier.get();
	}

	public <T> Collection<T> collectionOf(final Class<T> c1ass) {
		return listOf(c1ass);
	}

	public <T> List<T> listOf(final Class<T> c1ass) {
		return ImmutableList.of(get(c1ass), get(c1ass), get(c1ass));
	}

	public <T> Set<T> setOf(final Class<T> c1ass) {
		return ImmutableSet.of(get(c1ass), get(c1ass), get(c1ass));
	}
}
