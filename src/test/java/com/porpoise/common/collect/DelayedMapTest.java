package com.porpoise.common.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the delayed map class
 */
public class DelayedMapTest {

	private static final String	       VALUE	= "value";
	private static final String	       KEY	 = "key";
	private Map<String, String>	       underlyingMap;
	private DelayedMap<String, String>	dMap;

	/**
	 * prepare an underlying and delayed map
	 */
	@Before
	public final void setup() {
		this.underlyingMap = new HashMap<String, String>();
		this.dMap = new DelayedMap<String, String>(this.underlyingMap);
	}

	/**
	 * test the delayed map will find items in the underlying map
	 */
	@Test
	public void test_underlyingMapAccess() {
		this.underlyingMap.put(KEY, VALUE);
		final String actual = this.dMap.get(KEY);
		Assert.assertEquals(VALUE, actual);
	}

	/**
	 * test that putting a new object which previously did not exist in the delayed map still returns the underlying
	 * value
	 */
	@Test
	public void test_putReturnsOldValue() {
		this.underlyingMap.put(KEY, VALUE);
		final String replacedValue = this.dMap.put(KEY, "new value");
		Assert.assertEquals(VALUE, replacedValue);
	}

	/**
	 * test the add method does not add to the underlying map until it is flushed
	 */
	@Test
	public void test_putDelayedUntilFlush() {
		this.dMap.put(KEY, VALUE);
		Assert.assertFalse(this.underlyingMap.containsKey(KEY));
		Assert.assertNull(this.underlyingMap.get(KEY));

		this.dMap.flush();

		Assert.assertTrue(this.underlyingMap.containsKey(KEY));
		Assert.assertEquals(VALUE, this.underlyingMap.get(KEY));
	}

	/**
	 * test the remove method does not remove from the underlying map until it is flushed
	 */
	@Test
	public void test_removeDelayedUntilFlush() {
		this.underlyingMap.put(KEY, VALUE);
		final Object removed = this.dMap.remove(KEY);
		Assert.assertEquals(removed, VALUE);
		Assert.assertFalse("the dmap should no longer contain the key", this.dMap.containsKey(KEY));
		Assert.assertNull("the dmap should now have the entry removed", this.dMap.get(KEY));
		Assert.assertTrue("The underlying map should still contain the key", this.underlyingMap.containsKey(KEY));
		Assert.assertEquals("The underlying map should still be mapped", this.underlyingMap.get(KEY), VALUE);

		this.dMap.flush();

		Assert.assertFalse("the dmap should no longer contain the key", this.dMap.containsKey(KEY));
		Assert.assertNull("the dmap should still have the entry removed", this.dMap.get(KEY));
		Assert.assertFalse("The underlying map should have had the entry removed", this.underlyingMap.containsKey(KEY));
		Assert.assertNull("The underlying map should have had the entry removed", this.underlyingMap.get(KEY));
	}

	/**
	 * test the putAll method does not add to the underlying map until it is flushed
	 */
	@Test
	public void test_putAllDelayedUntilFlush() {
		final Map<String, String> all = new HashMap<String, String>() {
			private static final long	serialVersionUID	= -9208018166010034076L;
			{
				put("a", "1");
				put("b", "2");
				put("c", "3");
			}
		};
		this.dMap.putAll(all);

		for (final Entry<String, String> entry : all.entrySet()) {
			Assert.assertTrue(this.dMap.containsKey(entry.getKey()));
			Assert.assertEquals(this.dMap.get(entry.getKey()), entry.getValue());
			Assert.assertFalse(this.underlyingMap.containsKey(entry.getKey()));
			Assert.assertNull(this.underlyingMap.get(entry.getKey()));
		}

		this.dMap.flush();

		for (final Entry<String, String> entry : all.entrySet()) {
			Assert.assertTrue(this.dMap.containsKey(entry.getKey()));
			Assert.assertEquals(this.dMap.get(entry.getKey()), entry.getValue());
			Assert.assertTrue(this.underlyingMap.containsKey(entry.getKey()));
			Assert.assertEquals(this.underlyingMap.get(entry.getKey()), entry.getValue());
		}
	}

