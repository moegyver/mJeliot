package org.mJeliot.model;

public interface LectureListener {
	void onUserRemoved(Lecture lecture, User user);

	void onUserAdded(Lecture lecture, User user);
}
