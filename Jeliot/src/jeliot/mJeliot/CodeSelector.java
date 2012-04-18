package jeliot.mJeliot;

import org.mJeliot.model.coding.CodingTask;
import org.mJeliot.model.coding.CodingTaskListener;
import org.mJeliot.model.coding.CodingTaskUserCode;
import org.mJeliot.model.coding.CodingTaskUserCodeListener;
import org.mJeliot.protocol.ProtocolParser;

import jeliot.gui.CodeEditor2;

public class CodeSelector implements CodingTaskListener,
		CodingTaskUserCodeListener {

	private final CodeEditor2 codeEditor;
	private final String originalCode;
	private CodingTaskUserCode currentUserCode;
	private final int originalCursorPosition;
	private final MJeliotController controller;
	private int codeOffset = 0;
	private int codeLength = 0;
	private String selectedCode;

	public CodeSelector(MJeliotController controller, CodeEditor2 codeEditor,
			CodingTask codingTask) {
		this.controller = controller;
		this.codeEditor = codeEditor;
		this.originalCode = codeEditor.getProgram();
		this.selectedCode = codeEditor.getSelectedText();
		this.codeOffset = codeEditor.getSelectionOffset();
		this.codeLength = codeEditor.getSelectionLength();
		this.originalCursorPosition = codeEditor.getCursorPosition();
		codingTask.addCodingTaskListener(this);
	}

	@Override
	public void onCodingTaskUserCodeAdded(CodingTask codingTask,
			CodingTaskUserCode userCode) {
		controller.sendMessage(ProtocolParser.generateCodingTask(originalCode,
				controller.getUser().getId(), userCode.getUser().getId(),
				codingTask.getLecture().getId()));
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
			codeEditor.setProgram(mergeCode(usercode.getCode()));
			codeEditor.setCursorPosition(usercode.getCursorPosition() + this.codeOffset);
			if (currentUserCode.isDone()) {
				controller.getGUI().tryToEnterAnimate();
				controller.sendMessage(ProtocolParser.generateRemoteCommand(
						codingTask.getLecture().getId(), controller.getUser()
								.getId(), currentUserCode.getUser().getId(), -1, 
						"control"));
			} else {
				this.controller.setLiveMode(true, currentUserCode);
			}
		} else {
			codeEditor.setProgram(originalCode);
			codeEditor.setCursorPosition(this.originalCursorPosition);
		}
	}

	private String mergeCode(String code) {
		return this.originalCode.substring(0, codeOffset) + code + originalCode.substring(codeOffset + this.codeLength) ;
	}

	@Override
	public void onIsDoneChanged(CodingTaskUserCode codingTaskUserCode,
			boolean isDone) {
		if (codingTaskUserCode == this.currentUserCode && isDone) {
			controller.getGUI().tryToEnterAnimate();
			controller.sendMessage(ProtocolParser.generateRemoteCommand(
					codingTaskUserCode.getCodingTask().getLecture().getId(), controller.getUser()
							.getId(), currentUserCode.getUser().getId(), controller.getGUI().getSpeedSlider().getValue(), 
					"control"));
		}
	}

	@Override
	public void onRequestedAttentionChanged(
			CodingTaskUserCode codingTaskUserCode, boolean requestedAttention) {
	}

	@Override
	public void onCodeModified(CodingTaskUserCode codingTaskUserCode,
			String modifiedCode, boolean isOriginalCode) {
		this.codeEditor.setProgram(mergeCode(modifiedCode));
	}

	@Override
	public void onCursorMoved(CodingTaskUserCode codingTaskUserCode,
			int cursorPosition) {
		this.codeEditor.setCursorPosition(cursorPosition + this.codeOffset);
	}

	@Override
	public void onUserRemoved(CodingTaskUserCode codingTaskUserCode) {
	}

	@Override
	public void onUserCodeChanged(CodingTaskUserCode source,
			CodingTaskUserCode currentUserCode) {
	}
}