	/**
	 * test clear doesn't clear the underlying map until flushed
	 */
	@Test
	public void test_clearDoesntClearUntilFlushed() {
		this.underlyingMap.put("A", "b");
		this.underlyingMap.put("c", "D");
		this.underlyingMap.put("asdf", "F");

		Assert.assertFalse(this.dMap.isEmpty());
		Assert.assertFalse(this.dMap.values().isEmpty());
		Assert.assertFalse(this.dMap.keySet().isEmpty());
		Assert.assertFalse(this.dMap.entrySet().isEmpty());
		Assert.assertFalse(this.underlyingMap.isEmpty());

		this.dMap.put("unique to dmap", "val");
		Assert.assertEquals("val", this.dMap.get("unique to dmap"));

		this.dMap.clear();

		Assert.assertNull(this.dMap.get("unique to dmap"));
		Assert.assertTrue(this.dMap.isEmpty());
		Assert.assertTrue(this.dMap.values().isEmpty());
		Assert.assertTrue(this.dMap.keySet().isEmpty());
		Assert.assertFalse(this.underlyingMap.isEmpty());
		Assert.assertFalse(this.underlyingMap.values().isEmpty());

		this.dMap.flush();

		Assert.assertTrue(this.dMap.isEmpty());
		Assert.assertTrue(this.dMap.values().isEmpty());
		Assert.assertTrue(this.dMap.keySet().isEmpty());
		Assert.assertTrue(this.dMap.entrySet().isEmpty());
		Assert.assertTrue(this.underlyingMap.isEmpty());
	}

	/**
     * 
     */
	@Test
	public void test_clearThenAddThenFlush() {
		//
		// start with a unique entry in both the underlying map
		// and the delayed map
		//
		this.underlyingMap.put("a", "b");
		this.dMap.put("c", "d");

		Assert.assertEquals("b", this.dMap.get("a"));
		Assert.assertNull(this.underlyingMap.get("c"));

		//
		// now call clear -- this would clear both maps if flushed.
		//
		this.dMap.clear();

		Assert.assertNull(this.dMap.get("c"));
		Assert.assertEquals("b", this.underlyingMap.get("a"));

		//
		// now add some new entries to the delayed map
		//
		this.dMap.put("f", "g");

		Assert.assertEquals("g", this.dMap.get("f"));
		Assert.assertNull(this.underlyingMap.get("f"));

		//
		// now flush -- given the clear and later the add, the underlying map
		// should now just contain the single new entry
		//
		this.dMap.flush();

		Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(this.underlyingMap.size()));
		Assert.assertEquals(Integer.valueOf(1), Integer.valueOf(this.dMap.size()));
	}

	/**
	 * test the values method
	 */
	@Test
	public void test_values() {
		//
		// start with an underlying entry and assert the delayed map re
		//
		this.underlyingMap.put(KEY, VALUE);

		Assert.assertEquals(Arrays.asList(VALUE), this.dMap.values());

		//
		// add an entry to the delayed map which doesn't exist in the underlying
		// map
		//
		this.dMap.put("abc", "def");

		//
		// assert the delayed map returns the correct values() data
		//
		List<String> expected = Arrays.asList(VALUE, "def");
		Collection<String> actual = this.dMap.values();
		Assert.assertTrue(expected.containsAll(actual));
		Assert.assertTrue(actual.containsAll(expected));

		//
		// remove the entry which only exists in the underlying map
		// and assert the values() excludes that value
		//
		final String removedValue = this.dMap.remove(KEY);
		Assert.assertEquals(VALUE, removedValue);

		Assert.assertEquals(Arrays.asList("def"), this.dMap.values());

		expected = Arrays.asList(VALUE);
		actual = this.underlyingMap.values();
		Assert.assertTrue(expected.containsAll(actual));
		Assert.assertTrue(actual.containsAll(expected));

		//
		// flush the changes - both maps should just contain the entry which
		// was added to the delayed map
		//
		this.dMap.flush();

		expected = Arrays.asList("def");
		actual = this.underlyingMap.values();
		Assert.assertTrue(expected.containsAll(actual));
		Assert.assertTrue(actual.containsAll(expected));
	}

	/**
	 * tests tiered delayed maps
	 */
	@Test
	public void test_cascadingDelayedMaps() {
		final Map<Long, String> cache = new HashMap<Long, String>();
		final DelayedMap<Long, String> appMap = new DelayedMap<Long, String>(cache);
		final DelayedMap<Long, String> sessionMap = new DelayedMap<Long, String>(appMap);
		final DelayedMap<Long, String> localMap = new DelayedMap<Long, String>(sessionMap);

		for (int i = 0; i < 10; i++) {
			final String value = Character.toString((char) (65 + i));
			localMap.put(Long.valueOf(i), value);
		}

		localMap.flush();
		Assert.assertTrue(cache.isEmpty());
		sessionMap.flush();
		Assert.assertTrue(cache.isEmpty());
		appMap.flush();
		Assert.assertFalse(cache.isEmpty());
	}
}
