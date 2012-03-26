package org.mJeliot.model.coding;

public interface CodingTaskUserCodeListener {

	public void onIsDoneChanged(CodingTaskUserCode codingTaskUserCode,
			boolean isDone);
	public void onRequestedAttentionChanged(CodingTaskUserCode codingTaskUserCode,
			boolean requestedAttention);
	public void onCodeModified(CodingTaskUserCode codingTaskUserCode,
			String modifiedCode, boolean isOriginalCode);
	public void onCursorMoved(CodingTaskUserCode codingTaskUserCode,
			int cursorPosition);
	public void onUserRemoved(CodingTaskUserCode codingTaskUserCode);
	public void onUserCodeChanged(CodingTaskUserCode source,
			CodingTaskUserCode currentUserCode);
}
