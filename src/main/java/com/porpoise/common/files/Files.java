package com.porpoise.common.files;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.porpoise.common.collect.Sequences;

/**
 * File utilities
 */
public enum Files {
    ;// uninstantiable

    /**
     * 
     * @param leaves
     * @return the files for the given directories
     */
    public static Iterable<File> listFiles(final Iterable<File> leaves) {
        return Sequences.flatMap(leaves, FileFunctions.LIST_FILES);
    }

    /**
     * the leaf directories for the given parent
     * 
     * @param dir
     * @return the leaf directories
     */
    public static Collection<File> leafDirs(final File dir) {
        final List<File> dirs = Lists.newArrayList();
        if (dir != null && dir.isDirectory()) {
            final List<File> childDirs = Lists.newArrayList();
            for (final File f : dir.listFiles()) {
                childDirs.addAll(leafDirs(f));
            }
            if (childDirs.isEmpty()) {
                childDirs.add(dir);
            }
            dirs.addAll(childDirs);
        }
        return dirs;
    }
}
