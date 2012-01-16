package jeliot.mcode;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import jeliot.lang.ArrayInstance;
import jeliot.lang.ClassInfo;
import jeliot.lang.Instance;
import jeliot.lang.ObjectFrame;
import jeliot.lang.Reference;
import jeliot.lang.Value;
import jeliot.lang.Variable;
import jeliot.lang.VariableInArray;
import jeliot.theater.Actor;
import jeliot.theater.Director;
import jeliot.theater.ExpressionActor;
import jeliot.theater.ValueActor;
import jeliot.util.DebugUtil;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;

/**
 * NOT IN USE IN THE CURRENT RELEASE, REPLACED BY MCodeInterpreter!
 * The MCode interpreter that interprets the MCode received during the
 * intepretation in DynamicJava.
 * 
 * @author Niko Myller
 * @author Andrés Moreno
 * @see MCodeInterpreter
 * @deprecated
 */
public class Interpreter {

    //  DOC: document!
    /**
     *  
     */
    private Director director = null;

    /**
     *  
     */
    private BufferedReader ecode = null;

    /**
     *  
     */
    private PrintWriter input = null;

    /**
     *  
     */
    private String programCode = "";

    /**
     *  
     */
    private boolean running = true;

    /**
     *  
     */
    private boolean start = true;

    /**
     *  
     */
    private boolean firstLineRead = false;

    /**
     *  
     */
    private boolean invokingMethod = false;

    /**
     * Keeps track of current return value
     */
    private boolean returned = false;

    /**
     *  
     */
    private Value returnValue = null;

    /**
     *  
     */
    private Actor returnActor = null;

    /**
     *  
     */
    private long returnExpressionCounter = 0;

    /**
     *  
     */
    private Stack commands = new Stack();

    /**
     *  
     */
    private Stack exprs = new Stack();

    /**
     *  
     */
    private Hashtable values = new Hashtable();

    /**
     *  
     */
    private Hashtable variables = new Hashtable();

    /**
     *  
     */
    private Hashtable instances = new Hashtable();

    /**
     *  
     */
    private Stack methodInvocation = new Stack();

    /**
     *  
     */
    private Hashtable postIncsDecs = new Hashtable();

    /**
     *  
     */
    private ClassInfo currentClass = null;

    /**
     *  
     */
    private Hashtable classes = new Hashtable();

    /**
     *  
     */
    private String line = null;

    /**
     * currentMethodInvocation keeps track of all the information that is
     * collected during the method invocation.
     * Cells:
     * 0: Method name
     * 1: Class/Object expression
     * 2: Parameter values
     * 3: Parameter types
     * 4: Parameter names
     * 5: Highlight info for invocation
     * 6: Highlight info for declaration
     * 7: Parameter expression references
     * 8: Object reference if method is constructor or object method
     */
    private Object[] currentMethodInvocation = null;

    /**
     *  
     */
    private Stack objectCreation = new Stack();

    /**
     * Related to Super method calls in constructor's first line in classes
     * with inheritance.
     */
    private boolean constructorCall = false;

    /**
     *  
     */
    private Stack constructorCalls = new Stack();

    /**
     *  
     */
    private Vector superMethods = null;

    /**
     *  
     */
    private Vector superMethodsReading = null;

    /**
     *  
     */
    private long superMethodCallNumber = 0;

    /**
     * The resource bundle for mcode messages
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getMCodeMessageResourceBundle();

    /**
     * The resource bundle for mcode properties
     */
    static private UserProperties propertiesBundle = ResourceBundles
            .getMCodeUserProperties();

    /**
     *  
     */
    protected Interpreter() {
    }

    /**
     * @param r
     * @param d
     * @param programCode
     * @param pr
     */
    public Interpreter(BufferedReader r, Director d, String programCode,
            PrintWriter pr) {
        this.ecode = r;
        this.director = d;
        this.programCode = programCode;
        this.input = pr;
    }

    /**
     * Initializes the
     */
    public void initialize() {
        running = true;
        start = true;
        returnActor = null;
        returnValue = null;
        currentMethodInvocation = null;
        currentClass = null;
        classes = new Hashtable();
        commands = new Stack();
        exprs = new Stack();
        values = new Hashtable();
        variables = new Hashtable();
        methodInvocation = new Stack();
        postIncsDecs = new Hashtable();
        instances = new Hashtable();
        classes = new Hashtable();
        objectCreation = new Stack();
        constructorCall = false;
        constructorCalls = new Stack();
        superMethods = null;
        superMethodsReading = null;
        superMethodCallNumber = 0;
        try {
            line = ecode.readLine();
            DebugUtil.printDebugInfo(line);
        } catch (Exception e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        }

        if (line == null) {
            line = "" + Code.ERROR + Code.DELIM
                    + messageBundle.getString("unknown.exception") + Code.DELIM
                    + "0" + Code.LOC_DELIM + "0" + Code.LOC_DELIM + "0"
                    + Code.LOC_DELIM + "0";
        }

        StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

        if (Long.parseLong(tokenizer.nextToken()) == Code.ERROR) {
            String message = tokenizer.nextToken();
            Highlight h = MCodeUtilities.makeHighlight(tokenizer.nextToken());

            director.showErrorMessage(new InterpreterError(message, h));
            running = false;
        } else {
            firstLineRead = true;
        }

    }

    /**
     * @return
     */
    public boolean starting() {
        return start;
    }

    /**
     * @return
     */
    public boolean emptyScratch() {
        return exprs.empty();
    }

    /**
     * @return
     */
    public boolean readNew() {
        return constructorCalls.empty();
    }

    /**
     * @return
     */
    public String readLine() {
        String readLine = null;
        if (readNew()) {
            try {
                readLine = ecode.readLine();
            } catch (Exception e) {
            }
        } else {
            if (!superMethods.isEmpty()) {
                readLine = (String) superMethods.remove(0);
            } else {
                constructorCalls.pop();
                if (!constructorCalls.empty()) {
                    superMethods = (Vector) constructorCalls.peek();
                }
                return readLine();
            }
        }
        //Change this to be something more meaningful!
        if (readLine == null) {
            readLine = "" + Code.ERROR + Code.DELIM
                    + messageBundle.getString("unknown.exception")/*
                     * + " <H1> Runtime
                     * Exception </H1> " + "
                     * <P> The reason
                     * for runtime
                     * exception is
                     * unknown. </P> "
                     */
                    + Code.DELIM + "0" + Code.LOC_DELIM + "0" + Code.LOC_DELIM
                    + "0" + Code.LOC_DELIM + "0";
        }
        DebugUtil.printDebugInfo(line);

        return readLine;
    }

    /**
     *  
     */
    public void execute() {

        try {

            director.openScratch();

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
            director.closeScratch();

        } catch (Exception e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
            director.showErrorMessage(new InterpreterError(
                    " <H1> Runtime Error </H1> "
                            + " <P> The feature is not yet implemented. </P> ",
                    null));
        }

    }

