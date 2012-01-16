/*
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package koala.dynamicjava.util;

/**
 * @author amoreno
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */


public class ParamTypes {

	 private static Class resolveType(Class c) {
	 	
	 	String type = c.getName();
	 	
	       if (type.equals(boolean.class.getName()) ||
	           type.equals(Boolean.class.getName())) {
	            
	            return Boolean.class;

	        } else if (type.equals(byte.class.getName())
	                || type.equals(Byte.class.getName())) {

	            return Byte.class;

	        } else if (type.equals(short.class.getName())
	                || type.equals(Short.class.getName())) {

	            return Short.class;

	        } else if (type.equals(int.class.getName())
	                || type.equals(Integer.class.getName())) {

	            return Integer.class;

	        } else if (type.equals(long.class.getName())
	                || type.equals(Long.class.getName())) {

	            return Long.class;

	        } else if (type.equals(char.class.getName())
	                || type.equals(Character.class.getName())) {

	            return Character.class;

	        } else if (type.equals(float.class.getName())
	                || type.equals(Float.class.getName())) {

	            return Float.class;

	        } else if (type.equals(double.class.getName())
	                || type.equals(Double.class.getName())) {

	            return Double.class;

	        } else if (type.equals(String.class.getName()) || type.equals("L" + String.class.getName() + ";")) {

	            return String.class;

	        } else {
	            return c;
	        }
    }
	/**
	 * Compares two method's list of parameters
	 * 
	 * @param paramTypes1 Formal parameters
	 * @param paramTypes2 Actual parameters or Formal parameters
	 * @return Returns true if both of them are the same, false otherewise
	 */
	public static boolean compareSignatures(Class[] paramTypes1,
			Class[] paramTypes2) {

		if (paramTypes1.length == 0) {
			if (paramTypes2.length == 0) {
				return true;
			} else {
			    return false;
			}
		} else if (paramTypes1.length == paramTypes2.length) {
			// && paramTypes1.length > 0 && paramTypes2.length > 0
			
			int n = paramTypes1.length;
			for (int i = 0; i < n; i++) {
				paramTypes1[i] = resolveType(paramTypes1[i]);
				paramTypes2[i] = resolveType(paramTypes2[i]);
                if (isPrimitive(paramTypes1[i]) && isPrimitive(paramTypes2[i])) {
                    if (isTypeAssignableFromType(paramTypes1[i], paramTypes2[i]));
                } else {
                    if (!paramTypes1[i].isAssignableFrom(paramTypes2[i])) {
                        return false;
                    }
                }
                /* Too simplistic
				String p1 = paramTypes1[i].getName();
				String p2 = paramTypes2[i].getName();
				if (!p1.equals(p2)) {
					return false;
				}
                */
				i++;
			}

		} else {
		    return false;
		}
		return true;
	}
    
    private static boolean isTypeAssignableFromType(Class c1, Class c2) {
        if (classEquals(Integer.class, c2)) {
            //int to int, long, float, or double
            if (classEquals(c1, Integer.class) || classEquals(c1, Long.class) || classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }
        } else if (classEquals(Double.class, c2)) {
            //double to double
            if (classEquals(c1, Double.class)) {
                return true;
            }            
        } else if (classEquals(Byte.class, c2)) {
            //byte to byte, short, int, long, float, or double
            if (classEquals(c1, Byte.class) || classEquals(c1, Short.class) ||  classEquals(c1, Integer.class) || classEquals(c1, Long.class) || classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }
        } else if (classEquals(Long.class, c2)) {
            //long to long, float, or double
            if (classEquals(c1, Long.class) || classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }            
        } else if (classEquals(Short.class, c2)) {
            //byte to byte, short, int, long, float, or double
            if (classEquals(c1, Short.class) ||  classEquals(c1, Integer.class) || classEquals(c1, Long.class) || classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }            
        } else if (classEquals(Boolean.class, c2)) {
            //boolean to boolean
            if (classEquals(c1, Boolean.class)) {
                return true;
            }
        } else if (classEquals(Float.class, c2)) {
            //float to float, or double
            if (classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }                        
        } else if (classEquals(Character.class, c2)) {
            //char to char, int, long, float, or double
            if (classEquals(c1, Character.class) || classEquals(c1, Integer.class) || classEquals(c1, Long.class) || classEquals(c1, Float.class) || classEquals(c1, Double.class)) {
                return true;
            }            
        }
        return false;
    }
    
    private static boolean classEquals(Class a, Class b) {
        return a.getName().equals(b.getName());
    }
    
    private static boolean isPrimitive(Class c) {
        return c.isPrimitive() 
        || classEquals(Integer.class, c)
        || classEquals(Double.class, c)
        || classEquals(Byte.class, c)
        || classEquals(Long.class, c)
        || classEquals(Short.class, c)
        || classEquals(Boolean.class, c)
        || classEquals(Float.class, c)
        || classEquals(Character.class, c);
    }

}

