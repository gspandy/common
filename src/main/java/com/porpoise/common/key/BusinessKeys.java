package com.porpoise.common.key;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.Pair;
import com.porpoise.common.collect.Sequences;

interface BusinessKeyAccessor<T, V> extends Function<T, V> {
    public String getName();

    public boolean isRequired();
}

/**
 */
class KeySupplier<T, V> implements Supplier<BusinessKeyAccessor<T, V>> {

    private BusinessKeyAccessor<T, V> instance;

    public KeySupplier(final Method m, final boolean required) {
        Preconditions.checkArgument(m.getReturnType() != null, "Annotated method %s must have a non-null return type", m.getName());
        Preconditions.checkArgument(m.getParameterTypes().length == 0, "Annotated method %s cannot take any parameters", m.getName());
        final String name = m.getName();
        instance = new BusinessKeyAccessor<T, V>() {
            @SuppressWarnings("unchecked")
            @Override
            public V apply(final T input) {
                try {
                    return input == null ? null : (V) m.invoke(input);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isRequired() {
                return required;
            }
        };
    }

    @Override
    public BusinessKeyAccessor<T, V> get() {
        return instance;
    }
}

/**
 * Utility class to support operations on objects based on methods annotated with the {@link BusinessKey} annotation
 */
public class BusinessKeys<T> {

    private final Class<T>                                    c1ass;
    private final Map<String, BusinessKeyAccessor<T, Object>> keyByName;

    /**
     * Factory method for creating business keys
     * 
     * @param <T>
     * @param class1
     * @return
     */
    public static <T> BusinessKeys<T> valueOf(final Class<T> class1) {
        return new BusinessKeys<T>(class1);
    }

    private BusinessKeys(final Class<T> c1ass) {
        this.c1ass = c1ass;
        final Collection<Supplier<BusinessKeyAccessor<T, Object>>> keys = Lists.newArrayList();
        for (final Method m : c1ass.getMethods()) {
            final BusinessKey key = m.getAnnotation(BusinessKey.class);
            if (key != null) {
                keys.add(new KeySupplier<T, Object>(m, key.required()));
            }
        }
        final Function<Supplier<BusinessKeyAccessor<T, Object>>, BusinessKeyAccessor<T, Object>> sf = Suppliers.supplierFunction();
        final Collection<BusinessKeyAccessor<T, Object>> businessKeys = Collections2.transform(keys, sf);
        keyByName = ImmutableMap.copyOf(Sequences.groupByUnique(businessKeys, getNameFunction()));
    }

    private Function<BusinessKeyAccessor<T, Object>, String> getNameFunction() {
        return new Function<BusinessKeyAccessor<T, Object>, String>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public String apply(final BusinessKeyAccessor<T, Object> key) {
                return getName(key.getName());
            }
        };
    }

    private String getName(final String n) {
        // sorry - a little obfuscation is good for the sole
        return trim(trim(n, "is"), "get");
    }

    private static String trim(final String name, final String prefix) {
        if (name.toLowerCase().startsWith(prefix)) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name.substring(prefix.length()));
        }
        return name;
    }

    private Iterable<BusinessKeyAccessor<T, Object>> businessKeys() {
        return keyByName.values();
    }

    /**
     * @param instance
     * @return a string consisting of all the business keys
     */
    public String toString(final T instance) {
        final ToStringHelper helper = Objects.toStringHelper(c1ass);
        for (final BusinessKeyAccessor<T, Object> accessor : businessKeys()) {
            final String requiredStr = accessor.isRequired() ? "*" : "";
            final String name = getName(accessor.getName());
            helper.add(name + requiredStr, accessor.apply(instance));
        }
        return helper.toString();
    }

    /**
     * @param instance
     * @return a hash code consisting of all the business keys
     */
    public int hashCode(final T instance) {
        final Iterable<Object> values = Iterables.transform(businessKeys(), apply(instance));
        return Objects.hashCode(Iterables.toArray(values, Object.class));
    }

    /**
     * @param instance
     * @return true if the two objects are equal according to their business keys
     */
    public boolean equals(final T first, final T second) {
        if (first == null) {
            return second == null;
        }
        if (second == null) {
            return false;
        }
        final Iterable<Object> values1 = Iterables.transform(businessKeys(), apply(first));
        final Iterable<Object> values2 = Iterables.transform(businessKeys(), apply(second));
        return Iterables.elementsEqual(values1, values2);
    }

    /**
     * @param first
     * @param second
     * @return a collection of the field names and values which differ
     */
    public Map<String, Pair<Object, Object>> differences(final T first, final T second) {
        final Map<String, Pair<Object, Object>> valuesByName = Maps.transformValues(keyByName, getValues(first, second));
        return Maps.filterValues(valuesByName, Pair.different());
    }

    /**
     * @param first
     * @return the names of the fields which were annotated as required but are missing
     */
    public Set<String> missingRequiredValues(final T first) {
        return Maps.filterValues(valuesByName(first), Predicates.isNull()).keySet();
    }

    private Function<BusinessKeyAccessor<T, Object>, Pair<Object, Object>> getValues(final T first, final T second) {
        return new Function<BusinessKeyAccessor<T, Object>, Pair<Object, Object>>() {
            @Override
            public Pair<Object, Object> apply(final BusinessKeyAccessor<T, Object> key) {
                final Object alpha = key.apply(first);
                final Object beta = key.apply(second);
                return Pair.valueOf(alpha, beta);
            }
        };
    }

    private Function<BusinessKeyAccessor<T, Object>, Object> getValue(final T first) {
        return new Function<BusinessKeyAccessor<T, Object>, Object>() {
            @Override
            public Object apply(final BusinessKeyAccessor<T, Object> key) {
                return key.apply(first);
            }
        };
    }

    public Map<String, Object> valuesByName(final T first) {
        return Maps.transformValues(keyByName, getValue(first));
    }

    private Function<BusinessKeyAccessor<T, Object>, Object> apply(final T instance) {
        return new Function<BusinessKeyAccessor<T, Object>, Object>() {
            @Override
            public Object apply(final BusinessKeyAccessor<T, Object> key) {
                return key.apply(instance);
            }
        };
    }

}
