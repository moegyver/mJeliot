package jeliot.mcode;

import java.io.BufferedReader;
import java.util.Stack;

import jeliot.Jeliot;
import jeliot.calltree.TreeDraw;


/**
 * @author Niko Myller
 */
public class CallTreeMCodeInterpreter extends MCodeInterpreter {
    private TreeDraw callTree;
    private String currentMethodCall;
    private String currentReturnValue;
    private Stack methodCalls;
    private Jeliot jeliot;
    private int tabNumber;
    
    /**
     * 
     * @param bf
     * @param callTree
     * @param programCode
     */
    public CallTreeMCodeInterpreter(BufferedReader bf, TreeDraw callTree, String programCode, Jeliot jeliot, int tabNumber) {
        super(bf);
        //this.mcode = bf;
        this.programCode = programCode;
        this.callTree = callTree;
        this.methodCalls = new Stack();
        this.jeliot = jeliot;
        this.tabNumber = tabNumber;
        initialize();
    }
    
    public void initialize() {
        super.initialize();
    }
    
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#readLine()
     */
    public String readLine() {
        try {
        return mcode.readLine();
        } catch (Exception e) {
            return "" + Code.ERROR + Code.DELIM + messageBundle.getString("unknown.exception")
            + Code.DELIM + "0" + Code.LOC_DELIM + "0" + Code.LOC_DELIM + "0" + Code.LOC_DELIM + "0";
        }
    }

    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#showErrorMessage(jeliot.mcode.InterpreterError)
     */
    public void showErrorMessage(InterpreterError error) {
        //System.out.println(error.message);
    }

    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#emptyScratch()
     */
    public boolean emptyScratch() {
        return false;
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUTTED(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeINPUTTED(long expressionCounter, String value, String type, Highlight h) {
        value = MCodeUtilities.getValue(value, type);
        callTree.returnMethodCall(value);
        jeliot.highlightTabTitle(true, tabNumber);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUT(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeINPUT(long expressionCounter, String className, String methodName, String type, String prompt, Highlight h) {
        int n = type.lastIndexOf(".");
        if (n != -1) {
            type = type.substring(n, type.length());
        }
        callTree.insertMethodCall(className + "." + methodName + "()");
        jeliot.highlightTabTitle(true, tabNumber);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOUTPUT(long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeOUTPUT(long expressionReference, String className, String methodName, String value, String type, boolean breakLine, Highlight highlight) {
        value = MCodeUtilities.getValue(value, ("java.lang.Object".equals(type) ? String.class.getName() : type));
        callTree.insertMethodCall(className + "." + methodName + "(" + value + ")");
        callTree.returnMethodCall(null);
        jeliot.highlightTabTitle(true, tabNumber);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSMCC()
     */
    protected void handleCodeSMCC() {
        callTree.returnMethodCall(currentReturnValue);
        currentReturnValue = null;
        jeliot.highlightTabTitle(true, tabNumber);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeR(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeR(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
        value = MCodeUtilities.getValue(value, type);
        currentReturnValue = value;
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePARAMETERS(java.lang.String)
     */
    protected void handleCodePARAMETERS(String parameters) {
        // TODO: handle the methods parameter names from here! 
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMD(jeliot.mcode.Highlight)
     */
    protected void handleCodeMD(Highlight h) {
        if (currentMethodCall != null) {
            if (currentMethodCall.endsWith(", ")) {
                callTree.insertMethodCall(currentMethodCall.substring(0, currentMethodCall.length() - 2) + ")");
            } else {
                callTree.insertMethodCall(currentMethodCall + ")");
            }
            currentMethodCall = null;
            if (!methodCalls.isEmpty()) {
                currentMethodCall = (String) methodCalls.pop();
            }
            jeliot.highlightTabTitle(true, tabNumber);
        }
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeP(long, java.lang.String, java.lang.String)
     */
    protected void handleCodeP(long expressionReference, String value, String argType) {
        value = MCodeUtilities.getValue(value, argType);
        currentMethodCall += value + ", ";
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSMC(java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeSMC(String methodName, String className, int parameterCount, Highlight h) {
        if (currentMethodCall != null) {
            methodCalls.push(currentMethodCall);
        }
        currentMethodCall = className + "." + methodName + "(";
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOMCC()
     */
    protected void handleCodeOMCC() {
        callTree.returnMethodCall(currentReturnValue);
        currentReturnValue = null;
        jeliot.highlightTabTitle(true, tabNumber);
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOMC(java.lang.String, int, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeOMC(String methodName, int parameterCount, long objectCounter, String className, Highlight highlight) {
        if (currentMethodCall != null) {
            methodCalls.push(currentMethodCall);
        }
        if (methodName.equals("this") || methodName.equals("super")) {
            currentMethodCall = "(" + methodName +  " call) " + className + "(";
        } else {
            currentMethodCall = className + "." + methodName + "(";
        }
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSAC(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeSAC(long expressionCounter, String hashCode, Highlight h) {
        callTree.returnMethodCall(hashCode);
        currentReturnValue = null;
        jeliot.highlightTabTitle(true, tabNumber);
    }
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSA(long, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeSA(long expressionCounter, String declaringClass, String constructorName, int parameterCount, Highlight highlight) {
        if (currentMethodCall != null) {
            methodCalls.push(currentMethodCall);
        }
        currentMethodCall = "new " + constructorName + "(";
    }
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSCN(long)
     */
    protected void handleCodeCONSCN(long superMethodCallNumber) {
    }
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeL(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeL(long expressionCounter, String value, String type, Highlight highlight) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeQN(long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeQN(long expressionCounter, String variableName, String value, String type, Highlight highlight) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeVD(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeVD(String variableName, long initializerExpression, String value, String type, String modifier, Highlight highlight) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeSE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeDE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeDE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeRE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeME(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeME(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeGQT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeGQT(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeGT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeGT(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeEE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeEE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeOR(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAND(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeXOR(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeURSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeURSHIFT(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeRSHIFT(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLSHIFT(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITAND(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITXOR(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBITOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeBITOR(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePRDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePRDE(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePRIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePRIE(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePDE(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePIE(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeNO(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeNO(long expressionCounter, long unaryExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMINUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeMINUS(long expressionCounter, long unaryExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodePLUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodePLUS(long expressionCounter, long unaryExpressionReference, String value, String type, Highlight h) {
    }
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCOMP(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeCOMP(long expressionCounter, long unaryExpressionReference, String value, String type, Highlight h) {
    }
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeA(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeA(long expressionCounter, long fromExpression, long toExpression, String value, String type, Highlight h) {
    }
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeTO(long)
     */
    protected void handleCodeTO(long expressionReference) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBEGIN(long, long, java.lang.String)
     */
    protected void handleCodeBEGIN(long expressionType, long expressionReference, String location) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeRIGHT(long)
     */
    protected void handleCodeRIGHT(long token1) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLEFT(long)
     */
    protected void handleCodeLEFT(long token1) {
    }
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONT(int, jeliot.mcode.Highlight)
     */
    protected void handleCodeCONT(int statementName, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeBR(int, jeliot.mcode.Highlight)
     */
    protected void handleCodeBR(int statementName, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCH(jeliot.mcode.Highlight)
     */
    protected void handleCodeSWITCH(Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWIBF(long, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeSWIBF(long selectorReference, long switchBlockReference, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCHB(jeliot.mcode.Highlight)
     */
    protected void handleCodeSWITCHB(Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeDO(long, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeDO(long expressionReference, String value, long round, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeFOR(long, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeFOR(long expressionReference, String value, long round, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeWHI(long, java.lang.String, int, jeliot.mcode.Highlight)
     */
    protected void handleCodeWHI(long expressionReference, String value, int round, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeIFTE(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeIFTE(long expressionReference, String value, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeIFT(long, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeIFT(long expressionReference, String value, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#openScratch()
     */
    public void openScratch() {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#closeScratch()
     */
    public void closeScratch() {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#endRunning()
     */
    protected void endRunning() {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLQE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLQE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeLE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeLE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeNE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeNE(long expressionCounter, long leftExpressionReference, long rightExpressionReference, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeERROR(java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeERROR(String message, Highlight h) {
        running = false;
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeFIELD(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
     */
    protected void handleCodeFIELD(String name, String type, int modifiers, String value, String h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeMETHOD(java.lang.String, java.lang.String, int, java.lang.String)
     */
    protected void handleCodeMETHOD(String name, String returnType, int modifiers, String listOfParameters) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSTRUCTOR(java.lang.String)
     */
    protected void handleCodeCONSTRUCTOR(String listOfParameters) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeEND_CLASS()
     */
    protected void handleCodeEND_CLASS() {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCLASS(java.lang.String, java.lang.String)
     */
    protected void handleCodeCLASS(String name, String extendedClass) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAL(long, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAL(long expressionCounter, long arrayCounter, String name, String value, String type, Highlight highlight) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAAC(long, long, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAAC(long expressionCounter, long expressionReference, int dims, String cellNumberReferences, String cellNumbers, String value, String type, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAA(long, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeAA(long expressionReference, String hashCode, String compType, int dims, String dimensionReferences, String dimensionSizes, int actualdimensions, String subArraysHashCodes, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSCOPE(int)
     */
    protected void handleCodeSCOPE(int scope) {
    }
    
    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeOFA(long, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeOFA(long expressionCounter, long objectCounter, String variableName, String value, String type, int modifiers, Highlight h) {
    }

    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#beforeInterpretation(java.lang.String)
     */
    protected void beforeInterpretation(String line) {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeCAST(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeCAST(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeSFA(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
     */
    protected void handleCodeSFA(long expressionCounter, String declaringClass, String variableName, String value, String type, int modifiers, Highlight highlight) {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#beforeExecution()
     */
    public void beforeExecution() {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAIBEGIN(long, jeliot.mcode.Highlight)
     */
    protected void handleCodeAIBEGIN(long cells, Highlight highlight) {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAIE(long, long, java.lang.String, java.lang.String, long, jeliot.mcode.Highlight)
     */
    protected void handleCodeAIE(String arrayReference, long cellNumber, long expressionReference, String value, String type, long literal, Highlight highlight) {
    }


    /* (non-Javadoc)
     * @see jeliot.mcode.MCodeInterpreter#handleCodeAI(jeliot.mcode.Highlight)
     */
    protected void handleCodeAI(Highlight highlight) {
    }

    public void afterInterpretation(String line) {
        // TODO Auto-generated method stub
        
    }

    
}
