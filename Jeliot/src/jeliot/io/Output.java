package jeliot.io;

import java.io.PrintStream;

/**
 * Only a class stub for output handling the actual output handling is
 * done in koala.dynamicJava.intepreter.EvaluationVisitor and jeliot.mcode.Interpreter.
 * 
 * @author Pekka Uronen
 * @author Andrés Moreno
 * @see koala.dynamicJava.intepreter.EvaluationVisitor
 * @see jeliot.mcode.MCodeInterpreter
 */
public class Output {

    /** A copy of the System.out PrintStream. */
    private static PrintStream out = System.out;

	/**
	 * Prints out the given integer value.
	 *
	 * @param	i	The integer to be printed.
	 */
    public static void println(int i) {
        out.println(i);    
    }

	/**
	 * Prints out the given double value.
	 *
	 * @param	d	The double to be printed.
	 */
    public static void println(double d) {
        out.println(d);    
    }
    
	/**
	 * Prints out the given float value.
	 *
	 * @param	f	The float to be printed.
	 */
    public static void println(float f) {
        out.println(f);    
    }
    
	/**
	 * Prints out the given long value.
	 *
	 * @param	l	The long to be printed.
	 */
    public static void println(long l) {
        out.println(l);    
    }
    
	/**
	 * Prints out the given boolean value.
	 *
	 * @param	b	The boolean to be printed.
	 */
    public static void println(boolean b) {
        out.println(b);    
    }
    
    /**
     * Prints out the given String value.
     * 
     * @param s The String to be printed.
     */
    public static void println(String s) {
        out.println(s);    
    }
    
    /**
     * Prints out the given character.
     * 
     * @param c The character to be printed.
     */
    public static void println(char c) {
        out.println(c);    
    }
    
	/*
	 * Empty function.
     */
    /*
    public static void clear() {
    }
    */
}
