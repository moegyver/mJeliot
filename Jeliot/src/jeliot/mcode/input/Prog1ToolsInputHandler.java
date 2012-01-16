package jeliot.mcode.input;

import java.io.BufferedReader;
import java.lang.reflect.Method;

/**
 * @author Roland K�stermann
 */
public class Prog1ToolsInputHandler extends InputHandler  {

    public void setInputReader(BufferedReader in) {
        Prog1Tools.IOTools.setReader(in);
    }

    public boolean isByteInputMethod(Method m) {
        return false;
    }

    public boolean isCharInputMethod(Method m) {
        return m.getName().equals("readChar");
    }

    public boolean isDoubleInputMethod(Method m) {
        return m.getName().equals("readDouble");
    }

    public boolean isFloatInputMethod(Method m) {
        return m.getName().equals("readFloat");
    }

    public boolean isIntegerInputMethod(Method m) {
        return (m.getName().equals("readInteger") || m.getName().equals("readInt"));
    }

    public boolean isLongInputMethod(Method m) {
        return m.getName().equals("readLong");
    }

    public boolean isStringInputMethod(Method m) {
        return m.getName().equals("readString");
    }

    public boolean isShortInputMethod(Method m) {
        return m.getName().equals("readShort");
    }

	public boolean isBooleanInputMethod(Method m) {
		return m.getName().equals("readBoolean");
	}   


    public Object handleInput(Class aClass) {
        if (aClass.equals(int.class))
            return new Integer (Prog1Tools.IOTools.readInteger());
        if (aClass.equals(double.class))
            return new Double (Prog1Tools.IOTools.readDouble());
        if (aClass.equals(long.class))
            return new Long (Prog1Tools.IOTools.readLong());
        if (aClass.equals(char.class))
            return new Character (Prog1Tools.IOTools.readChar());
        if (aClass.equals(short.class))
            return new Short (Prog1Tools.IOTools.readShort());
        if (aClass.equals(float.class))
            return new Float (Prog1Tools.IOTools.readFloat());
        if (aClass.equals(boolean.class))
            return new Boolean (Prog1Tools.IOTools.readBoolean());
        if (aClass.equals(String.class))
            return Prog1Tools.IOTools.readLine();

        throw new NoSuchMethodError("Input Method for class '"+aClass+"' not supported!");
    }

}