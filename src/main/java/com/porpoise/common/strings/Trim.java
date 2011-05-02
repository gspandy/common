package com.porpoise.common.strings;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
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

    /**
     * trims the middle characters
     * 
     * @param text
     * @param maxLength
     * @param middleText
     * @return a trimmed string whos max length is the given length. The middle characters will be replaced
     */
    public static String middle(final String text, final int maxLength, final String middleText) {
        Preconditions.checkNotNull("middleText cannot be null", middleText);
        if (Strings.isNullOrEmpty(text)) {
            return text;
        }
        final int origLen = text.length();
        if (origLen > maxLength) {
            final int midLen = middleText.length();
            final int charsToTrim = origLen + midLen - maxLength;
            final int frontCharsToTrim = charsToTrim / 2;
            final int halfLen = origLen / 2;
            final int frontIndex = halfLen - frontCharsToTrim;
            final int endIndex;
            {
                final int endCharsToTrim = charsToTrim - frontCharsToTrim;
                endIndex = halfLen + endCharsToTrim;
            }
            final String front = text.substring(0, frontIndex);
            final String end = text.substring(endIndex, origLen);
            final String result = String.format("%s%s%s", front, middleText, end);
            assert result.length() == maxLength;
            return result;
        }
        return text;
    }

}
