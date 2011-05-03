package com.porpoise.common.files;

import java.io.File;

/**
 * File visitor
 */
public interface IFileVisitor {

	/**
	 * @param directory
	 *            the directory
	 * @return true if the visiting function should continue
	 */
	boolean onDirectory(File directory);

	/**
	 * @param file
	 *            a file
	 */
	void onFile(File file);

}