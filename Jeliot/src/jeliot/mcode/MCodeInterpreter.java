package jeliot.mcode;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import jeliot.FeatureNotImplementedException;
import jeliot.lang.LocalVariableNotFoundException;
import jeliot.lang.StaticVariableNotFoundException;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * The MCode interpreter that interprets the MCode received
 * during the intepretation in DynamicJava.
 * 
 * @author Niko Myller
 * @author Andr�s Moreno
 */
public abstract class MCodeInterpreter {

    //  DOC: document!

    /**
     *  
     */
    protected BufferedReader mcode = null;

    /**
     *  
     */
    protected PrintWriter input = null;

    /**
     *  
     */
    protected String programCode = "";

    /**
     *  
     */
    protected boolean running = true;

    /**
     *  
     */
    protected boolean start = true;

    /**
     *  
     */
    protected boolean firstLineRead = false;

    /**
     *  
     */
    protected boolean readNew = true;

    /**
     *  
     */
    protected String line = null;

    /**
     *  
     */
    protected Stack constructorCalls = new Stack();

    /**
     *  
     */
    protected Vector superMethods = null;

    /**
     * The resource bundle for mcode messages
     */
    static protected ResourceBundle messageBundle = ResourceBundles
            .getMCodeMessageResourceBundle();

    /**
     * The resource bundle for mcode properties
     */
    static protected UserProperties propertiesBundle = ResourceBundles
            .getMCodeUserProperties();

    /**
     * Related to Super method calls in constructor's first line in
     * classes with inheritance.
     */
    protected boolean constructorCall = false;

    /**
     *  
     */
    protected Vector superMethodsReading = null;

    /**
     *  
     */
    protected long superMethodCallNumber = 0;

    /**
     *  
     */
    protected MCodeInterpreter() {
    }

    /**
     *  
     */
    protected MCodeInterpreter(BufferedReader br) {
        this.mcode = br;
    }

    /**
     * Initializes the
     */
    public void initialize() {
        running = true;
        start = true;
    }

    public abstract void showErrorMessage(InterpreterError error);

    /**
     * @return
     */
    public boolean starting() {
        //return start;
        return false;
    }

    /**
     * @return
     */
    public boolean readNew() {
        return constructorCalls.empty();
    }

    /**
     * 
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    public abstract String readLine();

    public abstract void openScratch();

    public abstract void closeScratch();

    public abstract void beforeExecution();

    /**
     *  
     */
    public boolean execute() {

        try {

            beforeExecution();

            openScratch();

            while (running) {

                if (!constructorCall) {
                    if (!firstLineRead) {
                        line = readLine();
                        interpret(line);
                    } else {
                        firstLineRead = false;
                        interpret(line);
                    }
                    //Constructor call is going in super method calls.
                } else {
                    String storedLine = readLine();

                    StringTokenizer tokenizer = new StringTokenizer(storedLine,
                            Code.DELIM);
                    int token = Integer.parseInt(tokenizer.nextToken());
                    if (token == Code.INPUT) {
                        interpret(""
                                + Code.ERROR
                                + Code.DELIM
                                + messageBundle
                                        .getString("inputInConstructor.exception")
                                // + "<H1>Feature not implemented</H1> " + "<P>Super classes' constructors cannot "
                                + Code.DELIM + "0" + Code.LOC_DELIM + "0"
                                + Code.LOC_DELIM + "0" + Code.LOC_DELIM + "0");
                    }
                    if (token == Code.ERROR) {
                        interpret(storedLine);
                    }
                    if (token == Code.CONSCN) {
                        long number = Long.parseLong(tokenizer.nextToken());
                        if (number == superMethodCallNumber) {
                            //Interpret the rest of the constructor call
                            interpret(readLine());
                            interpret(readLine());

                            //Then start using the collected statements
                            constructorCalls.push(superMethodsReading);
                            superMethods = superMethodsReading;
                            superMethodsReading = null;
                            constructorCall = false;
                            continue;
                        }
                    }
                    superMethodsReading.add(storedLine);
                }

            }
            closeScratch();

        } catch (StoppingRequestedError e) {
            return false;
        } catch (FeatureNotImplementedException e) {
            MessageFormat notImplemented = new MessageFormat(messageBundle
                    .getString("notImplemented.exception"));
            handleCodeERROR(notImplemented
                    .format(new String[] { e.getMessage() }), null);
        } catch (StaticVariableNotFoundException e) {
            handleCodeERROR("<H1> Runtime Exception </H1> <P> "
                    + e.getMessage() + " </P> ", null);
            return true;
        } catch (LocalVariableNotFoundException e) {
            handleCodeERROR("<H1> Runtime Exception </H1> <P> "
                    + e.getMessage() + " </P> ", null);
            return true;
        } catch (Exception e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
            if (e.getMessage() != null) {
                handleCodeERROR("<H1> Runtime Exception </H1> <P> "
                        + e.getMessage()
                        + " or the feature is not yet implemented. </P> ", null);
            } else {
                handleCodeERROR("<H1> Runtime Exception </H1> <P> "
                        + "Feature is not yet implemented. </P> ", null);

            }
            return true;
        }
        return true;
    }

