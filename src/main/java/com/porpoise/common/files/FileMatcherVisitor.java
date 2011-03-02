package com.porpoise.common.files;

import java.io.File;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.porpoise.common.Log;

/**
 * @author Aaron
 */
public class FileMatcherVisitor extends FileVistiorAdapter {

    private final IFileVisitor      delegate;

    private final Predicate<String> filter;

    public FileMatcherVisitor(final Predicate<String> nameMatcher, final IFileVisitor visitor) {
        filter = Preconditions.checkNotNull(nameMatcher);
        delegate = Preconditions.checkNotNull(visitor);
    }

    @Override
    public boolean onDirectory(final File directory) {
        return delegate.onDirectory(directory);
    }

    @Override
    public void onFile(final File file) {
        if (filter.apply(file.getName())) {
            Log.debug("visiting matching file %s", file.getPath());
            delegate.onFile(file);
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