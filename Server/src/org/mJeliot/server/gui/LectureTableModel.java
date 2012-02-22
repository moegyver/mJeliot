package org.mJeliot.server.gui;


import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.mJeliot.model.Lecture;
import org.mJeliot.server.ServerController;

public class LectureTableModel implements TableModel {

	private ServerController controller = null;
	
	public LectureTableModel(ServerController controller) {
		this.controller  = controller;
	}
	
	@Override
	public int getRowCount() {
		return this.controller.numberOfLectures();
	}
	@Override
	public int getColumnCount() {
		return 4;
	}
	@Override
	public String getColumnName(int columnIndex) {
		String result = "";
		switch (columnIndex) {
			case 0:
				result = "Name";
				break;
			case 1:
				result = "ID";
				break;
			case 2:
				result = "Admin password";
				break;
			case 3:
				result = "User password";
				break;
		}
		return result;
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		case 2:
		case 3:
			return String.class;
		}
		return null;
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex != 2;
	}
	@Override
	public Object getValueAt(int row, int col) {
		Lecture lecture = this.controller.getLectures()[row];
		switch (col) {
			case 0:
				return lecture.getName();
			case 1:
				return lecture.getId();
			case 2:
				return lecture.getAdminPassword();
			case 3:
				return lecture.getClientPassword();
			default:
				return null;
		}
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Lecture lecture = this.controller.getLecture((Integer)this.getValueAt(rowIndex, 1));
		switch (columnIndex) {
			case 0:
				lecture.setName((String)aValue);
				break;
			case 2:
				lecture.setAdminPassword((String)aValue);
				break;
			case 3:
				lecture.setClientPassword((String)aValue);
				break;
		}
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}
}
