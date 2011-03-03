package com.tullettprebon.dms.key;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 * 
 */
class BusinessKeyMap
{
	private static BusinessKeyMap										instance	= new BusinessKeyMap();

	private final Map<Class<?>, Iterable<BusinessKeyAccessor<?, ?>>>	map;

	/**
	 * 
	 */
	public BusinessKeyMap()
	{
		this.map = new MapMaker().makeComputingMap(new Function<Class<?>, Iterable<BusinessKeyAccessor<?, ?>>>()
		{

			@Override
			public Iterable<BusinessKeyAccessor<?, ?>> apply(final Class<?> input)
			{
				return getAccessors(input);
			}
		});
	}

	/**
	 * @param input
	 * @return
	 */
	protected Iterable<BusinessKeyAccessor<?, ?>> getAccessors(final Class<?> input)
	{
		final Collection<BusinessKeyAccessor<?, ?>> keys = Lists.newArrayList();
		for (final Field f : input.getFields())
		{
			final BusinessKey key = f.getAnnotation(BusinessKey.class);
			if (key != null)
			{
				keys.add(new BusinessKeyAccessorImpl(f));
			}
		}
		return ImmutableList.copyOf(keys);
	}

	/**
	 * @return
	 */
	static BusinessKeyMap instance()
	{
		return instance;
	}

	/**
	 * @param abstractBaseEntity
	 * @return
	 */
	public <T> Iterable<BusinessKeyAccessor<T, ?>> getBusinessKeys(
			final T abstractBaseEntity)
	{
		this.map.get(abstractBaseEntity);
		return null;
	}

}
