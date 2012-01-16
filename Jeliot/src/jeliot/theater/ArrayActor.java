package jeliot.theater;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ResourceBundle;

import jeliot.util.ResourceBundles;

/**
 * Array Actor represents the array instance and
 * contains VariableInArrayActors for every index
 * of the array. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.lang.ArrayInstance
 * @see jeliot.lang.VariableInArray
 * @see jeliot.theater.VariableInArrayActor
 */
public class ArrayActor extends InstanceActor {

    /**
     * The resource bundle for theater package
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getTheaterMessageResourceBundle();

    //DOC: Document!

    /**
     *
     */
    private String emptyArray1 = messageBundle.getString("string.empty_array1");

    /**
     *
     */
    private String emptyArray2 = messageBundle.getString("string.empty_array2");

    /**
     *
     */
    private VariableInArrayActor[] variableActors;

    /**
     * 
     */
    private VariableActor arrayLength;

    /**
     *
     */
    private Color valueColor;

    /**
     * The x-coordinate of the vertical line separating indices from
     * values.
     */
    private int vlinex;

    /**
     * The width of a cell reserved for a single value actor.
     */
    private int valuew;

    /**
     * The height of a single value actor.
     */
    private int valueh;

    /**
     * The width of an index label.
     */
    private int indexw;

    //TODO: visualize lenght as a field in an array
    /**
     *
     */
    private int length;

    /**
     * 
     */
    private boolean primitive;

    /**
     * 
     */
    private String componentType;

    /**
     * @param valueActors
     * @param dimensions
     */
    public ArrayActor(ValueActor[] valueActors, int length, boolean primitive,
            String compType, VariableActor lengthVariable) {

        this.length = length;
        this.arrayLength = lengthVariable;
        this.arrayLength.setParent(this);
        this.primitive = primitive;
        this.componentType = compType;
        if (primitive) {
            this.variableActors = new VariableInArrayActor[length];
        } else {
            this.variableActors = new ReferenceVariableInArrayActor[length];
        }

        for (int i = 0; i < length; i++) {
            if (primitive) {
                VariableInArrayActor viaa = new VariableInArrayActor(this, "["
                        + i + "]");
                ValueActor va = (ValueActor) valueActors[i];
                viaa.setValue(va);
                viaa.setParent(this);
                variableActors[i] = viaa;
            } else {
                ReferenceVariableInArrayActor viaa = new ReferenceVariableInArrayActor(
                        this, "[" + i + "]");
                ValueActor va = (ValueActor) valueActors[i];
                if (va instanceof ReferenceActor) {
                    viaa.setValue((ReferenceActor) va);
                } else {
                    throw new RuntimeException(
                            "Reference Variable in array needs to be initialized with reference.");
                }
                viaa.setParent(this);
                variableActors[i] = viaa;
            }
        }
        setDescription("array: type: " + compType);
    }

    /**
     * @param valueColor
     */
    public void setValueColor(Color valueColor) {
        for (int i = 0; i < length; i++) {
            VariableInArrayActor viaa = (VariableInArrayActor) variableActors[i];
            viaa.setValueColor(valueColor);
        }
    }

    /**
     * @param index
     * @return
     */
    public VariableActor getVariableActor(int index) {
        return (VariableActor) variableActors[index];
    }

    /**
     * @param valuew
     * @param valueh
     */
    public void calculateSize(int valuew, int valueh) {

        FontMetrics fm = getFontMetrics();
        this.valuew = valuew;
        this.valueh = valueh;
        this.indexw = fm.stringWidth(messageBundle
                .getString("string.array_index"));

        if (length == 0) {
            int w = Math.max(4 + this.arrayLength.getWidth(), indexw
                    + Math.max(fm.stringWidth(emptyArray1), fm
                            .stringWidth(emptyArray2)));
            int h = this.arrayLength.getHeight() + 10 + 2 * (fm.getHeight());
            setSize(w, h);
        } else {
            int n = length;
            int w = Math.max(4 + this.arrayLength.getWidth(), 6 + valuew
                    + indexw);
            int h = this.arrayLength.getHeight() + 3 + (valueh + 1) * n;
            setSize(w, h);
            
            this.valuew = valuew = w - 6 - indexw;
            
            int x = 2;
            int y = 2 + this.arrayLength.getHeight();
            for (int i = 0; i < n; ++i) {
                VariableInArrayActor viaa = (VariableInArrayActor) variableActors[i];
                viaa.setSize(valuew, valueh);
                viaa.setLocation(x, y);
                viaa.calculateSize(indexw, valuew, valueh);
                y += 1 + valueh;
            }
        }
        this.arrayLength.setLocation(2, 2);
    }

    /* (non-Javadoc)
     * @see jeliot.theater.Actor#paintActor(java.awt.Graphics)
     */
    public void paintActor(Graphics g) {

        int w = this.width;
        int h = this.height;
        int bw = 2;
        FontMetrics fm = getFontMetrics();
        int fonth = fm.getHeight();
        int word1w = fm.stringWidth(emptyArray1);
        int word2w = fm.stringWidth(emptyArray2);
        int lengthActW = this.arrayLength.getWidth();
        int lengthActH = this.arrayLength.getHeight();

        int n = length;

        if (n == 0) {

            int word1x = (w - word1w) / 2;
            int word2x = (w - word2w) / 2;

            int word1y = (h - lengthActH) / 2 - fonth / 2 + lengthActH + 5;
            int word2y = (h - lengthActH) / 2 + fonth / 2 + lengthActH + 5;

            // fill the area
            g.setColor(lightColor);
            g.fillRect(0, 0, w - 1, h - 1);

            // draw border
            g.setColor(borderColor);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(darkColor);
            g.drawRect(1, 1, w - 3, h - 3);

            int actX = this.arrayLength.getX();
            int actY = this.arrayLength.getY();
            g.translate(actX, actY);
            this.arrayLength.paintActor(g);
            g.translate(-actX, -actY);

            g.setColor(fgcolor);
            g.setFont(font);
            g.drawString(emptyArray1, word1x, word1y);
            g.drawString(emptyArray2, word2x, word2y);

        } else {

            // draw border
            g.setColor(borderColor);
            g.drawRect(0, 0, w - 1, h - 1);
            g.setColor(darkColor);
            g.drawRect(1, 1, w - 3, h - 3);

            // draw vertical line
            int vlinex = 2 + indexw;
            g.drawLine(vlinex, bw + lengthActH, vlinex, h - 2);
            g.drawLine(vlinex + 1, bw + lengthActH, vlinex + 1, h - 2);

            // draw horizontal lines
            int x1 = bw, x2 = w - 2 * bw;
            int yc = bw - 1 + lengthActH;
            for (int i = 1; i < n; ++i) {
                yc += 1 + valueh;
                g.drawLine(x1, yc, x2, yc);
            }

            int actX = this.arrayLength.getX();
            int actY = this.arrayLength.getY();
            g.translate(actX, actY);
            this.arrayLength.paintActor(g);
            g.translate(-actX, -actY);

            // draw cells
            for (int i = 0; i < n; ++i) {

                VariableInArrayActor a = (VariableInArrayActor) variableActors[i];

                int x = a.getX();
                int y = a.getY();
                g.translate(x, y);
                a.paintActor(g);
                g.translate(-x, -y);
            }

        }
    }

    public String toString() {
        return "Array of type " + componentType;
    }

    public VariableActor getArrayLength() {
        return arrayLength;
    }

    public void setArrayLength(VariableActor arrayLength) {
        this.arrayLength = arrayLength;
    }
}