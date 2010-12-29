package com.porpoise.common.files.main;

import java.io.IOException;

public class StripHeaderAndFooterApp {

    public static void main(final String[] args) throws IOException {
        final Configuration config = Configuration.valueOf(args);
        final StripHeaderAndFooter replace = new StripHeaderAndFooter();
        replace.replace(config.getDirectory(), config.getSuffix());
    }
}