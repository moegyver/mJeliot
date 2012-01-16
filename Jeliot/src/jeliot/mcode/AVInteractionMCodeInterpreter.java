/*
 * Created on 7.3.2006
 */
package jeliot.mcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import jeliot.adapt.UMInteraction;
import jeliot.avinteraction.AVInteractionEngine;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;

/**
 * @author nmyller
 */
public class AVInteractionMCodeInterpreter extends MCodeInterpreter implements
        MCodePreProcessor {

    /**
     * 
     */
    private static ResourceBundle questionsResources = ResourceBundles
            .getAvInteractionResourceBundle();

    /**
     * 
     */
    private AVInteractionEngine engine;

    /**
     * Contains Vector objects that keep the record of used concepts.
     * Concepts are currently recorded and int values from the Code class.
     */
    private Map conceptVectors = new HashMap();

    /**
     * User Model used for this user
     */
    private UMInteraction userModel;

    private PrintWriter writerForMCodeOutput;

    private BufferedReader readerForMCodeOutput;

    private PrintWriter writerForMCodeInput;

    private BufferedReader readerForMCodeInput;

    /**
     * 
     */
    public AVInteractionMCodeInterpreter(BufferedReader bf,
            PrintWriter mCodeInputWriter, AVInteractionEngine engine,
            UMInteraction userModel) {
        super(bf);
        //this.mcode = bf;
        this.engine = engine;
        this.userModel = userModel;
        this.readerForMCodeInput = bf;
        this.writerForMCodeInput = mCodeInputWriter;

        PipedReader pr = new PipedReader();
        PipedWriter pw;
        try {
            pw = new PipedWriter(pr);
            this.readerForMCodeOutput = new BufferedReader(pr);
            this.writerForMCodeOutput = new PrintWriter(pw, true);
        } catch (IOException e) {
            DebugUtil.handleThrowable(e);
        }
    }

    /**
     * 
     * @param concept
     */
    public void addConcept(int concept) {
        for (Iterator i = this.conceptVectors.entrySet().iterator(); i
                .hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            if (e.getValue() != null) {
                ((Vector) e.getValue()).add(new Integer(concept));
            }
        }
    }

    public Integer[] removeConceptVector(long expressionId) {
        Vector v = (Vector) this.conceptVectors.remove(new Long(expressionId));
        if (v != null) {
            return (Integer[]) v.toArray(new Integer[0]);
        }
        return new Integer[0];
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#showErrorMessage(jeliot.mcode.InterpreterError)
     */
    public void showErrorMessage(InterpreterError error) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#readLine()
     */
    public String readLine() {
        try {
            return mcode.readLine();
        } catch (Exception e) {
            return "" + Code.ERROR + Code.DELIM
                    + messageBundle.getString("unknown.exception") + Code.DELIM
                    + "0" + Code.LOC_DELIM + "0" + Code.LOC_DELIM + "0"
                    + Code.LOC_DELIM + "0";
        }
    }

    private int randomInteger() {
        double d = Math.random();
        if (d > 0.5) {
            return (int) ((d - 0.2) * 5);
        } else {
            return (int) ((-1 + (d + 0.2)) * 5);
        }
    }

    private static final DecimalFormat NO_DECIMALS = new DecimalFormat("#0");

    private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("0.0");

    private float randomFloat() {
        double d = Math.random();
        float result = 0;
        if (d > 0.5) {
            result = (float) (d - 0.2) * 5;
        } else {
            result = (float) (-1 - (d + 0.2)) * 5;
        }
        return Float.parseFloat((Math.random() > 0.5) ? NO_DECIMALS
                .format(result) : ONE_DECIMAL.format(result));
    }

    private boolean isInArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && array[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat();

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBEGIN(long, long, java.lang.String)
     */
    protected void handleCodeBEGIN(long expressionType,
            long expressionReference, String location) {
        //Initialize the concept vector if necessary.
        if (expressionType == Code.A) {
            this.conceptVectors
                    .put(new Long(expressionReference), new Vector());
        }
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeA(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeA(long expressionCounter, long fromExpression,
            long toExpression, String value, String type, Highlight h) {

      
        //add concept to all concept vectors that are currently in the conceptVectors Map.
        addConcept(Code.A);
        if (MCodeUtilities.isPrimitive(type)) {
            String question = questionsResources
                    .getString("avinteraction.assignment.question");
            String id = "" + expressionCounter;
            String[] answers = new String[4];
            String[] comments = new String[4];
            int[] correctAnswers = new int[1];

            //Depending on the type of the variable generate different kinds of questions and answers.
            if (type.equals(boolean.class.getName())
                    || type.equals(Boolean.class.getName())) {
                boolean correct = Boolean.valueOf(value).booleanValue();
                Integer[] concepts = removeConceptVector(expressionCounter);
                engine
                        .addTFQuestion(
                                id,
                                id,
                                question,
                                correct,
                                1,
                                MessageFormat
                                        .format(
                                                questionsResources
                                                        .getString("avinteraction.assignment.boolean.general_reply"),
                                                new Object[] { value }),
                                concepts);
                return;
            } else if (type.equals(byte.class.getName())
                    || type.equals(Byte.class.getName())) {
                int v = Byte.parseByte(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomInteger());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Integer(v) });
                        i++;
                    }
                }
            } else if (type.equals(short.class.getName())
                    || type.equals(Short.class.getName())) {
                int v = Short.parseShort(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomInteger());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Integer(v) });
                        i++;
                    }
                }

            } else if (type.equals(int.class.getName())
                    || type.equals(Integer.class.getName())) {
                int v = Integer.parseInt(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomInteger());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Integer(v) });
                        i++;
                    }
                }
            } else if (type.equals(long.class.getName())
                    || type.equals(Long.class.getName())) {
                long v = Long.parseLong(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomInteger());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Long(v) });
                        i++;
                    }
                }

            } else if (type.equals(char.class.getName())
                    || type.equals(Character.class.getName())) {
                char v = value.charAt(0);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (char) (v + randomInteger());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Character(v) });
                        i++;
                    }
                }

            } else if (type.equals(float.class.getName())
                    || type.equals(Float.class.getName())) {
                float v = Float.parseFloat(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomFloat());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Double(v) });
                        i++;
                    }
                }

            } else if (type.equals(double.class.getName())
                    || type.equals(Double.class.getName())) {
                double v = Double.parseDouble(value);
                correctAnswers[0] = 1;
                answers[0] = "" + v;
                comments[0] = questionsResources
                        .getString("avinteraction.assignment.correct_answer_reply");
                for (int i = 1; i < answers.length;) {
                    String newAnswer = "" + (v + randomFloat());
                    if (!isInArray(answers, newAnswer)) {
                        answers[i] = newAnswer;
                        comments[i] = MessageFormat
                                .format(
                                        questionsResources
                                                .getString("avinteraction.assignment.incorrect_answer_reply"),
                                        new Object[] { new Double(v) });
                        i++;
                    }
                }

            } else if (type.equals(String.class.getName())
                    || type.equals("L" + String.class.getName() + ";")) {
                Integer[] concepts = removeConceptVector(expressionCounter);
                engine
                        .addFIBQuestion(
                                id,
                                id,
                                question,
                                value,
                                1,
                                MessageFormat
                                        .format(
                                                questionsResources
                                                        .getString("avinteraction.assignment.boolean.general_reply"),
                                                new Object[] { value }),
                                concepts);
                return;
            }
            Integer[] concepts = removeConceptVector(expressionCounter);
            engine.addMCQuestion(id, id, question, answers, new int[0], 1,
                    comments, correctAnswers, concepts);
        }

        //Remove concept vector for the current expression identifier if it is found.
        removeConceptVector(expressionCounter);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeQN(long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeQN(long expressionCounter, String variableName,
            String value, String type, Highlight highlight) {
        addConcept(Code.QN);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeL(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeL(long expressionCounter, String value,
            String type, Highlight highlight) {
        addConcept(Code.L);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#openScratch()
     */
    public void openScratch() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#closeScratch()
     */
    public void closeScratch() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#beforeExecution()
     */
    public void beforeExecution() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAIBEGIN(long, jeliot.mcode.Highlight)
     */
    protected void handleCodeAIBEGIN(long cells, Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAIE(java.lang.String, long, long, java.lang.String, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeAIE(String arrayReference, long cellNumber,
            long expressionReference, String value, String type, long literal,
            Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAI(jeliot.mcode.Highlight)
     */
    protected void handleCodeAI(Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSFA(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeSFA(long expressionCounter, String declaringClass,
            String variableName, String value, String type, int modifiers,
            Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCAST(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeCAST(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#beforeInterpretation(java.lang.String)
     */
    protected void beforeInterpretation(String line) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLQE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLQE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeNE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeNE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeERROR(java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeERROR(String message, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeFIELD(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
     */
    protected void handleCodeFIELD(String name, String type, int modifiers,
            String value, String h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMETHOD(java.lang.String, java.lang.String, int, java.lang.String)
     */
    protected void handleCodeMETHOD(String name, String returnType,
            int modifiers, String listOfParameters) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSTRUCTOR(java.lang.String)
     */
    protected void handleCodeCONSTRUCTOR(String listOfParameters) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeEND_CLASS()
     */
    protected void handleCodeEND_CLASS() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCLASS(java.lang.String, java.lang.String)
     */
    protected void handleCodeCLASS(String name, String extendedClass) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAL(long, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAL(long expressionCounter, long arrayCounter,
            String name, String value, String type, Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAAC(long, long, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAAC(long expressionCounter,
            long expressionReference, int dims, String cellNumberReferences,
            String cellNumbers, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAA(long, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, int, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAA(long expressionReference, String hashCode,
            String compType, int dims, String dimensionReferences,
            String dimensionSizes, int actualdimensions,
            String subArraysHashCodes, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSCOPE(int)
     */
    protected void handleCodeSCOPE(int scope) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUTTED(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeINPUTTED(long expressionCounter, String value,
            String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUT(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeINPUT(long expressionCounter, String className,
            String methodName, String type, String prompt, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOUTPUT(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, jeliot.mcode.Highlight)
     */
    protected void handleCodeOUTPUT(long expressionReference, String className,
            String methodName, String value, String type, boolean breakLine,
            Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONT(int, jeliot.mcode.Highlight)
     */
    protected void handleCodeCONT(int statementName, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBR(int, jeliot.mcode.Highlight)
     */
    protected void handleCodeBR(int statementName, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCH(jeliot.mcode.Highlight)
     */
    protected void handleCodeSWITCH(Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWIBF(long, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeSWIBF(long selectorReference,
            long switchBlockReference, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCHB(jeliot.mcode.Highlight)
     */
    protected void handleCodeSWITCHB(Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeDO(long, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeDO(long expressionReference, String value,
            long round, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeFOR(long, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeFOR(long expressionReference, String value,
            long round, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeWHI(long, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeWHI(long expressionReference, String value,
            int round, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeIFTE(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeIFTE(long expressionReference, String value,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeIFT(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeIFT(long expressionReference, String value,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSMCC()
     */
    protected void handleCodeSMCC() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeR(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeR(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePARAMETERS(java.lang.String)
     */
    protected void handleCodePARAMETERS(String parameters) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMD(jeliot.mcode.Highlight)
     */
    protected void handleCodeMD(Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeP(long, java.lang.String, java.lang.String)
     */
    protected void handleCodeP(long expressionReference, String value,
            String argType) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSMC(java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeSMC(String methodName, String className,
            int parameterCount, Highlight h) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOMCC()
     */
    protected void handleCodeOMCC() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOMC(java.lang.String, int, long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeOMC(String methodName, int parameterCount,
            long objectCounter, String objectValue, Highlight highlight) {

        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOFA(long, long, java.lang.String, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeOFA(long expressionCounter, long objectCounter,
            String variableName, String value, String type, int modifiers,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSAC(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeSAC(long expressionCounter, String hashCode,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSCN(long)
     */
    protected void handleCodeCONSCN(long superMethodCallNumber) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSA(long, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeSA(long expressionCounter, String declaringClass,
            String constructorName, int parameterCount, Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeVD(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeVD(String variableName,
            long initializerExpression, String value, String type,
            String modifier, Highlight highlight) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeSE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeDE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeDE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeRE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeME(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeME(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeGQT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeGQT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeGT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeGT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeEE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeEE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAND(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeXOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeURSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeURSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeRSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITAND(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITXOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePRDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePRDE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePRIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePRIE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePDE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePIE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeNO(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeNO(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMINUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeMINUS(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePLUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePLUS(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCOMP(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeCOMP(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeTO(long)
     */
    protected void handleCodeTO(long expressionReference) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRIGHT(long)
     */
    protected void handleCodeRIGHT(long token1) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLEFT(long)
     */
    protected void handleCodeLEFT(long token1) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#endRunning()
     */
    protected void endRunning() {
        this.writerForMCodeInput.close();
        this.writerForMCodeOutput.close();
        try {
            this.readerForMCodeInput.close();
        } catch (IOException e) {
            DebugUtil.handleThrowable(e);
        }
        try {
            this.readerForMCodeOutput.close();
        } catch (IOException e) {
            DebugUtil.handleThrowable(e);
        }
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#emptyScratch()
     */
    public boolean emptyScratch() {
        return false;
    }

    public void afterInterpretation(String line) {
        this.writerForMCodeOutput.println(line);
        this.writerForMCodeOutput.flush();
    }

    public void closeMCodeOutputReader() {
        this.writerForMCodeOutput.flush();
        this.writerForMCodeOutput.close();
        try {
            this.readerForMCodeOutput.close();
        } catch (IOException e) {
            DebugUtil.handleThrowable(e);
        }
    }

    public PrintWriter getMCodeInputWriter() {
        return this.writerForMCodeInput;
    }

    public BufferedReader getMCodeOutputReader() {
        return this.readerForMCodeOutput;
    }

}
