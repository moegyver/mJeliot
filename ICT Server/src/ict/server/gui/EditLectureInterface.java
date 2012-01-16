package ict.server.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ict.model.Lecture;
import ict.server.ServerController;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EditLectureInterface extends JFrame {
	private static final long serialVersionUID = 2607181184149678794L;
	private Lecture lecture = null;
	private JTextField lectureNameTextField = new JTextField(30);
	private JTextField lectureAdminPasswordTextField = new JTextField(30);
	private JTextField lectureClientPasswordTextField = new JTextField(30);
	private JButton cancelButton = new JButton("Cancel");
	private JButton okButton = new JButton("Ok");

	public EditLectureInterface(final ServerController controller) {
		super("Edit lecture");
		GridLayout layout = new GridLayout(4, 2);
		this.setLayout(layout);
		
		this.add(new JLabel("Lecture Name:"));
		this.add(this.lectureNameTextField);
		
		this.add(new JLabel("Admin Password:"));
		this.add(this.lectureAdminPasswordTextField);
		
		this.add(new JLabel("Client Password:"));
		this.add(this.lectureClientPasswordTextField);
		
		this.cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		this.add(cancelButton);
		
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lecture == null) {
					lecture = controller.addLecture(lectureNameTextField.getText());
				}
				lecture.setName(lectureNameTextField.getText());
				lecture.setAdminPassword(lectureAdminPasswordTextField.getText());
				lecture.setClientPassword(lectureClientPasswordTextField.getText());
				setVisible(false);
			}
		});
		this.add(okButton);
		
		this.pack();
	}
	public void addNewLecture() {
		this.lecture = null;
		this.lectureNameTextField.setText("");
		this.lectureAdminPasswordTextField.setText("");
		this.lectureClientPasswordTextField.setText("");
		this.setVisible(true);
	}
}
