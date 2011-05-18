package com.porpoise.common.files;

import java.io.File;

/**
 */
public class FileVistiorAdapter implements FileVisitor {
    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.files.FileVisitor#onDirectory(java.io.File)
     */
    @Override
    public boolean onDirectory(final File directory) {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.files.FileVisitor#onFile(java.io.File)
     */
    @Override
    public void onFile(final File directory) {
        // TODO Auto-generated method stub

    }

}