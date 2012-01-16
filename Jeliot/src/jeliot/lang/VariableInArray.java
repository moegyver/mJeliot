package jeliot.lang;


/**
  * VariableInArray is an instance of an array variable.
  * A new array variables are created at runtime every time
  * a new array is created.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  * 
  * @see jeliot.lang.ArrayInstance
  */
public class VariableInArray extends Variable {

//  DOC: document!
    /**
	 *
	 */
	ArrayInstance array;

    /**
	 * 
	 */
	public VariableInArray() {}

    /**
	 * @param array
	 * @param componentType
	 */
	public VariableInArray(ArrayInstance array, String componentType) {
        this.array = array;
        setType(componentType);
    }

}
