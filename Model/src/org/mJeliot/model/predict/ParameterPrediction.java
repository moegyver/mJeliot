package org.mJeliot.model.predict;

import org.mJeliot.model.User;

/**
 * @author Moritz Rogalli
 * A ParameterPrediction saves the value a user has predicted plus the value itself.
 */
public class ParameterPrediction {

	private String predictedValue = null;
	private User user = null;

	public ParameterPrediction(User user, String predictedValue) {
		this.user = user;
		this.predictedValue = predictedValue;
	}

	/**
	 * @param predictedValue the predictedValue to set
	 */
	public void setPredictedValue(String predictedValue) {
		this.predictedValue = predictedValue;
	}

	/**
	 * @return the predictedValue
	 */
	public String getPredictedValue() {
		return predictedValue;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

}
