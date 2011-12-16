package com.porpoise.common.functions;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.porpoise.common.core.Pair;

/**
 * Any non-test utilities/methods in support of testing FunctionMaps belong here.
 * 
 * This class may be extended or statically imported in tests
 */
class FunctionMapTestSupport {

	static class Thing {
		private final String	value;

		public String getValue() {
			return this.value;
		}

		public Thing(final String value) {
			this.value = value;
		}

		public int getLength() {
			return this.value.length();
		}
	}

	private static final Function<Thing, Character>	STARTS_WITH;
	private static final Function<Thing, Integer>	LENGTH;

	static {
		STARTS_WITH = new Function<Thing, Character>() {
			@Override
			public Character apply(final Thing input) {
				return input == null ? null : firstLetter(input.getValue());
			}
		};
		LENGTH = new Function<Thing, Integer>() {
			@Override
			public Integer apply(final Thing input) {
				return input.getLength();
			}
		};
	}

	private static Character firstLetter(final String value) {
		return Strings.isNullOrEmpty(value) ? null : value.charAt(0);
	}

	public static FunctionMap<Thing> newMapByFirstLetterAndLength() {
		final FunctionMap<Thing> thingByStartsWithAndLength = FunctionMap.<Thing> create(STARTS_WITH, LENGTH);
		assertEmptyMap(thingByStartsWithAndLength);
		return thingByStartsWithAndLength;
	}

	static <T> T verifyAdd(final FunctionMap<T> map, final T value) {
		final Pair<Key<T>, T> replaced = map.put(value);
		Assert.assertNull(replaced.getSecond());
		Assert.assertNotNull(replaced.getFirst());
		return value;
	}

	static <T> Pair<Key<T>, T> verifyNotAdded(final FunctionMap<T> map, final T value) {
		final Pair<Key<T>, T> replaced = map.put(value);
		Assert.assertNotNull(replaced.getSecond());
		return replaced;
	}

	public static <T> void assertContents(final FunctionMap<T> map, final T... values) {
		final int expectedSize = values.length;
		if (expectedSize == 0) {
			assertEmptyMap(map);
		} else {
			Assert.assertFalse(map.isEmpty());
			Assert.assertEquals(expectedSize, map.size());

			assertDoesNotContainNonsense(map);

			assertSize(expectedSize, map.keySet());
			assertSize(expectedSize, map.values());
			assertSize(expectedSize, map.entrySet());

			assertValuesInEntrySet(map, values);
			assertValues(map, values);
		}
	}

	private static <T> void assertValuesInEntrySet(final FunctionMap<T> map, final T... values) {
		final List<T> remainingValues = Lists.newArrayList(values);
		for (final Entry<Key<T>, T> entry : map.entrySet()) {
			Assert.assertTrue(remainingValues.remove(entry.getValue()));
		}
		Assert.assertTrue(remainingValues.isEmpty());
	}

	private static <T> void assertValues(final FunctionMap<T> map, final T... values) {
		final List<T> remainingValues = Lists.newArrayList(values);
		Assert.assertEquals(values.length, map.values());
		Assert.assertTrue(remainingValues.removeAll(map.values()));
		Assert.assertTrue(remainingValues.isEmpty());
	}

	public static void assertEmptyMap(final FunctionMap<?> map) {
		final int expectedSize = 0;

		Assert.assertTrue(map.isEmpty());
		Assert.assertEquals(expectedSize, map.size());

		assertDoesNotContainNonsense(map);

		assertSize(expectedSize, map.keySet());
		assertSize(expectedSize, map.values());
		assertSize(expectedSize, map.entrySet());
	}

	protected static void assertDoesNotContainNonsense(final FunctionMap<?> map) {
		Assert.assertFalse(map.containsKey(null));
		Assert.assertFalse(map.containsValue(null));
		Assert.assertFalse(map.containsKey(new Object()));
		Assert.assertFalse(map.containsValue(new Object()));
	}

	protected static void assertSize(final int expectedSize, final Collection<?> collection) {
		if (expectedSize > 0) {
			Assert.assertFalse(collection.isEmpty());
		} else {
			Assert.assertTrue(collection.isEmpty());
		}
		Assert.assertEquals(expectedSize, collection.size());
	}
}
