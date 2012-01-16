/*
 * Copyright (c) 2004 Roland K�stermann. All Rights Reserved.
 */
package jeliot.mcode.input;

import java.io.BufferedReader;
import java.lang.reflect.Method;

import jeliot.FeatureNotImplementedException;
import jeliot.mcode.Code;
import jeliot.mcode.MCodeGenerator;
import jeliot.mcode.MCodeUtilities;
import koala.dynamicjava.tree.MethodCall;

/**
 * Created by IntelliJ IDEA.
 * User: roku
 * Date: 04.08.2004
 * Time: 14:10:19
 * To change this template use File | Settings | File Templates.
 */
public abstract class InputHandler  {

	String methodName;
	/**
     * @param aClass
     * @param counter
     * @param m
     * @param node
     * @param prompt  indivual prompt string, maybe empty or null for default value
     * @return Input Handle, may throw NoSuchMethod Exception
     */
	
	public Object handleInput(Class aClass, long counter, Method m,
			MethodCall node, String prompt) {
		
		this.methodName = m.getName();
		
		if (prompt != null && prompt.length() > 0) {
			out(counter, "print", prompt, node);
		//	counter ++;
		}
		if (isInputMethod(m, aClass)){
			MCodeUtilities.write("" + Code.INPUT + Code.DELIM + counter +
					Code.DELIM
					+ m.getDeclaringClass().getName() + Code.DELIM +
					methodName + Code.DELIM
					+ aClass.getName() + Code.DELIM + prompt + Code.DELIM +
					MCodeGenerator.locationToString(node));
			return handleInput(aClass);
		} else{
			throw new FeatureNotImplementedException("Input method "
					+ m.getName()+ " is not supported");
		}		
	}
	
	private boolean isInputMethod(Method m, Class aClass) {
		
		if (aClass.equals(int.class))
            return isIntegerInputMethod(m);
        if (aClass.equals(double.class))
            return isDoubleInputMethod(m);
        if (aClass.equals(long.class))
        	return isLongInputMethod(m);
        if (aClass.equals(char.class))
        	return isCharInputMethod(m);
        if (aClass.equals(byte.class))
        	return isByteInputMethod(m);
        if (aClass.equals(float.class))
        	return isFloatInputMethod(m);
        if (aClass.equals(short.class))
        	return isShortInputMethod(m);        
        if (aClass.equals(boolean.class))
        	return isBooleanInputMethod(m);
        if (aClass.equals(String.class))
        	return isStringInputMethod(m);    
        return false;
	}

	public void out(final long counter, final String methodPrint, final
			String output, final MethodCall node) {
		MCodeUtilities.write("" + Code.OUTPUT + Code.DELIM
				+ counter + Code.DELIM + "System.out"
				+ Code.DELIM + methodPrint + Code.DELIM
				+ output + Code.DELIM
				+ String.class.getName() + Code.DELIM
				+ (methodPrint.equals("println") ? "1" : "0")
				+ Code.DELIM
				+ MCodeGenerator.locationToString(node));
	}
	protected abstract Object handleInput(Class aClass);
	
	public abstract void setInputReader(BufferedReader in);
	
	public abstract boolean isIntegerInputMethod(Method m);
	
	public abstract boolean isDoubleInputMethod(Method m);
	
	public abstract boolean isLongInputMethod(Method m);
	
	public abstract boolean isByteInputMethod(Method m);
	
	public abstract boolean isCharInputMethod(Method m);
	
	public abstract boolean isFloatInputMethod(Method m);
	
	public abstract boolean isStringInputMethod(Method m);
	
	public abstract boolean isShortInputMethod(Method m);
	
	public abstract boolean isBooleanInputMethod(Method m);
	
}
