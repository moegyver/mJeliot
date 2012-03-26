package org.mJeliot.model.coding;

import java.util.Vector;

import org.mJeliot.model.User;

public class CodingTaskUserCode implements CodingTaskListener {
	
	private CodingTask codingTask = null;
	private boolean isDone = false;
	private boolean requestedAttention = false;
	private String code = "";
	private Vector<CodingTaskUserCodeListener> listeners = new Vector<CodingTaskUserCodeListener>();
	private final User user;
	private int cursorPosition;
	
	public CodingTaskUserCode(CodingTask codingTask, User user, String initialCode, int cursorPosition) {
		System.out.println("CodingTaskUserCode created for user " + user);
		this.codingTask = codingTask;
		this.codingTask.addCodingTaskListener(this);
		this.user  = user;
		this.code = initialCode;
		this.cursorPosition = cursorPosition;
	}
	
	public boolean isDone() {
		return this.isDone;
	}
	public void setDone(boolean isDone) {
		if (isDone != this.isDone) {
			this.isDone = isDone;
			this.setRequestedAttention(false);
			this.fireOnIsDoneChanged(isDone);
		}
	}
	private void fireOnIsDoneChanged(boolean isDone) {
		for (CodingTaskUserCodeListener listener : this.listeners ) {
			listener.onIsDoneChanged(this, isDone);
		}
	}

	public boolean requestedAttention() {
		return this.requestedAttention;
	}
	public boolean hasModifiedCode() {
		return !this.code.equals(this.codingTask.getOriginalCode());
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	public void update(String code, int cursorPosition, boolean isDone,
			boolean requestedAttention) {
		this.updateCode(code);
		this.updateCursorPosition(cursorPosition);
		this.setDone(isDone);
		this.updateRequestedAttention(requestedAttention);
		this.printStatus();
	}

	private void updateCursorPosition(int cursorPosition) {
		if (this.cursorPosition != cursorPosition) {
			this.cursorPosition = cursorPosition;
			this.fireOnCursorMoved();
		}
	}

	private void fireOnCursorMoved() {
		for (CodingTaskUserCodeListener listener : listeners) {
			listener.onCursorMoved(this, cursorPosition);
		}
	}

	private void printStatus() {
		System.out.println("User " + this.user.getId() + 
				" CodingTask status: isDone: " + this.isDone + 
				" requestedAttention: " + requestedAttention +
				" hasModified: " + this.hasModifiedCode());
	}

	private void updateRequestedAttention(boolean requestedAttention) {
		if (!this.requestedAttention && requestedAttention) {
			this.setRequestedAttention(true);
		}
	}

	public void setRequestedAttention(boolean requestedAttention) {
		if (this.requestedAttention != requestedAttention) {
			this.requestedAttention = requestedAttention;
			this.fireOnRequestedAttentionChanged();
		}
	}

	private void fireOnRequestedAttentionChanged() {
		for (CodingTaskUserCodeListener listener : listeners) {
			listener.onRequestedAttentionChanged(this, requestedAttention);
		}
	}

	private void updateCode(String code) {
		if (code != this.code) {
			this.code = code;
			this.fireOnCodeModified();
	}
}

	private void fireOnCodeModified() {
		for (CodingTaskUserCodeListener listener : listeners) {
			listener.onCodeModified(this, this.code, this.hasModifiedCode());
		}
	}

	public void remove() {
		this.fireOnRemoved();
	}

	private void fireOnRemoved() {
		for (CodingTaskUserCodeListener listener : listeners) {
			listener.onUserRemoved(this);
		}
	}

	@Override
	public void onCodingTaskUserCodeAdded(CodingTask codingTask,
			CodingTaskUserCode userCode) {
	}

	@Override
	public void onCodingTaskEnded(CodingTask codingTask) {
		this.remove();
	}

	public void addCodingTaskUserCodeListener(CodingTaskUserCodeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void onUserCodeChanged(CodingTask codingTask,
			CodingTaskUserCode usercode) {
		this.fireOnUserCodeChanged(usercode);
	}

	private void fireOnUserCodeChanged(CodingTaskUserCode usercode) {
		for (CodingTaskUserCodeListener listener : this.listeners) {
			listener.onUserCodeChanged(this, usercode);
		}
	}

	public void select() {
		this.codingTask.setCurrentUserCode(this);
	}

	public String getCode() {
		return this.code;
	}

	public void removeCodingTaskUserCodeListener(CodingTaskUserCodeListener listener) {
		this.listeners.remove(listener);
	}

	public int getCursorPosition() {
		return this.cursorPosition;
	}
}