package com.porpoise.common.strings;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Utility for working with Java {@link Character}s
 */
public enum Characters {
    ; // uninstantiable
    private static final Collection<Character> UPPER_AND_LOWER_CASE;

    static {
        UPPER_AND_LOWER_CASE = ImmutableList.<Character> builder().addAll(range('A', 'Z')).addAll(range('a', 'z'))
                .build();
    }

    /**
     * @return a collection of all upper and lower case ASCII characters
     */
    public static Collection<Character> upperAndLowerCaseLetters() {
        return UPPER_AND_LOWER_CASE;
    }

    /**
     * Create a range of characters between the two characters, inclusive.
     * 
     * If the 'from' character is actually after the 'to' character, then the result will be the same as calling <code>
     * range(to, from)
     * </code>
     * 
     * This decision was made as it seems to make this API easier for typical usage. When this behavior is not
     * desirable, it hopefully won't make a big difference to the caller to make the comparison check.
     * 
     * @param from
     *            the start character
     * @param to
     *            the end character
     * @return a collection of characters in the given range
     */
    public static Collection<Character> range(final char from, final char to) {
        if (from > to) {
            return range(to, from);
        }
        final Collection<Character> characters = Lists.newArrayList();
        char f = from;
        while (f != to) {
            characters.add(Character.valueOf(f++));
        }
        characters.add(Character.valueOf(to));
        return characters;
    }

}
