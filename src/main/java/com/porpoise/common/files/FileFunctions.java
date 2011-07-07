package com.porpoise.common.files;

import java.io.File;
import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * A collection of file utilities
 */
public enum FileFunctions {
    ;//

    /**
     * Predicate which returns true if the file is a file (not a directory, etc)
     */
    public static final Predicate<File> IS_FILE;

    /**
     * Function which will return the files in a directory
     */
    public static final Function<File, Iterable<File>> LIST_FILES;

    static {
        LIST_FILES = new Function<File, Iterable<File>>() {
            @Override
            public Iterable<File> apply(final File dir) {
                return Iterables.filter(Arrays.asList(dir.listFiles()), FileFunctions.IS_FILE);
            }
        };

        IS_FILE = new Predicate<File>() {
            @Override
            public boolean apply(final File input) {
                return input != null && input.isFile();
            }
        };
    }

    /**
     * @return a function which will return the same file input
     */
    public static Function<File, File> fileIdentity() {
        return Functions.identity();
    }

    /**
     * @param suffix
     * @return a function which will return a new file with the same name and path as the input file, but with the given
     *         suffix appended
     */
    public static Function<File, File> withSuffix(final String suffix) {
        return new Function<File, File>() {
            @Override
            public File apply(final File input) {
                return new File(input.getParentFile(), input.getName() + suffix);
            }
        };
    }

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