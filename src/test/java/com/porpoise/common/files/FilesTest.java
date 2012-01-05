package com.porpoise.common.files;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Tests for {@link Files}
 */
public class FilesTest {

	/**
	 * Test for {@link Files#leafDirs(File)}
	 */
	@Test
	public void testLeafDirs() {
		// call the method under test
		final Collection<File> leaves = Files.leafDirs(new File(".",
				"src/main/java"));
		final Set<String> expected = Sets.newHashSet("tree", "concurrent",
				"core", "date", "main", "functions", "log", "metadata", "xml",
				"swing", "strings", "annotation");
		for (final File leave : leaves) {
			Assert.assertTrue(leave.isDirectory());
			Assert.assertTrue("Unexpected leaf " + leave.getName(),
					expected.remove(leave.getName()));
		}
		Assert.assertTrue(expected.isEmpty());
	}
}
