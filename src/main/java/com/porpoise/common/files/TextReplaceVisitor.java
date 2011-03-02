package com.porpoise.common.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.porpoise.common.Log;

/**
 * @author Aaron
 */
public class TextReplaceVisitor extends FileVistiorAdapter {
    private final Charset                  charset;

    private final Function<File, File>     targetFileFunction;

    private final Function<String, String> contentsTransform;

    public TextReplaceVisitor(final Function<String, String> transform) {
        this(Charsets.UTF_8, FileFunctions.fileIdentity(), transform);
    }

    public TextReplaceVisitor(final Function<String, String> textTransform, final Function<File, File> fileTransform) {
        this(Charsets.UTF_8, fileTransform, textTransform);
    }

    public TextReplaceVisitor(final Charset encoding, final Function<File, File> newFileFunction,
            final Function<String, String> textTransform) {
        charset = Preconditions.checkNotNull(encoding);
        targetFileFunction = Preconditions.checkNotNull(newFileFunction);
        contentsTransform = Preconditions.checkNotNull(textTransform);
    }

    @Override
    public void onFile(final File file) {
        try {
            final String contents = Files.toString(file, charset);
            final File to = targetFileFunction.apply(file);
            final CharSequence replaced = contentsTransform.apply(contents);
            if (contents.equals(replaced)) {
                Log.debug("skipping file '%s' as the function hasn't changed owt", file.getAbsolutePath());
            } else {
                Log.info("writing '%s' from '%s'", file.getAbsolutePath(), to.getAbsolutePath());
                Files.write(replaced, to, charset);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}