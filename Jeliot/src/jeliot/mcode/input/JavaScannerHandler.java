package jeliot.mcode.input;

import java.io.BufferedReader;
import java.lang.reflect.Method;

import jeliot.mcode.MCodeUtilities;

public class JavaScannerHandler extends InputHandler {

    public boolean isByteInputMethod(Method m) {
    	return methodName.equals("nextByte");
    }

    public boolean isShortInputMethod(Method m) {
    	return methodName.equals("nextShort");
    }

    public boolean isCharInputMethod(Method m) {
        return false;
    }

    public boolean isDoubleInputMethod(Method m) {
      return methodName.equals("nextDouble");
    }

    public boolean isFloatInputMethod(Method m) {
        return methodName.equals("nextFloat");  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isIntegerInputMethod(Method m) {
        return methodName.equals("nextInt");
    }

    public boolean isLongInputMethod(Method m) {
    	return methodName.equals("nextLong");
    }

    public boolean isStringInputMethod(Method m) {
        return methodName.equals("next") || methodName.equals("nextLine");
    }
    
    public boolean isBooleanInputMethod(Method m) {
    	return methodName.equals("nextBoolean");
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
