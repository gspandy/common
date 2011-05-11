package com.porpoise.common.strings;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A String iterator
 */
public class StringIterator implements Iterator<Character> {

    private final String value;
    private int index = 0;
    private final int length;

    /**
     * @param value
     *            the string over which the characters will be iterated
     */
    public StringIterator(final String value) {
        this.value = value;
        this.length = value.length();
    }

    @Override
    public boolean hasNext() {
        return this.index < this.length;
    }

    @Override
    public Character next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return Character.valueOf(this.value.charAt(this.index++));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
