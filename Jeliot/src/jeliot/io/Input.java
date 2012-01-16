package jeliot.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Only a class stub for input handling the actual input handling is
 * done in koala.dynamicJava.intepreter.EvaluationVisitor and jeliot.mcode.Interpreter.
 * 
 * @author Pekka Uronen
 * @author Andr�s Moreno
 * @see koala.dynamicJava.intepreter.EvaluationVisitor
 * @see jeliot.mcode.MCodeInterpreter
 */
public class Input {

    static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    /** 
     * A method stub for integer reading.
     * @return returns value zero 0.
     */
    public static int readInt() {
        int value = 0;
        boolean ok;
        do {
            try {
                value = Integer.parseInt(stdin.readLine());
                ok = true;
            } catch (Exception e) {
                System.out.println("Not valid integer value. Give a new one!");
                ok = false;
            }
        } while (!ok);
        return value;
    }

    /** 
     * A method stub for double reading.
     * @return returns value zero 0.0.
     */
    public static double readDouble() {
        double value = 0d;
        boolean ok;
        do {
            try {
                value = new Double(stdin.readLine()).doubleValue();
                ok = true;
            } catch (Exception e) {
                System.out.println("Not valid double value. Give a new one!");
                ok = false;
            }
        } while (!ok);
        return value;
    }

    /** 
     * A method stub for integer reading.
     * @return returns value zero 0.
     */
    public static int nextInt() {
        int value = 0;
        boolean ok;
        do {
            try {
                value = Integer.parseInt(stdin.readLine());
                ok = true;
            } catch (Exception e) {
                System.out.println("Not valid integer value. Give a new one!");
                ok = false;
            }
        } while (!ok);
        return value;
    }

    /** 
     * A method stub for double reading.
     * @return returns value zero 0.0.
     */
    public static double nextDouble() {
        double value = 0d;
        boolean ok;
        do {
            try {
                value = new Double(stdin.readLine()).doubleValue();
                ok = true;
            } catch (Exception e) {
                System.out.println("Not valid double value. Give a new one!");
                ok = false;
            }
        } while (!ok);
        return value;
    }
    /** 
     * A method stub for character reading.
     * @return returns value space ' '.
     */
    public static char readChar() {
        String str = readString();
        try {
            return str.charAt(0);
        } catch (Exception e) {
            return ' ';
        }
    }

    /** 
     * A method stub for string reading.
     * @return returns value space " ".
     */
    public static String readString() {
        String str = null;
        boolean ok;
        do {
            try {
                str = stdin.readLine();
                ok = true;
            } catch (Exception e) {
                System.out.println("Error in reading the input. Give new input!");
                ok = false;
            }
        } while (!ok);
        return str;
    }
    /** 
    * A method stub for string reading.
    * @return returns value space " ".
    */
   public static String next() {
       String str = null;
       boolean ok;
       do {
           try {
               str = stdin.readLine();
               ok = true;
           } catch (Exception e) {
               System.out.println("Error in reading the input. Give new input!");
               ok = false;
           }
       } while (!ok);
       return str;
   }
}