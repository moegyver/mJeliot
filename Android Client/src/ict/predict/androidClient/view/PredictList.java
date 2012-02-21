package ict.predict.androidClient.view;

import ict.model.User;
import ict.model.predict.Method;
import ict.model.predict.Parameter;
import ict.predict.androidClient.controller.ParameterController;
import android.content.Context;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Moritz Rogalli
 * A PredictList is a TableLayout the features everything you need to create a form to 
 * predict parameters. It even takes care of saving the guessed values to the current
 * assignment of the Controller. 
 */
public class PredictList extends TableLayout {
	/**
	 * Builds a TableLayout that contains a row for every parameter with a label to the
	 * left and a TextEdit to the right for predicting the value. The TextEdits have a
	 * ParameterController as an OnKeyListener to update the corresponding predicted value
	 * in the assignment.
	 * @param context the android context, needed to initialize the TableLayout
	 * @param user the corresponding user that is predicting the parameters
	 * @param method the current assignment that the values should get updated to
	 */
	public PredictList(Context context, User user, Method method) {
		super(context);
		this.setColumnStretchable(0, true);
		this.setColumnShrinkable(1, true);
		Parameter returnParameter = null;
		for(Parameter parameter : method.getParameters()){
			if (parameter.getName().equals("return")) {
				returnParameter = parameter;
			} else {
				this.addRow(parameter, user);
			}
		}
		if (returnParameter != null) {
			this.addRow(returnParameter, user);
		}

	}
	
	/**
	 * Adds a row to the PredictList and creates a ParameterController to update the user's prediction.
	 * @param parameter The parameter for which to create the row
	 * @param user The user that is predicting
	 */
	private void addRow(Parameter parameter, User user) {
		TableRow row = new TableRow(this.getContext());
		row.setBaselineAligned(true);
		TextView parameterNameLabel = new TextView(this.getContext());
		parameterNameLabel.setText(parameter.getName());
		row.addView(parameterNameLabel);
		EditText guessedValueEditText = new EditText(this.getContext());
		guessedValueEditText.addTextChangedListener(new ParameterController(user, parameter));
		guessedValueEditText.setWidth(70);
		guessedValueEditText.setSingleLine();
		row.addView(guessedValueEditText);
		this.addView(row);
	}
}