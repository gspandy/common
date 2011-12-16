package com.porpoise.common.functions;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class Functions2Test {

	@Test
	public void testOrElseWhenFirstFunctionReturnsNull() {
		final Function<Object, String> alwaysNonNull = Functions2.orElse(Functions.<String> constant(null),
		        Functions.constant("non null"));
		Assert.assertEquals("non null", alwaysNonNull.apply("owt"));
		Assert.assertEquals("non null", alwaysNonNull.apply(null));
	}

	@Test
	public void testOrElseWhenFirstFunctionReturnsNonNull() {
		final Function<Object, String> alwaysNonNull = Functions2.orElse(Functions.constant("A"),
		        Functions.constant("B"));
		Assert.assertEquals("A", alwaysNonNull.apply("owt"));
		Assert.assertEquals("B", alwaysNonNull.apply(null));
	}
}
