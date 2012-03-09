package jeliot.mcode;


import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.mJeliot.model.predict.Method;
import org.mJeliot.model.predict.Parameter;

import jeliot.mJeliot.MJeliotController;

/**
 * @author Moritz Rogalli
 * The MJeliotMCodeInterpreter gets invoked by Jeliot while walking through the code of the 
 * user. It builds data structures to identify methods and variables when the user clicks
 * the compile-button, since parameters are represented by variables in mCode. When the
 * user walks through the code, method calls and returns get identified and sent to the
 * interactMenu.
 */
public class MJeliotMCodeInterpreter extends MCodeInterpreter {

	/**
	 * All the methods from the source code.
	 */
	private HashMap<String, Method> methods = new HashMap<String, Method>();
	/**
	 * All the variables from the source code.
	 */
	private HashMap<Long, String> variables = new HashMap<Long, String>();
	/**
	 * The method that is running right now.
	 */
	private Method currentMethod = null;
	/**
	 * A stack that keeps track of all the methods that got called to be able to match
	 * returns to the correct method.
	 */
	private Stack<Method> runningMethods = new Stack<Method>();
	/**
	 * Only used during building the data structures. Represents the name of the current
	 * class.
	 */
	private String currentClassName = null;
	/**
	 * The parameters' values the current method got called with.
	 */
	private Vector<String> currentCallValues = new Vector<String>();
	private MJeliotController controller = null;

