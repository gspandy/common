package com.porpoise.common.functions;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.porpoise.common.functions.FunctionMapTest.Thing;

/**
 * Tests for {@link FunctionMap}
 */
public class FunctionMapTest {
	
	static class Thing {
		private final String value;

		public String getValue() {
			return value;
		}

		public Thing(String value){
			this.value = value;
		}
		
		public int getLength() {
			return value.length();
		}
	}
	
	private static final Function<Thing, String> VALUE;
	private static final Function<Thing, Integer> LENGTH;
	
	static {
		VALUE = new Function<FunctionMapTest.Thing, String>() {
			@Override
			public String apply(Thing input) {
				return input.getValue();
			}
		};
		LENGTH = new Function<FunctionMapTest.Thing, Integer>() {
			@Override
			public Integer apply(Thing input) {
				return input.getLength();
			}
		};
	}

	private FunctionMap<Thing> thingByValueAndLength;
	
	@Before
	public void setup() {
		thingByValueAndLength = FunctionMap.create(VALUE, LENGTH);
	}

	/**
	 * 
	 */
	@Test
	public void testPut() {
		thingByValueAndLength.put(new Thing("one"));
	}

}
