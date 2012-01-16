package ict.server;

import ict.model.Lecture;

public interface ServerControllerListener {
	public void onLectureAdded(ServerController controller, Lecture lecture);
	public void onLectureRemoved(ServerController controller, Lecture lecture);
}
