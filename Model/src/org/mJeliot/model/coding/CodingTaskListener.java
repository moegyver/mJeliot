package org.mJeliot.model.coding;

public interface CodingTaskListener {

	void onCodingTaskUserCodeAdded(CodingTask codingTask,
			CodingTaskUserCode userCode);

	void onCodingTaskEnded(CodingTask codingTask);

	void onUserCodeChanged(CodingTask codingTask, CodingTaskUserCode usercode);

}
