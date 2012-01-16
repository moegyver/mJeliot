package jeliot.gui;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * Filter for the file chooser to only show the java source code files. Modified
 * from the example for the Sun's Java Website.
 * 
 * @author Niko Myller
 */
public class JavaFileFilter extends FileFilter implements java.io.FileFilter {

	/**
	 * The resource bundle for gui package
	 */
	static private UserProperties propertiesBundle = ResourceBundles.getGuiUserProperties();

	static private ResourceBundle messageBbundle = ResourceBundles.getGuiMessageResourceBundle();

	/**
	 * Method accepts only the files whose extension which is defined in the
	 * resource bundle with name extension.java
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null) {
				if (extension.toLowerCase().equals(
						propertiesBundle.getStringProperty("extension.java"))) {
					return true;
				}
			}
			;
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name .
	 * 
	 * @param f
	 *            The file whose extension is needed.
	 * @return the string presentation of the files extension.
	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
		}
		return null;
	}

	/**
	 * Returns the human readable description of this filter described in the
	 * resource bundle as extension.java.description.
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return messageBbundle.getString("extension.java.description");
	}
}