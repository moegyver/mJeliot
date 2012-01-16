package jeliot.lang;

import jeliot.mcode.MCodeUtilities;
import jeliot.theater.ArrayActor;

 /**
  * The objects of this class represents an array of n-dimensions.
  * 
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class ArrayInstance extends Instance {

    /**
     * 
     */
    private Variable arrayLenghtVariable;
    
    /**
     * The array object.
     */
    private VariableInArray[] array;
    
    /**
     * The string presentation of the type of the component.
     */
    private String componentType;
    
    /**
     * The length of each dimension in the array.
     */
    private int dimensions;
    
    /**
     * The corresponding array actor.
     */
    private ArrayActor arrayActor;

    /**
     * 
     */
    private int length;
    
    /**
     * Creates the array and sets inside the VariableInArray object
     * which again contain the Value objects of the corresponding component type.
     * 
     * @param hashCode the hashCode of the array.
     * @param componentType The component type of the array.
     * @param dimensions The lengths of the dimensions of the array.
     * @param actualDimension It includes even those that are not declared, e.g. new int[4][] => aD = 2
     * 
     */
    public ArrayInstance(String hashCode, String componentType, int dimensions, int actualDimension, int length) {
        super(hashCode);
        
        String type = componentType;
        
        for (int i = 0; i < actualDimension; i++) {
               type = "[" + type;
               if (i >= 1) {
                   componentType = "[" + componentType;
               }
        }

        setType(type);

        this.arrayLenghtVariable = new Variable("length", "int");
        arrayLenghtVariable.assign(new Value("" + length, "int"));
        
        this.componentType = componentType;
        this.array = new VariableInArray[length];
        this.dimensions = actualDimension;
        this.length = length;
        
        int n = length;
        
        String defaultValue = MCodeUtilities.getDefaultValue(componentType);
        for (int i = 0; i < n; i++) {
            VariableInArray via = new VariableInArray(this, componentType);
            array[i] = via;
            Value v = new Value(defaultValue, componentType);
            via.assign(v);
        }
    }

    /**
     * Returns the array variable from the given index in the array.
     * @param index an array containing the indeces for all dimensions of the array.
     * @return The VariableInArray object from the given index of the array.
     */
    public VariableInArray getVariableAt(int index) {
        return array[index];
    }

    /**
     * Gives the length of the dimensions in the array.
     * @return The length of the dimensions in the array.
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * Assigns the given value (second parameter) into the
     * VariableInArray in the given index (first parameter) of the array.
     * @param index The index of the array
     * @param newValue The new value of the VariableInArray.
     */
    public void setValueAt(int index, Value newValue) {
        array[index].assign(newValue);
    }

    /**
     * The dimensions of the array.
     * Is this needed because there is the other method doing the same?
     * @return The dimensions of the array
     */
    public int length() {
        return length;
    }

    /**
     * Sets the corresponding ArrayActor.
     * @param aa Array actor for this array.
     */
    public void setArrayActor(ArrayActor aa) {
        this.arrayActor = aa;
        setActor(aa);
    }

    /**
     * Returns the corresponding array actor.
     * @return the array actor of this array.
     */
    public ArrayActor getArrayActor() {
        return arrayActor;
    }

    /**
     * Returns String presentation of the component type of the array
     * @return String presentation of the component type.
     */
    public String getComponentType() {
        return componentType;
    }

    public Variable getArrayLenghtVariable() {
        return arrayLenghtVariable;
    }

    public void setArrayLenghtVariable(Variable arrayLenght) {
        this.arrayLenghtVariable = arrayLenght;
    }

}