    public void cleanEvaluationArea(int token) {
    }

    /**
     * Handles the interpretation of the single line of the mcode.
     * 
     * @param line
     */
    public void interpret(String line) {
        beforeInterpretation(line);

        if (!line.equals("" + Code.END)) {

            StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

            if (tokenizer.hasMoreTokens()) {

                int token = Integer.parseInt(tokenizer.nextToken());

                /*
                 * Test whether or not the evaluation area should be cleaned.
                 */
                cleanEvaluationArea(token);

                //checkInstancesForRemoval();

                switch (token) {

                //Gives a reference to the left hand side of the expression
                case Code.LEFT: {
                    long token1 = Long.parseLong(tokenizer.nextToken());
                    handleCodeLEFT(token1);
                    break;
                }

                    //Gives a reference to the right hand side of the expression
                case Code.RIGHT: {
                    long token1 = Long.parseLong(tokenizer.nextToken());
                    handleCodeRIGHT(token1);
                    break;
                }

                    //Begins an expression
                case Code.BEGIN: {
                    //first token
                    long expressionType = Long.parseLong(tokenizer.nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String location = tokenizer.nextToken();
                    handleCodeBEGIN(expressionType, expressionReference,
                            location);
                    break;
                }

                    //Indicates where the value is assigned
                case Code.TO: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    handleCodeTO(expressionReference);
                    break;
                }

                    //Assignment
                case Code.A: {
                	long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long fromExpression = Long.parseLong(tokenizer.nextToken());
                    long toExpression = Long.parseLong(tokenizer.nextToken());
                    String value = "";
                    if (tokenizer.countTokens() > 2) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeA(expressionCounter, fromExpression,
                            toExpression, value, type, h);

                    break;
                }

                    /*
                     * Unary Expressions
                     */
                    // Complement
                case Code.COMP: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long unaryExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeCOMP(expressionCounter, unaryExpressionReference,
                            value, type, h);
                    break;
                }

                    // Plus operator
                case Code.PLUS: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long unaryExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodePLUS(expressionCounter, unaryExpressionReference,
                            value, type, h);
                    break;
                }

                    // Minus operator
                case Code.MINUS: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long unaryExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeMINUS(expressionCounter,
                            unaryExpressionReference, value, type, h);
                    break;
                }

                    // Boolean Not
                case Code.NO: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long unaryExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeNO(expressionCounter, unaryExpressionReference,
                            value, type, h);
                    break;
                }

