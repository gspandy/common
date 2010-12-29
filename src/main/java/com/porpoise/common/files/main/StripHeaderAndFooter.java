package com.porpoise.common.files.main;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.porpoise.common.files.FileFunctions;

/**
 * replace text in all java files, stripping all text which comes before 'package' and after the last '}'
 */
public class StripHeaderAndFooter extends AbstractTextReplace {

    @Override
    protected Function<String, String> getTextReplaceFunction() {
        final Function<String, String> replaceHeader = FileFunctions.trimBeforeFirst("package");
        final Function<String, String> replaceFooter = FileFunctions.trimAfterLast("}");
        final Function<String, String> replace = Functions.compose(replaceHeader, replaceFooter);
        return replace;
    }
}