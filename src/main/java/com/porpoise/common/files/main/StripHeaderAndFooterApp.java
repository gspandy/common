package com.porpoise.common.files.main;

import java.io.IOException;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.porpoise.common.strings.Trim;

/**
 * Application to strip header and footer text
 */
public class StripHeaderAndFooterApp {
	static class StripHeaderAndFooter extends AbstractTextReplace {

		@Override
		protected Function<String, String> getTextReplaceFunction() {
			final Function<String, String> replaceHeader = Trim.trimBeforeFirst("package");
			final Function<String, String> replaceFooter = Trim.trimAfterLast("}");
			final Function<String, String> replace = Functions.compose(replaceHeader, replaceFooter);
			return replace;
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Configuration config = Configuration.valueOf(args);
		final StripHeaderAndFooter replace = new StripHeaderAndFooter();
		replace.replace(config.getDirectory(), config.getSuffix());
	}
}