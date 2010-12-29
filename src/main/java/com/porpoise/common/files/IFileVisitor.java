package com.porpoise.common.files;

import java.io.File;

public interface IFileVisitor {
    boolean onDirectory(File directory);

    void onFile(File file);

}