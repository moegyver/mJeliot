package jeliot.mcode.input;

import jeliot.mcode.MCodeUtilities;

import java.lang.reflect.Method;
import java.io.BufferedReader;

/**
 * @author Roland K�stermann
 */
public class JeliotInputHandlerImpl extends InputHandler  {
    public boolean isByteInputMethod(Method m) {
        return false;
    }

    public boolean isShortInputMethod(Method m) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isCharInputMethod(Method m) {
        return m.getName().equals("readChar");
    }

    public boolean isDoubleInputMethod(Method m) {
      return (m.getName().equals("readDouble") || m.getName().equals("nextDouble"));
    }

    public boolean isFloatInputMethod(Method m) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isIntegerInputMethod(Method m) {
        return (m.getName().equals("readInt") || m.getName().equals("nextInt"));
    }

    public boolean isLongInputMethod(Method m) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isStringInputMethod(Method m) {
        return (m.getName().equals("readString") || m.getName().equals("next"));
    }
    
	public boolean isBooleanInputMethod(Method m) {
		return false;
	}

    public void setInputReader(BufferedReader in) {
        //To change body of implemented methods use File | Settings | File Templates.
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
