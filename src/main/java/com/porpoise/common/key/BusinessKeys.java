package com.porpoise.common.key;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.ValidationException;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.porpoise.common.Pair;
import com.porpoise.common.collect.Sequences;

abstract class BusinessKeyAccessor<T, V> implements Function<T, V> {
    private final String name;
    private final boolean required;
    private final Collection<String> types;

    BusinessKeyAccessor(final String name, final boolean required, final String... typeArray) {
        this.name = name;
        this.required = required;
        final Function<String, String> asKeyFnc = new Function<String, String>() {
            @Override
            public String apply(final String input) {
                return BusinessKeys.asKey(input);
            }
        };
        this.types = Lists.transform(ImmutableList.copyOf(typeArray), asKeyFnc);

    }

    public Collection<String> getTypes() {
        return this.types;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s%s %s", this.name, (this.required ? "*" : ""), this.types);
    }
}

/**
 */
class KeySupplier<T, V> implements Supplier<BusinessKeyAccessor<T, V>> {

    private final BusinessKeyAccessor<T, V> instance;

    public KeySupplier(final Method m, final boolean required, final String... types) {
        Preconditions.checkArgument(m.getReturnType() != null, "Annotated method %s must have a non-null return type",
                m.getName());
        Preconditions.checkArgument(m.getParameterTypes().length == 0,
                "Annotated method %s cannot take any parameters", m.getName());
        final String name = m.getName();
        this.instance = new BusinessKeyAccessor<T, V>(name, required, types) {
            @SuppressWarnings("unchecked")
            @Override
            public V apply(final T input) {
                try {
                    return input == null ? null : (V) m.invoke(input);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * @param f
     * @param required
     * @param types
     */
    public KeySupplier(final Field f, final boolean required, final String[] types) {
        final String name = f.getName();
        this.instance = new BusinessKeyAccessor<T, V>(name, required, types) {
            @SuppressWarnings("unchecked")
            @Override
            public V apply(final T input) {
                try {
                    return input == null ? null : (V) f.get(input);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public BusinessKeyAccessor<T, V> get() {
        return this.instance;
    }
}

/**
 * Utility class to support operations on objects based on methods annotated with the {@link BusinessKey} annotation
 * 
 * @param <T>
 *            The type for these keys
 */
public class BusinessKeys<T> {

    private final Class<T> c1ass;
    private final Map<String, BusinessKeyAccessor<T, Object>> keyByName;
    private final Function<String, Map<String, BusinessKeyAccessor<T, Object>>> keysByType;

    /**
     * Factory method for creating business keys
     * 
     * @param <T>
     * @param class1
     * @return a {@link BusinessKeys} object for the given class
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
                keys.add(new KeySupplier<T, Object>(m, key.required(), key.type()));
            }
        }
        for (final Field f : c1ass.getFields()) {
            final BusinessKey key = f.getAnnotation(BusinessKey.class);
            if (key != null) {
                keys.add(new KeySupplier<T, Object>(f, key.required(), key.type()));
            }
        }
        final Function<Supplier<BusinessKeyAccessor<T, Object>>, BusinessKeyAccessor<T, Object>> sf = Suppliers
                .supplierFunction();
        final Collection<BusinessKeyAccessor<T, Object>> businessKeys = Collections2.transform(keys, sf);
        this.keyByName = ImmutableMap.copyOf(Sequences.groupByUnique(businessKeys, getNameFunction()));
        final ConcurrentMap<String, Map<String, BusinessKeyAccessor<T, Object>>> byType = new MapMaker()
                .makeComputingMap(new Function<String, Map<String, BusinessKeyAccessor<T, Object>>>() {
                    @Override
                    public Map<String, BusinessKeyAccessor<T, Object>> apply(final String input) {
                        return computeKeyByName(input);
                    }
                });
        final Map<String, BusinessKeyAccessor<T, Object>> defaultMap = Collections.emptyMap();
        this.keysByType = Functions.forMap(byType, defaultMap);
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

    Iterable<BusinessKeyAccessor<T, Object>> businessKeysForType(final String type) {
        return lookupForType(type).values();
    }

    private Map<String, BusinessKeyAccessor<T, Object>> computeKeyByName(final String type) {
        final String typeAsKey = asKey(type);
        final Predicate<BusinessKeyAccessor<T, Object>> filter = new Predicate<BusinessKeyAccessor<T, Object>>() {
            @Override
            public boolean apply(final BusinessKeyAccessor<T, Object> input) {
                final Collection<String> types = input.getTypes();
                return types.contains(typeAsKey);
            }
        };
        final Map<String, BusinessKeyAccessor<T, Object>> filteredDeyByName = Maps.filterValues(this.keyByName, filter);
        return filteredDeyByName;
    }

    public String toString(final T instance) {
        return toString(null, instance);
    }

    /**
     * @param instance
     * @return a string consisting of all the business keys
     */
    public String toString(final String type, final T instance) {
        final boolean hasType = !Strings.isNullOrEmpty(type);
        final ToStringHelper helper = Objects.toStringHelper(this.c1ass);
        for (final BusinessKeyAccessor<T, Object> accessor : businessKeysForType(type)) {
            final String requiredStr = accessor.isRequired() ? "*" : "";
            final String name = getName(accessor.getName());

            String str;
            final Object obj = accessor.apply(instance);
            if (hasType && obj instanceof BusinessEquality) {
                final BusinessEquality be = (BusinessEquality) obj;
                str = be.businessToString(type);
            } else {
                str = obj.toString();
            }
            helper.add(name + requiredStr, str);
        }
        return helper.toString();
    }

    public int hashCode(final T instance) {
        return hashCode(null, instance);
    }

    /**
     * @param instance
     * @return a hash code consisting of all the business keys
     */
    public int hashCode(final String type, final T instance) {
        final Map<String, BusinessKeyAccessor<T, Object>> map = lookupForType(type);
        if (map.isEmpty()) {
            return System.identityHashCode(instance);
        }

        final Iterable<Object> values = Iterables.transform(map.values(), apply(instance));

        if (Strings.isNullOrEmpty(type)) {
            return Objects.hashCode(Iterables.toArray(values, Object.class));
        }
        final Function<Pair<Integer, Object>, Integer> hash = new Function<Pair<Integer, Object>, Integer>() {
            @Override
            public Integer apply(final Pair<Integer, Object> input) {
                final Integer oldHash = input.getFirst();
                final Object obj = input.getSecond();
                int newHash;
                if (obj instanceof BusinessEquality) {
                    final BusinessEquality be = (BusinessEquality) obj;
                    newHash = be.businessHashCode(type);
                } else {
                    newHash = Objects.hashCode(obj);
                }
                final int result = Objects.hashCode(oldHash, newHash);
                return Integer.valueOf(result);
            }
        };
        final Integer hashCode = Sequences.foldLeft(Integer.valueOf(17), values, hash);
        return hashCode.intValue();
    }

    private Map<String, BusinessKeyAccessor<T, Object>> lookupForType(final String type) {
        Map<String, BusinessKeyAccessor<T, Object>> map;
        if (!Strings.isNullOrEmpty(type)) {
            map = this.keysByType.apply(type);
        } else {
            map = this.keyByName;
        }
        return map;
    }

    /**
     * @return true if no business keys were defined
     */
    public boolean isEmpty() {
        return this.keyByName.isEmpty();
    }

    /**
     * @param first
     * @param second
     * @return true if the two objects are equal according to their business keys
     */
    public boolean equals(final T first, final T second) {
        if (first == null) {
            return second == null;
        }
        if (second == null) {
            return false;
        }
        final Iterable<Object> values1 = Iterables.transform(businessKeysForType(null), apply(first));
        final Iterable<Object> values2 = Iterables.transform(businessKeysForType(null), apply(second));
        return Iterables.elementsEqual(values1, values2);
    }

    /**
     * @param type
     * @param first
     * @param second
     * @return true if the two equal
     */
    public boolean equals(final String type, final T first, final T second) {
        if (first == null) {
            return second == null;
        }
        if (second == null) {
            return false;
        }
        final Map<String, BusinessKeyAccessor<T, Object>> lookup = lookupForType(type);
        final Iterable<BusinessKeyAccessor<T, Object>> keys = lookup.values();
        if (Iterables.isEmpty(keys)) {
            return false;
        }
        final Iterable<Object> values1 = Iterables.transform(keys, apply(first));
        final Iterable<Object> values2 = Iterables.transform(keys, apply(second));
        return elementsEqual(type, values1, values2);
    }

    /**
     * @param values1
     * @param values2
     * @return
     */
    static boolean elementsEqual(final String type, final Iterable<Object> values1, final Iterable<Object> values2) {
        final Iterator<Object> iterator1 = values1.iterator();
        final Iterator<Object> iterator2 = values2.iterator();
        final boolean hasType = !Strings.isNullOrEmpty(type);
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            final Object o1 = iterator1.next();
            final Object o2 = iterator2.next();
            if (hasType) {
                if (!typeAwareEquals(type, o1, o2)) {
                    return false;
                }
            } else if (!Objects.equal(o1, o2)) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }

    @SuppressWarnings("unchecked")
    private static boolean typeAwareEquals(final String type, final Object o1, final Object o2) {
        if (o1 instanceof BusinessEquality) {
            final BusinessEquality be = (BusinessEquality) o1;
            if (!be.businessEquals(type, o2)) {
                return false;
            }
        } else if (o1 instanceof Collection<?>) {
            if (!(o2 instanceof Collection<?>)) {
                return false;
            }
            final Collection<Object> collectionOne = (Collection<Object>) o1;
            final Collection<Object> collectionTwo = (Collection<Object>) o2;
            if (!elementsEqual(type, collectionOne, collectionTwo)) {
                return false;
            }
        } else if (!Objects.equal(o1, o2)) {
            return false;
        }
        return true;
    }

    /**
     * @param first
     * @param second
     * @return a collection of the field names and values which differ
     */
    public Map<String, Pair<Object, Object>> differences(final T first, final T second) {
        return diff(null, this.keyByName, first, second);
    }

    /**
     * @param keys
     * @param first
     * @param second
     * @return
     */
    private Map<String, Pair<Object, Object>> diff(final String type,
            final Map<String, BusinessKeyAccessor<T, Object>> keys, final T first, final T second) {
        final Map<String, Pair<Object, Object>> valuesByName = Maps.transformValues(keys, getValues(first, second));
        Predicate<Pair<Object, Object>> predicate;
        if (Strings.isNullOrEmpty(type)) {
            predicate = Pair.different();
        } else {
            predicate = different(type);
        }
        return Maps.filterValues(valuesByName, predicate);
    }

    static Predicate<Pair<Object, Object>> different(final String type) {
        return new Predicate<Pair<Object, Object>>() {
            @Override
            public boolean apply(final Pair<Object, Object> input) {
                return !typeAwareEquals(type, input.getFirst(), input.getSecond());
            }
        };
    }

    /**
     * 
     * @param type
     * @param first
     * @param second
     * @return the diff
     */
    public Map<String, Pair<Object, Object>> differences(final String type, final T first, final T second) {
        final Map<String, BusinessKeyAccessor<T, Object>> filteredDeyByName = lookupForType(type);
        return diff(type, filteredDeyByName, first, second);
    }

    /**
     * @param first
     * @return the names of the fields which were annotated as required but are missing
     */
    public Set<String> missingRequiredValues(final T first) {
        return Maps.filterValues(valuesByName(first), Predicates.isNull()).keySet();
    }

    private static <K> Function<BusinessKeyAccessor<K, Object>, Pair<Object, Object>> getValues(final K first,
            final K second) {
        return new Function<BusinessKeyAccessor<K, Object>, Pair<Object, Object>>() {
            @Override
            public Pair<Object, Object> apply(final BusinessKeyAccessor<K, Object> key) {
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

    /**
     * @param first
     * @return a map of the business key values by their property name
     */
    public Map<String, Object> valuesByName(final T first) {
        return Maps.transformValues(this.keyByName, getValue(first));
    }

    private Function<BusinessKeyAccessor<T, Object>, Object> apply(final T instance) {
        return new Function<BusinessKeyAccessor<T, Object>, Object>() {
            @Override
            public Object apply(final BusinessKeyAccessor<T, Object> key) {
                return key.apply(instance);
            }
        };
    }

    /**
     * @return a function which will convert objects into keys
     */
    public Function<T, Object> keyFunction() {
        return new Function<T, Object>() {
            @Override
            public Object apply(final T input) {
                return makeKey(input);
            }
        };
    }

    /**
     * @return a function which will convert objects into keys
     */
    public Function<T, Object> keyFunction(final String type) {
        return new Function<T, Object>() {
            @Override
            public Object apply(final T input) {
                return makeKey(type, input);
            }
        };
    }

    /**
     * @param objects
     *            the input objects
     * @return a map containing the objects by their common key
     */
    public Map<Object, Collection<T>> group(final Iterable<T> objects) {
        return group(null, objects);
    }

    /**
     * @param objects
     *            the input objects
     * @return a map containing the objects by their common key
     */
    public Map<Object, Collection<T>> group(final String type, final Iterable<T> objects) {
        return Sequences.groupBy(objects, keyFunction(type));
    }

    /**
     * @param objects
     *            the input objects
     * @return a map containing the objects by their unique key
     */
    public Map<Object, T> groupUnique(final Iterable<T> objects) {
        return groupUnique(null, objects);
    }

    /**
     * @param objects
     *            the input objects
     * @return a map containing the objects by their unique key
     */
    public Map<Object, T> groupUnique(final String type, final Iterable<T> objects) {
        try {
            return Sequences.groupByUnique(objects, keyFunction(type));
        } catch (final IllegalArgumentException notUnique) {
            final String error = invalidBusinessKeyError(objects);
            throw new IllegalArgumentException(String.format("%s%nBusiness Values Are:%n%s", notUnique.getMessage(),
                    error));
        }
    }

    /**
     * We're trusting that we get some unique business properties.
     * 
     * @param values
     *            the values which are suspected to result in hash collisions
     * @return a more useful string method to explain which objects have collided and what their properties/hashes are
     */
    private String invalidBusinessKeyError(final Iterable<T> values) {
        final StringBuilder b = new StringBuilder();

        final Map<Object, Collection<T>> all = group(values);
        final Predicate<Iterable<T>> filter = Sequences.sizeGreaterThan(1);
        final Map<Object, Collection<T>> duplicates = Maps.filterValues(all, filter);
        for (final Entry<Object, Collection<T>> duplicateEntry : duplicates.entrySet()) {
            b.append(String.format("%n==== DUPLICATE FOR HASH %d ====%n", duplicateEntry.getKey()));
            final Collection<T> collisions = duplicateEntry.getValue();
            for (final T collision : collisions) {
                final Integer hash = Integer.valueOf(hashCode(collision));
                final String hashString = toString(collision);
                b.append(String.format("%10s => %s%n", hash, hashString));
            }
        }
        return b.toString();
    }

    /**
     * @param first
     *            the input objects
     * @param second
     *            the input objects
     * @param merge
     * @return a map containing the objects by their unique key
     */
    public Collection<T> mergeUnique(final Iterable<T> first, final Iterable<T> second,
            final Function<Pair<T, T>, T> merge) {
        final Map<Object, T> map1 = groupUnique(first);
        final Map<Object, T> map2 = groupUnique(second);
        return Sequences.mergeMaps(map1, map2, merge).values();
    }

    /**
     * @param <K>
     * @param propertyName
     * @return a function which will return the value for the given property
     */
    @SuppressWarnings("unchecked")
    public <K> Function<T, K> getPropertyFunction(final String propertyName) {
        return (Function<T, K>) Preconditions.checkNotNull(this.keyByName.get(propertyName),
                "Invalid property name '%s'. Available properties are: %s", propertyName,
                Sequences.toString(this.keyByName.keySet()));
    }

    class HashKey {
        private final T wrappedObject;
        private final int hashCode;
        private final Class<T> keyedClass;
        private final String type;

        @SuppressWarnings("synthetic-access")
        HashKey(final String type, final T obj) {
            this.type = type;
            this.keyedClass = BusinessKeys.this.c1ass;
            this.wrappedObject = obj;
            // we assume that a business key *should* not change -- in particular if we're going to be using
            // it for a key as we are in this context. Therefore we pre-compute the hashCode here, both for some
            // small performance benefits AND to protect against a business key property changing AFTER we've stored
            // it in a map or set
            this.hashCode = BusinessKeys.this.hashCode(type, this.wrappedObject);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public boolean equals(final Object other) {
            if (other == null) {
                return false;
            }
            if (other == this.wrappedObject) {
                return true;
            }
            if (other instanceof BusinessKeys.HashKey) {
                final BusinessKeys.HashKey otherKey = (BusinessKeys.HashKey) other;
                if (otherKey.keyedClass.isAssignableFrom(this.keyedClass)) {
                    return BusinessKeys.this.equals(this.type, this.wrappedObject, (T) otherKey.wrappedObject);
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "KEY FOR " + BusinessKeys.this.toString(this.type, this.wrappedObject);
        }
    }

    public Object makeKey(final T obj) {
        return makeKey(null, obj);
    }

    /**
     * Create a key which can be used to store instances of this object in maps, sets, etc.
     * 
     * @param string
     * 
     * @param obj
     *            the object for which to make a key
     * @return an object which can be used as a key for the given object
     */
    public Object makeKey(final String type, final T obj) {
        return new HashKey(type, obj);
    }

    /**
     * Convenience method to group by a given property
     * 
     * @param <K>
     * @param propertyName
     * @param values
     * @return the values by the given property
     */
    public <K> Map<K, T> groupByPropertyUnique(final String propertyName, final Iterable<T> values) {
        final Function<T, K> fnc = getPropertyFunction(propertyName);
        return Sequences.groupByUnique(values, fnc);
    }

    /**
     * Convenience method to group by a given property
     * 
     * @param <K>
     * @param propertyName
     * @param values
     * @return the values by the given property
     */
    public <K> Map<K, Collection<T>> groupByProperty(final String propertyName, final Iterable<T> values) {
        final Function<T, K> fnc = getPropertyFunction(propertyName);
        return Sequences.groupBy(values, fnc);
    }

    /**
     * @param value
     * @throws ValidationException
     */
    public void validate(final T value) throws ValidationException {
        final Set<String> missing = missingRequiredValues(value);
        if (!missing.isEmpty()) {
            throw new IllegalStateException(String.format("%s missing required properties: %s", toString(value),
                    Sequences.toString(missing)));
        }
    }

    static String asKey(final String value) {
        return CharMatcher.WHITESPACE.removeFrom(value).toUpperCase();
    }

}