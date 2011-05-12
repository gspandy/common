package com.porpoise.common.strings;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

/**
 * EVEN MORE string functions!
 */
public enum Strings2 {

    ; // uninstantiable

    /**
     */
    public static final String NEW_LINE = String.format("%n");

    /**
     * @param suffix
     *            the suffix to test against
     * @return a predicate which returns true if the input string ends with the supplied suffix
     */
    public static Predicate<String> endsWith(final String suffix) {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input != null && input.endsWith(suffix);
            }
        };
    }

    /**
     * @param string
     * @return a predicate which returns true if the input string contains the supplied string
     */
    public static Predicate<String> contains(final String string) {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input != null && input.contains(string);
            }
        };
    }

    /**
     * @return the string identity function
     */
    public static Function<String, String> textIdentity() {
        return Functions.identity();
    }

    /**
     * @param source
     * @param target
     * @return a function which will replace the source string with the target string in its input
     */
    public static Function<String, String> replace(final String source, final String target) {
        return new Function<String, String>() {
            @Override
            public String apply(final String string) {
                final String newValue = string.replaceAll(source, target);
                return newValue;
            }
        };
    }
}
