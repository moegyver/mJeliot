/*
 * Created on 22.2.2006
 */
package jeliot.mcode;

import java.util.Iterator;
import java.util.List;

import koala.dynamicjava.interpreter.EvaluationVisitor;
import koala.dynamicjava.interpreter.NodeProperties;
import koala.dynamicjava.tree.Node;

/**
 * Utility class to generate some MCode commands
 * 
 * @author nmyller
 */
public class MCodeGenerator {

    private MCodeGenerator() {
    }

    public static void generateCodeA(long assigncounter, long from, long to,
            Object result, Node node) {
        generateCodeA(assigncounter, from, to, MCodeUtilities.getValue(result),
                NodeProperties.getType(node).getName(), MCodeGenerator
                        .locationToString(node));
    }

    public static void generateCodeA(long assigncounter, long from, long to,
            String result, String className, String location) {
        MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
                + Code.DELIM + from + Code.DELIM + to + Code.DELIM + result
                + Code.DELIM + className + Code.DELIM + location);
    }

    /**
     * Converts the node location into a string list. Each element is delimited
     * by Code.LOC_DELIM
     * @param node the node to visit
     */
    public static String locationToString(Node node) {
        return node.getBeginLine() + Code.LOC_DELIM + node.getBeginColumn()
                + Code.LOC_DELIM + node.getEndLine() + Code.LOC_DELIM
                + node.getEndColumn();
        //+ Code.DELIM + node.getFilename();
    }

    /**
     * @param visitor
     * @param robj
     * @return
     */
    public static String toStringCall(Node expression, EvaluationVisitor visitor) {

        Long l = new Long(EvaluationVisitor.getCounter());
        EvaluationVisitor.returnExpressionCounterStack.push(l);
        EvaluationVisitor.incrementCounter();
        Class[] typs;
        long counter = EvaluationVisitor.getCounter();

        Object obj = expression.acceptVisitor(visitor);

        MCodeUtilities.startToString();

        MCodeUtilities.write("" + Code.OMC + Code.DELIM + "toString"
                + Code.DELIM + "0" + Code.DELIM + counter + Code.DELIM
                + (obj != null ? obj.getClass().getName() : Object.class.getName()) + Code.DELIM + "0,0,0,0");

        
        String result = String.valueOf(obj); 

        if (!MCodeUtilities.isToStringOverloaded()) {
            //fake everything
            MCodeUtilities.write(Code.PARAMETERS + Code.DELIM + "");
            MCodeUtilities.write(Code.MD + Code.DELIM + "0,0,0,0");

            long auxCounter = EvaluationVisitor.getCounter();

            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.R
                    + Code.DELIM + l.toString() + Code.DELIM
                    + locationToString(expression));

            MCodeUtilities.write(Code.L + Code.DELIM + auxCounter + Code.DELIM
                    + MCodeUtilities.getValue(result) + Code.DELIM + String.class.getName() + Code.DELIM
                    + "0,0,0,0");
            EvaluationVisitor.incrementCounter();
            MCodeUtilities.write("" + Code.R + Code.DELIM
                    + EvaluationVisitor.returnExpressionCounterStack.pop()
                    + Code.DELIM + auxCounter + Code.DELIM + MCodeUtilities.getValue(result)
                    + Code.DELIM + String.class.getName() + Code.DELIM
                    + "0,0,0,0");
        } else {
            EvaluationVisitor.returnExpressionCounterStack.pop();
            EvaluationVisitor.unsetInside();
        }

        MCodeUtilities.endToString();
        MCodeUtilities.write("" + Code.OMCC);

        return result;
    }

    /**
     * @param array
     * @return
     */
    public static String arrayToString(Object[] array) {
        //Displays the array as an string
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += array[i];
            if (i < array.length - 1)
                result += Code.LOC_DELIM;
        }
        return result;
    }

    /**
     * @param array
     * @return
     */
    public static String parameterArrayToString(Object[] array) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += ((Class) array[i]).getName();
            if (i < array.length - 1)
                result += Code.LOC_DELIM;
        }

        return result;
    }

    /**
     * @param argnames
     * @return
     */
    public static String argToString(List argnames) {
        //Change to extract elements from list and add delims

        if (!argnames.isEmpty()) {
            String result = "";
            Iterator it = argnames.listIterator();
            while (it.hasNext()) {
                result += (String) it.next() + Code.LOC_DELIM;
            }
            return result.substring(0, result.length() - 1);
        }
        return "";
    }

}
