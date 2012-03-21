package jeliot.gui.mJeliot;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.User;
import org.mJeliot.model.predict.Method;

import jeliot.mJeliot.MJeliotController;
import jeliot.mJeliot.MJeliotControllerListener;

/**
 * @author Moritz Rogalli
 * Shows a pie chart representing the percentage of correct, partly correct, false and 
 * not given answers in different colours. Green for correct, yellow for partly correct, 
 * red for false and grey for no answer.
 */
public class PredictResultStats extends Component implements MJeliotControllerListener {

	private static final long serialVersionUID = 7295222716325821302L;
	
	/**
	 * The height.
	 */
	private static final int HEIGHT = 130;
	/**
	 * The width.
	 */
	private static final int WIDTH = 110;
	/**
	 * Padding to make it look pretty.
	 */
	private static final int PADDING = 5;
	
	/**
	 * The percentage of correct answers.
	 */
	private double correctPercentage = 0;
	/**
	 * The percentage of partly correct answers.
	 */
	private double partlyCorrectPercentage = 0;
	/**
	 * The percentage of false answers.
	 */
	private double falsePercentage = 0;

	/**
	 * The PredictResultStats size. 
	 */
	private Dimension size = null;

	/**
	 * Whether or not the PredictResultStats should act on input.
	 */
	private boolean active = false;

