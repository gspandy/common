package com.porpoise.common.files;

import java.io.File;

import com.porpoise.common.log.Log;

/**
 * utility to parse the file tree (if you're not using Java7)
 */
public enum FileIterator {
    ;// uninstantiable

    /**
     * perform a depth-first parse of the given directory
     * 
     * @param directory
     * @param visitor
     */
    public static void depthFirst(final File directory, final IFileVisitor visitor) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }

        final boolean visitDir = visitor.onDirectory(directory);

        if (visitDir) {
            for (final File file : directory.listFiles()) {
                if (file.isFile()) {
                    visitor.onFile(file);
                } else if (file.isDirectory()) {
                    depthFirst(file, visitor);
                } else {
                    Log.debug("Skipping '%s' as it is not a file or directory", file);
                }
            }
        } else {
            Log.debug("Skipping directory '%s'", directory);
        }
    }

}