package org.mJeliot.androidClient.edit;

import org.mJeliot.androidClient.view.edit.CodeEditor;

import android.text.Editable;
import android.text.TextWatcher;

public class CodeChangeWatcher implements TextWatcher {
	private CodeEditor editor;
	public CodeChangeWatcher(CodeEditor editor) {
		this.editor = editor;
	}
	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		this.editor.updateText(s, start);
	}
}
