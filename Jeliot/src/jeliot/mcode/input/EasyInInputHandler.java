package jeliot.mcode.input;

import java.io.BufferedReader;
import java.lang.reflect.Method;

import jeliot.mcode.MCodeUtilities;

public class EasyInInputHandler extends InputHandler {

	public void setInputReader(BufferedReader in) {
        return;
    }

    public boolean isByteInputMethod(Method m) {
        return m.getName().equals("getByte");
    }

    public boolean isCharInputMethod(Method m) {
        return m.getName().equals("getChar");
    }

    public boolean isDoubleInputMethod(Method m) {
        return m.getName().equals("getDouble");
    }

    public boolean isFloatInputMethod(Method m) {
        return m.getName().equals("getFloat");
    }

    public boolean isIntegerInputMethod(Method m) {
        return m.getName().equals("getInt");
    }

    public boolean isLongInputMethod(Method m) {
        return m.getName().equals("getLong");
    }

    public boolean isStringInputMethod(Method m) {
        return m.getName().equals("getString");
    }

    public boolean isShortInputMethod(Method m) {
        return m.getName().equals("getShort");
    }

	public boolean isBooleanInputMethod(Method m) {
		return false;
	}

    public Object handleInput(Class aClass) {
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
