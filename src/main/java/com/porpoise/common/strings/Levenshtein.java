package com.porpoise.common.strings;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;

/**
 * A slow implementation of the Levenstein algorithm
 * 
 * see http://www.levenshtein.net/
 */
public enum Levenshtein {

    ;// uninstantiable

    /**
     * Utility class to aid in choosing the closest string from a group of input strings
     */
    public static final class DistanceMatcher {

        private final String input;

        public DistanceMatcher(final String string) {
            this.input = checkNotNull(string);
        }

        public String pickBestfrom(final String... values) {
            return pickBestfrom(Arrays.asList(values));
        }

        public String pickBestfrom(final Iterable<String> values) {
            return comparator().min(values);
        }

        public Ordering<String> comparator() {
            final Comparator<Integer> c = new Comparator<Integer>() {
                @Override
                public int compare(final Integer arg0, final Integer arg1) {
                    return arg0.compareTo(arg1);
                }
            };
            final Ordering<String> dist = Ordering.from(c).onResultOf(distanceFunction(this.input));
            return dist;
        }

    }

    @SuppressWarnings("boxing")
    private static class Matrix {

        Table<Integer, Integer, Integer> matrix;
        private final int width;
        private final int height;
        private final String one;
        private final String two;

        public Matrix(final String one, final String two) {
            this.one = one;
            this.two = two;
            this.width = one.length();
            this.height = two.length();
            this.matrix = HashBasedTable.create(this.width, this.height);

            for (int i = 0; i <= this.width; i++) {
                put(i, 0, i);
            }
            for (int j = 0; j <= this.height; j++) {
                put(0, j, j);
            }

            final int lenOne = this.width;
            final int lenTwo = this.height;
            for (int i = 1; i <= lenOne; i++) {
                final int firstIndex = i - 1;
                for (int j = 1; j <= lenTwo; j++) {
                    final int secondIndex = j - 1;
                    final char alpha = two.charAt(secondIndex);
                    final char beta = one.charAt(firstIndex);

                    final int cost = alpha == beta ? 0 : 1;
                    final int firstDistance = get(firstIndex, j) + 1;
                    final int secondDistance = get(i, secondIndex) + 1;
                    final int d1 = Math.min(firstDistance, secondDistance);
                    final int diagonalCost = get(firstIndex, secondIndex);
                    final int d2 = diagonalCost + cost;
                    put(i, j, Math.min(d1, d2));
                }
            }
        }

        public int get(final int row, final int col) {
            Integer value = this.matrix.get(row, col);
            if (value == null) {
                value = put(row, col, 0);
            }
            return value;
        }

        private int put(final int row, final int col, final int value) {
            this.matrix.put(row, col, value);
            return value;
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            final String divider = Strings.repeat("-", 4 * (this.width + 1));

            b.append(String.format("|%3s", " ")).append("|");
            for (int c = 0; c < this.width; c++) {
                b.append(String.format("%3s", this.one.charAt(c))).append("|");
            }
            b.append(String.format("%n"));
            for (int r = 0; r < this.height; r++) {
                b.append(divider);
                b.append(String.format("%n|"));
                b.append(String.format("%3s", this.two.charAt(r))).append("|");
                for (int c = 0; c < this.width; c++) {
                    b.append(String.format("%3s", get(r, c))).append("|");
                }
                b.append(String.format("%n"));
            }
            b.append(divider);
            return b.toString();
        }

        public int compute() {
            return get(this.width, this.height);
        }

    }

    /**
     * @param from
     *            the input string against which other string distances can be computed
     * @return a distance function for the given input
     */
    public static Function<String, Integer> distanceFunction(final String from) {
        return new Function<String, Integer>() {

            @Override
            public Integer apply(final String input) {
                return Integer.valueOf(distance(from, input));
            }
        };
    }

    /**
     * @param one
     * @param two
     * @return the two strings as a levenstein grid
     */
    public static String toString(final String one, final String two) {
        return new Matrix(one, two).toString();
    }

    public static int distance(final String one, final String two) {
        if (one.isEmpty()) {
            return two.length();
        }
        if (two.isEmpty()) {
            return one.length();
        }

        return new Matrix(one, two).compute();
    }

    public static DistanceMatcher match(final String string) {
        return new DistanceMatcher(string);
    }
}
