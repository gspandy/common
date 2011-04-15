package com.porpoise.common.strings;

import com.google.common.base.Function;
import com.google.common.base.Strings;

/**
 * 
 */
public enum Trim {
    ; // uninstantiable

    public static String right(final Object value, final int max) {
        if (value == null) {
            return "";
        }
        return right(value.toString(), max);
    }

    /**
     * @param value
     * @param max
     * @return
     */
    public static String right(final String value, final int max) {
        if (Strings.isNullOrEmpty(value)) {
            return value;
        }
        if (value.length() > max) {
            return value.substring(value.length() - max, value.length());
        }
        return value;
    }

    /**
     * @param firstMatch
     * @return a function which will remove the text before the first match of the given string
     */
    public static Function<String, String> trimBeforeFirst(final String firstMatch) {
        return new Function<String, String>() {
            @Override
            public String apply(final String string) {
                final int firstMatchIndex = string.indexOf(firstMatch);
                if (firstMatchIndex > 0) {
                    return string.substring(firstMatchIndex);
                }
                return string;
            }
        };
    }

    /**
     * @param lastMatch
     * @return a function which will remove the text after the last occurrence of the given string
     */
    public static Function<String, String> trimAfterLast(final String lastMatch) {
        return new Function<String, String>() {
            @Override
            public String apply(final String input) {
                final int index = input.lastIndexOf(lastMatch);
                if (index > 0) {
                    final int toIndex = index + lastMatch.length();
                    if (toIndex < input.length()) {
                        return input.substring(0, toIndex);
                    }
                }
                return input;
            }
        };
    }
}
