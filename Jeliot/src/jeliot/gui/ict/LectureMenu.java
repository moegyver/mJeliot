package jeliot.gui.ict;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.mJeliot.model.Lecture;

import jeliot.ict.ICTController;

public class LectureMenu extends JMenu {

	private static final long serialVersionUID = -5002553511422141949L;
	private final ICTController controller;
	private Vector<Integer> lectureIds = new Vector<Integer>();

	public LectureMenu(String menuTitle, ICTController controller) {
		super(menuTitle);
		this.controller = controller;
		for (Lecture lecture : this.controller.getAvailableLectures()) {
			this.addLecture(lecture);
		}
	}

	public void addLecture(final Lecture lecture) {
		if (!this.lectureIds.contains(lecture.getId())) {
			this.lectureIds.add(lecture.getId());
			JMenuItem lectureItem = new JMenuItem(lecture.getName());
			lectureItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (controller.getCurrentLecture() != null) {
						logout();
					}
					controller.setLecture(lecture);
				}
			});
			this.add(lectureItem, this.getComponentCount() - 1);
			this.repaint();
		}
	}

	public void onLoggedIn() {
		if (this.controller.getCurrentLecture() != null) {
			System.out.println("Index is: " + this.lectureIds.indexOf(this.controller.getCurrentLecture().getId()));
			JMenuItem logoutItem = new JMenuItem("Logout");
			logoutItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logout();
				}
			});
			this.add(logoutItem);
		}
	}

	protected void logout() {
		this.controller.logout();
	}
	public void setVisible(boolean isVisible) {
		System.out.println("visible status:" + isVisible);
		super.setVisible(isVisible);
	}

	public void updateLecture(Lecture lecture) {
		if (this.lectureIds.contains(lecture.getId())) {
			((JMenuItem)this.getComponent(this.lectureIds.indexOf(lecture.getId()) + 1)).setText(lecture.getName());
		}
	}
}
