package jeliot.gui.mJeliot;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

import org.mJeliot.model.User;
import org.mJeliot.model.coding.CodingTaskUserCode;
import org.mJeliot.model.coding.CodingTaskUserCodeListener;

public class UserButton extends JButton implements CodingTaskUserCodeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1411685024522565441L;
	static private UserProperties propertiesBundle = ResourceBundles
			.getGuiUserProperties();
	private final CodingTaskUserCode codingTaskUserCode;
	private final UserSelection userSelection;
	private boolean isUserSelected;

	public UserButton(UserSelection userSelection, final CodingTaskUserCode codingTaskUserCode) {
		super();
		this.userSelection = userSelection;
		this.codingTaskUserCode = codingTaskUserCode;
		this.codingTaskUserCode.addCodingTaskUserCodeListener(this);
		this.makeUserButton(this.getUser().getName(), propertiesBundle.getStringProperty("image.user_icon"));
		this.userSelection.addToPanel(this);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				codingTaskUserCode.toggleSelect();
			}
		});
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
		repaint();
	}

	private void updateButton() {
		String buttonName = makeButtonName();
		System.out.println("updating button: "  + this.getUser() + " button: "+ buttonName);
		this.makeUserButton(this.getUser().getName(), UserButton.propertiesBundle.getStringProperty(buttonName));
	}

	private String makeButtonName() {
		String buttonName = "image.user";
		if (this.isUserSelected) {
			buttonName += "_selected";
		}
		if (this.codingTaskUserCode.hasModifiedCode() && ! this.codingTaskUserCode.isDone()) {
			buttonName += "_coding";
		}
		if (this.codingTaskUserCode.requestedAttention()) {
			buttonName += "_attention";
		}
		if (this.codingTaskUserCode.isDone() && !this.codingTaskUserCode.requestedAttention()) {
			buttonName += "_done";
		}
		return buttonName + "_icon";
	}
	
	private User getUser() {
		return codingTaskUserCode.getUser();
	}

	@Override
	public void onIsDoneChanged(CodingTaskUserCode codingTaskUserCode,
			boolean isDone) {
		this.updateButton();		
	}

	@Override
	public void onRequestedAttentionChanged(
			CodingTaskUserCode codingTaskUserCode, boolean requestedAttention) {
		this.updateButton();
	}

	@Override
	public void onCodeModified(CodingTaskUserCode codingTaskUserCode,
			String modifiedCode, boolean isOriginalCode) {
		this.updateButton();
	}

	@Override
	public void onCursorMoved(CodingTaskUserCode codingTaskUserCode,
			int cursorPosition) {
	}

	@Override
	public void onUserRemoved(CodingTaskUserCode codingTaskUserCode) {
		this.userSelection.removeFromPanel(this);
	}

	@Override
	public void onUserCodeChanged(CodingTaskUserCode source,
			CodingTaskUserCode currentUserCode) {
		System.out.println("setting button for user " + source.getUser() + " to " + (source == currentUserCode));
		this.setUserSelected(source == currentUserCode);
		this.updateButton();
	}

	private void setUserSelected(boolean isSelected) {
		this.isUserSelected = isSelected;
	}
}
