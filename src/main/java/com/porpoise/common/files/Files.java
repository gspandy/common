package com.porpoise.common.files;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 */
public enum Files {
    ;// uninstantiable

    public static Iterable<File> listFiles(final Iterable<File> leaves) {
        final List<File> files = Lists.newArrayList();
        for (final File dir : leaves) {
            for (final File f : Arrays.asList(dir.listFiles())) {
                if (f.isFile()) {
                    files.add(f);
                }
            }
        }
        return files;
    }

    public static String toString(final Iterable<File> leaves) {
        return Joiner.on(String.format(",%n")).join(leaves);
    }

    public static Collection<File> leafDirs(final File parent) {
        final List<File> dirs = Lists.newArrayList();
        if (parent != null && parent.isDirectory()) {
            final List<File> childDirs = Lists.newArrayList();
            for (final File f : Arrays.asList(parent.listFiles())) {
                childDirs.addAll(leafDirs(f));
            }
            if (childDirs.isEmpty()) {
                childDirs.add(parent);
            }
            dirs.addAll(childDirs);
        }
        return dirs;
    }
}
