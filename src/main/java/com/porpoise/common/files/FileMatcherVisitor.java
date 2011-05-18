package com.porpoise.common.files;

import java.io.File;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.porpoise.common.log.Log;

/**
 * 
 */
public class FileMatcherVisitor extends FileVistiorAdapter {

    private final FileVisitor delegate;

    private final Predicate<String> filter;

    /**
     * @param nameMatcher
     *            the file name matcher
     * @param visitor
     *            the visitor
     */
    public FileMatcherVisitor(final Predicate<String> nameMatcher, final FileVisitor visitor) {
        this.filter = Preconditions.checkNotNull(nameMatcher);
        this.delegate = Preconditions.checkNotNull(visitor);
    }

    @Override
    public boolean onDirectory(final File directory) {
        return this.delegate.onDirectory(directory);
    }

    @Override
    public void onFile(final File file) {
        if (this.filter.apply(file.getName())) {
            Log.debug("visiting matching file %s", file.getPath());
            this.delegate.onFile(file);
        } else {
            Log.debug("skipping file %s", file.getPath());
        }
    }

    final static Pattern createSuffixPattern(final String suffix) {
        if (suffix.startsWith(".")) {
            return Pattern.compile(".*\\" + suffix);
        }
        return Pattern.compile(".*" + suffix);
    }

}