	/**
	 * Initialises the size.
	 */
	void init() {
		this.size = new Dimension(WIDTH + PADDING * 2, HEIGHT + 2 * PADDING + getFontMetrics(getFont()).getHeight());
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		init();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#preferredSize()
	 */
	public Dimension preferredSize() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#minimumSize()
	 */
	public Dimension minimumSize() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint (Graphics g) {
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		String correct = "correct";
		String partly = "partly";
		String fals = "false";
		Color green = Color.GREEN.darker();
		Color yellow = Color.YELLOW.darker();
		Color red = Color.RED.darker();
		g.setColor(green);
		g.drawString(correct, PADDING, PADDING + fm.getHeight());
		g.setColor(yellow);
		g.drawString(partly, PADDING + fm.stringWidth(correct + "/"), PADDING + fm.getHeight());
		g.setColor(red);
		g.drawString(fals, PADDING + fm.stringWidth(correct + "/" + partly + "/"), PADDING + fm.getHeight());
		g.setColor(Color.BLACK);
		g.drawString("/", PADDING + fm.stringWidth(correct), PADDING + fm.getHeight());
		g.drawString("/", PADDING + fm.stringWidth(correct + "/" + partly), PADDING + fm.getHeight());
		g.setColor(Color.GRAY);
		g.drawString("no answer", PADDING, PADDING + fm.getHeight() * 2);
		if (this.isActive()) {
			g.fillOval(PADDING - 1, 2 * PADDING + fm.getHeight() * 2 - 1, WIDTH - 2 * PADDING + 2, HEIGHT - 6 * PADDING + 2);
			int offset = paintPercentage(g, Color.GREEN, fm.getHeight(), this.getCorrectPercentage(), 90);
			offset = paintPercentage(g, Color.YELLOW, fm.getHeight(), this.getPartlyCorrectPercentage(), offset);
			offset = paintPercentage(g, Color.RED, fm.getHeight(), this.getFalsePercentage(), offset);
		} else {
			g.drawOval(PADDING, 2 * PADDING + fm.getHeight() * 2, WIDTH - 2 * PADDING, HEIGHT - 6 * PADDING);
		}
	}
	
	/**
	 * Paints a percentage of the pie chart.
	 * @param g The graphics
	 * @param color The colour the percentage will be painted in
	 * @param extraPadding The extra padding by which the pie chart should be moved
	 * @param percent How big a percentage will be painted 
	 * @param offset The offset in degrees starting at 12 o'clock of the pie chart
	 * @return The offset for the next part 
	 */
	private int paintPercentage(Graphics g, Color color, int extraPadding, double percent, int offset) {
		Color oldColor = g.getColor();
		g.setColor(color);
		int angle = -(int)(360 * percent);
		g.fillArc(PADDING, 5 * PADDING + extraPadding, WIDTH - 2 * PADDING, HEIGHT - 6 * PADDING, offset, angle);
		g.setColor(oldColor);
		return angle + offset;
	}
	
	/**
	 * Sets the percentages and causes a repaint of the component.
	 * @param correctPercentage
	 * @param partlyCorrectPercentage
	 * @param falsePercentage
	 */
	public void setPercentage(double correctPercentage, double partlyCorrectPercentage, double falsePercentage) {
		this.correctPercentage = correctPercentage;
		this.partlyCorrectPercentage = partlyCorrectPercentage;
		this.falsePercentage = falsePercentage;
		this.repaint();
	}
	
	/**
	 * @return The correct percentage
	 */
	public double getCorrectPercentage() {
		return correctPercentage;
	}
	
	/**
	 * @return The partly correct percentage
	 */
	public double getPartlyCorrectPercentage() {
		return partlyCorrectPercentage;
	}
	
	/**
	 * @return The false percentage
	 */
	public double getFalsePercentage() {
		return falsePercentage;
	}
	
	/**
	 * Sets the component's active state
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return the active state
	 */
	public boolean isActive() {
		return active;
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onUserCountChanged(jeliot.mJeliot.MJeliotController)
	 */
	@Override
	public void onUserCountChanged(MJeliotController mJeliotController) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onAnswerCountChanged(jeliot.mJeliot.MJeliotController)
	 */
	@Override
	public void onAnswerCountChanged(MJeliotController mJeliotController) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onNewMethod(jeliot.mJeliot.MJeliotController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onNewMethod(MJeliotController mJeliotController, Method method) {
		this.active = false;
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onResultPosted(jeliot.mJeliot.MJeliotController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onResultPosted(MJeliotController mJeliotController, Method method) {
		int partlyCorrect = 0;
		int notCorrect = 0;
		int correct = 0;
		for (int i = 0; i < mJeliotController.getReceivedAnswerCount(); i++) {

			boolean correctAnswer = false;
			boolean falseAnswer = false;
			for (int j = 0; j < method.getParameters().size(); j++) {
				if (method.getParameters().get(j).getActualValue().equals(method.getParameters().get(j).getPredictions().get(i).getPredictedValue())) {
					correctAnswer = true;
				} else {
					falseAnswer = true;
				}
			}
			if (correctAnswer && !falseAnswer) {
				correct++;
			} else if (correctAnswer && falseAnswer) {
				partlyCorrect++;
			} else {
				notCorrect++;
			}
		}
		if (mJeliotController.getReceivedAnswerCount() != 0) {
			this.correctPercentage = (double)correct / (double)(mJeliotController.getUserCount() - 1);
			this.partlyCorrectPercentage = (double)partlyCorrect / (double)(mJeliotController.getUserCount() - 1);
			this.falsePercentage = (double)notCorrect / (double)(mJeliotController.getUserCount() - 1);
		}
		this.active = true;
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onMethodCalled(jeliot.mJeliot.MJeliotController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodCalled(MJeliotController mJeliotController, Method method) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onMethodReturned(jeliot.mJeliot.MJeliotController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodReturned(MJeliotController mJeliotController, Method method) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onClientConnected(jeliot.mJeliot.MJeliotController)
	 */
	@Override
	public void onClientConnected(MJeliotController mJeliotController, boolean isReconnected) {
	}

	/* (non-Javadoc)
	 * @see jeliot.mJeliot.MJeliotControllerListener#onClientDisconnected(jeliot.mJeliot.MJeliotController)
	 */
	@Override
	public void onClientDisconnected(MJeliotController mJeliotController) {
	}

	@Override
	public void onNewLecture(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLogin(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLoggedIn(MJeliotController mJeliotController, Lecture currentLecture) {
	}

	@Override
	public void onLogout(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLectureUpdated(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onLoggedOut(MJeliotController mJeliotController, Lecture lecture) {
	}

	@Override
	public void onUserLoggedIn(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLoggedOut(MJeliotController mJeliotController, User user,
			Lecture lecture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCodeUpdate(Lecture lecture, User user, String code,
			int cursorPosition, boolean isDone, boolean requestedAttention) {
		// TODO Auto-generated method stub
		
	}
}