package com.porpoise.common.key;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.porpoise.common.Pair;

@SuppressWarnings("synthetic-access")
public class BusinessKeysTest {

    public static class Person {
        private final String name;
        private final int    age;

        public Person(final String value, final int age) {
            name = value;
            this.age = age;
        }

        public int getAge() {
            return age;
        }

        @BusinessKey(required = false)
        public String getName() {
            return name;
        }

    }

    public static class Employee extends Person {
        private static final BusinessKeys<Employee> KEY = BusinessKeys.valueOf(Employee.class);
        private final long                          id;

        public Employee(final long id, final String value, final int age) {
            super(value, age);
            this.id = id;
        }

        @BusinessKey
        public long id() {
            return id;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Employee) {
                return KEY.equals(this, (Employee) obj);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return KEY.hashCode(this);
        }

        @Override
        public String toString() {
            return KEY.toString(this);
        }
    }

    @Test
    public void testPersonsBusinessKeyEquals() {
        final BusinessKeys<Person> bk = BusinessKeys.valueOf(Person.class);
        Assert.assertTrue(bk.equals(new Person("Steve", 12), new Person("Steve", 14)));
        Assert.assertFalse(bk.equals(new Person("Dave", 12), new Person("Steve", 14)));
        Assert.assertFalse(bk.equals(new Person("Dave", 12), null));
        Assert.assertFalse(bk.equals(null, new Person("Carl", 12)));
        Assert.assertTrue(bk.equals(null, null));
        Assert.assertTrue(bk.equals(new Employee(1, "Steve", 45), new Person("Steve", 14)));
        Assert.assertTrue(bk.equals(new Person("Steve", 14), new Employee(1, "Steve", 45)));
    }

    @Test
    public void testEmployeeBusinessKeyEquals() {
        final BusinessKeys<Employee> bk = Employee.KEY;
        Assert.assertTrue(bk.equals(new Employee(1, "Steve", 45), new Employee(1, "Steve", 14)));
        Assert.assertFalse(bk.equals(new Employee(1000, "Steve", 45), new Employee(1, "Steve", 14)));
        Assert.assertFalse(bk.equals(new Employee(1, "Dave", 45), new Employee(1, "Steve", 14)));
    }

    @Test
    public void testHashCode() {
        final Employee s1 = new Employee(1, "Steve", 6);
        final Employee c1 = new Employee(1, "Carl", 7);
        final Employee s2 = new Employee(2, "Steve", 8);
        final Employee c2 = new Employee(2, "Carl", 9);
        final Employee repeat = new Employee(1, "Steve", 123);
        final Set<Employee> set = Sets.newHashSet(//
                s1,//
                c1,//
                s2,//
                c2,//
                repeat//
                );

        Assert.assertEquals(4, set.size());
        Assert.assertTrue(set.contains(s1));
        Assert.assertTrue(set.contains(s2));
        Assert.assertTrue(set.contains(c1));
        Assert.assertTrue(set.contains(c2));
    }

    @Test
    public void testDifferenceByName() {
        final Map<String, Pair<Object, Object>> diff = Employee.KEY.differences(new Employee(1, "Steve", 6), new Employee(1, "Paul", 123));
        Assert.assertEquals(1, diff.size());
        Assert.assertEquals(Pair.valueOf("Steve", "Paul"), diff.get("name"));
    }

    @SuppressWarnings("boxing")
    @Test
    public void testDifferenceById() {
        final Map<String, Pair<Object, Object>> diff = Employee.KEY.differences(new Employee(2, "Steve", 6), new Employee(1, "Steve", 16));
        Assert.assertEquals(1, diff.size());
        Assert.assertEquals(Pair.valueOf(2L, 1L), diff.get("id"));
    }

    @Test
    public void testValuesByName() {
        final Map<String, Object> valuesByName = Employee.KEY.valuesByName(new Employee(555, "Aaron", 33));
        Assert.assertEquals(2, valuesByName.size());
        Assert.assertEquals(Long.valueOf(555), valuesByName.get("id"));
        Assert.assertEquals("Aaron", valuesByName.get("name"));
    }

    @Test
    public void testMissingRequiredValues() {
        Assert.assertTrue(Employee.KEY.missingRequiredValues(new Employee(555, "Aaron", 33)).isEmpty());
        Assert.assertEquals("name", Iterables.getOnlyElement(Employee.KEY.missingRequiredValues(new Employee(555, null, 33))));
    }

    @Test
    public void testToString() {
        final String string = new Employee(555, "Aaron", 33).toString();
        Assert.assertEquals("Employee{name=Aaron, id*=555}", string);
    }
}
