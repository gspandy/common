package com.porpoise.common.files.main;

import java.io.File;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.porpoise.common.files.FileFunctions;
import com.porpoise.common.files.FileIterator;
import com.porpoise.common.files.FileMatcherVisitor;
import com.porpoise.common.files.FileVisitor;
import com.porpoise.common.files.SkipDirectoryVisitor;
import com.porpoise.common.files.TextReplaceVisitor;

/**
 * replace text in all java files, stripping all text which comes before 'package' and after the last '}'
 */
abstract class AbstractTextReplace {

    public void replace(final File directory, final String suffix) {
        final Function<String, String> replace = getTextReplaceFunction();
        final Function<File, File> newFileFunction = getFileFunctionForSuffix(suffix);
        final FileVisitor skipDirectoryVisitor = createJavaVisitor(replace, newFileFunction);

        FileIterator.depthFirst(directory, skipDirectoryVisitor);

    }

    protected abstract Function<String, String> getTextReplaceFunction();

    protected static Function<File, File> getFileFunctionForSuffix(final String suffix) {
        Function<File, File> newFileFunction;
        if (suffix == null) {
            newFileFunction = FileFunctions.fileIdentity();
        } else {
            newFileFunction = FileFunctions.withSuffix(suffix);
        }
        return newFileFunction;
    }

    protected FileVisitor createJavaVisitor(final Function<String, String> replace,
            final Function<File, File> newFileFunction) {
        final FileVisitor renameVisitor = createTextReplaceVisitor(replace, newFileFunction);
        final Predicate<String> nameMatcher = getFileNamePredicate();
        final FileVisitor fileMatcherVisitor = new FileMatcherVisitor(nameMatcher, renameVisitor);
        final SkipDirectoryVisitor skipDirectoryVisitor = new SkipDirectoryVisitor(fileMatcherVisitor, ".git", "build",
                "target", "dist", "target-platform", "org.junit");
        return skipDirectoryVisitor;
    }

    private static FileVisitor createTextReplaceVisitor(final Function<String, String> replace,
            final Function<File, File> newFileFunction) {
        final FileVisitor renameVisitor = new TextReplaceVisitor(replace, newFileFunction);
        return renameVisitor;
    }

    protected Predicate<String> getFileNamePredicate() {
        final Predicate<String> isSourceFile;
        {
            final Predicate<String> isJavaFile = FileFunctions.endsWith(".java");
            final Predicate<String> isScalaFile = FileFunctions.endsWith(".scala");
            isSourceFile = Predicates.or(isScalaFile, isJavaFile);
        }

        final Predicate<String> notPackageInfo = Predicates.not(FileFunctions.contains("package-info"));

        final Predicate<String> nameMatcher = Predicates.and(isSourceFile, notPackageInfo);
        return nameMatcher;
    }
}