package org.mJeliot.model.coding;

import java.util.HashMap;
import java.util.Vector;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.LectureListener;
import org.mJeliot.model.User;

public class CodingTask implements LectureListener {
	private String originalCode = "";
	private HashMap<User, CodingTaskUserCode> codingTaskUserCodes = new HashMap<User, CodingTaskUserCode>();
	private CodingTaskUserCode currentUserCode = null;
	private final Lecture lecture;
	private Vector<CodingTaskListener> listeners = new Vector<CodingTaskListener>();
	
	public CodingTask(Lecture lecture, String originalCode) {
		this.lecture = lecture;
		this.lecture.addLectureListener(this);
		this.originalCode = originalCode;
		for (User user : this.lecture.getUsers()) {
			this.addUser(user);
		}
	}
	
	public String getOriginalCode() {
		return this.originalCode;
	}
	
	public void addUser(User user) {
		this.codingTaskUserCodes.put(user, new CodingTaskUserCode(this, user, this.originalCode, 0));
	}
	public CodingTaskUserCode getUserCodeTask(User user) {
		return this.codingTaskUserCodes.get(user);
	}
	public void updateUserCode(Lecture lecture, User user, String code, int cursorPosition, boolean isDone, boolean requestedAttention) {
		System.out.println("updating user code..." + user);
		if (lecture.equals(this.lecture)) {
			System.out.println("lecture correct");
			CodingTaskUserCode codingTaskUserCode = this.codingTaskUserCodes.get(user);
			if (codingTaskUserCode != null) {
				System.out.println("found user code");
				codingTaskUserCode.update(code, cursorPosition, isDone, requestedAttention);
			}
		}
	}

	@Override
	public void onUserRemoved(Lecture lecture, User user) {
		CodingTaskUserCode codingTaskUserCode = this.codingTaskUserCodes.remove(user);
		if (codingTaskUserCode != null) {
			codingTaskUserCode.remove();
		}
	}
	public void endCodingTask() {
		this.lecture.removeLectureListener(this);
		this.fireOnCodingTaskEnded();
	}

	private void fireOnCodingTaskEnded() {
		for (CodingTaskListener listener : this.listeners) {
			listener.onCodingTaskEnded(this);
		}
	}

	public void setCurrentUserCode(CodingTaskUserCode usercode) {
		this.currentUserCode = usercode;
		this.fireOnUserCodeChanged(usercode);
	}
	private void fireOnUserCodeChanged(CodingTaskUserCode usercode) {
		for (CodingTaskListener listener : this.listeners) {
			listener.onUserCodeChanged(this, usercode);
		}
	}

	@Override
	public void onUserAdded(Lecture lecture, User user) {
		CodingTaskUserCode codingTaskUserCode = new CodingTaskUserCode(this, user, this.originalCode, 0);
		this.codingTaskUserCodes.put(user, codingTaskUserCode);
		this.fireOnCodingTaskUserCodeAdded(codingTaskUserCode);
	}
	
	private void fireOnCodingTaskUserCodeAdded(
			CodingTaskUserCode userCode) {
		for (CodingTaskListener listener : this.listeners) {
			listener.onCodingTaskUserCodeAdded(this, userCode);
		}
	}

	public void addCodingTaskListener(CodingTaskListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public Lecture getLecture() {
		return this.lecture;
	}
}
