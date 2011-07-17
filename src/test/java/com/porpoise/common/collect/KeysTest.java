package com.porpoise.common.collect;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.porpoise.common.functions.FunctionSet;
import com.porpoise.common.functions.Key;
import com.porpoise.common.functions.Keys;
import com.porpoise.common.test.Assertions;

/**
 * Test for {@link Keys}
 */
public class KeysTest {
    private final Grape a = new Grape(1, 2, Color.Red);
    private final Grape b = new Grape(1, 3, Color.Red);

    /**
     * test the {@link Keys#keyFunction(com.google.common.base.Function...) } method
     */
    @Test
    public void testKeyFunctionKeyOnSingleProperty() {
        final Function<Grape, ? extends Object> keyOnColor = GrapeAccessors.GET_COLOR;
        final Set<Grape> set = new FunctionSet<Grape>(keyOnColor, this.a, this.b);
        Assert.assertSame(this.a, Iterables.getOnlyElement(set));
    }

    /**
     * test the {@link Keys#keyFunction(com.google.common.base.Function...) } method
     */
    @Test
    public void testKeyFunctionKeyOnMultipleProperties() {
        @SuppressWarnings("unchecked")
        final Function<Grape, Key<Grape>> keyOnInts = Keys.keyFunction(GrapeAccessors.GET_RADIUS,
                GrapeAccessors.GET_RIPENESS);
        final Set<Grape> intSet = new FunctionSet<Grape>(keyOnInts, this.a, this.b);
        Assert.assertEquals(2, intSet.size());
        Assert.assertTrue(intSet.contains(this.a));
        Assert.assertTrue(intSet.contains(this.b));
    }
    
    /**
     * test we can key with null values
     */
    @Test
    public void testKeyOnNullableProperty() {
    	final Function<Grape, Key<Grape>> keyOnColor = Keys.keyFunction(GrapeAccessors.GET_COLOR);
    	final Grape grape1 = new Grape(1,1,null);
    	final Key<Grape> key1 = keyOnColor.apply(grape1);
		
    	final Grape grape2 = new Grape(2,2,null);
    	final Key<Grape> key2 = keyOnColor.apply(grape2);
		
		Assertions.assertEquality(key1, key2);
    }
    
    /**
     */
    @Test(expected=NullPointerException.class)
    public void testKeysForNullInputThowsNullPointerException() {
    	final Function<Grape, Key<Grape>> f = Keys.keyFunction(GrapeAccessors.GET_COLOR);
    	f.apply(null);
    }
}