                    // Unary Expression
                    // PostIncrement
                case Code.PIE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());

                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodePIE(expressionCounter, expressionReference,
                            value, type, h);
                    break;
                }

                    // PostDecrement
                case Code.PDE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());

                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodePDE(expressionCounter, expressionReference,
                            value, type, h);
                    break;
                }

                    // Unary Expression
                    // PreIncrement
                case Code.PRIE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodePRIE(expressionCounter, expressionReference,
                            value, type, h);
                    break;
                }

                    // PreDecrement
                case Code.PRDE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodePRDE(expressionCounter, expressionReference,
                            value, type, h);
                    break;
                }

                    /*
                     * Binary Expressions
                     */

                    // Bitwise Or
                case Code.BITOR: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeBITOR(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Bitwise Xor
                case Code.BITXOR: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeBITXOR(expressionCounter,
                            leftExpressionReference, rightExpressionReference,
                            value, type, h);
                    break;
                }

                    // Bitwise And
                case Code.BITAND: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeBITAND(expressionCounter,
                            leftExpressionReference, rightExpressionReference,
                            value, type, h);
                    break;
                }

                    // Bitwise Left Shift
                case Code.LSHIFT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeLSHIFT(expressionCounter,
                            leftExpressionReference, rightExpressionReference,
                            value, type, h);
                    break;
                }

                    // Bitwise Right Shift
                case Code.RSHIFT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeRSHIFT(expressionCounter,
                            leftExpressionReference, rightExpressionReference,
                            value, type, h);
                    break;
                }

                    // Unsigned Right Shift
                case Code.URSHIFT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeURSHIFT(expressionCounter,
                            leftExpressionReference, rightExpressionReference,
                            value, type, h);
                    break;
                }

                    // Xor Expression
                case Code.XOR: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeXOR(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // And Expression
                case Code.AND: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeAND(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Or Expression
                case Code.OR: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeOR(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Equal Expression
                case Code.EE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeEE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Not Equal Expression
                case Code.NE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeNE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                case Code.CAST: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeCAST(expressionCounter, expressionReference,
                            value, type, h);
                    break;
                }

                    // Less Expression
                case Code.LE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeLE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Less or Equal Expression
                case Code.LQE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeLQE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Greater Than
                case Code.GT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeGT(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Greater or Equal Expression
                case Code.GQT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeGQT(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Multiplication Expression
                case Code.ME: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeME(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Remainder (mod) Expression
                case Code.RE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeRE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Division Expression
                case Code.DE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeDE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Substract Expression
                case Code.SE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeSE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    // Add Expression
                case Code.AE: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    handleCodeAE(expressionCounter, leftExpressionReference,
                            rightExpressionReference, value, type, h);
                    break;
                }

                    //Variable Declaration
                case Code.VD: {
                    String variableName = tokenizer.nextToken();
                    long initializerExpression = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 4) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    String modifier = tokenizer.nextToken();

                    //Make the location information for the location token
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    handleCodeVD(variableName, initializerExpression, value,
                            type, modifier, highlight);
                    break;
                }

                    //Qualified Name (variable)
                case Code.QN: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    String variableName = tokenizer.nextToken();
                    String value = null;
                    if (tokenizer.countTokens() >= 2) {
                        value = tokenizer.nextToken();
                    } else {
                        value = "";
                    }
                    String type = tokenizer.nextToken();
                    Highlight highlight = null;
                    if (tokenizer.hasMoreElements()) {
                        highlight = MCodeUtilities.makeHighlight(tokenizer
                                .nextToken());
                    }

                    handleCodeQN(expressionCounter, variableName, value, type,
                            highlight);
                    break;
                }

                    //Static field access
                case Code.SFA: {

                    //Second token is the expression counter
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());

                    String declaringClass = tokenizer.nextToken();

                    String variableName = tokenizer.nextToken();

                    String value = null;

                    // fixed by rku:  3 to 4, because added modifier to mcode
                    if (tokenizer.countTokens() >= 4) {
                        //Third token is the value of the literal
                        value = tokenizer.nextToken();
                    } else {
                        /*
                         * There is no third token because the
                         * literal is an empty string.
                         */
                        value = "";
                    }

                    //Fourth token is the type of the literal
                    String type = tokenizer.nextToken();

                    // fixed by rku, added modifiers
                    int modifiers = Integer.parseInt(tokenizer.nextToken());

                    //Fifth token is the highlight information.
                    //Not normally used because the whole expression is highlighted.
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    handleCodeSFA(expressionCounter, declaringClass,
                            variableName, value, type, modifiers, highlight);
                    break;
                }
                    //Parameter
                case Code.P: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";

                    if (tokenizer.countTokens() > 1) {
                        value = tokenizer.nextToken();
                    }
                    String argType = tokenizer.nextToken();

                    handleCodeP(expressionReference, value, argType);
                    break;
                }
                    //Literal
                case Code.L: {
                    //Second token is the expression counter
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());

                    String value = null;
                    if (tokenizer.countTokens() >= 3) {
                        //Third token is the value of the literal
                        value = tokenizer.nextToken();
                    } else {
                        /*
                         * There is no third token because the
                         * literal is an empty string.
                         */
                        value = "";
                    }

                    //Fourth token is the type of the literal
                    String type = tokenizer.nextToken();

                    //Fifth token is the highlight information.
                    //Not normally used because the whole expression is highlighted.
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    handleCodeL(expressionCounter, value, type, highlight);
                    break;
                }

                    //Simple Allocation (Object Allocation)
                case Code.SA: {
                    //simpleAllocationCounter
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());

                    String declaringClass = tokenizer.nextToken();
                    String constructorName = tokenizer.nextToken();

                    int parameterCount = Integer
                            .parseInt(tokenizer.nextToken());
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    handleCodeSA(expressionCounter, declaringClass,
                            constructorName, parameterCount, highlight);
                    break;
                }

                case Code.CONSCN: {
                    //Normally nothing needs to be done for this!
                    handleCodeCONSCN(Long.parseLong(tokenizer.nextToken()));
                    break;
                }

                    // Simple class allocation close
                case Code.SAC: {
                    //simpleAllocationCounter
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    String hashCode = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeSAC(expressionCounter, hashCode, h);
                    break;
                }

                    // Object field access
                case Code.OFA: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long objectCounter = Long.parseLong(tokenizer.nextToken());
                    String variableName = tokenizer.nextToken();
                    String value = "";
                    // fixed rku: used because we added modifiers in Mcode
                    // was 3
                    if (tokenizer.countTokens() >= 4) {
                        value = tokenizer.nextToken();
                    }

                    String type = tokenizer.nextToken();
                    int modifiers = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeOFA(expressionCounter, objectCounter,
                            variableName, value, type, modifiers, h);
                    break;
                }

                    // Object method call
                case Code.OMC: {

                    String methodName = tokenizer.nextToken();
                    int parameterCount = Integer
                            .parseInt(tokenizer.nextToken());
                    long objectCounter = Long.parseLong(tokenizer.nextToken());
                    String objectValue = "";
                    if (tokenizer.countTokens() > 1) {
                        objectValue = tokenizer.nextToken();
                    }
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    handleCodeOMC(methodName, parameterCount, objectCounter,
                            objectValue, highlight);
                    break;
                }

                    // Object method call close
                case Code.OMCC: {
                    handleCodeOMCC();
                    break;
                }

                    //Static Method Call
                case Code.SMC: {
                    String methodName = tokenizer.nextToken();
                    String className = tokenizer.nextToken();
                    int parameterCount = Integer
                            .parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeSMC(methodName, className, parameterCount, h);
                    break;
                }

                    //Method declaration
                case Code.MD: {
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeMD(h);
                    break;
                }

                    //Parameters list
                case Code.PARAMETERS: {
                    String parameters = "";
                    if (tokenizer.hasMoreTokens()) {
                        parameters = tokenizer.nextToken();
                    }

                    handleCodePARAMETERS(parameters);
                    break;
                }

                    // Return Statement
                case Code.R: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeR(expressionCounter, expressionReference, value,
                            type, h);
                    break;
                }

                    // Static method call closed
                case Code.SMCC: {
                    handleCodeSMCC();
                    break;
                }

                    //If Then Statement
                case Code.IFT: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();

                    Highlight h = null;
                    if (tokenizer.hasMoreElements()) {
                        h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                    }

                    handleCodeIFT(expressionReference, value, h);

                    break;
                }

                    //IF Then Else Statement
                case Code.IFTE: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeIFTE(expressionReference, value, h);

                    break;
                }

                    //While Statement
                case Code.WHI: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    int round = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeWHI(expressionReference, value, round, h);
                    break;
                }

                    //For Statement
                case Code.FOR: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    long round = Long.parseLong(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeFOR(expressionReference, value, round, h);
                    break;
                }

                    //Do-While Statement
                case Code.DO: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    long round = Long.parseLong(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeDO(expressionReference, value, round, h);
                    break;
                }

                case Code.SWITCHB: {
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeSWITCHB(h);
                    break;
                }

                case Code.SWIBF: {
                    long selectorReference = Long.parseLong(tokenizer
                            .nextToken());
                    long switchBlockReference = Long.parseLong(tokenizer
                            .nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeSWIBF(selectorReference, switchBlockReference, h);

                    break;
                }

                case Code.SWITCH: {
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeSWITCH(h);
                    break;
                }

                    //Break Statement
                case Code.BR: {
                    int statementName = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeBR(statementName, h);

                    break;
                }

                    //Continue Statement
                case Code.CONT: {
                    int statementName = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeCONT(statementName, h);

                    break;
                }

                    //Outputting an expression
                case Code.OUTPUT: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String className = tokenizer.nextToken();
                    String methodName = tokenizer.nextToken();
                    String value = "";
                    if (tokenizer.countTokens() >= 4) {
                        value = tokenizer.nextToken();
                    }
                    String type = "";
                    if (tokenizer.countTokens() >= 3) {
                        type = tokenizer.nextToken();
                    }
                    boolean breakLine = tokenizer.nextToken().equals("1") ? true
                            : false;
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    handleCodeOUTPUT(expressionReference, className,
                            methodName, value, type, breakLine, highlight);
                    break;
                }

                    //Input needs to be read
                case Code.INPUT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    String className = tokenizer.nextToken();
                    String methodName = tokenizer.nextToken();
                    String type = tokenizer.nextToken();

                    String prompt = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeINPUT(expressionCounter, className, methodName,
                            type, ((prompt.equals("null")) ? null : prompt), h);

                    break;
                }
                    /*
                     //Array Access
                     case Code.AAC: {
                     long expressionCounter = Long.parseLong(tokenizer.nextToken());
                     long expressionReference = Long.parseLong(tokenizer.nextToken());
                     int dims = Integer.parseInt(tokenizer.nextToken());
                     String cellNumberReferences = tokenizer.nextToken();

                     //int values of the dimension sizes
                     String cellNumbers = tokenizer.nextToken();
                     String value = "";
                     if (tokenizer.countTokens() >= 3) {
                     value = tokenizer.nextToken();
                     }
                     String type = tokenizer.nextToken();
                     Highlight h = null;
                     if (tokenizer.hasMoreElements()) {
                     h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                     }
                     break;
                     }*/

                    //Inputted value is returned
                case Code.INPUTTED: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeINPUTTED(expressionCounter, value, type, h);

                    break;
                }

                    //Opening and closing scopes
                case Code.SCOPE: {
                    int scope = Integer.parseInt(tokenizer.nextToken());

                    handleCodeSCOPE(scope);
                    break;
                }

                    //Array Allocation
                case Code.AA: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());

                    String hashCode = tokenizer.nextToken();
                    String compType = tokenizer.nextToken();
                    int dims = Integer.parseInt(tokenizer.nextToken());

                    //References of the dimension values
                    String dimensionReferences = tokenizer.nextToken();

                    //int values of the dimension sizes
                    String dimensionSizes = tokenizer.nextToken();
                    //number of real dimensions the array has, even if they are not yet allocated	
                    int actualDimension = Integer.parseInt(tokenizer
                            .nextToken());
                    String subArraysHashCodes = "";
                    if (tokenizer.countTokens() >= 2) {
                        //Hashcodes of the sub arrays
                        subArraysHashCodes = tokenizer.nextToken();
                    }
                    Highlight h = null;
                    if (tokenizer.hasMoreElements()) {
                        h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                    }
                    handleCodeAA(expressionReference, hashCode, compType, dims,
                            dimensionReferences, dimensionSizes,
                            actualDimension, subArraysHashCodes, h);
                    break;
                }
                    //Array Initializer's element
                case Code.AIE: {
                    String arrayReference = tokenizer.nextToken();
                    long cellNumber = Long.parseLong(tokenizer.nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = "";
                    if (tokenizer.countTokens() > 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    long literal = Long.parseLong(tokenizer.nextToken());
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    handleCodeAIE(arrayReference, cellNumber,
                            expressionReference, value, type, literal,
                            highlight);

                    break;

                }
                    //Array Access
                case Code.AAC: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    int dims = Integer.parseInt(tokenizer.nextToken());
                    String cellNumberReferences = tokenizer.nextToken();

                    //int values of the dimension sizes
                    String cellNumbers = tokenizer.nextToken();
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }
                    String type = tokenizer.nextToken();
                    Highlight h = null;
                    if (tokenizer.hasMoreElements()) {
                        h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                    }

                    handleCodeAAC(expressionCounter, expressionReference, dims,
                            cellNumberReferences, cellNumbers, value, type, h);
                    break;
                }

                    //Array Length
                case Code.AL: {
                    //Second token is the expression counter
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long arrayCounter = Long.parseLong(tokenizer.nextToken());

                    String name = tokenizer.nextToken();
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        //Third token is the value of the literal
                        value = tokenizer.nextToken();
                    }
                    //Fourth token is the type of the literal
                    String type = tokenizer.nextToken();

                    //Fifth token is the highlight information.
                    //Not used because the whole expression is
                    // highlighted.
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    handleCodeAL(expressionCounter, arrayCounter, name, value,
                            type, highlight);
                    break;

                }

                    //Beginning of the Array Initializer
                case Code.AIBEGIN: {
                    long cells = Long.parseLong(tokenizer.nextToken());
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    handleCodeAIBEGIN(cells, highlight);

                    break;
                }

                case Code.AI: {
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    handleCodeAI(highlight);

                    break;
                }

                    //Class information starts for a class
                case Code.CLASS: {
                    String name = tokenizer.nextToken();
                    String extendedClass = "";
                    if (tokenizer.hasMoreTokens()) {
                        extendedClass = tokenizer.nextToken();
                    }

                    handleCodeCLASS(name, extendedClass);
                    break;
                }

                    //Class information ends for a class
                case Code.END_CLASS: {
                    handleCodeEND_CLASS();
                    break;
                }

                    //Class information for constructor
                case Code.CONSTRUCTOR: {
                    String listOfParameters = "";
                    if (tokenizer.hasMoreTokens()) {
                        listOfParameters = tokenizer.nextToken();
                    }

                    handleCodeCONSTRUCTOR(listOfParameters);
                    break;
                }

                    //Class information for method
                case Code.METHOD: {
                    String name = tokenizer.nextToken();
                    String returnType = tokenizer.nextToken();
                    int modifiers = -1;
                    if (tokenizer.hasMoreTokens()) {
                        modifiers = Integer.parseInt(tokenizer.nextToken());
                    }
                    String listOfParameters = "";
                    if (tokenizer.hasMoreTokens()) {
                        listOfParameters = tokenizer.nextToken();
                    }

                    handleCodeMETHOD(name, returnType, modifiers,
                            listOfParameters);
                    break;
                }

                    //Class information for field
                case Code.FIELD: {
                    String name = tokenizer.nextToken();
                    String type = tokenizer.nextToken();
                    int modifiers = Integer.parseInt(tokenizer.nextToken());
                    String value = "";
                    if (tokenizer.countTokens() > 1) {
                        value = tokenizer.nextToken();
                    }
                    String h = tokenizer.nextToken();

                    handleCodeFIELD(name, type, modifiers, value, h);
                    break;
                }

                    //Error has occured during the execution
                case Code.ERROR: {
                    String message = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    handleCodeERROR(message, h);

                    break;
                }

                    //There is an error if the execution comes here.
                default: {
                    //director.showErrorMessage(new InterpreterError(bundle
                    //        .getString("notImplemented.exception"), null));
                    /*
                     * " <H1> Runtime Error </H1><P> The feature is not
                     * yet implemented or " +
                     */
                    break;
                }
                }
            }
            afterInterpretation(line);
        } else {
            running = false;
            endRunning();
        }
    }

    public abstract void afterInterpretation(String line);

    /**
     * @param cells
     * @param highlight
     */
    protected abstract void handleCodeAIBEGIN(long cells, Highlight highlight);

    /**
     * @param cellNumber
     * @param expressionReference
     * @param value
     * @param type
     * @param literal
     * @param highlight
     */
    protected abstract void handleCodeAIE(String arrayReference,
            long cellNumber, long expressionReference, String value,
            String type, long literal, Highlight highlight);

    /**
     * @param highlight
     */
    protected abstract void handleCodeAI(Highlight highlight);

    /**
     * @param expressionCounter
     * @param value
     * @param type
     * @param modifiers
     * @param highlight
     */
    protected abstract void handleCodeSFA(long expressionCounter,
            String declaringClass, String variableName, String value,
            String type, int modifiers, Highlight highlight);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeCAST(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param line2
     */
    protected abstract void beforeInterpretation(String line);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeLQE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeLE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeNE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param message
     * @param h
     */
    protected abstract void handleCodeERROR(String message, Highlight h);

    /**
     * @param name
     * @param type
     * @param modifiers
     * @param value
     * @param h
     */
    protected abstract void handleCodeFIELD(String name, String type,
            int modifiers, String value, String h);

    /**
     * @param name
     * @param returnType
     * @param modifiers
     * @param listOfParameters
     */
    protected abstract void handleCodeMETHOD(String name, String returnType,
            int modifiers, String listOfParameters);

    /**
     * @param listOfParameters
     */
    protected abstract void handleCodeCONSTRUCTOR(String listOfParameters);

    /**
     * 
     */
    protected abstract void handleCodeEND_CLASS();

    /**
     * @param name
     * @param extendedClass
     */
    protected abstract void handleCodeCLASS(String name, String extendedClass);

    /**
     * @param expressionCounter
     * @param arrayCounter
     * @param name
     * @param value
     * @param type
     * @param highlight
     */
    protected abstract void handleCodeAL(long expressionCounter,
            long arrayCounter, String name, String value, String type,
            Highlight highlight);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param dims
     * @param cellNumberReferences
     * @param cellNumbers
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeAAC(long expressionCounter,
            long expressionReference, int dims, String cellNumberReferences,
            String cellNumbers, String value, String type, Highlight h);

    /**
     * @param expressionReference
     * @param hashCode
     * @param compType
     * @param dims
     * @param dimensionReferences
     * @param dimensionSizes
     * @param actualdimensions
     * @param h
     */
    protected abstract void handleCodeAA(long expressionReference,
            String hashCode, String compType, int dims,
            String dimensionReferences, String dimensionSizes,
            int actualdimensions, String subArraysHashCodes, Highlight h);

    /**
     * @param scope
     */
    protected abstract void handleCodeSCOPE(int scope);

    /**
     * @param expressionCounter
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeINPUTTED(long expressionCounter,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param type
     * @param h
     */
    protected abstract void handleCodeINPUT(long expressionCounter,
            String className, String methodName, String type, String prompt,
            Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param type
     * @param breakLine
     * @param highlight
     */
    protected abstract void handleCodeOUTPUT(long expressionReference,
            String className, String methodName, String value, String type,
            boolean breakLine, Highlight highlight);

    /**
     * @param statementName
     * @param h
     */
    protected abstract void handleCodeCONT(int statementName, Highlight h);

    /**
     * @param statementName
     * @param h
     */
    protected abstract void handleCodeBR(int statementName, Highlight h);

    /**
     * @param h
     */
    protected abstract void handleCodeSWITCH(Highlight h);

    /**
     * @param selectorReference
     * @param switchBlockReference
     * @param h
     */
    protected abstract void handleCodeSWIBF(long selectorReference,
            long switchBlockReference, Highlight h);

    /**
     * @param h
     */
    protected abstract void handleCodeSWITCHB(Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param round
     * @param h
     */
    protected abstract void handleCodeDO(long expressionReference,
            String value, long round, Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param round
     * @param h
     */
    protected abstract void handleCodeFOR(long expressionReference,
            String value, long round, Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param round
     * @param h
     */
    protected abstract void handleCodeWHI(long expressionReference,
            String value, int round, Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param h
     */
    protected abstract void handleCodeIFTE(long expressionReference,
            String value, Highlight h);

    /**
     * @param expressionReference
     * @param value
     * @param h
     */
    protected abstract void handleCodeIFT(long expressionReference,
            String value, Highlight h);

    /**
     * 
     */
    protected abstract void handleCodeSMCC();

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeR(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param parameters
     */
    protected abstract void handleCodePARAMETERS(String parameters);

    /**
     * @param h
     */
    protected abstract void handleCodeMD(Highlight h);

    /**
     * @param expressionReference
     * @param argType
     */
    protected abstract void handleCodeP(long expressionReference, String value,
            String argType);

    /**
     * @param methodName
     * @param className
     * @param parameterCount
     * @param h
     */
    protected abstract void handleCodeSMC(String methodName, String className,
            int parameterCount, Highlight h);

    /**
     * 
     */
    protected abstract void handleCodeOMCC();

    /**
     * @param methodName
     * @param parameterCount
     * @param objectCounter
     * @param highlight
     */
    protected abstract void handleCodeOMC(String methodName,
            int parameterCount, long objectCounter, String objectValue,
            Highlight highlight);

    /**
     * @param expressionCounter
     * @param objectCounter
     * @param variableName
     * @param value
     * @param type
     * @param modifiers
     * @param h
     */
    protected abstract void handleCodeOFA(long expressionCounter,
            long objectCounter, String variableName, String value, String type,
            int modifiers, Highlight h);

    /**
     * @param expressionCounter
     * @param hashCode
     * @param h
     */
    protected abstract void handleCodeSAC(long expressionCounter,
            String hashCode, Highlight h);

    /**
     * @param superMethodCallNumber2
     */
    protected abstract void handleCodeCONSCN(long superMethodCallNumber);

    /**
     * @param expressionCounter
     * @param declaringClass
     * @param constructorName
     * @param parameterCount
     * @param highlight
     */
    protected abstract void handleCodeSA(long expressionCounter,
            String declaringClass, String constructorName, int parameterCount,
            Highlight highlight);

    /**
     * @param expressionCounter
     * @param value
     * @param type
     * @param highlight
     */
    protected abstract void handleCodeL(long expressionCounter, String value,
            String type, Highlight highlight);

    /**
     * @param expressionCounter
     * @param variableName
     * @param value
     * @param type
     * @param highlight
     */
    protected abstract void handleCodeQN(long expressionCounter,
            String variableName, String value, String type, Highlight highlight);

    /**
     * @param variableName
     * @param initializerExpression
     * @param value
     * @param type
     * @param modifier
     * @param highlight
     */
    protected abstract void handleCodeVD(String variableName,
            long initializerExpression, String value, String type,
            String modifier, Highlight highlight);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeAE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeSE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeDE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeRE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeME(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeGQT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeGT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeEE(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeAND(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeXOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeURSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeRSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeLSHIFT(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeBITAND(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeBITXOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param leftExpressionReference
     * @param rightExpressionReference
     * @param value
     * @param type
     * @param h
     * @param operator
     */
    protected abstract void handleCodeBITOR(long expressionCounter,
            long leftExpressionReference, long rightExpressionReference,
            String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodePRDE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodePRIE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodePDE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param expressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodePIE(long expressionCounter,
            long expressionReference, String value, String type, Highlight h);

    /**
     * @param expressionCounter
     * @param unaryExpressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeNO(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h);

    /**
     * @param expressionCounter
     * @param unaryExpressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeMINUS(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h);

    /**
     * @param expressionCounter
     * @param unaryExpressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodePLUS(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h);

    /**
     * @param expressionCounter
     * @param unaryExpressionReference
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeCOMP(long expressionCounter,
            long unaryExpressionReference, String value, String type,
            Highlight h);

    /**
     * @param expressionCounter
     * @param fromExpression
     * @param toExpression
     * @param value
     * @param type
     * @param h
     */
    protected abstract void handleCodeA(long expressionCounter,
            long fromExpression, long toExpression, String value, String type,
            Highlight h);

    /**
     * @param expressionReference
     */
    protected abstract void handleCodeTO(long expressionReference);

    /**
     * @param expressionType
     * @param expressionReference
     * @param location
     */
    protected abstract void handleCodeBEGIN(long expressionType,
            long expressionReference, String location);

    /**
     * @param token1
     */
    protected abstract void handleCodeRIGHT(long token1);

    /**
     * @param token1
     */
    protected abstract void handleCodeLEFT(long token1);

    /**
     * 
     */
    protected abstract void endRunning();

    /**
     * @return
     */
    public abstract boolean emptyScratch();

}
