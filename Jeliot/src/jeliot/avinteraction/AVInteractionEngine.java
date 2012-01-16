/*
 * Created on 7.3.2006
 */
package jeliot.avinteraction;

import javax.swing.JFrame;

import jeliot.adapt.UMInteraction;
import jeliot.util.DebugUtil;
import avinteraction.FIBQuestion;
import avinteraction.HTMLDocumentation;
import avinteraction.InteractionModule;
import avinteraction.MCQuestion;
import avinteraction.TFQuestion;
import avinteraction.UnknownInteractionException;

import java.awt.*;

public class AVInteractionEngine {

    /** Backend to use */
    protected PointMachine backend;

    /** Instance of the interaction module */
    protected InteractionModule testModule;

    /**
     * 
     */
    protected JFrame parent;

    /**
     * 
     * @throws Exception
     */
    public AVInteractionEngine(JFrame parent, UMInteraction userModel) throws Exception {
        super();
        this.parent = parent;
        backend = new PointMachine(userModel);
        testModule = new InteractionModule(backend, parent, DebugUtil.DEBUGGING);
        //testModule.addWindowListener(this);
    }

    public void showResults(Component component) {
        backend.showResults(component);
    }

    /**
     * 
     * @param interactionID
     */
    public void interaction(String interactionID) {
        try {
            testModule.interaction(interactionID);
        } catch (UnknownInteractionException e) {
            if (DebugUtil.DEBUGGING) {
                System.out.println("Didn't find an interaction");
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param id
     * @param url
     */
    public void addHTMLDocument(String id, String url) {
        HTMLDocumentation doc = new HTMLDocumentation(id, DebugUtil.DEBUGGING);
        doc.setURL(url);
        testModule.addInteractionObject(id, doc);
    }

    /**
     * 
     * @param id
     * @param groupId
     * @param questionText
     * @param answer
     * @param points
     * @param comment
     */
    public void addTFQuestion(String id, String groupId, String questionText,
            boolean answer, int points, String comment, Integer[] concept) {
        // create a TFQuestion object
        TFQuestion question = new TFQuestion(id, groupId, DebugUtil.DEBUGGING, concept);
        question.setQuestion(questionText);
        question.setAnswer(answer);
        question.setPoints(points);
        question.setComment(comment);

        // add it to the interactions
        testModule.addInteractionObject(id, question);
    }

    public void addFIBQuestion(String id, String groupId, String questionText,
            String answer, int points, String comment, Integer[] concepts) {
        FIBQuestion question = new FIBQuestion(id, groupId, DebugUtil.DEBUGGING, concepts);
        question.setQuestion(questionText);
        question.addAnswer(answer);
        question.setPoints(points);
        question.setComment(comment);
        
        // add it to the interactions
        testModule.addInteractionObject(id, question);        
    }
    
    /**
     * 
     * @param interactionID
     */
    public void removeInteractionObject(String interactionID) {
        testModule.removeInteractionObject(interactionID);
    }

    /**
     * 
     * @return
     */
    public PointMachine getBackend() {
        return backend;
    }

    /**
     * 
     * @param backend
     */
    public void setBackend(PointMachine backend) {
        this.backend = backend;
    }

    /**
     * 
     * @return
     */
    public JFrame getParent() {
        return parent;
    }

    /**
     * 
     * @param parent
     */
    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    /**
     * 
     * @param id
     * @param groupId
     * @param questionText
     * @param answer
     * @param subpoints
     * @param overallPoints
     * @param comments
     * @param correctAnswers
     */
    public void addMCQuestion(String id, String groupId, String questionText, String[] answer, int[] subpoints, int overallPoints, String[] comments, int[] correctAnswers, Integer[] concepts) {
        boolean severalAnswers = correctAnswers.length > 1;
        MCQuestion question = new MCQuestion(id, groupId, DebugUtil.DEBUGGING, concepts);
        question.setQuestion(questionText);

        for (int i = 0; i < answer.length; i++) {
            if (subpoints != null && i < subpoints.length) {
                question.addPossibleAnswer(answer[i], subpoints[i], comments[i]);
            } else {
                question.addPossibleAnswer(answer[i], 0, comments[i]);                
            }
        }
        
        for (int i = 0; i < correctAnswers.length; i++) {
            question.addCorrectAnswer(correctAnswers[i]);
        }
        question.randomize();
        question.setPoints(overallPoints);
        question.useCheckBoxes(severalAnswers);

        // add it to the interactions
        testModule.addInteractionObject(id, question);
    }
}