    /**
     * Handles the interpretation of the single line of the mcode.
     * 
     * @param line
     */
    public void interpret(String line) {

        if (!line.equals("" + Code.END)) {

            StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

            if (tokenizer.hasMoreTokens()) {

                int token = Integer.parseInt(tokenizer.nextToken());

                /*
                 * Test whether or not the evaluation area should be cleaned.
                 */
                if (exprs.empty() && !invokingMethod && token != Code.WHI
                        && token != Code.FOR && token != Code.DO
                        && token != Code.IFT && token != Code.IFTE
                        && token != Code.SWIBF && token != Code.SWITCHB
                        && token != Code.SWITCH && token != Code.VD
                        && token != Code.OUTPUT && token != Code.INPUT
                        && token != Code.INPUTTED) {
                    director.closeScratch();
                    director.openScratch();
                }

                //checkInstancesForRemoval();

                switch (token) {

                //Gives a reference to the left hand side of the expression
                case Code.LEFT: {

                    long token1 = Long.parseLong(tokenizer.nextToken());
                    commands.push("" + Code.LEFT + Code.DELIM + token1);
                    break;
                }

                //Gives a reference to the right hand side of the expression
                case Code.RIGHT: {

                    long token1 = Long.parseLong(tokenizer.nextToken());
                    commands.push("" + Code.RIGHT + Code.DELIM + token1);
                    break;
                }

                //Begins an expression
                case Code.BEGIN: {

                    //first token
                    long expressionType = Long.parseLong(tokenizer.nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String location = tokenizer.nextToken();
                    exprs.push(expressionType + Code.DELIM
                            + expressionReference + Code.DELIM + location);
                    break;
                }

                //Indicates where the value is assigned
                case Code.TO: {

                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    commands.push("" + Code.TO + Code.DELIM
                            + expressionReference);
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

                    Variable toVariable = (Variable) variables.remove(new Long(
                            toExpression));

                    //just to get rid of extra references
                    variables.remove(new Long(fromExpression));

                    Value fromValue = (Value) values.remove(new Long(
                            fromExpression));
                    Value casted = null;
                    Value expressionValue = null;
                    if (MCodeUtilities.isPrimitive(type) || type.equals("null")) {
                        casted = new Value(value, type);
                        expressionValue = new Value(value, type);

                        if (!casted.getType().equals(fromValue.getType())
                                && MCodeUtilities.resolveType(casted.getType()) != MCodeUtilities
                                        .resolveType(fromValue.getType())) {
                            director.animateCastExpression(fromValue, casted);
                            fromValue.setActor(casted.getActor());
                        }

                    } else {
                        Instance inst = (Instance) instances.get(MCodeUtilities
                                .getHashCode(value));
                        if (inst != null) {
                            casted = new Reference(inst);
                            ((Reference) casted).makeReference();
                            expressionValue = new Reference(inst);
                        } else {
                            casted = new Reference(type);
                            expressionValue = new Reference(type);
                        }
                    }
                    Value copiedValue = director.prepareForAssignment(
                            toVariable, fromValue);

                    Object[] postIncDec = (Object[]) postIncsDecs
                            .remove(new Long(fromExpression));

                    if (postIncDec != null) {
                        doPostIncDec(postIncDec);
                    }

                    director.animateAssignment(toVariable, fromValue,
                            copiedValue, casted, expressionValue, h);
                    toVariable.assign(casted);

                    values.put(new Long(expressionCounter), expressionValue);

                    postIncDec = (Object[]) postIncsDecs.remove(new Long(
                            toExpression));

                    if (postIncDec != null) {
                        doPostIncDec(postIncDec);
                    }

                    exprs.pop();

                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                /*
                 * Unary Expressions
                 */
                // Complement
                case Code.COMP:
                // Plus operator
                case Code.PLUS:
                // Minus operator
                case Code.MINUS:
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
                    Value result = new Value(value, type);
                    Value val = (Value) values.remove(new Long(
                            unaryExpressionReference));

                    int unaryOperator = MCodeUtilities.resolveUnOperator(token);

                    ExpressionActor expr = director.getCurrentScratch()
                            .findActor(expressionCounter);

                    if (expr == null) {
                        expr = director.beginUnaryExpression(unaryOperator,
                                val, expressionCounter, h);
                    }

                    Object[] postIncDec = (Object[]) postIncsDecs
                            .remove(new Long(unaryExpressionReference));

                    if (postIncDec != null) {
                        doPostIncDec(postIncDec);
                    }

                    Value expressionValue = director.finishUnaryExpression(
                            unaryOperator, expr, result, expressionCounter, h);

                    //NOT NEEDED ANY MORE!
                    //This is not needed after the changes.
                    //Value expressionValue =
                    //        director.animateUnaryExpression(operator,
                    //                                        val,
                    //                                        result,
                    //                                        expressionCounter,
                    //                                        h);

                    //                          values.put(new Long(expressionCounter),
                    // expressionValue);

                    exprs.pop();

                    handleExpression(expressionValue, expressionCounter);

                    /*
                     * //command that wait for this expression (left,
                     * right) int command = -1; int oper = -1; int size =
                     * commands.size();
                     * 
                     * //We find the command for (int i = size - 1; i >= 0;
                     * i--) { StringTokenizer commandTokenizer = new
                     * StringTokenizer( (String) commands.elementAt(i),
                     * Code.DELIM); int comm =
                     * Integer.parseInt(commandTokenizer.nextToken()); long
                     * cid = Long.parseLong(commandTokenizer.nextToken());
                     * if (expressionCounter == cid) { command = comm;
                     * commands.removeElementAt(i); break; } }
                     */
                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    /*
                     * long expressionReference = 0; Highlight highlight =
                     * null;
                     * 
                     * if (!exprs.empty()) {
                     * 
                     * StringTokenizer expressionTokenizer = new
                     * StringTokenizer( (String) exprs.peek(), Code.DELIM);
                     * 
                     * oper =
                     * Integer.parseInt(expressionTokenizer.nextToken());
                     * 
                     * expressionReference = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * //Make the location information for the location
                     * token highlight = ECodeUtilities.makeHighlight(
                     * expressionTokenizer.nextToken()); }
                     * 
                     * //Do different things depending on in what
                     * expression //the literal is used.
                     * 
                     * //If operator is assignment we just store the value
                     * if (oper == Code.A) {
                     * 
                     * values.put(new Long(expressionCounter),
                     * expressionValue);
                     * 
                     * //If oper is other binary operator we will show it
                     * //on the screen with operator } else if
                     * (ECodeUtilities.isBinary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveBinOperator(oper);
                     * 
                     * if (command == Code.LEFT) {
                     * 
                     * director.beginBinaryExpression(expressionValue,
                     * operator, expressionReference, highlight); } else if
                     * (command == Code.RIGHT) {
                     * 
                     * ExpressionActor ea = (ExpressionActor)
                     * director.getCurrentScratch().findActor(expressionReference);
                     * if (ea != null) {
                     * director.rightBinaryExpression(expressionValue, ea,
                     * highlight); } else { values.put(new
                     * Long(expressionCounter), expressionValue); } } else {
                     * values.put(new Long(expressionCounter),
                     * expressionValue); }
                     * 
                     * //If oper is a unary operator we will show it //on
                     * the screen with operator } else if
                     * (ECodeUtilities.isUnary(oper)) {
                     * 
                     * values.put(new Long(expressionCounter),
                     * expressionValue);
                     * 
                     * int operator =
                     * ECodeUtilities.resolveUnOperator(oper);
                     * 
                     * if (command == Code.RIGHT) {
                     * director.beginUnaryExpression(operator,
                     * expressionValue, expressionReference, highlight); }
                     * 
                     * //If it is something else we will store it for later
                     * use. } else {
                     * 
                     * values.put(new Long(expressionCounter),
                     * expressionValue); }
                     */
                    break;

                }

                // Unary Expression
                // PostIncrement
                case Code.PIE:
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

                    Value result = new Value(value, type);

                    exprs.pop();

                    if (exprs.empty()) {

                        Variable var = (Variable) variables.remove(new Long(
                                expressionReference));

                        int operator = MCodeUtilities.resolveUnOperator(token);
                        director.animateIncDec(operator, var, result, h);

                    } else {

                        Object[] postIncDec = {
                                new Long(MCodeUtilities
                                        .resolveUnOperator(token)),
                                new Long(expressionReference), result, h };
                        postIncsDecs.put(new Long(expressionCounter),
                                postIncDec);

                    }

                    break;

                }

                // Unary Expression
                // PreIncrement
                case Code.PRIE:
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

                    Value result = new Value(value, type);
                    Variable var = (Variable) variables.remove(new Long(
                            expressionReference));

                    int operator = MCodeUtilities.resolveUnOperator(token);
                    director.animateIncDec(operator, var, result, h);
                    values.put(new Long(expressionCounter), result);

                    exprs.pop();

                    Object[] postIncDec = (Object[]) postIncsDecs
                            .remove(new Long(expressionReference));

                    if (postIncDec != null) {
                        doPostIncDec(postIncDec);
                    }

                    break;
                }

                /*
                 * Binary Expressions
                 */

                // Bitwise Or
                case Code.BITOR:
                // Bitwise Xor
                case Code.BITXOR:
                // Bitwise And
                case Code.BITAND:

                // Bitwise Left Shift
                case Code.LSHIFT:
                // Bitwise Right Shift
                case Code.RSHIFT:
                // Unsigned Right Shift
                case Code.URSHIFT:

                // Xor Expression
                case Code.XOR:
                // And Expression
                case Code.AND:
                // Or Expression
                case Code.OR:

                // Equal Expression
                case Code.EE:
                // Not Equal Expression
                case Code.NE:
                // Less Expression
                case Code.LE:
                // Less or Equal Expression
                case Code.LQE:
                // Greater Than
                case Code.GT:
                // Greater or Equal Expression
                case Code.GQT:

                // Multiplication Expression
                case Code.ME:
                // Remainder (mod) Expression
                case Code.RE:
                // Division Expression
                case Code.DE:
                // Substract Expression
                case Code.SE:
                // Add Expression
                case Code.AE: {

                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long leftExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    long rightExpressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = null;
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    } else {
                        value = "";
                    }
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    Value result = new Value(value, type);

                    ExpressionActor expr = director.getCurrentScratch()
                            .findActor(expressionCounter);

                    Value expressionValue = null;

                    /*
                     * The expression is created because its left side
                     * consists of literal or variable.
                     */
                    if (expr != null) {

                        /*
                         * It is possible that the right hand side is not
                         * yet set thus we need to check that to be sure.
                         */

                        Value right = (Value) values.remove(new Long(
                                rightExpressionReference));

                        if (right != null) {
                            director.rightBinaryExpression(right, expr, h);
                        }

                        // token is declared and assigned in the line 91.
                        expressionValue = director.finishBinaryExpression(
                                result, MCodeUtilities
                                        .resolveBinOperator(token), expr, h);

                        exprs.pop();

                        //values.put(new Long(expressionCounter),
                        // expressionValue);

                        Object[] postIncDec = (Object[]) postIncsDecs
                                .remove(new Long(rightExpressionReference));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }

                        /*
                         * The expression is not created before because its
                         * left side consists of expression.
                         */
                    } else {

                        Value left = (Value) values.remove(new Long(
                                leftExpressionReference));
                        Value right = (Value) values.remove(new Long(
                                rightExpressionReference));

                        expr = director.beginBinaryExpression(left,
                                MCodeUtilities.resolveBinOperator(token),
                                expressionCounter, h);

                        Object[] postIncDec = (Object[]) postIncsDecs
                                .remove(new Long(leftExpressionReference));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }

                        director.rightBinaryExpression(right, expr, h);

                        postIncDec = (Object[]) postIncsDecs.remove(new Long(
                                rightExpressionReference));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }
                        // token is declared and assigned in the line 91.
                        expressionValue = director.finishBinaryExpression(
                                result, MCodeUtilities
                                        .resolveBinOperator(token), expr, h);

                        /*
                         * Value expressionValue =
                         * director.animateBinaryExpression(
                         * ECodeUtilities.resolveBinOperator(token), left,
                         * right, result, expressionCounter, h);
                         */
                        exprs.pop();

                        //                              values.put(new Long(expressionCounter),
                        // expressionValue);

                    }

                    handleExpression(expressionValue, expressionCounter);