	/**
	 * Pretty standard mCodeInterpreter-constructor.
	 * @param bf
	 * @param programCode
	 * @param interactMenu
	 */
	public MJeliotMCodeInterpreter(BufferedReader bf, String programCode, MJeliotController controller) {
		super(bf);
		this.programCode = programCode;
		this.controller = controller;
		initialize();
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#initialize()
	 */
	@Override
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
	 * @see jeliot.mcode.MCodeInterpreter#beforeInterpretation(java.lang.String)
	 */
	@Override
	protected void beforeInterpretation(String line) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#afterInterpretation(java.lang.String)
	 */
	@Override
	public void afterInterpretation(String line) {
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCLASS(java.lang.String, java.lang.String)
	 */
	@Override
	protected void handleCodeCLASS(String name, String extendedClass) {
		this.currentClassName = name;
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeMETHOD(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	protected void handleCodeMETHOD(String name, String returnType,
			int modifiers, String listOfParameters) {
		// We don't care about the main-method
		if (!name.equals("main")) {
			this.currentMethod = new Method(this.currentClassName, name);
			this.methods.put(currentClassName + "." + name, this.currentMethod);
			if (!returnType.equals("void")) {
				this.currentMethod.addParameter("return");
			}
		}
		this.currentMethod = null;
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSMC(java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSMC(String methodName, String className, int parameterCount, Highlight h) {
		if (this.currentMethod != null) {
			this.runningMethods.push(this.currentMethod);
		}
		if (!methodName.equals(new String("main"))) {
			this.currentMethod = this.methods.get(className + "." + methodName);
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSMCC()
	 */
	@Override
	protected void handleCodeSMCC() {
		if (this.currentMethod != null) {
			this.controller.methodReturned(this.currentMethod);
		}
		if (!this.runningMethods.isEmpty())
			this.currentMethod = this.runningMethods.pop();
		else
			this.currentMethod = null;
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeOMC(java.lang.String, int, long, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeOMC(String methodName, int parameterCount,
			long objectCounter, String className, Highlight highlight) {
		if (this.currentMethod != null) {
			this.runningMethods.push(this.currentMethod);
		}
		if (!methodName.equals(new String("main"))) {
			this.currentMethod = this.methods.get(className + "." + methodName);
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeOMCC()
	 */
	@Override
	protected void handleCodeOMCC() {
		this.controller.methodReturned(this.currentMethod);
		if (!this.runningMethods.isEmpty())
			this.currentMethod = this.runningMethods.pop();
		else
			this.currentMethod = null;
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeR(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeR(long expressionCounter, long expressionReference, String value, String type, Highlight h) {
		if (this.currentMethod.getParameterByName("return") != null) {
			this.currentMethod.getParameterByName("return").setActualValue(value);
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePARAMETERS(java.lang.String)
	 */
	@Override
	protected void handleCodePARAMETERS(String parameters) {
		if (parameters != null) {
			String[] parameterArray = parameters.split(",");
			if (parameterArray != null && currentMethod != null) {
				for (int i = 0; i < parameterArray.length; i++) {
					currentMethod.addParameter(parameterArray[i]);
					Parameter p = currentMethod.getParameters().get(currentMethod.getParameters().size()-1);
					p.setActualValue(currentCallValues.get(i));
				}
			}
			this.currentCallValues = new Vector<String>();
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeMD(jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeMD(Highlight h) {
		if (this.currentMethod != null) {
			this.controller.methodCalled(currentMethod);
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeP(long, java.lang.String, java.lang.String)
	 */
	@Override
	protected void handleCodeP(long expressionReference, String value, String argType) {
		this.currentCallValues.add(value);
	}	
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeA(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeA(long expressionCounter, long fromExpression,
			long toExpression, String value, String type, Highlight h) {
		if (this.currentMethod != null && this.variables.get(toExpression) != null) {
			Parameter p = this.currentMethod.getParameterByName(this.variables.get(toExpression));
			if (p != null) {
				p.setActualValue(value);
			}
		}
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeQN(long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeQN(long expressionCounter, String variableName,
			String value, String type, Highlight highlight) {
		this.variables.put(expressionCounter, variableName);
	}	
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
	}
	
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#showErrorMessage(jeliot.mcode.InterpreterError)
	 */
	@Override
	public void showErrorMessage(InterpreterError error) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUTTED(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeINPUTTED(long expressionCounter, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeINPUT(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeINPUT(long expressionCounter, String className, String methodName, String type, String prompt, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeOUTPUT(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeOUTPUT(long expressionReference, String className, String methodName, String value, String type, boolean breakLine, Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSAC(long, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSAC(long expressionCounter, String hashCode, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSA(long, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSA(long expressionCounter, String declaringClass, String constructorName, int parameterCount, Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#endRunning()
	 */
	@Override
	protected void endRunning() {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#closeScratch()
	 */
	@Override
	public void closeScratch() {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#emptyScratch()
	 */
	@Override
	public boolean emptyScratch() {
		return false;
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAIBEGIN(long, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAIBEGIN(long cells, Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAIE(java.lang.String, long, long, java.lang.String, java.lang.String, long, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAIE(String arrayReference, long cellNumber,
			long expressionReference, String value, String type, long literal,
			Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAI(jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAI(Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSFA(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSFA(long expressionCounter, String declaringClass,
			String variableName, String value, String type, int modifiers,
			Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCAST(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeCAST(long expressionCounter,
			long expressionReference, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeLQE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeLQE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeLE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeLE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeNE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeNE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeERROR(java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeERROR(String message, Highlight h) {
		running = false;
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeFIELD(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	protected void handleCodeFIELD(String name, String type, int modifiers,
			String value, String h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSTRUCTOR(java.lang.String)
	 */
	@Override
	protected void handleCodeCONSTRUCTOR(String listOfParameters) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeEND_CLASS()
	 */
	@Override
	protected void handleCodeEND_CLASS() {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAL(long, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAL(long expressionCounter, long arrayCounter,
			String name, String value, String type, Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAAC(long, long, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAAC(long expressionCounter,
			long expressionReference, int dims, String cellNumberReferences,
			String cellNumbers, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAA(long, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, int, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAA(long expressionReference, String hashCode,
			String compType, int dims, String dimensionReferences,
			String dimensionSizes, int actualdimensions,
			String subArraysHashCodes, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSCOPE(int)
	 */
	@Override
	protected void handleCodeSCOPE(int scope) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCONT(int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeCONT(int statementName, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeBR(int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeBR(int statementName, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCH(jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSWITCH(Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSWIBF(long, long, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSWIBF(long selectorReference,
			long switchBlockReference, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSWITCHB(jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSWITCHB(Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeDO(long, java.lang.String, long, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeDO(long expressionReference, String value,
			long round, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeFOR(long, java.lang.String, long, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeFOR(long expressionReference, String value,
			long round, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeWHI(long, java.lang.String, int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeWHI(long expressionReference, String value,
			int round, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeIFTE(long, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeIFTE(long expressionReference, String value,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeIFT(long, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeIFT(long expressionReference, String value,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeOFA(long, long, java.lang.String, java.lang.String, java.lang.String, int, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeOFA(long expressionCounter, long objectCounter,
			String variableName, String value, String type, int modifiers,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCONSCN(long)
	 */
	@Override
	protected void handleCodeCONSCN(long superMethodCallNumber) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeL(long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeL(long expressionCounter, String value,
			String type, Highlight highlight) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeVD(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeVD(String variableName,
			long initializerExpression, String value, String type,
			String modifier, Highlight highlight) {
		
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeSE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeSE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeDE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeDE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeRE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeRE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeME(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeME(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeGQT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeGQT(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeGT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeGT(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeEE(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeEE(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeOR(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeAND(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeXOR(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeURSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeURSHIFT(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeRSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeRSHIFT(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeLSHIFT(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeLSHIFT(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeBITAND(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeBITAND(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeBITXOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeBITXOR(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeBITOR(long, long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeBITOR(long expressionCounter,
			long leftExpressionReference, long rightExpressionReference,
			String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePRDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodePRDE(long expressionCounter,
			long expressionReference, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePRIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodePRIE(long expressionCounter,
			long expressionReference, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePDE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodePDE(long expressionCounter,
			long expressionReference, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePIE(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodePIE(long expressionCounter,
			long expressionReference, String value, String type, Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeNO(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeNO(long expressionCounter,
			long unaryExpressionReference, String value, String type,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeMINUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeMINUS(long expressionCounter,
			long unaryExpressionReference, String value, String type,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodePLUS(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodePLUS(long expressionCounter,
			long unaryExpressionReference, String value, String type,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeCOMP(long, long, java.lang.String, java.lang.String, jeliot.mcode.Highlight)
	 */
	@Override
	protected void handleCodeCOMP(long expressionCounter,
			long unaryExpressionReference, String value, String type,
			Highlight h) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeTO(long)
	 */
	@Override
	protected void handleCodeTO(long expressionReference) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeBEGIN(long, long, java.lang.String)
	 */
	@Override
	protected void handleCodeBEGIN(long expressionType,
			long expressionReference, String location) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeRIGHT(long)
	 */
	@Override
	protected void handleCodeRIGHT(long token1) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#handleCodeLEFT(long)
	 */
	@Override
	protected void handleCodeLEFT(long token1) {
	}
	/* (non-Javadoc)
	 * @see jeliot.mcode.MCodeInterpreter#openScratch()
	 */
	@Override
	public void openScratch() {
	}
}
