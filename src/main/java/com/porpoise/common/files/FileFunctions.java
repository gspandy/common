package com.porpoise.common.files;

import java.io.File;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

public enum FileFunctions {
    ;//

    public static Predicate<String> endsWith(final String suffix) {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String arg0) {
                return arg0.endsWith(suffix);
            }
        };
    }

    public static Predicate<String> contains(final String string) {
        return new Predicate<String>() {
            @Override
            public boolean apply(final String arg0) {
                return arg0.contains(string);
            }
        };
    }

    public static Function<File, File> fileIdentity() {
        return Functions.identity();
    }

    public static Function<File, File> withSuffix(final String suffix) {
        return new Function<File, File>() {
            @Override
            public File apply(final File arg0) {
                return new File(arg0.getParentFile(), arg0.getName() + suffix);
            }
        };
    }

    public static Function<String, String> textIdentity() {
        return Functions.identity();
    }

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

    public static Function<String, String> trimAfterLast(final String lastMatch) {
        return new Function<String, String>() {
            @Override
            public String apply(final String string) {
                final int index = string.lastIndexOf(lastMatch);
                if (index > 0) {
                    final int toIndex = index + lastMatch.length();
                    if (toIndex < string.length()) {
                        return string.substring(0, toIndex);
                    }
                }
                return string;
            }
        };
    }

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