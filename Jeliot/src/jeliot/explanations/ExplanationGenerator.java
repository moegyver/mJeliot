/*
 * Created on 5.7.2006
 */
package jeliot.explanations;

import jeliot.mcode.Highlight;


/**
 * @author nmyller
 */
public class ExplanationGenerator {

    /**
     * 
     */
    private static ExplanationGenerator explanationGenerator = null;
    
    /**
     * 
     */
    private ExplanationGenerator() {
        super();
    }
    
    /**
     * 
     * @return
     */
    public static synchronized ExplanationGenerator getInstance() {
        if( explanationGenerator == null)
            explanationGenerator = new ExplanationGenerator();
        return explanationGenerator;
    }
    
    /**
     * 
     * @param text
     * @param h
     */
    public void addExplanation(String text, Highlight h) {
        System.out.println(text + " " + (h != null?h.toString():""));
    }
    
    /**
     * 
     * @param variableName
     * @param assignedValue
     * @param h
     */
    public void addAssignmentExplanation(String variableName, String assignedValue, Highlight h) {
        addExplanation(assignedValue + " is assigned to " + variableName, h);
    }
} 
