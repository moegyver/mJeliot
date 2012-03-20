package jeliot.gui.mJeliot;

import java.awt.Insets;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import jeliot.theater.ImageLoader;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import org.mJeliot.model.User;

public class UserButton extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1411685024522565441L;
	private User user;
    static private UserProperties propertiesBundle = ResourceBundles
            .getGuiUserProperties();

	public UserButton(User user) {
		super();
		this.user = user;
		this.makeUserButton(user.getName(), propertiesBundle.getStringProperty("image.user_icon"));
	}
	
    private void makeUserButton(String label, String iconName) {

        URL imageURL = this.getClass().getClassLoader().getResource(
                propertiesBundle.getStringProperty("directory.images")
                        + iconName);
        if (imageURL == null) {
            imageURL = Thread.currentThread().getContextClassLoader()
                    .getResource(
                            propertiesBundle
                                    .getStringProperty("directory.images")
                                    + iconName);
        }
        ImageIcon icon = new ImageIcon(imageURL);
        //new ImageIcon(bundle.getString("directory.images")+ iconName);
        this.setText(label);
        this.setIcon(icon);
        this.setVerticalTextPosition(AbstractButton.BOTTOM);
        this.setHorizontalTextPosition(AbstractButton.CENTER);
        //  b.setBorder(BorderFactory.createEtchedBorder());
        this.setMargin(new Insets(0, 0, 0, 0));
    }

}