                    /*
                     * int command = -1; int oper = -1; int size =
                     * commands.size();
                     * 
                     * //We find the command for (int i = size - 1; i >= 0;
                     * i--) { StringTokenizer commandTokenizer = new
                     * StringTokenizer( (String) commands.elementAt(i),
                     * Code.DELIM); int comm =
                     * Long.parseLong(commandTokenizer.nextToken()); int
                     * cid = Long.parseLong(commandTokenizer.nextToken());
                     * if (expressionCounter == cid) { command = comm;
                     * commands.removeElementAt(i); break; } }
                     */
                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    /*
                     * int expressionReference = 0; Highlight highlight =
                     * null;
                     * 
                     * if (!exprs.empty()) {
                     * 
                     * StringTokenizer expressionTokenizer = new
                     * StringTokenizer( (String) exprs.peek(), Code.DELIM);
                     * 
                     * oper =
                     * Long.parseLong(expressionTokenizer.nextToken());
                     * 
                     * expressionReference = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * //Make the location information for the location
                     * token highlight = ECodeUtilities.makeHighlight(
                     * expressionTokenizer.nextToken()); }
                     * 
                     * //Do different things depending on in what
                     * expression //the literal is used.
                     * 
                     * //If operator is assignment we just store the value
                     * if (oper == Code.A){ values.put(new
                     * Long(expressionCounter), expressionValue);
                     * 
                     * //If oper is other binary operator we will show it
                     * //on the screen with operator } else if
                     * (ECodeUtilities.isBinary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveBinOperator(oper);
                     * 
                     * if (command == Code.LEFT) {
                     * 
                     * director.beginBinaryExpression(expressionValue,
                     * operator, expressionReference, highlight); } else if
                     * (command == Code.RIGHT) {
                     * 
                     * ExpressionActor ea = (ExpressionActor)
                     * director.getCurrentScratch().findActor(expressionReference);
                     * if (ea != null) {
                     * director.rightBinaryExpression(expressionValue, ea,
                     * highlight); } else { values.put(new
                     * Long(expressionCounter), expressionValue); } } else {
                     * values.put(new Long(expressionCounter),
                     * expressionValue); }
                     * 
                     * //If oper is a unary operator we will show it //on
                     * the screen with operator } else if
                     * (ECodeUtilities.isUnary(oper)) {
                     * 
                     * values.put(new Long(expressionCounter),
                     * expressionValue);
                     * 
                     * int operator =
                     * ECodeUtilities.resolveUnOperator(oper);
                     * 
                     * if (command == Code.RIGHT) {
                     * director.beginUnaryExpression(operator,
                     * expressionValue, expressionReference, highlight); }
                     * 
                     * //If it is something else we will store it for later
                     * use. } else {
                     * 
                     * values.put(new Long(expressionCounter),
                     * expressionValue); }
                     */
                    break;
                }

                //Variable Declaration
                case Code.VD: {

                    String variableName = tokenizer.nextToken();
                    long initializerExpression = Long.parseLong(tokenizer
                            .nextToken());
                    String value = null;
                    if (tokenizer.countTokens() >= 4) {
                        value = tokenizer.nextToken();
                    } else {
                        value = "";
                    }
                    String type = tokenizer.nextToken();
                    String modifier = tokenizer.nextToken();

                    //Make the location information for the location token
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    Variable var = director.declareVariable(variableName, type,
                            highlight);

                    Value casted = null;

                    if (MCodeUtilities.isPrimitive(type)) {
                        casted = new Value(value, type);
                    } else {
                        if (value.equals("null")) {
                            casted = new Reference(type);
                        } else {
                            Instance inst = (Instance) instances
                                    .get(MCodeUtilities.getHashCode(value));

                            if (inst != null) {
                                casted = new Reference(inst);
                            } else {
                                casted = new Reference(type);
                            }
                        }
                        casted.setActor(var.getActor().getValue());
                    }

                    if (initializerExpression > 0) {

                        Value val = (Value) values.remove(new Long(
                                initializerExpression));

                        Value copiedValue = director.prepareForAssignment(var,
                                val);
                        director.animateAssignment(var, val, copiedValue,
                                casted, null, highlight);

                        Object[] postIncDec = (Object[]) postIncsDecs
                                .remove(new Long(initializerExpression));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }
                    }

                    director.closeScratch();
                    director.openScratch();

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

                    Variable var = director.getCurrentMethodFrame()
                            .getVariable(variableName);

                    //command that waits for this expression
                    int command = -1;
                    int oper = -1;
                    int size = commands.size();

                    //We find the command
                    for (int i = size - 1; i >= 0; i--) {
                        StringTokenizer commandTokenizer = new StringTokenizer(
                                (String) commands.elementAt(i), Code.DELIM);
                        int comm = Integer.parseInt(commandTokenizer
                                .nextToken());
                        long cid = Long.parseLong(commandTokenizer.nextToken());

                        if (expressionCounter == cid) {
                            command = comm;
                            commands.removeElementAt(i);
                            break;
                        }
                    }

                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */

                    long expressionReference = 0;
                    Highlight highlight = null;
                    if (!exprs.empty()) {
                        StringTokenizer expressionTokenizer = new StringTokenizer(
                                (String) exprs.peek(), Code.DELIM);

                        oper = Integer
                                .parseInt(expressionTokenizer.nextToken());

                        expressionReference = Long
                                .parseLong(expressionTokenizer.nextToken());

                        //Make the location information for the location
                        // token
                        highlight = MCodeUtilities
                                .makeHighlight(expressionTokenizer.nextToken());
                    }

                    Value val = null;
                    if (MCodeUtilities.isPrimitive(type)) {
                        val = new Value(value, type);
                        ValueActor va = var.getActor().getValue();
                        val.setActor(va);
                    } else {
                        if (value.equals("null")) {
                            val = new Reference(type);
                        } else {
                            Instance inst = (Instance) instances
                                    .get(MCodeUtilities.getHashCode(value));
                            if (inst != null) {
                                val = new Reference(inst);
                            } else {
                                val = new Reference(type);
                            }
                        }
                        val.setActor(var.getActor().getValue());
                        variables.put(new Long(expressionCounter), var);
                    }

                    /*
                     * Do different kind of things depending on in what
                     * expression the variable is used.
                     */

                    //If operator is assignment we just store the value
                    if (oper == Code.A) {
                        if (command == Code.TO) {
                            variables.put(new Long(expressionCounter), var);
                        } else {
                            values.put(new Long(expressionCounter), val);
                        }
                        //If oper is other binary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isBinary(oper)) {
                        int operator = MCodeUtilities.resolveBinOperator(oper);
                        if (command == Code.LEFT) {
                            director.beginBinaryExpression(val, operator,
                                    expressionReference, highlight);
                        } else if (command == Code.RIGHT) {
                            ExpressionActor ea = director.getCurrentScratch()
                                    .findActor(expressionReference);
                            if (ea != null) {
                                director.rightBinaryExpression(val, ea,
                                        highlight);
                            } else {
                                values.put(new Long(expressionCounter), val);
                            }
                        } else {
                            values.put(new Long(expressionCounter), val);
                        }

                        //If oper is a unary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isUnary(oper)) {
                        if (oper == Code.PRIE || oper == Code.PRDE) {
                            variables.put(new Long(expressionCounter), var);
                            values.put(new Long(expressionCounter), val);
                        } else if (oper == Code.PIE || oper == Code.PDE) {
                            variables.put(new Long(expressionCounter), var);
                            values.put(new Long(expressionReference), val);
                            values.put(new Long(expressionCounter), val);
                        } else {
                            values.put(new Long(expressionCounter), val);
                            int operator = MCodeUtilities
                                    .resolveUnOperator(oper);
                            if (command == Code.RIGHT) {
                                director.beginUnaryExpression(operator, val,
                                        expressionReference, highlight);
                            }
                        }
                        //If it is something else we will store it for
                        // later use.
                    } else {
                        values.put(new Long(expressionCounter), val);
                        variables.put(new Long(expressionCounter), var);
                    }

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
                        /**
                         * There is no third token because the literal is
                         * an empty string
                         */
                        value = "";
                    }

                    //Fourth token is the type of the literal
                    String type = tokenizer.nextToken();

                    //Fifth token is the highlight information.
                    //Not used because the whole expression is
                    // highlighted.
                    //Highlight highlight =
                    //ECodeUtilities.makeHighlight(tokenizer.nextToken());

                    Value lit = new Value(value, type);
                    director.introduceLiteral(lit);

                    handleExpression(lit, expressionCounter);

                    /*
                     * //command that wait for this expression (left,
                     * right) int command = -1; int oper = -1; int size =
                     * commands.size();
                     * 
                     * //We find the command for (int i = size - 1; i >= 0;
                     * i--) { StringTokenizer commandTokenizer = new
                     * StringTokenizer( (String) commands.elementAt(i),
                     * Code.DELIM); int comm =
                     * Long.parseLong(commandTokenizer.nextToken()); int
                     * cid = Long.parseLong(commandTokenizer.nextToken());
                     * if (expressionCounter == cid) { command = comm;
                     * commands.removeElementAt(i); break; } }
                     */
                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    /*
                     * int expressionReference = 0; Highlight highlight =
                     * null;
                     * 
                     * if (!exprs.empty()) { StringTokenizer
                     * expressionTokenizer = new StringTokenizer( (String)
                     * exprs.peek(), Code.DELIM);
                     * 
                     * oper =
                     * Long.parseLong(expressionTokenizer.nextToken());
                     * 
                     * expressionReference = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * //Make the location information for the location
                     * token highlight = ECodeUtilities.makeHighlight(
                     * expressionTokenizer.nextToken()); }
                     * 
                     * //Value of the literal Value lit = new Value(value,
                     * type); director.introduceLiteral(lit);
                     * 
                     * //Do different things depending on in what
                     * expression //the literal is used.
                     * 
                     * //If operator is assignment we just store the value
                     * if (oper == Code.A){ values.put(new
                     * Long(expressionCounter), lit);
                     * 
                     * //If oper is other binary operator we will show it
                     * //on the screen with operator } else if
                     * (ECodeUtilities.isBinary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveBinOperator(oper);
                     * 
                     * if (command == Code.LEFT) {
                     * 
                     * director.beginBinaryExpression(lit, operator,
                     * expressionReference, highlight); } else if (command ==
                     * Code.RIGHT) {
                     * 
                     * ExpressionActor ea = (ExpressionActor)
                     * director.getCurrentScratch().findActor(expressionReference);
                     * if (ea != null) {
                     * director.rightBinaryExpression(lit, ea, highlight); }
                     * else { values.put(new Long(expressionCounter), lit); } }
                     * else { values.put(new Long(expressionCounter), lit); }
                     * 
                     * //If oper is a unary operator we will show it //on
                     * the screen with operator } else if
                     * (ECodeUtilities.isUnary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveUnOperator(oper);
                     * values.put(new Long(expressionCounter), lit); if
                     * (command == Code.RIGHT) {
                     * director.beginUnaryExpression(operator, lit,
                     * expressionReference, highlight); }
                     * 
                     * //If it is something else we will store it for later
                     * use. } else { values.put(new
                     * Long(expressionCounter), lit); }
                     */
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

                    //Create here Object Stage with initial variables and
                    // values
                    ClassInfo ci = (ClassInfo) classes.get(declaringClass);

                    //If ci is not null it means that we are dealing with
                    //user defined class and can find the class
                    // information
                    //extracted during the compilation with DynamicJava.
                    //If ci is null there is no user defined class and we
                    //need to use the Class.for(String name) method to
                    //find out as much as possible from the class.
                    if (ci == null) {
                        Class declaredClass = null;
                        try {
                            declaredClass = Class.forName(declaringClass);
                        } catch (Exception e) {
                            //String message = "<H1>Runtime Error</H1>
                            // <P>The class that was supposed to be
                            // initiated could not be found.</P>";
                            director
                                    .showErrorMessage(new InterpreterError(
                                            messageBundle
                                                    .getString("notfoundclass.exception")));
                        }
                        ci = new ClassInfo(declaredClass);
                        classes.put(ci.getName(), ci);
                    }

                    //This works for not primitive classes.
                    //There needs to be a check whether invoked
                    //class is primitive or not.
                    //ObjectFrame of = createNewInstance(ci, highlight);
                    //Reference ref = new Reference(of);

                    invokingMethod = true;

                    if (currentMethodInvocation != null) {
                        methodInvocation.push(currentMethodInvocation);
                    }
                    currentMethodInvocation = new Object[9];

                    int n = currentMethodInvocation.length;
                    for (int i = 0; i < n; i++) {
                        currentMethodInvocation[i] = null;
                    }

                    currentMethodInvocation[0] = constructorName;
                    currentMethodInvocation[1] = "";

                    Value[] parameterValues = new Value[parameterCount];
                    String[] parameterTypes = new String[parameterCount];
                    String[] parameterNames = new String[parameterCount];
                    Long[] parameterExpressionReferences = new Long[parameterCount];

                    for (int i = 0; i < parameterCount; i++) {
                        parameterValues[i] = null;
                        parameterTypes[i] = null;
                        parameterNames[i] = null;
                        parameterExpressionReferences[i] = null;
                    }

                    currentMethodInvocation[2] = parameterValues;
                    currentMethodInvocation[3] = parameterTypes;
                    currentMethodInvocation[4] = parameterNames;
                    currentMethodInvocation[5] = highlight;
                    currentMethodInvocation[7] = parameterExpressionReferences;
                    //Here we put the ClassInfo of the class in the array
                    //just to wait for the Method Declaration to be read
                    // and the
                    //object can be created from this method info.
                    currentMethodInvocation[8] = ci /* ref */;

                    /* objectCreation.push(new Reference(of)); */

                    break;
                }

                case Code.CONSCN: {

                    constructorCall = true;
                    superMethodsReading = new Vector();
                    superMethodCallNumber = Long.parseLong(tokenizer
                            .nextToken());

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

                    //This should handle the possible object
                    //assignment etc.
                    if (!objectCreation.empty()) {
                        //First reference to the created object is taken
                        Reference ref = (Reference) objectCreation.pop();
                        Instance inst = ref.getInstance();
                        inst.setHashCode(hashCode);
                        //The instance is putted in the hashtable that
                        //keeps the instances
                        instances.put(hashCode, inst);
                        //Then we handle the possible expressions
                        //concerning this reference.
                    }

                    Value ret = director.getCurrentMethodFrame().getVariable(
                            "this").getValue();
                    Value casted = null;
                    Instance inst = (Instance) instances.get(hashCode);

                    if (inst != null) {
                        casted = new Reference(inst);
                    } else {
                        casted = new Reference("null");
                    }

                    Actor returnActor = director.animateReturn(ret, casted, h);
                    Value returnValue = (Value) casted.clone();
                    Value rv;

                    if (returnValue instanceof Reference) {
                        rv = (Value) ((Reference) returnValue).clone();
                    } else {
                        rv = (Value) returnValue.clone();
                    }

                    ValueActor va = director.finishMethod(returnActor,
                            expressionCounter);
                    rv.setActor(va);
                    handleExpression(rv, expressionCounter);

                    break;
                }

                // Object field access
                case Code.OFA: {

                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long objectCounter = Long.parseLong(tokenizer.nextToken());
                    String variableName = tokenizer.nextToken();
                    String value = "";
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    }

                    String type = tokenizer.nextToken();

                    Reference objVal = (Reference) values.remove(new Long(
                            objectCounter));
                    if (objVal == null && !objectCreation.empty()) {
                        objVal = (Reference) objectCreation.peek();
                    }

                    ObjectFrame obj = (ObjectFrame) objVal.getInstance();

                    if (obj == null && !objectCreation.empty()) {
                        objVal = (Reference) objectCreation.peek();
                        obj = (ObjectFrame) objVal.getInstance();
                    }

                    Variable var = obj.getVariable(variableName);

                    //command that waits for this expression
                    int command = -1;
                    int oper = -1;
                    int size = commands.size();

                    //We find the command
                    for (int i = size - 1; i >= 0; i--) {
                        StringTokenizer commandTokenizer = new StringTokenizer(
                                (String) commands.elementAt(i), Code.DELIM);
                        int comm = Integer.parseInt(commandTokenizer
                                .nextToken());
                        long cid = Long.parseLong(commandTokenizer.nextToken());

                        if (expressionCounter == cid) {
                            command = comm;
                            commands.removeElementAt(i);
                            break;
                        }
                    }

                    /**
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    long expressionReference = 0;
                    Highlight highlight = null;
                    if (!exprs.empty()) {
                        StringTokenizer expressionTokenizer = new StringTokenizer(
                                (String) exprs.peek(), Code.DELIM);

                        oper = Integer
                                .parseInt(expressionTokenizer.nextToken());

                        expressionReference = Long
                                .parseLong(expressionTokenizer.nextToken());

                        //Make the location information for the location
                        // token
                        highlight = MCodeUtilities
                                .makeHighlight(expressionTokenizer.nextToken());
                    }

                    Value val = null;
                    if (MCodeUtilities.isPrimitive(type)) {
                        val = new Value(value, type);
                        ValueActor va = var.getActor().getValue();
                        val.setActor(va);
                    } else {
                        if (value.equals("null")) {
                            val = new Reference(type);
                        } else {
                            Instance inst = (Instance) instances
                                    .get(MCodeUtilities.getHashCode(value));
                            if (inst != null) {
                                val = new Reference(inst);
                            } else {
                                val = new Reference(type);
                            }
                        }
                        val.setActor(var.getActor().getValue());
                    }

                    /**
                     * Do different kind of things depending on in what
                     * expression the variable is used.
                     */

                    //If operator is assignment we just store the value
                    if (oper == Code.A) {

                        if (command == Code.TO) {

                            variables.put(new Long(expressionCounter), var);

                        } else {

                            values.put(new Long(expressionCounter), val);
                        }

                        //If oper is other binary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isBinary(oper)) {

                        int operator = MCodeUtilities.resolveBinOperator(oper);

                        if (command == Code.LEFT) {

                            director.beginBinaryExpression(val, operator,
                                    expressionReference, highlight);

                        } else if (command == Code.RIGHT) {

                            ExpressionActor ea = director.getCurrentScratch()
                                    .findActor(expressionReference);
                            if (ea != null) {

                                director.rightBinaryExpression(val, ea,
                                        highlight);

                            } else {
                                values.put(new Long(expressionCounter), val);
                            }
                        } else {
                            values.put(new Long(expressionCounter), val);
                        }

                        //If oper is a unary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isUnary(oper)) {

                        if (oper == Code.PRIE || oper == Code.PRDE) {

                            variables.put(new Long(expressionCounter), var);
                            values.put(new Long(expressionCounter), val);

                        } else if (oper == Code.PIE || oper == Code.PDE) {

                            variables.put(new Long(expressionCounter), var);
                            values.put(new Long(expressionReference), val);
                            values.put(new Long(expressionCounter), val);

                        } else {

                            values.put(new Long(expressionCounter), val);
                            int operator = MCodeUtilities
                                    .resolveUnOperator(oper);
                            if (command == Code.RIGHT) {
                                director.beginUnaryExpression(operator, val,
                                        expressionReference, highlight);
                            }
                        }

                        //If it is something else we will store it for
                        // later use.
                    } else {

                        values.put(new Long(expressionCounter), val);
                        variables.put(new Long(expressionCounter), var);

                    }

                    break;
                }

                // Object method call
                case Code.OMC: {

                    String methodName = tokenizer.nextToken();
                    int parameterCount = Integer
                            .parseInt(tokenizer.nextToken());
                    long objectCounter = Long.parseLong(tokenizer.nextToken());
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    Value val = (Value) values.remove(new Long(objectCounter));
                    Variable var = (Variable) variables.remove(new Long(
                            objectCounter));

                    if (val == null && !objectCreation.empty()) {
                        val = (Reference) objectCreation.peek();
                    } else if (val.getValue().equals("null")
                            && !objectCreation.empty()) {
                        val = (Reference) objectCreation.peek();
                    }

                    if (val instanceof Reference) {
                        ObjectFrame obj = (ObjectFrame) ((Reference) val)
                                .getInstance();

                        if (obj == null && !objectCreation.empty()) {
                            val = (Reference) objectCreation.peek();
                            obj = (ObjectFrame) ((Reference) val).getInstance();
                        }
                    }

                    invokingMethod = true;

                    if (currentMethodInvocation != null) {
                        methodInvocation.push(currentMethodInvocation);
                    }
                    currentMethodInvocation = new Object[9];

                    /*
                     * int n = currentMethodInvocation.length;
                     *
                     * for (int i = 0; i < n; i++) {
                     * currentMethodInvocation[i] = null; }
                     */

                    currentMethodInvocation[0] = methodName;

                    if (var != null) {
                        currentMethodInvocation[1] = var.getName();
                    } else {
                        if (val instanceof Reference) {
                            currentMethodInvocation[1] = "new "
                                    + ((Reference) val).getInstance().getType();
                        } else {
                            currentMethodInvocation[1] = val.getValue();
                        }
                    }

                    Value[] parameterValues = new Value[parameterCount];
                    String[] parameterTypes = new String[parameterCount];
                    String[] parameterNames = new String[parameterCount];
                    Long[] parameterExpressionReferences = new Long[parameterCount];

                    for (int i = 0; i < parameterCount; i++) {
                        parameterValues[i] = null;
                        parameterTypes[i] = null;
                        parameterNames[i] = null;
                        parameterExpressionReferences[i] = null;
                    }

                    currentMethodInvocation[2] = parameterValues;
                    currentMethodInvocation[3] = parameterTypes;
                    currentMethodInvocation[4] = parameterNames;
                    currentMethodInvocation[5] = highlight;
                    currentMethodInvocation[7] = parameterExpressionReferences;
                    currentMethodInvocation[8] = val;

                    break;
                }

                // Object method call close
                case Code.OMCC: {

                    if (!returned) {

                        director.finishMethod(null, 0);

                    } else {

                        Value rv = null;

                        if (returnValue instanceof Reference) {
                            rv = (Value) ((Reference) returnValue).clone();
                        } else {

                            rv = (Value) returnValue.clone();
                        }

                        ValueActor va = director.finishMethod(returnActor,
                                returnExpressionCounter);
                        rv.setActor(va);

                        handleExpression(rv, returnExpressionCounter);
                    }

                    returned = false;

                    break;
                }

                //Static Method Call
                case Code.SMC: {

                    invokingMethod = true;

                    if (currentMethodInvocation != null) {
                        methodInvocation.push(currentMethodInvocation);
                    }
                    currentMethodInvocation = new Object[8];

                    for (int i = 0; i < currentMethodInvocation.length; i++) {
                        currentMethodInvocation[i] = null;
                    }

                    currentMethodInvocation[0] = tokenizer.nextToken();
                    currentMethodInvocation[1] = tokenizer.nextToken();
                    int parameterCount = Integer
                            .parseInt(tokenizer.nextToken());

                    Value[] parameterValues = new Value[parameterCount];
                    String[] parameterTypes = new String[parameterCount];
                    String[] parameterNames = new String[parameterCount];
                    Long[] parameterExpressionReferences = new Long[parameterCount];

                    for (int i = 0; i < parameterCount; i++) {
                        parameterValues[i] = null;
                        parameterTypes[i] = null;
                        parameterNames[i] = null;
                        parameterExpressionReferences[i] = null;
                    }

                    currentMethodInvocation[2] = parameterValues;
                    currentMethodInvocation[3] = parameterTypes;
                    currentMethodInvocation[4] = parameterNames;
                    currentMethodInvocation[5] = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());
                    currentMethodInvocation[7] = parameterExpressionReferences;

                    break;
                }

                //Parameter
                case Code.P: {

                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());

                    Value[] parameterValues = (Value[]) currentMethodInvocation[2];
                    String[] parameterTypes = (String[]) currentMethodInvocation[3];
                    Long[] parameterExpressionReferences = (Long[]) currentMethodInvocation[7];

                    Value parameterValue = (Value) values.remove(new Long(
                            expressionReference));

                    //if (parameterValue == null) {
                    //  System.out.println("Mistake");
                    //}

                    int i = 0;
                    while (parameterValues[i] != null) {
                        i++;
                    }
                    parameterValues[i] = parameterValue;
                    parameterTypes[i] = tokenizer.nextToken();
                    parameterExpressionReferences[i] = new Long(
                            expressionReference);

                    exprs.pop();

                    break;
                }

                //Method declaration
                case Code.MD: {

                    //Make the location information for the location token
                    currentMethodInvocation[6] = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    //Object method call or constructor
                    if (currentMethodInvocation.length == 9) {

                        Value[] args = null;

                        if (currentMethodInvocation[1] != null
                                && ((String) currentMethodInvocation[1])
                                        .equals("")) {
                            //System.out.println("CI: " + "new " +
                            // ((String) currentMethodInvocation[0]));
                            args = director.animateConstructorInvocation(
                                    (String) currentMethodInvocation[0],
                                    (Value[]) currentMethodInvocation[2],
                                    (Highlight) currentMethodInvocation[5]);

                            //This works for not primitive classes.
                            //There needs to be a check whether invoked
                            //class is primitive or not.
                            if (!MCodeUtilities
                                    .isPrimitive(((ClassInfo) currentMethodInvocation[8])
                                            .getName())) {
                                ObjectFrame of = createNewInstance(
                                        (ClassInfo) currentMethodInvocation[8],
                                        (Highlight) currentMethodInvocation[5]);

                                Reference ref = new Reference(of);
                                currentMethodInvocation[8] = ref;
                                objectCreation.push(new Reference(of));
                            } else {
                                //TODO: Make the contructor call work for
                                // semi-primitive types
                            }

                        } else {
                            //System.out.println("OMI: " + "." + ((String)
                            // currentMethodInvocation[0]));
                            args = director.animateOMInvocation("."
                                    + ((String) currentMethodInvocation[0]),
                                    (Value[]) currentMethodInvocation[2],
                                    (Highlight) currentMethodInvocation[5],
                                    (Value) currentMethodInvocation[8]);
                        }

                        String call;

                        if (currentMethodInvocation[1] != null
                                && ((String) currentMethodInvocation[1])
                                        .equals("")) {
                            call = (String) currentMethodInvocation[0];
                        } else if (((String) currentMethodInvocation[0])
                                .startsWith("super")) {
                            call = "this."
                                    + (String) currentMethodInvocation[0];
                        } else if (currentMethodInvocation[1] == null) {
                            call = ((Value) currentMethodInvocation[8])
                                    .getValue()
                                    + "." + (String) currentMethodInvocation[0];
                        } else {
                            call = (String) currentMethodInvocation[1] + "."
                                    + (String) currentMethodInvocation[0];
                            //System.out.println("METHOD: " + call);
                        }

                        director.setUpMethod(call, args,
                                (String[]) currentMethodInvocation[4],
                                (String[]) currentMethodInvocation[3],
                                (Highlight) currentMethodInvocation[6],
                                (Value) currentMethodInvocation[8]);

                        //Static method invocation
                    } else {
                        Value[] args = null;
                        if (start) {
                            args = director
                                    .animateSMInvocation(
                                            ((String) currentMethodInvocation[1])
                                                    + "."
                                                    + ((String) currentMethodInvocation[0]),
                                            (Value[]) currentMethodInvocation[2],
                                            null);
                            start = false;
                        } else {
                            args = director
                                    .animateSMInvocation(
                                            ((String) currentMethodInvocation[1])
                                                    + "."
                                                    + ((String) currentMethodInvocation[0]),
                                            (Value[]) currentMethodInvocation[2],
                                            (Highlight) currentMethodInvocation[5]);
                        }

                        director
                                .setUpMethod(
                                        ((String) currentMethodInvocation[1])
                                                + "."
                                                + ((String) currentMethodInvocation[0]),
                                        args,
                                        (String[]) currentMethodInvocation[4],
                                        (String[]) currentMethodInvocation[3],
                                        (Highlight) currentMethodInvocation[6]);
                    }

                    Long[] parameterExpressionReferences = (Long[]) currentMethodInvocation[7];

                    if (parameterExpressionReferences != null) {
                        int i = 0;
                        while (i < parameterExpressionReferences.length) {
                            Object[] postIncDec = (Object[]) postIncsDecs
                                    .remove(parameterExpressionReferences[i]);
                            if (postIncDec != null) {
                                doPostIncDec(postIncDec);
                            }
                            i++;
                        }
                    }

                    if (!methodInvocation.empty()) {
                        currentMethodInvocation = (Object[]) methodInvocation
                                .pop();
                    } else {
                        currentMethodInvocation = null;
                    }

                    invokingMethod = false;

                    break;
                }

                //Parameters list
                case Code.PARAMETERS: {

                    if (tokenizer.hasMoreTokens()) {
                        String parameters = tokenizer.nextToken();
                        String[] parameterNames = (String[]) currentMethodInvocation[4];
                        StringTokenizer names = new StringTokenizer(parameters,
                                ",");
                        for (int i = 0; i < parameterNames.length; i++) {
                            parameterNames[i] = names.nextToken();
                        }
                    }
                    break;
                }

                // Return Statement
                case Code.R: {

                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = null;
                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    } else {
                        value = "";
                    }
                    String type = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    if (type.equals(Void.TYPE.getName())) {

                        //director.finishMethod(null, expressionCounter);
                        returned = false;

                    } else {

                        Value ret = (Value) values.remove(new Long(
                                expressionReference));

                        Value casted = null;

                        if (MCodeUtilities.isPrimitive(type)) {
                            casted = new Value(value, type);
                        } else {
                            Instance inst = (Instance) instances
                                    .get(MCodeUtilities.getHashCode(value));
                            if (inst != null) {
                                casted = new Reference(inst);
                            } else {
                                casted = new Reference(type);
                            }
                        }

                        returnActor = director.animateReturn(ret, casted, h);
                        returnValue = (Value) casted.clone();
                        returnExpressionCounter = expressionCounter;
                        returned = true;

                    }

                    exprs.pop();

                    break;
                }

                // Static method call closed
                case Code.SMCC: {

                    if (!returned) {

                        director.finishMethod(null, 0);

                    } else {

                        Value rv = null;

                        if (returnValue instanceof Reference) {
                            rv = (Value) ((Reference) returnValue).clone();
                        } else {

                            rv = (Value) returnValue.clone();
                        }

                        ValueActor va = director.finishMethod(returnActor,
                                returnExpressionCounter);
                        rv.setActor(va);

                        handleExpression(rv, returnExpressionCounter);
                    }

                    returned = false;

                    /*
                     * //command that wait for this expression (left,
                     * right) int command = -1; int size = commands.size();
                     * //We find the command for (int i = size - 1; i >= 0;
                     * i--) { StringTokenizer commandTokenizer = new
                     * StringTokenizer( (String) commands.elementAt(i),
                     * Code.DELIM); command =
                     * Long.parseLong(commandTokenizer.nextToken()); int
                     * cid = Long.parseLong(commandTokenizer.nextToken());
                     * if (returnExpressionCounter == cid) {
                     * commands.removeElementAt(i); break; } }
                     */
                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    /*
                     * int expressionReference = 0; int oper = -1;
                     * Highlight highlight = null;
                     * 
                     * if (!exprs.empty()) {
                     * 
                     * StringTokenizer expressionTokenizer = new
                     * StringTokenizer( (String) exprs.peek(), Code.DELIM);
                     * 
                     * oper =
                     * Long.parseLong(expressionTokenizer.nextToken());
                     * 
                     * expressionReference = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * //Make the location information for the location
                     * token highlight = ECodeUtilities.makeHighlight(
                     * expressionTokenizer.nextToken()); }
                     * 
                     * //Do different things to the return value
                     * //depending on in what expression the return value
                     * is used.
                     * 
                     * //If operator is assignment we just store the value
                     * if (oper == Code.A){
                     * 
                     * values.put(new Long(returnExpressionCounter), rv);
                     * 
                     * //If oper is other binary operator we will show it
                     * //on the screen with operator } else if
                     * (ECodeUtilities.isBinary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveBinOperator(oper);
                     * 
                     * if (command == Code.LEFT) {
                     * 
                     * director.beginBinaryExpression(rv, operator,
                     * expressionReference, highlight); } else if (command ==
                     * Code.RIGHT) {
                     * 
                     * ExpressionActor ea = (ExpressionActor)
                     * director.getCurrentScratch().findActor(expressionReference);
                     * director.rightBinaryExpression(rv, ea, highlight); }
                     * else { values.put(new Long(returnExpressionCounter),
                     * rv); }
                     * 
                     * //If oper is a unary operator we will show it //on
                     * the screen with operator } else if
                     * (ECodeUtilities.isUnary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveUnOperator(oper);
                     * 
                     * values.put(new Long(returnExpressionCounter), rv);
                     * 
                     * if (command == Code.RIGHT) {
                     * director.beginUnaryExpression(operator, rv,
                     * expressionReference, highlight); }
                     * 
                     * //If it is something else we will store it for later
                     * use. } else {
                     * 
                     * values.put(new Long(returnExpressionCounter), rv); } }
                     * returned = false;
                     */
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

                    Value result = (Value) values.remove(new Long(
                            expressionReference));

                    if (value.equals(Boolean.TRUE.toString())) {
                        director.branchThen(result, h);
                    } else {
                        director.skipIf(result, h);
                    }

                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                //IF Then Else Statement
                case Code.IFTE: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());
                    String value = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    Value result = (Value) values.remove(new Long(
                            expressionReference));

                    if (value.equals(Boolean.TRUE.toString())) {
                        director.branchThen(result, h);
                    } else {
                        director.branchElse(result, h);
                    }

                    director.closeScratch();
                    director.openScratch();

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

                    Value result = (Value) values.remove(new Long(
                            expressionReference));

                    if (round == 0) {

                        if (value.equals(Boolean.TRUE.toString())) {
                            director.enterLoop(propertiesBundle
                                    .getStringProperty("statement_name.while"),
                                    result, h);
                        } else {
                            director.skipLoop(propertiesBundle
                                    .getStringProperty("statement_name.while"),
                                    result);
                        }

                    } else {

                        if (value.equals(Boolean.TRUE.toString())) {
                            director.continueLoop(propertiesBundle
                                    .getStringProperty("statement_name.while"),
                                    result, h);
                        } else {
                            director.exitLoop(propertiesBundle
                                    .getStringProperty("statement_name.while"),
                                    result);
                        }

                    }

                    director.closeScratch();
                    director.openScratch();

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

                    Value result = (Value) values.remove(new Long(
                            expressionReference));

                    if (round == 0) {
                        if (value.equals(Boolean.TRUE.toString())) {
                            director.enterLoop(propertiesBundle
                                    .getStringProperty("statement_name.for"),
                                    result, h);
                        } else {
                            director.skipLoop(propertiesBundle
                                    .getStringProperty("statement_name.for"),
                                    result);
                        }
                    } else {
                        if (value.equals(Boolean.TRUE.toString())) {
                            director.continueLoop(propertiesBundle
                                    .getStringProperty("statement_name.for"),
                                    result, h);
                        } else {
                            director.exitLoop(propertiesBundle
                                    .getStringProperty("statement_name.for"),
                                    result);
                        }
                    }
                    director.closeScratch();
                    director.openScratch();
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

                    Value result = (Value) values.remove(new Long(
                            expressionReference));

                    if (round == 0) {
                        director.enterLoop(propertiesBundle
                                .getStringProperty("statement_name.do_while"),
                                h);
                    } else {
                        if (value.equals(Boolean.TRUE.toString())) {
                            director
                                    .continueLoop(
                                            propertiesBundle
                                                    .getStringProperty("statement_name.do_while"),
                                            result, h);
                        } else {
                            director
                                    .exitLoop(
                                            propertiesBundle
                                                    .getStringProperty("statement_name.do_while"),
                                            result);
                        }
                    }
                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                case Code.SWITCHB: {
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    director.openSwitch(h);
                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                case Code.SWIBF: {
                    long selectorReference = Long.parseLong(tokenizer
                            .nextToken());
                    long switchBlockReference = Long.parseLong(tokenizer
                            .nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    if (switchBlockReference != -1) {
                        Value selector = (Value) values.remove(new Long(
                                selectorReference));
                        Value switchBlock = (Value) values.remove(new Long(
                                switchBlockReference));
                        Value result = new Value("true", "boolean");

                        director.animateBinaryExpression(MCodeUtilities
                                .resolveBinOperator(Code.EE), selector,
                                switchBlock, result, -3, h);
                        director.switchSelected(h);
                    } else {
                        director.switchDefault(h);
                    }

                    break;
                }

                case Code.SWITCH: {
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    director.closeSwitch(h);

                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                //Break Statement
                case Code.BR: {
                    int statementName = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    String stmt = "";

                    if (statementName == Code.WHI) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.while");
                        director.breakLoop(stmt, h);
                    } else if (statementName == Code.FOR) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.for");
                        director.breakLoop(stmt, h);
                    } else if (statementName == Code.DO) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.do_while");
                        director.breakLoop(stmt, h);
                    } else if (statementName == Code.SWITCH) {
                        director.breakSwitch(h);
                    }

                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                //Continue Statement
                case Code.CONT: {
                    int statementName = Integer.parseInt(tokenizer.nextToken());
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    String stmt = "";

                    if (statementName == Code.WHI) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.while");
                    } else if (statementName == Code.FOR) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.for");
                    } else if (statementName == Code.DO) {
                        stmt = propertiesBundle
                                .getStringProperty("statement_name.do_while");
                    }

                    director.continueLoop(stmt, h);

                    director.closeScratch();
                    director.openScratch();

                    break;
                }

                //Opening and closing scopes
                case Code.OUTPUT: {
                    long expressionReference = Long.parseLong(tokenizer
                            .nextToken());

                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();
                    String breakLine = tokenizer.nextToken();
                    Highlight highlight = MCodeUtilities
                            .makeHighlight(tokenizer.nextToken());

                    Value output = (Value) values.remove(new Long(
                            expressionReference));

                    if (output == null) {
                        output = new Value(value, type);
                    }
                    if (breakLine.equals("1")) {
                        output.setValue(output.getValue() + "\\n");
                    }

                    //Value output = new Value(value, type);

                    director.output(output, highlight);

                    // To pop the OUTPUT statement on top of the expression Stack if it is there
                    if (!exprs.empty()) {
                        StringTokenizer expressionTokenizer = new StringTokenizer(
                                (String) exprs.peek(), Code.DELIM);
                        if (Integer.parseInt(expressionTokenizer.nextToken()) == (Code.OUTPUT)) {
                            exprs.pop();
                        }
                    }

                    // To handle the post increments and decrements if there are any.
                    Object[] postIncDec = (Object[]) postIncsDecs
                            .remove(new Long(expressionReference));
                    if (postIncDec != null) {
                        doPostIncDec(postIncDec);
                    }

                    break;
                }

                //Input needs to be read
                case Code.INPUT: {
                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());
                    String type = tokenizer.nextToken();

                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());
                    Value in = director.animateInputHandling(type, null, h);

                    input.println(in.getValue());
                    values.put(new Long(expressionCounter), in);

                    break;
                }

                //Inputted value is returned
                case Code.INPUTTED: {

                    long expressionCounter = Long.parseLong(tokenizer
                            .nextToken());

                    String value = tokenizer.nextToken();
                    String type = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    Value in = (Value) values
                            .remove(new Long(expressionCounter));
                    if (in == null) {
                        in = new Value(value, type);
                    }

                    handleExpression(in, expressionCounter);

                    /*
                     * //command that wait for this expression (left,
                     * right) int command = -1; int oper = -1; int size =
                     * commands.size();
                     * 
                     * //We find the command for (int i = size - 1; i >= 0;
                     * i--) { StringTokenizer commandTokenizer = new
                     * StringTokenizer( (String) commands.elementAt(i),
                     * Code.DELIM); int comm =
                     * Integer.parseInt(commandTokenizer.nextToken()); long
                     * cid = Long.parseLong(commandTokenizer.nextToken());
                     * if (expressionCounter == cid) { command = comm;
                     * commands.removeElementAt(i); break; } }
                     */
                    /*
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    /*
                     * long expressionReference = 0; Highlight highlight =
                     * null;
                     * 
                     * if (!exprs.empty()) { StringTokenizer
                     * expressionTokenizer = new StringTokenizer( (String)
                     * exprs.peek(), Code.DELIM);
                     * 
                     * oper = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * expressionReference = Long.parseLong(
                     * expressionTokenizer.nextToken());
                     * 
                     * //Make the location information for the location
                     * token highlight = ECodeUtilities.makeHighlight(
                     * expressionTokenizer.nextToken()); }
                     * 
                     * //Do different things depending on in what
                     * expression //the literal is used.
                     * 
                     * //If operator is assignment we just store the value
                     * if (oper == Code.A){ values.put(new
                     * Long(expressionCounter), in);
                     * 
                     * //If oper is other binary operator we will show it
                     * //on the screen with operator } else if
                     * (ECodeUtilities.isBinary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveBinOperator(oper);
                     * 
                     * if (command == Code.LEFT) {
                     * 
                     * director.beginBinaryExpression(in, operator,
                     * expressionReference, highlight); } else if (command ==
                     * Code.RIGHT) {
                     * 
                     * ExpressionActor ea = (ExpressionActor)
                     * director.getCurrentScratch().findActor(expressionReference);
                     * if (ea != null) { director.rightBinaryExpression(in,
                     * ea, highlight); } else { values.put(new
                     * Long(expressionCounter), in); } } else {
                     * values.put(new Long(expressionCounter), in); }
                     * 
                     * //If oper is a unary operator we will show it //on
                     * the screen with operator } else if
                     * (ECodeUtilities.isUnary(oper)) {
                     * 
                     * int operator =
                     * ECodeUtilities.resolveUnOperator(oper);
                     * 
                     * values.put(new Long(expressionCounter), in);
                     * 
                     * if (command == Code.RIGHT) {
                     * director.beginUnaryExpression(operator, in,
                     * expressionReference, highlight); }
                     * 
                     * //If it is something else we will store it for later
                     * use. } else { values.put(new
                     * Long(expressionCounter), in); }
                     */
                    break;
                }

                //Opening and closing scopes
                case Code.SCOPE: {

                    int scope = Integer.parseInt(tokenizer.nextToken());

                    //Open the scope
                    if (scope == 1) {

                        director.openScope();
                        director.closeScratch();
                        director.openScratch();

                        //Close the scope
                    } else if (scope == 0) {

                        director.closeScope();
                        director.closeScratch();
                        director.openScratch();
                    }

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
                    StringTokenizer st = new StringTokenizer(
                            dimensionReferences, Code.LOC_DELIM);

                    long[] dimensionReference = new long[dims];

                    for (int i = 0; st.hasMoreTokens(); i++) {
                        dimensionReference[i] = Long.parseLong(st.nextToken());
                    }

                    //int values of the dimension sizes
                    String dimensionSizes = tokenizer.nextToken();
                    st = new StringTokenizer(dimensionSizes, ",");
                    int[] dimensionSize = new int[dims];

                    for (int i = 0; st.hasMoreTokens(); i++) {
                        dimensionSize[i] = Integer.parseInt(st.nextToken());
                    }

                    int actualDimension = Integer.parseInt(tokenizer
                            .nextToken());

                    Highlight h = null;
                    if (tokenizer.hasMoreElements()) {
                        h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                    }

                    Value[] dimensionValues = new Value[dims];

                    for (int i = 0; i < dims; i++) {
                        dimensionValues[i] = (Value) values.remove(new Long(
                                dimensionReference[i]));

                    }

                    ArrayInstance ai = new ArrayInstance(hashCode, compType,
                            dimensionSize.length, actualDimension,
                            dimensionSize[0]);

                    Reference ref = new Reference(ai);

                    director.showArrayCreation(ai, ref, null, null,
                            dimensionValues, expressionReference,
                            actualDimension, h);

                    //director.arrayCreation(dimensionSize, h);

                    instances.put(hashCode, ai);

                    ref.makeReference();

                    values.put(new Long(expressionReference), ref);

                    for (int i = 0; i < dims; i++) {
                        Object[] postIncDec = (Object[]) postIncsDecs
                                .remove(new Long(dimensionReference[i]));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }
                    }

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
                    StringTokenizer st = new StringTokenizer(
                            cellNumberReferences, ",");

                    long[] cellNumberReference = new long[dims];

                    for (int i = 0; st.hasMoreTokens(); i++) {
                        cellNumberReference[i] = Long.parseLong(st.nextToken());
                    }

                    //int values of the dimension sizes
                    String cellNumbers = tokenizer.nextToken();
                    st = new StringTokenizer(cellNumbers, ",");
                    int[] cellNumber = new int[dims];

                    for (int i = 0; st.hasMoreTokens(); i++) {
                        cellNumber[i] = Integer.parseInt(st.nextToken());
                    }

                    String value = null;

                    if (tokenizer.countTokens() >= 3) {
                        value = tokenizer.nextToken();
                    } else {
                        value = "";
                    }

                    String type = tokenizer.nextToken();

                    Highlight h = null;
                    if (tokenizer.hasMoreElements()) {
                        h = MCodeUtilities.makeHighlight(tokenizer.nextToken());
                    }

                    //Finding the VariableInArray
                    values.remove(new Long(expressionReference));
                    Variable variable = (Variable) variables.remove(new Long(
                            expressionReference));
                    Reference varRef = (Reference) variable.getValue();
                    ArrayInstance ainst = (ArrayInstance) varRef.getInstance();
                    int n = cellNumber.length;

                    VariableInArray[] vars = new VariableInArray[n];
                    for (int i = 0; i < n; i++) {
                        vars[i] = ainst.getVariableAt(cellNumber[i]);
                        if (i != n - 1) {
                            ainst = (ArrayInstance) ((Reference) vars[i]
                                    .getValue()).getInstance();
                        }
                    }
                    //Getting the right Values that point to the cell in
                    // the array
                    Value[] cellNumberValues = new Value[dims];
                    for (int i = 0; i < dims; i++) {
                        cellNumberValues[i] = (Value) values.remove(new Long(
                                cellNumberReference[i]));

                    }

                    //Actual value in the array in pointed cell
                    Value val = null;
                    if (MCodeUtilities.isPrimitive(type)) {
                        val = new Value(value, type);
                    } else {
                        if (value.equals("null")) {
                            val = new Reference(type);
                        } else {
                            Instance inst = (Instance) instances
                                    .get(MCodeUtilities.getHashCode(value));
                            if (inst != null) {
                                val = new Reference(inst);
                            } else {
                                val = new Reference(type);
                            }
                        }
                    }

                    director.showArrayAccess(vars, cellNumberValues, val, h);

                    exprs.pop();

                    //command that waits for this expression
                    int command = -1;
                    int oper = -1;
                    int size = commands.size();

                    //We find the command
                    for (int i = size - 1; i >= 0; i--) {
                        StringTokenizer commandTokenizer = new StringTokenizer(
                                (String) commands.elementAt(i), Code.DELIM);
                        int comm = Integer.parseInt(commandTokenizer
                                .nextToken());
                        long cid = Long.parseLong(commandTokenizer.nextToken());

                        if (expressionCounter == cid) {
                            command = comm;
                            commands.removeElementAt(i);
                            break;
                        }
                    }

                    /**
                     * Look from the expression stack what expression
                     * should be shown next
                     */
                    expressionReference = 0;
                    Highlight highlight = null;
                    if (!exprs.empty()) {
                        StringTokenizer expressionTokenizer = new StringTokenizer(
                                (String) exprs.peek(), Code.DELIM);

                        oper = Integer
                                .parseInt(expressionTokenizer.nextToken());

                        expressionReference = Long
                                .parseLong(expressionTokenizer.nextToken());

                        //Make the location information for the location
                        // token
                        highlight = MCodeUtilities
                                .makeHighlight(expressionTokenizer.nextToken());
                    }

                    /**
                     * Do different kind of things depending on in what
                     * expression the variable is used.
                     */

                    //If operator is assignment we just store the value
                    if (oper == Code.A) {

                        if (command == Code.TO) {

                            variables.put(new Long(expressionCounter),
                                    vars[n - 1]);

                        } else {

                            values.put(new Long(expressionCounter), val);
                        }

                        //If oper is other binary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isBinary(oper)) {

                        int operator = MCodeUtilities.resolveBinOperator(oper);

                        if (command == Code.LEFT) {

                            director.beginBinaryExpression(val, operator,
                                    expressionReference, highlight);

                        } else if (command == Code.RIGHT) {

                            ExpressionActor ea = director.getCurrentScratch()
                                    .findActor(expressionReference);
                            if (ea != null) {

                                director.rightBinaryExpression(val, ea,
                                        highlight);

                            } else {
                                values.put(new Long(expressionCounter), val);
                            }
                        } else {
                            values.put(new Long(expressionCounter), val);
                        }

                        //If oper is a unary operator we will show it
                        //on the screen with operator
                    } else if (MCodeUtilities.isUnary(oper)) {

                        if (oper == Code.PRIE || oper == Code.PRDE) {

                            variables.put(new Long(expressionCounter),
                                    vars[n - 1]);
                            values.put(new Long(expressionCounter), val);

                        } else if (oper == Code.PIE || oper == Code.PDE) {

                            variables.put(new Long(expressionCounter),
                                    vars[n - 1]);
                            values.put(new Long(expressionReference), val);
                            values.put(new Long(expressionCounter), val);

                        } else {

                            values.put(new Long(expressionCounter), val);
                            int operator = MCodeUtilities
                                    .resolveUnOperator(oper);
                            if (command == Code.RIGHT) {
                                director.beginUnaryExpression(operator, val,
                                        expressionReference, highlight);
                            }
                        }

                        //If it is something else we will store it for
                        // later use.
                    } else {

                        values.put(new Long(expressionCounter), val);
                        variables.put(new Long(expressionCounter), vars[n - 1]);

                    }

                    for (int i = 0; i < dims; i++) {
                        Object[] postIncDec = (Object[]) postIncsDecs
                                .remove(new Long(cellNumberReference[i]));

                        if (postIncDec != null) {
                            doPostIncDec(postIncDec);
                        }
                    }

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

                    Reference ref = (Reference) values.remove(new Long(
                            arrayCounter));
                    ArrayInstance array = (ArrayInstance) ref.getInstance();

                    Value length = new Value(value, type);
                    director.introduceArrayLength(length, array);

                    handleExpression(length, expressionCounter);

                    break;
                }
                //Class information starts for a class
                case Code.CLASS: {

                    String name = tokenizer.nextToken();
                    String extendedClass = "";

                    if (tokenizer.hasMoreTokens()) {
                        extendedClass = tokenizer.nextToken();
                    }

                    currentClass = new ClassInfo(name);
                    ClassInfo ci = (ClassInfo) classes.get(extendedClass);

                    //if extended class is user defined class
                    if (ci != null) {
                        currentClass.extendClass(ci);
                    }

                    break;
                }

                //Class information ends for a class
                case Code.END_CLASS: {

                    if (currentClass != null) {
                        classes.put(currentClass.getName(), currentClass);
                    }

                    currentClass = null;

                    break;
                }

                //Class information for constructor
                case Code.CONSTRUCTOR: {

                    String listOfParameters = "";
                    if (tokenizer.hasMoreTokens()) {
                        listOfParameters = tokenizer.nextToken();
                    }

                    currentClass.declareConstructor(currentClass.getName()
                            + Code.DELIM + listOfParameters, "");

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

                    currentClass.declareMethod(name + Code.DELIM
                            + listOfParameters, "" + modifiers + Code.DELIM
                            + returnType);

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

                    if (value.equals(Code.UNKNOWN)) {
                        value = MCodeUtilities.getDefaultValue(type);
                    }

                    currentClass.declareField(name, "" + modifiers + Code.DELIM
                            + type + Code.DELIM + value + Code.DELIM + h);

                    break;
                }

                //Error has occured during the execution
                case Code.ERROR: {

                    String message = tokenizer.nextToken();
                    Highlight h = MCodeUtilities.makeHighlight(tokenizer
                            .nextToken());

                    director.showErrorMessage(new InterpreterError(message, h));
                    running = false;

                    break;
                }

                //There is an error if the execution comes here.
                default: {
                    director
                            .showErrorMessage(new InterpreterError(
                                    messageBundle
                                            .getString("notImplemented.exception"),
                                    null));
                    /*
                     * " <H1> Runtime Error </H1><P> The feature is not
                     * yet implemented or " +
                     */
                    break;
                }
                }
            }

        } else {
            running = false;
            removeInstances();
        }
    }

    /**
     * @param ci
     * @param h
     * @return
     */
    public ObjectFrame createNewInstance(ClassInfo ci, Highlight h) {
        ObjectFrame of = new ObjectFrame("-1", ci.getName(), ci
                .getNonStaticFieldsAmount());

        //director: create object
        director.showObjectCreation(of, h);

        //director: create variables and initialize them
        Hashtable fields = ci.getFields();
        ListIterator i = ci.getFieldNamesInDeclarationOrder().listIterator();

        //for (Enumeration keyEnum = fields.keys(); keyEnum.hasMoreElements();) {
        while (i.hasNext()) {
            //String name = (String) keyEnum.nextElement();
            String name = (String) i.next();
            String info = (String) fields.get(name);
            boolean extended = info.endsWith("<E>");
            StringTokenizer st = new StringTokenizer(info, Code.DELIM);
            String mods = st.nextToken();
            String type = st.nextToken();
            String value = "";
            if ((st.countTokens() >= 2 && extended == false)
                    || (st.countTokens() >= 3 && extended == true)) {
                value = st.nextToken();
            }
            Highlight highlight = MCodeUtilities.makeHighlight(st.nextToken());

            if (!Modifier.isStatic(Integer.parseInt(mods))
                    && name.indexOf("$") < 0) {

                Variable var = director.declareObjectVariable(of, name, type,
                        highlight);
            }
            /*
             * if (!value.equals(Code.UNKNOWN)) {
             * 
             * Value casted = null; Value val = null; if
             * (ECodeUtilities.isPrimitive(type)) { casted = new Value(value,
             * type); val = new Value(value, type);
             * director.introduceLiteral(val); } else { if
             * (value.equals("null")) { casted = new Reference(); val = new
             * Reference(); director.introduceLiteral(val); } else { //This
             * should be done differently! //This does not work here if some
             * things //are not changed when the initial values of //each field
             * in the class are collected in DJ. Instance inst = (Instance)
             * instances.get( ECodeUtilities.getHashCode(value));
             * 
             * if (inst != null) { casted = new Reference(inst); val = new
             * Reference(inst); } else { casted = new Reference(); val = new
             * Reference(); director.introduceLiteral(val); } }
             * casted.setActor(var.getActor().getValue()); }
             * 
             * director.animateAssignment(var, val, casted, null, null); }
             */
        }

        return of;
    }

    /**
     * Not in use at the moment.
     */
    public void checkInstancesForRemoval() {
        Enumeration enumeration = instances.keys();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            Instance inst = (Instance) instances.get(obj);
            if (inst != null) {
                //For testing
                //System.out.println("number of references1: " +
                // inst.getNumberOfReferences());
                //System.out.println("number of references2: " +
                // inst.getActor().getNumberOfReferences());
                if (inst.getNumberOfReferences() == 0
                        && inst.getActor().getNumberOfReferences() == 0) {

                    instances.remove(obj);
                    director.removeInstance(inst.getActor());
                    inst = null;
                    //System.out.println("instance removed!");
                }
            }
        }
    }

    /**
     * Not in use at the moment
     */
    public void removeInstances() {
        Enumeration enumeration = instances.keys();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            Instance inst = (Instance) instances.get(obj);
            if (inst != null) {
                instances.remove(obj);
                director.removeInstance(inst.getActor());
                inst.setActor(null);
                inst = null;
                //System.out.println("instance removed!");
            }
        }
    }

    /**
     * @param val
     * @param expressionCounter
     */
    private void handleExpression(Value val, long expressionCounter) {

        //command that wait for this expression (left, right)
        int command = -1;
        int oper = -1;
        int size = commands.size();

        //We find the command
        for (int i = size - 1; i >= 0; i--) {
            StringTokenizer commandTokenizer = new StringTokenizer(
                    (String) commands.elementAt(i), Code.DELIM);

            int comm = Integer.parseInt(commandTokenizer.nextToken());
            long cid = Long.parseLong(commandTokenizer.nextToken());
            if (expressionCounter == cid) {
                command = comm;
                commands.removeElementAt(i);
                break;
            }
        }

        /*
         * Look from the expression stack what expression should be shown next
         */
        long expressionReference = 0;
        Highlight highlight = null;

        if (!exprs.empty()) {
            StringTokenizer expressionTokenizer = new StringTokenizer(
                    (String) exprs.peek(), Code.DELIM);

            oper = Integer.parseInt(expressionTokenizer.nextToken());
            expressionReference = Long.parseLong(expressionTokenizer
                    .nextToken());

            //Make the location information for the location token
            highlight = MCodeUtilities.makeHighlight(expressionTokenizer
                    .nextToken());
        }

        //Do different things depending on in what expression
        //the value is used.

        //If operator is assignment we just store the value
        if (oper == Code.A) {
            values.put(new Long(expressionCounter), val);

            //If oper is other binary operator we will show it
            //on the screen with operator
        } else if (MCodeUtilities.isBinary(oper)) {

            int operator = MCodeUtilities.resolveBinOperator(oper);

            if (command == Code.LEFT) {

                director.beginBinaryExpression(val, operator,
                        expressionReference, highlight);

            } else if (command == Code.RIGHT) {

                ExpressionActor ea = director.getCurrentScratch().findActor(
                        expressionReference);

                if (ea != null) {
                    director.rightBinaryExpression(val, ea, highlight);
                } else {
                    values.put(new Long(expressionCounter), val);
                }

            } else {
                values.put(new Long(expressionCounter), val);
            }

            /*
             * If oper is a unary operator we will show it on the screen with
             * operator
             */
        } else if (MCodeUtilities.isUnary(oper)) {

            int operator = MCodeUtilities.resolveUnOperator(oper);

            values.put(new Long(expressionCounter), val);

            if (command == Code.RIGHT) {
                director.beginUnaryExpression(operator, val,
                        expressionReference, highlight);
            }

            //If it is something else we will store it for later use.
        } else {
            values.put(new Long(expressionCounter), val);
        }
    }

    /**
     * @param postIncDecInfo
     */
    public void doPostIncDec(Object[] postIncDecInfo) {

        Variable var = (Variable) variables.remove(postIncDecInfo[1]);

        director.animateIncDec(((Long) postIncDecInfo[0]).intValue(), var,
                ((Value) postIncDecInfo[2]), ((Highlight) postIncDecInfo[3]));
    }

}
