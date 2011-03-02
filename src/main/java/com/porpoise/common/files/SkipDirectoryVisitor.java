package com.porpoise.common.files;

import java.io.File;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.porpoise.common.Log;

/**
 * @author Aaron
 */
public class SkipDirectoryVisitor extends FileVistiorAdapter {
    private final IFileVisitor delegate;

    final Collection<String>   blackList;

    public SkipDirectoryVisitor(final IFileVisitor visitor, final String... ignore) {
        blackList = Lists.newArrayList();
        for (final String dir : ignore) {
            blackList.add(dir.trim().toLowerCase());
        }
        delegate = Preconditions.checkNotNull(visitor);
    }

    @Override
    public void onFile(final File file) {
        delegate.onFile(file);
    }

    @Override
    public boolean onDirectory(final File directory) {
        if (blackList.contains(directory.getName().toLowerCase())) {
            Log.debug("skipping matching (blacklisted) directory %s", directory.getPath());
            return false;
        }
        return delegate.onDirectory(directory);
    }

}