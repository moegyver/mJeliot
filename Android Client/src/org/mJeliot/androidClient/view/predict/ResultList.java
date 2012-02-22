package org.mJeliot.androidClient.view.predict;

import org.mJeliot.androidClient.R;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;
import org.mJeliot.model.predict.Parameter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Moritz Rogalli
 * The ResultList creates a TableLayout to present a result of a prediction. It shows the
 * parameter and the guessed value vs. the predicted value on the left and a check for
 * correctly predicted parameters or an x for falsely predicted parameters to the right.
 */
public class ResultList extends TableLayout {

	/**
	 * Creates a ResultList which is a TableLayout which shows the parameter and the
	 * guessed value vs. the predicted value on the left and a check for correctly 
	 * predicted parameters or an x for falsely predicted parameters to the right.
	 * @param context android context to create the TableLayout
	 * @param user the current user to identify which predictions to take
	 * @param method the current assignment
	 */
	public ResultList(Context context, User user, Method method) {
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setColumnShrinkable(0, true);
		this.setColumnStretchable(1, true);
		this.setColumnShrinkable(2, true);
		Parameter returnParameter = null;
		for (Parameter parameter : method.getParameters()) {
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
	private void addRow(Parameter parameter, User user) {
		TableRow row = new TableRow(super.getContext());
		row.setBaselineAligned(true);
		
		TextView parameterName = new TextView(super.getContext());
		parameterName.setTypeface(Typeface.MONOSPACE);
		parameterName.setText(parameter.getName() + ": ");
		row.addView(parameterName);
		
		TextView value = new TextView(super.getContext());
		value.setTypeface(Typeface.DEFAULT_BOLD);
		String equality = "";
		if (parameter.isCorrectlyPredictedByUser(user)) {
			equality = " == ";
			value.setTextColor(Color.GREEN);
		} else {
			equality = " != ";
			value.setTextColor(Color.RED);
		}
		value.setText(parameter.getPredictedValueForUser(user) + equality + parameter.getActualValue());
		row.addView(value);
		
		ImageView icon = new ImageView(super.getContext());
		if (parameter.isCorrectlyPredictedByUser(user)) {
			icon.setImageResource(R.drawable.check);
		} else {
			icon.setImageResource(R.drawable.x);
		}
		row.addView(icon);
		
		this.addView(row);
	}

}
