package ict.predict.androidClient.controller;

import ict.model.User;
import ict.model.predict.Parameter;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * A ParameterController updates a Parameter when the user enters his value every time the user presses a key.
 * However, the class must be registered as an onKeyListener to have an effect.
 * @author Moritz Rogalli
 *
 */
public class ParameterController implements TextWatcher {
	/**
	 * The parameter which prediction  to update.
	 */
	private Parameter parameter = null;
	/**
	 * The user that makes the prediction.
	 */
	private User user = null;

	/**
	 * Initializes a new ParameterController. The controller still has to be added as an onKeyListener.
	 * @param user The user that is predicting a value.
	 * @param parameter The parameter that is to be changed.
	 */
	public ParameterController(User user, Parameter parameter) {
		this.user = user;
		this.parameter = parameter;
	}
	/**
	 * @return the parameter that is controlled
	 */
	public Parameter getParameter() {
		return parameter;
	}
	
	/**
	 * @return the user that is predicting
	 */
	public User getUser() {
		return user;
	}
	/* (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		String predictedValue = s.toString();
		this.parameter.setPredictedValue(this.user, predictedValue);
	}
	/* (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}
	/* (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

}
