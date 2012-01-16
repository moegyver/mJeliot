package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import jeliot.Jeliot;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * LoadJeliot displays a splash screen and
 * starts the Jeliot application.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class LoadJeliot {

	/**
	 * The resource bundle for gui package
	 */
	static private ResourceBundle messageBundle = ResourceBundles.getGuiMessageResourceBundle();

	/**
	 * The resource bundle for gui package
	 */
	static private UserProperties propertiesBundle = ResourceBundles.getGuiUserProperties();

	/**
	 * Initializes the Jeliot's splash screen window. Initializes the
	 * jeliot.Jeliot object.
	 */
	public static void start(final Jeliot jeliot) {
		// Get the splash screen image
		final Image image = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(propertiesBundle.getStringProperty("directory.images") + messageBundle.getString("image.splash_screen"))).getImage();

		// create the splash screen window
		Component splash = new Component() {
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, this);
			}
		};
		JLabel label = new JLabel(messageBundle.getString("label.splash_screen"));
		label.setHorizontalAlignment(JLabel.CENTER);
		final JWindow window = new JWindow();
		Container c = window.getContentPane();
		c.setLayout(new BorderLayout());
		c.add("Center", splash);
		c.add("South", label);

		// Set the window size to conform to the image and put the
		// window the center of the screen
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		int iw = Math.max(image.getWidth(window), label.getWidth());
		int ih = image.getHeight(window) + label.getHeight();

		window.setBounds((screen.width - iw) / 2, (screen.height - ih) / 2, iw, ih);
		window.setVisible(true);
		//window.show();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jeliot.run();
				window.dispose();
			}
		});
	}
}

