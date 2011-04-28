package com.porpoise.common.files;

import java.io.File;
import java.util.Collection;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.porpoise.common.log.Log;

/**
 * visitor which can skip certain directories
 */
public class SkipDirectoryVisitor extends FileVistiorAdapter {
    private final IFileVisitor delegate;

    final Collection<String>   blackList;

    /**
     * @param visitor
     * @param ignore
     */
    public SkipDirectoryVisitor(final IFileVisitor visitor, final String... ignore) {
        this.blackList = Lists.newArrayList();
        for (final String dir : ignore) {
            this.blackList.add(dir.trim().toLowerCase());
        }
        this.delegate = Preconditions.checkNotNull(visitor);
    }

    @Override
    public void onFile(final File file) {
        this.delegate.onFile(file);
    }

    @Override
    public boolean onDirectory(final File directory) {
        if (this.blackList.contains(directory.getName().toLowerCase())) {
            Log.debug("skipping matching (blacklisted) directory %s", directory.getPath());
            return false;
        }
        return this.delegate.onDirectory(directory);
    }

}