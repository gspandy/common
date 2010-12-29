package com.porpoise.common.files.main;

import java.io.IOException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.porpoise.common.files.FileFunctions;

public class ReplaceTextApp {

    public static void main(final String[] args) throws IOException {
        final Configuration config = Configuration.valueOf(args);

        if (config == null) {
            return;
        }
        final StripHeaderAndFooter replace = new StripHeaderAndFooter() {
            @Override
            protected Function<String, String> getTextReplaceFunction() {
                return FileFunctions.replace("com.technophobia", "com.porpoise");
            }

            @Override
            protected Predicate<String> getFileNamePredicate() {
                // don't replace text in this source file
                final Predicate<String> predicate = super.getFileNamePredicate();
                final String fileName = ReplaceTextApp.class.getSimpleName() + ".java";
                final Predicate<String> notThisFile = Predicates.not(FileFunctions.contains(fileName));
                return Predicates.and(predicate, notThisFile);
            }
        };
        replace.replace(config.getDirectory(), config.getSuffix());
    }
}