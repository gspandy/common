package com.porpoise.common.files.main;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;

class Configuration {
	private final File	 directory;

	private final String	suffix;

	public Configuration(final File dir, final String replaceSuffix) {
		this.directory = Preconditions.checkNotNull(dir);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid directory " + dir);
		}
		this.suffix = replaceSuffix;
	}

	/**
	 * @return the directory
	 */
	public File getDirectory() {
		return this.directory;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * naughty parsing method which spits stuff to the UI and asks for user input
	 * 
	 * @param args
	 * @return null if the args were invalid, a configuration otherwise
	 * @throws IOException
	 */
	public static Configuration valueOf(final String[] args) throws IOException {
		String suffix = null;
		final String directoryString;
		if (args.length == 1) {
			directoryString = args[0];
		} else if (args.length == 2) {
			directoryString = args[0];
			suffix = args[1];
		} else {
			System.err.println("Usage: StripHeaderAndFooterApp <directory> [backup suffix]");
			return null;
		}

		if (suffix == null) {
			System.out.println("Replacing in directory '%s' with no backup - continue? [y|n]");
			final int input = System.in.read();
			if ('y' != input && 'Y' != input) {
				return null;
			}
		}

		final File directory = new File(directoryString);
		if (!directory.exists()) {
			System.err.println("Invalid directory: " + directory);
			return null;
		}

		return new Configuration(directory, suffix);
	}

}