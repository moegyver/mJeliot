package jeliot.mJeliot;

import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.coding.CodingTaskListener;
import org.mJeliot.model.coding.CodingTaskUserCode;
import org.mJeliot.model.coding.CodingTaskUserCodeListener;

import jeliot.gui.CodeEditor2;

public class CodeSelector implements CodingTaskListener, CodingTaskUserCodeListener {

	private final CodeEditor2 codeEditor;
	private final String originalCode;
	private CodingTaskUserCode currentUserCode;
	private final int originalCursorPosition;
	private final MJeliotController controller;

	public CodeSelector(MJeliotController controller, CodeEditor2 codeEditor, CodingTask codingTask, String originalCode, int originalCursorPosition) {
		this.controller = controller;
		this.codeEditor = codeEditor;
		this.originalCode = originalCode;
		this.originalCursorPosition = originalCursorPosition;
		codingTask.addCodingTaskListener(this);
	}

	@Override
	public void onCodingTaskUserCodeAdded(CodingTask codingTask,
			CodingTaskUserCode userCode) {
	}

	@Override
	public void onCodingTaskEnded(CodingTask codingTask) {
	}

	@Override
	public void onUserCodeChanged(CodingTask codingTask,
			CodingTaskUserCode usercode) {
		if (currentUserCode != null) {
			this.controller.setLiveMode(false, currentUserCode);
			currentUserCode.removeCodingTaskUserCodeListener(this);
		}
		currentUserCode = usercode;
		if (currentUserCode != null) {
			currentUserCode.addCodingTaskUserCodeListener(this);
			codeEditor.setProgram(usercode.getCode());
			codeEditor.setCursorPosition(usercode.getCursorPosition());
			if (currentUserCode.isDone()) {
				// TODO go to compile
			} else {
				this.controller.setLiveMode(true, currentUserCode);
			}
		} else {
			codeEditor.setProgram(originalCode);
			codeEditor.setCursorPosition(this.originalCursorPosition);
		}
	}

	@Override
	public void onIsDoneChanged(CodingTaskUserCode codingTaskUserCode,
			boolean isDone) {
		// TODO Go to compile when done
	}

	@Override
	public void onRequestedAttentionChanged(
			CodingTaskUserCode codingTaskUserCode, boolean requestedAttention) {
	}

	@Override
	public void onCodeModified(CodingTaskUserCode codingTaskUserCode,
			String modifiedCode, boolean isOriginalCode) {
		this.codeEditor.setProgram(modifiedCode);
	}

	@Override
	public void onCursorMoved(CodingTaskUserCode codingTaskUserCode,
			int cursorPosition) {
		this.codeEditor.setCursorPosition(cursorPosition);
	}

	@Override
	public void onUserRemoved(CodingTaskUserCode codingTaskUserCode) {
	}

	@Override
	public void onUserCodeChanged(CodingTaskUserCode source,
			CodingTaskUserCode currentUserCode) {
	}
}
