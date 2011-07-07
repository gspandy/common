package com.porpoise.common.files.main;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.io.Files;

/**
 * 
 */
public class AbstractTextReplaceTest {

    private AbstractTextReplace replacer;
    private File directory;
    private File matchingFile;
    private File notReplacedInFile;
    private String originalText;

    /**
     * Create some test files
     * 
     * @throws IOException
     */
    @Before
    public void setup() throws IOException {
        this.replacer = new AbstractTextReplace() {
            @Override
            protected Function<String, String> getTextReplaceFunction() {
                return new Function<String, String>() {
                    /**
                     * @see com.google.common.base.Function#apply(java.lang.Object)
                     */
                    @Override
                    public String apply(final String input) {
                        return input.replace("dog", "cat");
                    }
                };
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.porpoise.common.files.main.AbstractTextReplace#getFileNamePredicate()
             */
            @Override
            protected Predicate<String> getFileNamePredicate() {
                return new Predicate<String>() {
                    @Override
                    public boolean apply(final String input) {
                        return input.endsWith(".txt");
                    }

                };
            }
        };

        this.directory = Files.createTempDir();
        this.directory.deleteOnExit();

        final File dirOne = new File(this.directory, "one");
        Assert.assertTrue(dirOne.mkdirs());

        this.matchingFile = new File(dirOne, "cat.txt");
        this.notReplacedInFile = new File(dirOne, "not-replaced.cat");
        this.originalText = String.format("replace%ndog%nwith%ncat%n");
        Files.write(this.originalText, this.matchingFile, Charsets.UTF_8);
        Files.write(this.originalText, this.notReplacedInFile, Charsets.UTF_8);

        System.out.println(this.directory);
    }

    /**
     * @throws IOException
     */
    @After
    public void tearDown() throws IOException {
        Files.deleteDirectoryContents(this.directory);
    }

    /**
     * test that the text was replaced in the given file
     * 
     * @throws IOException
     */
    @Test
    public void testReplace() throws IOException {
        final String unchangedContents = Files.toString(this.notReplacedInFile, Charsets.UTF_8);

        assertOriginalContentsIn(this.matchingFile);

        // call the method under test
        this.replacer.replace(this.directory, ".replaced");

        // the original file's contents should not have been changed
        assertOriginalContentsIn(this.matchingFile);

        // a new file should now exist with the original file name suffixed with ".replaced"
        final File newFile = new File(this.matchingFile.getParentFile(), this.matchingFile.getName() + ".replaced");
        Assert.assertTrue(newFile.exists());
        final String contents = Files.toString(newFile, Charsets.UTF_8);
        final String expected = this.originalText.replace("dog", "cat");
        Assert.assertEquals("the file should have new contents", expected, contents);

        // our non-matching file should NOT have changed
        Assert.assertEquals("The file with the non-matching extension should NOT have been changed", unchangedContents,
                Files.toString(this.notReplacedInFile, Charsets.UTF_8));
    }

    /**
     * @param file
     * @throws IOException
     */
    private void assertOriginalContentsIn(final File file) throws IOException {
        final String originalContents = Files.toString(file, Charsets.UTF_8);
        Assert.assertEquals("precondition failed: original contents aren't as expected", this.originalText,
                originalContents);
    }

}
