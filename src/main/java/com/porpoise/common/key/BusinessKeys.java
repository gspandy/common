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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.Sequences;
import com.porpoise.common.core.Pair;

/**
 * A BusinessKeyAccessor is used internally as a way to access the @BusinessKey annotated fields and methods.
 * 
 * This class is for internal use only
 * 
 * @param <T>
 *            The type of the annotated class
 * @param <V>
 *            The target type of the annotated value
 */
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

    @Override
    public String toString() {
        return String.format("%s%s %s", this.name, (this.required ? "*" : ""), this.types);
    }
}

/**
 * Factory class for creating BusinessKeyAccessor
 */
enum AccessorFactory {
    ;// uninstantiable

    static <T, V> BusinessKeyAccessor<T, V> valueOf(final Method m, final boolean required, final String... types) {
        Preconditions.checkArgument(m.getReturnType() != null, "Annotated method %s must have a non-null return type",
                m.getName());
        Preconditions.checkArgument(m.getParameterTypes().length == 0,
                "Annotated method %s cannot take any parameters", m.getName());
        final String name = m.getName();
        return new BusinessKeyAccessor<T, V>(name, required, types) {
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

    static <T, V> BusinessKeyAccessor<T, V> valueOf(final Field f, final boolean required, final String[] types) {
        final String name = f.getName();
        return new BusinessKeyAccessor<T, V>(name, required, types) {
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
}

/**
 * Utility class to support operations on objects based on methods annotated with the {@link BusinessKey} annotation
 * 
 * @param <T>
 *            The type for these keys
 */
@SuppressWarnings("synthetic-access")
public class BusinessKeys<T> {

    private final Class<T> c1ass;
    private ImmutableMap<String, BusinessKeyAccessor<T, Object>> keyByName;
    private Function<String, Map<String, BusinessKeyAccessor<T, Object>>> keysByType;

    /**
     * Internal representation of a key object which represents just the values specified by a set of business keys
     */
    class HashKey {
        private final T wrappedObject;
        private final int hashCode;
        private final Class<T> keyedClass;
        private final String type;

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
        final Collection<BusinessKeyAccessor<T, Object>> accessors = Lists.newArrayList();
        for (final Method m : c1ass.getMethods()) {
            final BusinessKey key = m.getAnnotation(BusinessKey.class);
            if (key != null) {
                accessors.add(AccessorFactory.<T, Object> valueOf(m, key.required(), key.type()));
            }
        }
        for (final Field f : c1ass.getFields()) {
            final BusinessKey key = f.getAnnotation(BusinessKey.class);
            if (key != null) {
                accessors.add(AccessorFactory.<T, Object> valueOf(f, key.required(), key.type()));
            }
        }
        this.keyByName = ImmutableMap.copyOf(Sequences.groupByUnique(accessors,
                new Function<BusinessKeyAccessor<T, Object>, String>() {
                    @Override
                    public String apply(final BusinessKeyAccessor<T, Object> key) {
                        return getName(key.getName());
                    }
                }));
        // our keysByType is actually a function which will return a default value for any undefined keys
        this.keysByType = Functions
                .forMap(mapByType(), Collections.<String, BusinessKeyAccessor<T, Object>> emptyMap());
    }

    /**
     * @return a map which will in-turn return a mapping between a property name and accessor for a given business key
     *         type
     */
    private ConcurrentMap<String, Map<String, BusinessKeyAccessor<T, Object>>> mapByType() {
        final ConcurrentMap<String, Map<String, BusinessKeyAccessor<T, Object>>> byType = new MapMaker()
                .makeComputingMap(new Function<String, Map<String, BusinessKeyAccessor<T, Object>>>() {
                    @Override
                    public Map<String, BusinessKeyAccessor<T, Object>> apply(final String input) {
                        return computeKeyByName(input);
                    }
                });
        return byType;
    }

    /**
     * performs a linear search through all knows keys, filtering out only those matching the given type string
     * 
     * @param type
     *            the business key type
     * @return a subset of the keyByName map which only contains keys for the given type
     */
    private Map<String, BusinessKeyAccessor<T, Object>> computeKeyByName(final String type) {
        final String typeAsKey = asKey(type);
        return Maps.filterValues(this.keyByName, new Predicate<BusinessKeyAccessor<T, Object>>() {
            @Override
            public boolean apply(final BusinessKeyAccessor<T, Object> input) {
                final Collection<String> types = input.getTypes();
                return types.contains(typeAsKey);
            }
        });
    }

    /**
     * @return a consistent string representation for a given string value
     */
    static String asKey(final String value) {
        return CharMatcher.WHITESPACE.removeFrom(value).toUpperCase();
    }

    /**
     * @param n
     *            the source name
     * @return a lower-camel-case string with the accessor prefix removed if found (e.g. getThing => thing)
     */
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

    /**
     * @param type
     *            the business key 'type' (e.g. "logical" in @BusinessKey("logical"))
     * @return all business key accessors for the given business key type
     */
    Iterable<BusinessKeyAccessor<T, Object>> businessKeysForType(final String type) {
        return lookupForType(type).values();
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

        final Iterable<Object> values = Iterables.transform(map.values(), accessorFunction(instance));

        if (Strings.isNullOrEmpty(type)) {
            return Objects.hashCode(Iterables.toArray(values, Object.class));
        }
        /**
         * Function which takes into account objects which implement the "BusinessEqquality" interface, preferring to
         * use that over standard Object#hashCode if found.
         */
        final Function<Pair<Integer, Object>, Integer> hashingFunction = new Function<Pair<Integer, Object>, Integer>() {
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
                final int result = Objects.hashCode(oldHash, Integer.valueOf(newHash));
                return Integer.valueOf(result);
            }
        };
        return Sequences.foldLeft(Integer.valueOf(17), values, hashingFunction).intValue();
    }

    private Map<String, BusinessKeyAccessor<T, Object>> lookupForType(final String type) {
        if (!Strings.isNullOrEmpty(type)) {
            return this.keysByType.apply(type);
        }
        return this.keyByName;
    }

    /**
     * @return true if no business keys are defined
     */
    public boolean isEmpty() {
        return this.keyByName.isEmpty();
    }

    /**
     * @param first
     *            the first object to compare
     * @param second
     *            the second object to compare
     * @return true if the two objects are equal according to all of their business keys
     */
    public boolean equals(final T first, final T second) {
        return equals(null, first, second);
    }

    /**
     * @param type
     *            An optional, specific business key type
     * @param first
     *            the first object to compare
     * @param second
     *            the second object to compare
     * @return true if the two equal according to the business keys of the given type
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
        Preconditions.checkArgument(!Iterables.isEmpty(keys), "No business keys for type '%s' are defined for %s",
                type, this.c1ass);
        final Iterable<Object> values1 = Iterables.transform(keys, accessorFunction(first));
        final Iterable<Object> values2 = Iterables.transform(keys, accessorFunction(second));
        return typeAwareElementsEqual(type, values1, values2);
    }

    private Function<BusinessKeyAccessor<T, Object>, Object> accessorFunction(final T instance) {
        return new Function<BusinessKeyAccessor<T, Object>, Object>() {
            @Override
            public Object apply(final BusinessKeyAccessor<T, Object> key) {
                return key.apply(instance);
            }
        };
    }

    /**
     * @return true if all elements are equal in the same order, taking objects which implement BusinessEquals into
     *         consideration
     */
    private static boolean typeAwareElementsEqual(final String type, final Iterable<Object> values1,
            final Iterable<Object> values2) {
        // this could save us a bit of work: no point comparing 100 elements only to find the collections
        // are of unequal sizes
        if (Iterables.size(values1) != Iterables.size(values2)) {
            return false;
        }
        final Iterator<Object> iterator1 = values1.iterator();
        final Iterator<Object> iterator2 = values2.iterator();
        final boolean hasType = !Strings.isNullOrEmpty(type);
        while (iterator1.hasNext()) {
            assert iterator2.hasNext();
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
        } else if (o1 instanceof Iterable<?>) {
            if (!(o2 instanceof Iterable<?>)) {
                return false;
            }
            final Iterable<Object> collectionOne = (Iterable<Object>) o1;
            final Iterable<Object> collectionTwo = (Iterable<Object>) o2;
            if (!typeAwareElementsEqual(type, collectionOne, collectionTwo)) {
                return false;
            }
        } else if (!Objects.equal(o1, o2)) {
            return false;
        }
        return true;
    }

    /**
     * @see #differences(String, Object, Object)
     * 
     * @param first
     *            the first object to compare
     * @param second
     *            the second object to compare
     * @return a collection of values which differ, keyed on their property name
     */
    public Map<String, Pair<Object, Object>> differences(final T first, final T second) {
        return diff(null, this.keyByName, first, second);
    }

    /**
     * Return the differences between two types.
     * 
     * For instance, given a Person class with the annotated field and method: <code>
     * 
     * @BusinessKey({"public", "logical"}) private int age;
     * 
     * @BusinessKey("public" ) public String getFirstName() { ... } </code> If two Person instances are given which have
     *                       different ages and first name values, a call to <code>
     * diff = KEY.differences("public", person1, person2);
     * </code> will result in 'diff' containing a map with keys 'firstName' and 'age' whos values contain a pair of the
     *                       different values
     * 
     * @param type
     *            the optional business key type
     * @param first
     *            the first object to compare
     * @param second
     *            the second object to compare
     * @return a collection of values which differ, keyed on their property name
     */
    public Map<String, Pair<Object, Object>> differences(final String type, final T first, final T second) {
        final Map<String, BusinessKeyAccessor<T, Object>> filteredDeyByName = lookupForType(type);
        return diff(type, filteredDeyByName, first, second);
    }

    private Map<String, Pair<Object, Object>> diff(final String type,
            final Map<String, BusinessKeyAccessor<T, Object>> keys, final T first, final T second) {
        final Map<String, Pair<Object, Object>> valuesByName = Maps.transformValues(keys,
                getValueFunction(first, second));
        Predicate<Pair<Object, Object>> predicate;
        if (Strings.isNullOrEmpty(type)) {
            predicate = Pair.different();
        } else {
            predicate = new Predicate<Pair<Object, Object>>() {
                @Override
                public boolean apply(final Pair<Object, Object> input) {
                    return !typeAwareEquals(type, input.getFirst(), input.getSecond());
                }
            };
        }
        return Maps.filterValues(valuesByName, predicate);
    }

    /**
     * A BusinessKey annotation contains an optional 'required' property:
     * 
     * <pre>
     * &#064;BusinessKey(required = true)
     * private Long id;
     * &#064;BusinessKey(required = false)
     * private String name;
     * </pre>
     * 
     * <p>
     * This method will return a set of the property names for all BusinessKey properties marked as 'required' which
     * have null values
     * </p>
     * 
     * @param instance
     *            The object to check for missing values
     * @return the names of the fields which were annotated as required but are missing
     */
    public Set<String> missingRequiredValues(final T instance) {
        return Maps.filterValues(valuesByName(instance), Predicates.isNull()).keySet();
    }

    /**
     * @return a function which will return the values of the two parameters for any given business key.
     */
    private static <K> Function<BusinessKeyAccessor<K, Object>, Pair<Object, Object>> getValueFunction(final K first,
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

    /**
     * @param instance
     *            the object instance
     * @return a map of the business key values by their property name
     */
    public Map<String, Object> valuesByName(final T instance) {
        return Maps.transformValues(this.keyByName, new Function<BusinessKeyAccessor<T, Object>, Object>() {
            @Override
            public Object apply(final BusinessKeyAccessor<T, Object> key) {
                return key.apply(instance);
            }
        });
    }

    /**
     * @return a function which will convert objects into keys
     */
    public Function<T, Object> keyFunction() {
        return keyFunction(null);
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

    /**
     * Create a key object for the given object instance based on its business keys
     * 
     * @param obj
     * @return an object whos hashCode and equals methods will resolve to the business key values for the given instance
     */
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
     * @return an object whos hashCode and equals methods will resolve to the business key values for the given instance
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
     * @throws IllegalStateException
     */
    public void validate(final T value) throws IllegalStateException {
        final Set<String> missing = missingRequiredValues(value);
        if (!missing.isEmpty()) {
            throw new IllegalStateException(String.format("%s missing required properties: %s", toString(value),
                    Sequences.toString(missing)));
        }
    }

}