package com.porpoise.common.files;

import java.io.File;

/**
 * @author Aaron
 */
public class FileVistiorAdapter implements IFileVisitor {
	/**
	 * {@inheritDoc}
	 * 
	 * @see com.porpoise.common.files.IFileVisitor#onDirectory(java.io.File)
	 */
	@Override
	public boolean onDirectory(final File directory) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.porpoise.common.files.IFileVisitor#onFile(java.io.File)
	 */
	@Override
	public void onFile(final File directory) {
		// TODO Auto-generated method stub

	}

}