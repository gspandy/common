package com.porpoise.common.collect;

import static com.google.common.base.CharMatcher.DIGIT;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Utility/Factory class for {@link Comparator}s/ordering.
 */
public enum Comparators {
	;// uninstantiable

	/**
	 * A string comparator which takes into consideration numerical portions.
	 * 
	 * So, instead a lexicographical ordering of:
	 * 
	 * string1 string10 string2 string3
	 * 
	 * it does the 'right' thing and orders numerical portions separately
	 */
	static final class StringComparator implements Comparator<String>, Serializable {
		/** required by Serializable */
		private static final long	serialVersionUID		= 1L;

		private static final int	SECOND_STRING_LONGER	= -1;

		private static final int	FIRST_STRING_LONGER		= 1;

		@Override
		public int compare(final String arg0, final String arg1) {
			//
			// first check the initial comparison. If the result is the same
			// when the digits are removed, then we can just return that result
			// and save ourselves some work.
			//
			final String withoutDigits0 = DIGIT.removeFrom(arg0);
			final String withoutDigits1 = DIGIT.removeFrom(arg1);
			final int result = arg0.compareTo(arg1);
			final int resultWithDigitsRemoved = withoutDigits0.compareTo(withoutDigits1);
			if (sameByComparison(result, resultWithDigitsRemoved)) {
				return result;
			}

			// there is a disparity - we have some work to do
			return compareWithDigits(arg0, arg1);
		}

		/**
		 * With comparators, the only result that counts is if the result is positive, zero or negative. So, for
		 * example, if we have two different numbers which are both positive, then that's considered the same
		 * 
		 * @param resultOne
		 * @param resultTwo
		 * @return true if both numbers are positive, both are negative, or both are zero
		 */
		private static boolean sameByComparison(final int resultOne, final int resultTwo) {
			// easy first option - identical compare
			if (resultOne == resultTwo) {
				return true;
			}

			// nope - check if both are 'effectively' the same:
			if (resultOne == 0 && resultTwo == 0) {
				return true;
			}
			if (resultOne < 0 && resultTwo < 0) {
				return true;
			}
			if (resultOne > 0 && resultTwo > 0) {
				return true;
			}
			return false;
		}

		/**
		 * compare the two strings, using a numerical comparison for numerical sections
		 * 
		 * @param arg0
		 * @param arg1
		 * @return the comparison result
		 */
		private static int compareWithDigits(final String arg0, final String arg1) {
			// split the strings into ([string][number])* groupings. If there are no numbers present,
			// then each string may only contain a single entry
			final Iterator<String> iterOne = split(arg0);
			final Iterator<String> iterTwo = split(arg1);

			/*
			 * iterate over each "section" -- a section being either a regular string OR a string of numbers which can
			 * be treated as an integer
			 */
			while (iterOne.hasNext()) {
				if (!iterTwo.hasNext()) {
					return FIRST_STRING_LONGER;
				}

				final String sectionOne = iterOne.next();
				final String sectionTwo = iterTwo.next();

				final boolean bothAreNumbers = DIGIT.matchesAllOf(sectionOne) && DIGIT.matchesAllOf(sectionTwo);
				if (bothAreNumbers) {
					final int r = Integer.valueOf(sectionOne).compareTo(Integer.valueOf(sectionTwo));
					if (r != 0) {
						return r;
					}
				} else {
					// compare both parts as strings
					final int r = sectionOne.compareTo(sectionTwo);
					if (r != 0) {
						return r;
					}
				}
			}

			// the second strings still has some sections
			if (iterTwo.hasNext()) {
				return SECOND_STRING_LONGER;
			}

			return 0;
		}

		/**
		 * slit the string into its string/numerical sections
		 * 
		 * @param string
		 *            the string to split. Nulls will be considered empty strings
		 * @return an iterator of substring sections
		 */
		private static Iterator<String> split(final String string) {
			final String nullSafeString = Strings.nullToEmpty(string);
			final Iterable<String> splitByNumericalSections = splitNumbers(nullSafeString);
			return splitByNumericalSections.iterator();
		}

	}

	/**
	 * split the given string into string and number sequences
	 * 
	 * e.g. for the string "ab-cd 123 efg4" it would return an iterable containing:
	 * 
	 * <ol>
	 * <li>ab-cd</li>
	 * <li>123</li>
	 * <li>efg</li>
	 * <li>4</li>
	 * </ol>
	 * 
	 * If the string contains no numbers, then a single element containing the string is returned
	 * 
	 * @param string
	 * @return an iterable of strings
	 */
	static Iterable<String> splitNumbers(final String string) {
		final List<String> stringsAndNumbers = Lists.newArrayList();

		int digitIndex = DIGIT.indexIn(string);
		if (digitIndex == -1) {
			// no digits - return the whole string
			stringsAndNumbers.add(string);
			return stringsAndNumbers;
		}

		//
		// the iteration below works on [number][string] pairs, so if our string starts with a
		// string section, then we need to consume that before entering the loop
		//
		final boolean startsWithString = digitIndex > 0;
		if (startsWithString) {
			// consume the first string
			stringsAndNumbers.add(string.substring(0, digitIndex));
		}

		//
		// iterate over the rest of the string, adding each number string and non-numeric string
		// in turn.
		//
		while (digitIndex >= 0 && digitIndex < string.length()) {
			// consume a number
			final int endOfDigitIndex = nextIndexSafe(string, DIGIT.negate(), digitIndex);
			final String numberPart = string.substring(digitIndex, endOfDigitIndex);
			assert !numberPart.isEmpty() : String
			        .format("Somehow a numerical string section was empty in '%s'", string);
			stringsAndNumbers.add(numberPart);

			// check there is more to process
			if (endOfDigitIndex >= string.length()) {
				break;
			}

			// consume a string (non-numeric) section
			digitIndex = nextIndexSafe(string, DIGIT, endOfDigitIndex);
			final String stringPart = string.substring(endOfDigitIndex, digitIndex);
			assert !stringPart.isEmpty() : String.format("Somehow a non-numerical string section was empty in '%s'",
			        string);
			stringsAndNumbers.add(stringPart);
		}

		return stringsAndNumbers;
	}

	/**
	 * static (shared) string comparator instance, as the string comparator is immutable
	 */
	private static StringComparator	STRING_COMPARATOR	= new StringComparator();

	/**
	 * convenience method for determining the next "safe" index of the matcher's occurence in the string.
	 * 
	 * If there is no next occurrence (i.e. indexIn returns -1), then we return the last index in the string
	 * 
	 * @param substring
	 * @param matcher
	 * @param lastIndex
	 * @return
	 */
	private static int nextIndexSafe(final String substring, final CharMatcher matcher, final int lastIndex) {
		int nextIndex = matcher.indexIn(substring, lastIndex);
		if (nextIndex < 0) {
			nextIndex = substring.length();
		}
		return nextIndex;
	}

	/**
	 * Create a string comparator which supports strings with numbers.
	 * 
	 * For example, the default comparator would produce:
	 * 
	 * <ol>
	 * <li>string1</li>
	 * <li>string10</li>
	 * <li>string11</li>
	 * <li>string2</li>
	 * <li>string20</li>
	 * <li>string3</li>
	 * <li>...</li>
	 * <li>string3</li>
	 * </ol>
	 * 
	 * @return a number-aware comparator
	 */
	public static Comparator<String> numberAwareStringComparator() {
		return STRING_COMPARATOR;
	}

}