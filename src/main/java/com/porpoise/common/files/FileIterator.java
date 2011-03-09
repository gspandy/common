package com.porpoise.common.files;

import java.io.File;

import com.porpoise.common.log.Log;

public enum FileIterator {
    ;// uninstantiable

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