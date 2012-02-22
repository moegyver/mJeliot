package jeliot.gui.ict;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import org.mJeliot.model.Lecture;
import org.mJeliot.model.predict.Method;

import jeliot.Jeliot;
import jeliot.ict.ICTController;
import jeliot.ict.ICTControllerListener;

public class ICTPredictUsersStats extends Component implements ICTControllerListener {
	private static final long serialVersionUID = -8666907288749625379L;
	/**
	 * Padding to make it look pretty.
	 */
	private static final int PADDING = 5;
	/**
	 * The height of the user bar as soon as more than zero clients are connected.
	 */
	private static final int BARHEIGHT = 80;
	/**
	 * The width of the bars.
	 */
	private static final int BARWIDTH = 50;
	/**
	 * Half of the space between the two bars.
	 */
	private static final int HALFMIDDLEPADDING = 2;
	
	/**
	 * The total height.
	 */
	private static final int HEIGHT = BARHEIGHT * 2 + 5 * PADDING;
	/**
	 * The total width.
	 */
	private static final int WIDTH = 2 * PADDING + 2 * HALFMIDDLEPADDING + 2 * BARWIDTH;
	/**
	 * A factor to make the colours prettier.
	 */
	private static final float DARKENING_FACTOR = 0.9f;
	/**
	 * The UserStats' size.
	 */
	private Dimension size = null;
	/**
	 * The jeliot main object.
	 */
	private Jeliot jeliot = null;

	/**
	 * Constructor.
	 * @param jeliot The jeliot main object.
	 */
	public ICTPredictUsersStats(Jeliot jeliot) {
		this.jeliot  = jeliot;
	}
	
	/**
	 * Sets the size to the correct size.
	 */
	void init() {
		this.size = new Dimension(WIDTH, (int) (HEIGHT + getFontMetrics(getFont()).getHeight() * 1.2));
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
	@Override
	public Dimension preferredSize() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#minimumSize()
	 */
	@Override
	public Dimension minimumSize() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		int connectedClients = Math.max(this.jeliot.getIctController().getUserCount() - 1, 0);
		int receivedAnswers = this.jeliot.getIctController().getReceivedAnswerCount();
		Font f = getFont();
		FontMetrics fm = getFontMetrics (f);
		int fontWidthConnectedClients = (int) fm.getStringBounds(Integer.toString(connectedClients), g).getWidth();
		g.setColor(Color.BLUE.brighter());
		int height = 1;
		int offset = BARHEIGHT - 1;
		if (connectedClients != 0) {
			height = BARHEIGHT;
			offset = 0;
		}
		g.fillRect(PADDING, PADDING + offset, BARWIDTH, height);
		g.setColor(Color.BLACK);
		if (connectedClients != 0) {
			g.setColor(Color.WHITE);
		}
		g.drawString(Integer.toString(connectedClients), PADDING + BARWIDTH / 2 - fontWidthConnectedClients / 2, BARHEIGHT + PADDING - fm.getHeight());
		
		float percentageOfReceivedAnswers = (float) 0.0;
		if (connectedClients != 0) {
			percentageOfReceivedAnswers = (float)receivedAnswers / (float)connectedClients;
		}
		float cr = 0;
		float cg = 0;

		if (percentageOfReceivedAnswers <= 0.5) {
			cr = 1;
			cg = percentageOfReceivedAnswers * 2;
		} else {
			cr = (1 - percentageOfReceivedAnswers) * 2;
			cg = 1;
		}
		float cb = 0;
		g.setColor(new Color(cr * DARKENING_FACTOR, cg * DARKENING_FACTOR, cb * DARKENING_FACTOR));
		int heightOfConnectedBar = 1; 
		if (connectedClients != 0) {
			heightOfConnectedBar = Math.max(BARHEIGHT * receivedAnswers / connectedClients, 1);
		}
		g.fillRect(PADDING + BARWIDTH + 2 * HALFMIDDLEPADDING,
				PADDING + BARHEIGHT - heightOfConnectedBar,
				BARWIDTH,
				heightOfConnectedBar);
		
		g.setColor(Color.BLACK);
		g.drawString("#", 0, PADDING + BARHEIGHT + fm.getHeight());
		g.drawString("#", BARWIDTH + fm.charWidth('#') + 2 * HALFMIDDLEPADDING, PADDING + BARHEIGHT + fm.getHeight());

		Font f2 = getFont().deriveFont((float)(f.getSize() / 1.25));
		g.setFont(f2);
		g.drawString("connected", fm.charWidth('#'), (int) (PADDING * 2 + BARHEIGHT + fm.getHeight() * 1.1));
		g.drawString("answered", fm.charWidth('#') * 2 + BARWIDTH + 2 * HALFMIDDLEPADDING, (int) (PADDING * 2 + BARHEIGHT + fm.getHeight() * 1.1));
		g.setFont(f);
		
		int fontWidthReceivedAnswers = (int) fm.getStringBounds(Integer.toString(receivedAnswers), g).getWidth();
		if (percentageOfReceivedAnswers < 0.35) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.WHITE);
		}
		g.drawString(Integer.toString(receivedAnswers), PADDING + BARWIDTH + HALFMIDDLEPADDING * 2 + BARWIDTH / 2 - fontWidthReceivedAnswers / 2, BARHEIGHT + PADDING - fm.getHeight());
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onUserCountChanged(jeliot.ict.ICTController)
	 */
	@Override
	public void onUserCountChanged(ICTController ictController) {
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onAnswerCountChanged(jeliot.ict.ICTController)
	 */
	@Override
	public void onAnswerCountChanged(ICTController ictController) {
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onNewMethod(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onNewMethod(ICTController ictController, Method method) {
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onResultPosted(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onResultPosted(ICTController ictController, Method method) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onMethodCalled(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodCalled(ICTController ictController, Method method) {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onMethodReturned(jeliot.ict.ICTController, org.mJeliot.model.predict.Method)
	 */
	@Override
	public void onMethodReturned(ICTController ictController, Method method) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onClientConnected(jeliot.ict.ICTController)
	 */
	@Override
	public void onClientConnected(ICTController ictController) {
	}

	/* (non-Javadoc)
	 * @see jeliot.ict.ICTControllerListener#onClientDisconnected(jeliot.ict.ICTController)
	 */
	@Override
	public void onClientDisconnected(ICTController ictController) {
	}

	@Override
	public void onNewLecture(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLogin(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLoggedIn(ICTController ictController, Lecture currentLecture) {
	}

	@Override
	public void onLogout(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLectureUpdated(ICTController ictController, Lecture lecture) {
	}

	@Override
	public void onLoggedOut(ICTController ictController, Lecture lecture) {
	}
}
