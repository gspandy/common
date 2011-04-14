package com.porpoise.common.strings;

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

    public static String right(final String value, final int max) {
        if (Strings.isNullOrEmpty(value)) {
            return value;
        }
        if (value.length() > max) {
            return value.substring(value.length() - max, value.length());
        }
        return value;
    }

}
