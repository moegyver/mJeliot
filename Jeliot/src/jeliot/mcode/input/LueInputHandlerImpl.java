/*
 * Created on Aug 5, 2004
 */
package jeliot.mcode.input;

import java.io.BufferedReader;
import java.lang.reflect.Method;

import jeliot.mcode.MCodeUtilities;

/**
 * @author nmyller
 */
public class LueInputHandlerImpl extends InputHandler {

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#setInputReader(java.io.BufferedReader)
	 */
	public void setInputReader(BufferedReader in) {
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isIntegerInputMethod(java.lang.reflect.Method)
	 */
	public boolean isIntegerInputMethod(Method m) {
		return m.getName().equals("kluku");
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isDoubleInputMethod(java.lang.reflect.Method)
	 */
	public boolean isDoubleInputMethod(Method m) {
		return m.getName().equals("dluku");
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isLongInputMethod(java.lang.reflect.Method)
	 */
	public boolean isLongInputMethod(Method m) {
		return false;
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isByteInputMethod(java.lang.reflect.Method)
	 */
	public boolean isByteInputMethod(Method m) {
		return false;
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isCharInputMethod(java.lang.reflect.Method)
	 */
	public boolean isCharInputMethod(Method m) {
		return m.getName().equals("merkki");
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isFloatInputMethod(java.lang.reflect.Method)
	 */
	public boolean isFloatInputMethod(Method m) {
		return false;
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isStringInputMethod(java.lang.reflect.Method)
	 */
	public boolean isStringInputMethod(Method m) {
		return m.getName().equals("rivi");
	}

	/* (non-Javadoc)
	 * @see jeliot.mcode.input.InputHandler#isShortInputMethod(java.lang.reflect.Method)
	 */
	public boolean isShortInputMethod(Method m) {
		return false;
	}
	
	public boolean isBooleanInputMethod(Method m) {
		return false;
	}

	   protected Object handleInput(Class aClass) {
        if (aClass.equals(int.class))
            return MCodeUtilities.readInt();
        if (aClass.equals(double.class))
            return MCodeUtilities.readDouble();
        if (aClass.equals(long.class))
             return MCodeUtilities.readLong();
        if (aClass.equals(char.class))
             return MCodeUtilities.readChar();
        if (aClass.equals(byte.class))
             return MCodeUtilities.readByte();
        if (aClass.equals(float.class))
             return MCodeUtilities.readFloat();
        if (aClass.equals(short.class))
             return MCodeUtilities.readShort();        
        if (aClass.equals(boolean.class))
             return MCodeUtilities.readBoolean();
        if (aClass.equals(String.class))
            return MCodeUtilities.readString();
        throw new NoSuchMethodError("Input Method for class '"+aClass+"' not supported!");
    }


}
