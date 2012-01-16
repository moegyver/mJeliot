package jeliot.util;

import java.lang.reflect.Array;

/**
 * A class to help the array handling.
 * 
 * @author Niko Myller
 */
public class ArrayUtilities {

    /**
     * sets the given index (first parameter) to point to the next index
     * of the array in depth first order. If the all the indeces are visited
     * then a false is returned otherwise a true is returned.
     * @param indexCounters The current index of the array.
     * @param lengths The lenght of the dimensions in the array.
     * @return true if there is a next index otherwise a false is returned.
     */
    public static boolean nextIndex(int[] indexCounters, int[] lengths) {
        int length = lengths.length;
        int index = length - 1;

        while (index >= 0) {

            indexCounters[index]++;

            if (indexCounters[index] < lengths[index]) {
                break;
            }

            indexCounters[index] = 0;
            index--;
        }

        for (int i = 0; i < length - 1; i++) {
            if (indexCounters[i] != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the object from the given array and from the given index.
     * @param array The array from where the object should be returned.
     * @param index The index from where the object should be returned.
     * @return The object from the given array and given index.
     */
    public static Object getObjectAt(Object array, int[] index) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                return Array.get(tempArray, index[i]);
            }
            tempArray = Array.get(tempArray, index[i]);
        }
        return null;
    }

    /**
     * Assigns the given object into the given array and the given index.
     * @param array The array in which the object should be assigned.
     * @param index The index in which the object should be assigned.
     * @param newObject The value to be assigned
     */
    public static void setObjectAt(Object array, int[] index, Object newObject) {
        Object tempArray = array;
        int n = index.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                Array.set(array, index[i], newObject);
            } else {
                tempArray = Array.get(tempArray, index[i]);
            }
        }
    }
    
}