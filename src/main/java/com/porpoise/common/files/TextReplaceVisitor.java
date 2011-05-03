package com.porpoise.common.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.porpoise.common.log.Log;

/**
 * visitor which replaces text content in files
 */
public class TextReplaceVisitor extends FileVistiorAdapter {
	private final Charset	               charset;

	private final Function<File, File>	   targetFileFunction;

	private final Function<String, String>	contentsTransform;

	/**
	 * @param transform
	 *            the text transform
	 */
	public TextReplaceVisitor(final Function<String, String> transform) {
		this(Charsets.UTF_8, FileFunctions.fileIdentity(), transform);
	}

	/**
	 * @param textTransform
	 *            the content transform
	 * @param fileTransform
	 *            a file transform for the new files (i.e. original.txt will be written to original_2.txt)
	 */
	public TextReplaceVisitor(final Function<String, String> textTransform, final Function<File, File> fileTransform) {
		this(Charsets.UTF_8, fileTransform, textTransform);
	}

	/**
	 * @param encoding
	 *            the file encoding
	 * @param newFileFunction
	 *            a file transform for the new files (i.e. original.txt will be written to original_2.txt)
	 * @param textTransform
	 *            the content transform
	 */
	public TextReplaceVisitor(final Charset encoding, final Function<File, File> newFileFunction,
	        final Function<String, String> textTransform) {
		this.charset = Preconditions.checkNotNull(encoding);
		this.targetFileFunction = Preconditions.checkNotNull(newFileFunction);
		this.contentsTransform = Preconditions.checkNotNull(textTransform);
	}

	@Override
	public void onFile(final File file) {
		try {
			final String contents = Files.toString(file, this.charset);
			final File to = this.targetFileFunction.apply(file);
			final CharSequence replaced = this.contentsTransform.apply(contents);
			if (contents.equals(replaced)) {
				Log.debug("skipping file '%s' as the function hasn't changed owt", file.getAbsolutePath());
			} else {
				Log.info("writing '%s' from '%s'", file.getAbsolutePath(), to.getAbsolutePath());
				Files.write(replaced, to, this.charset);
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}