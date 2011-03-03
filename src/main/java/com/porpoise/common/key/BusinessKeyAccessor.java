package com.tullettprebon.dms.key;

import com.google.common.base.Function;

/**
 * 
 */
public interface BusinessKeyAccessor<T, V> extends Function<T, V>
{
	public String getName();

	public boolean isRequired();

}
