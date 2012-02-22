package org.mJeliot.server.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.mJeliot.model.Lecture;
import org.mJeliot.server.ServerController;
import org.mJeliot.server.ServerControllerListener;

public class ServerUI extends JFrame implements Runnable, ServerControllerListener {
	private static final long serialVersionUID = 5998763736754034768L;
	private ServerController controller = null;
	private TableModel tableModel = null;
	private JTable table = null;
	private EditLectureInterface editLectureInterface = null;
	public ServerUI(ServerController controller) {
		super("mJeliot server interface");
		this.controller = controller;
		this.controller.addServerControllerListener(this);
		this.editLectureInterface = new EditLectureInterface(this.controller);
	}
	@Override
	public void run() {
		this.setLayout(new BorderLayout());
		this.tableModel = new LectureTableModel(this.controller);
		this.table = new JTable(this.tableModel);
		this.table.setPreferredSize(new Dimension((int) this.table.getPreferredSize().getWidth()*2, table.getRowHeight()*8));
		JScrollPane scrollpane = new JScrollPane(this.table);
		scrollpane.setPreferredSize(this.table.getPreferredSize());
		this.add(scrollpane, BorderLayout.CENTER);
        makeButtons();
        this.pack();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				showShutdownQuestion();
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
        });
        this.setVisible(true);
	}
	private void makeButtons() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		JButton newLectureButton = new JButton("New lecture");
		newLectureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editLectureInterface.addNewLecture();
			}
		});
		p.add(newLectureButton);
		
		JButton deleteLectureButton = new JButton("Delete lecture");
		deleteLectureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1) {
					controller.deleteLecture(getCurrentlySelectedLecture());
				}
			}
		});
		p.add(deleteLectureButton);
		
		final JButton shutdownServerButton = new JButton("Shutdown server");
		shutdownServerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showShutdownQuestion();
			}
		});
		p.add(shutdownServerButton);
		
		this.add(p, BorderLayout.SOUTH);
	}
	
	private Lecture getCurrentlySelectedLecture() {
		if (this.table.getSelectedRow() != -1) {
			return this.controller.getLecture((Integer)this.table.getValueAt(this.table.getSelectedRow(), 1));
		} else {
			return null;
		}
	}

	protected void showShutdownQuestion() {
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this,
				"This will disconnect all running sessions, do you want to continue?",
				"Final check", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[1]);
		if (n == 0) {
			this.controller.shutdown();
		}
	}
	@Override
	public void onLectureAdded(ServerController controller, Lecture lecture) {
		this.table.setModel(new LectureTableModel(this.controller));
		this.table.repaint();
	}
	@Override
	public void onLectureRemoved(ServerController controller, Lecture lecture) {
		this.table.setModel(new LectureTableModel(this.controller));
		table.repaint();
	}
}
