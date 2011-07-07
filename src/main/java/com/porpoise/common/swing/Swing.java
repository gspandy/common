package com.porpoise.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.google.common.base.Strings;

/**
 * Yet another clump of swing utilities
 */
public enum Swing {
    ;// uninstantiable

    enum OpenMode {
        DIRECTORIES_ONLY(JFileChooser.DIRECTORIES_ONLY), FILES_AND_DIRECTORIES(JFileChooser.FILES_AND_DIRECTORIES), FILES_ONLY(
                JFileChooser.FILES_ONLY);

        private final int value;

        OpenMode(final int mode) {
            this.value = mode;

        }

        public int getMode() {
            return this.value;
        }
    }

    /**
     * @return the user preferences
     */
    public static Preferences prefs() {
        return Preferences.userNodeForPackage(Swing.class);
    }

    /**
     * setup a given component within a {@link JFrame}. A main method might call this method to display the main
     * application pane.
     * 
     * @param title
     * @param component
     * @return the host {@link JFrame}
     */
    public static JFrame show(final String title, final Component component) {
        final JFrame host = new JFrame(title);
        host.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        host.getContentPane().add(component, BorderLayout.CENTER);
        host.pack();
        host.setVisible(true);
        return host;
    }

    /**
     * Set the system default theme
     */
    public static void setDefaultTheme() {
        setTheme("Nimbus");
    }

    /**
     * @param name
     *            the theme name
     */
    public static void setTheme(final String name) {
        for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (name.equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    /**
     * Given a an owning component, show a find file dialog to return a file which will be set in the given target test
     * field, and the file location will be saved in the user preferences
     * 
     * @param owner
     *            the owning component of the open file dialog
     * @param target
     *            the text field to contain the file path
     * @param chooseFile
     *            the file open mode
     */
    public static void find(final Component owner, final JTextField target, final OpenMode chooseFile) {
        final File chosen = openFinder(owner, target.getText(), chooseFile);
        if (chosen != null) {
            final String path = chosen.getAbsolutePath();
            target.setText(path);
            savePrefs(target, path);
        }
    }

    /**
     * display the find file dialog
     * 
     * @param owner
     *            the owning container
     * @param text
     *            the file path to choose
     * @param mode
     *            the open mode
     * @return the chosen file or null if the user cancelled
     */
    public static File openFinder(final Component owner, final String text, final OpenMode mode) {
        final JFileChooser chooser = new JFileChooser();
        if (!Strings.isNullOrEmpty(text)) {
            final File file = new File(text);
            if (file.exists()) {
                chooser.setSelectedFile(file);
            }
        }
        chooser.setFileSelectionMode(mode.getMode());

        final int returnVal = chooser.showOpenDialog(owner);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * @param evt
     *            a key event
     * @return true if the key event represents an enter key
     */
    public static boolean isEnter(final KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_ENTER;
    }

    /**
     * @param evt
     *            a key event
     * @return true if the key event represents a delete key
     */
    public static boolean isDelete(final KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    /**
     * save the contents of the text field in the user preferences
     * 
     * @param target
     *            the text field for which the values should be saved
     */
    public static void savePrefs(final JTextField target) {
        savePrefs(target, target.getText());
    }

    /**
     * save the contents of the text field in the user preferences
     * 
     * @param target
     *            the text field to save
     * @param path
     *            preference key
     */
    public static void savePrefs(final JTextField target, final String path) {
        final String key = prefKeyForField(target);
        savePrefs(key, path);
    }

    /**
     * @param target
     *            the text field for which a unique key should be returned
     * @return a unique key for the given text field
     */
    @SuppressWarnings("boxing")
    public static String prefKeyForField(final JTextField target) {
        String name = target.getName();
        if (name == null) {
            final Point point = target.getLocation();
            name = String.format("FieldAt%sX%s", point.x, point.y);
        }
        return name + ".text";
    }

    /**
     * @param key
     *            the preference key
     * @param value
     *            the value to store
     */
    public static void savePrefs(final String key, final String value) {
        final Preferences prefs = prefs();

        if (Strings.isNullOrEmpty(key)) {
            throw new IllegalStateException("Can't save preferences without a key!");
        }
        prefs.put(key, value);
        try {
            prefs.flush();
        } catch (final BackingStoreException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param field
     *            the text field to initialise based on saved user preferences (or to the default value if none were
     *            saved)
     * @param defaultValue
     *            a default value
     * @return a test field
     */
    public static JTextField initFromPrefs(final JTextField field, final String defaultValue) {
        final String key = prefKeyForField(field);
        final String value = prefs().get(key, defaultValue);
        field.setText(value);
        return field;
    }
}
