package com.porpoise.common.functions;

import org.junit.Assert;
import org.junit.Test;

import com.porpoise.common.core.Pair;

/**
 * Tests for {@link FunctionMap}
 */
public class FunctionMapTest extends FunctionMapTestSupport {

	@Test
	public void testPutSingleEntryIntoEmptyMap() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Thing value = verifyAdd(thingByStartsWithAndLength, new Thing("A1"));

		assertContents(thingByStartsWithAndLength, value);
	}

	@Test
	public void testPutNullIntoEmptyMap() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Pair<Key<Thing>, Thing> result = thingByStartsWithAndLength.put(null);
		Assert.assertNull(result.getFirst());
		Assert.assertNull(result.getSecond());

		assertEmptyMap(thingByStartsWithAndLength);
	}

	@Test
	public void testPutDuplicateEntryIntoEmptyMap() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Thing value = verifyAdd(thingByStartsWithAndLength, new Thing("A1"));
		verifyNotAdded(thingByStartsWithAndLength, value);

		assertContents(thingByStartsWithAndLength, value);
	}

	@Test
	public void testPutDuplicateEquivalentEntryIntoEmptyMap() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Thing value = verifyAdd(thingByStartsWithAndLength, new Thing("A1"));

		// This entry is not the same as the first, but the functions will evaluate it to be equivalent as it has the
		// same starting letter and length
		final Thing newValue = new Thing("A2");

		Assert.assertSame(value, verifyNotAdded(thingByStartsWithAndLength, newValue).getSecond());

		assertContents(thingByStartsWithAndLength, newValue);
	}

	@Test
	public void testPutTwoElementsIntoEmptyMapWithDifferOnTheEvaluationOfTheFirstFunction() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Thing a1 = verifyAdd(thingByStartsWithAndLength, new Thing("A1"));

		final Thing b2 = verifyAdd(thingByStartsWithAndLength, new Thing("B2"));

		assertContents(thingByStartsWithAndLength, a1, b2);
	}

	@Test
	public void testPutTwoElementsIntoEmptyMapWithDifferOnTheEvaluationOfTheSecondFunction() {
		final FunctionMap<Thing> thingByStartsWithAndLength = newMapByFirstLetterAndLength();

		final Thing a1 = verifyAdd(thingByStartsWithAndLength, new Thing("A1"));

		final Thing longerEntry = verifyAdd(thingByStartsWithAndLength, new Thing("A longer entry"));

		assertContents(thingByStartsWithAndLength, a1, longerEntry);
	}

}
