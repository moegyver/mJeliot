package ict.model.predict;

import ict.model.User;

import java.util.Vector;


/**
 * @author Moritz Rogalli
 * A Parameter represents a parameter of a method, its actual value and the predictions
 * made on it.
 */
public class Parameter {
	/**
	 * The parameter's name.
	 */
	private String name = null;
	/**
	 * The parameter's actual value.
	 */
	private String actualValue = null;
	/**
	 * A list of predictions made by Users.
	 */
	private Vector<ParameterPrediction> predictions = new Vector<ParameterPrediction>();
	/**
	 * Initiates a new Parameter.
	 * @param name the name of the parameter
	 */
	public Parameter(String name) {
		this.name = name;
	}
	/**
	 * @return the name of the parameter
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return the actual value if already set, null otherwise
	 */
	public String getActualValue() {
		return this.actualValue;
	}
	/**
	 * @return the Prediction-Vector
	 */
	public Vector<ParameterPrediction> getPredictions() {
		return this.predictions;
	}
	/**
	 * Sets the actual value.
	 * @param actualValue the value to set
	 */
	public void setActualValue(String actualValue) {
		this.actualValue = actualValue;
	}
	/**
	 * Adds a new PredictedValue to the Parameter if there is not yet a PredictedValue
	 * set. Otherwise the PredictedValue for the user gets updated.
	 * @param user the user corresponding to the prediction made
	 * @param value the value predicted by the user
	 */
	public void setPredictedValue(User user, String value) {
		ParameterPrediction prediction = this.getPredictionForUser(user);
		if (prediction == null) {
			prediction = new ParameterPrediction(user, value);
			this.predictions.add(prediction);
		} else {
			prediction.setPredictedValue(value);
		}
	}
	/**
	 * Checks whether or not a user made a correct prediction.
	 * @param user the user that should be checked
	 * @return true if the user made a prediction and it equals the actual value, false
	 * otherwise.
	 */
	public boolean isCorrectlyPredictedByUser(User user) {
		return (this.getPredictedValueForUser(user) != null && this.getPredictedValueForUser(user).equals(this.actualValue));
	}
	/**
	 * Calculates the percentage of users that predicted the correct value.
	 * @return the percentage of correct answers. 
	 */
	public double getCorrectlyPredicted() {
		int correct = 0;
		for (int i = 0; i < this.predictions.size(); i++) {
			if (this.predictions.get(i).getPredictedValue().equals(this.actualValue)) {
				correct++;
			}
		}
		if (this.predictions.size() > 0) {
			return correct / this.predictions.size();
		} else {
			return -1.0;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	/**
	 * Returns the value predicted by a user.
	 * @param user the user
	 * @return a String containing the predicted value if the user made one, null
	 * otherwise.
	 */
	public String getPredictedValueForUser(User user) {
		for (ParameterPrediction prediction : predictions) {
			if (prediction.getUser() == user) {
				return prediction.getPredictedValue();
			}
		}
		return null;
	}
	
	/**
	 * Gets a prediction for a user. 
	 * @param user the user in question
	 * @return the prediction made by the user if made, null otherwise
	 */
	public ParameterPrediction getPredictionForUser(User user) {
		for (ParameterPrediction prediction : predictions) {
			if (prediction.getUser() == user) {
				return prediction;
			}
		}
		return null;
	}
	/**
	 * This method makes it possible to remove a Prediction from the list.
	 * @param prediction the prediction to remove
	 */
	public void removePrediction(ParameterPrediction prediction) {
		this.predictions.remove(prediction);
	}
}
