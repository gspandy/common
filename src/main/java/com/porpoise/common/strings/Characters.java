package com.porpoise.common.strings;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public enum Characters {
    ; // uninstantiable
    private static final Collection<Character> UPPER_AND_LOWER_CASE;

    static {
        UPPER_AND_LOWER_CASE = ImmutableList.<Character> builder().addAll(range('A', 'Z')).addAll(range('a', 'z'))
                .build();
    }

    public static Collection<Character> upperAndLowerCaseLetters() {
        return UPPER_AND_LOWER_CASE;
    }

    public static Collection<Character> range(final char from, final char to) {
        final Collection<Character> characters = Lists.newArrayList();
        char f = from;
        while (f != to) {
            characters.add(Character.valueOf(f++));
        }
        characters.add(Character.valueOf(to));
        return characters;
    }

}
