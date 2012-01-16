package ict.model.predict;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * @author Moritz Rogalli
 * A method represents a prediction task. It contains the identifying characteristics of 
 * the method like the class it belongs to, the method's name and a generated pseudo-random
 * id to make it uniquely.
 */
public class Method {
	/**
	 * The name of the class that contains the method.
	 */
	private String className = null;
	/**
	 * The name of the method itself.
	 */
	private String methodName = null;
	/**
	 * The pseudo-unique id to identify the user.
	 */
	private int id;
	/**
	 * The method's parameters and return-value if applicable.
	 */
	private Vector<Parameter> parameters = new Vector<Parameter>();
	/**
	 * This constructor creates a new Method-object with a pseudo-randomly generated id.
	 * @param className the name of the class that contains the method
	 * @param methodName the method's name
	 */
	public Method(String className, String methodName) {
		this(className, methodName, new Random().nextInt());
	}
	/**
	 * This constructor creates a Method-object with a given id.
	 * @param className the name of the class that contains the method
	 * @param methodName the method's name
	 * @param id the method's id
	 */
	public Method(String className, String methodName, int id) {
		this.className = className;
		this.methodName = methodName;
		this.id = id;
	}
	
	/**
	 * @return the method's name
	 */
	public String getMethodName() {
		return this.methodName;
	}
	/**
	 * @return the method's parameters
	 */
	public Vector<Parameter> getParameters() {
		return this.parameters;
	}
	/**
	 * Get a parameter by its name.
	 * @param name the name of the parameter.
	 * @return a parameter by that name if it exists, null otherwise.
	 */
	public Parameter getParameterByName(String name) {
		Iterator<Parameter> i = this.parameters.iterator();
		while (i.hasNext()) {
			Parameter pm = i.next();
			if (pm.getName().equals(name)) {
				return pm;
			}
		}
		return null;
	}
	
	/**
	 * Method to add a parameter to the Method-object. This method does not check whether
	 * or not there's already a parameter by that name!
	 * @param name the name of the parameter
	 */
	public void addParameter(String name) {
		Parameter parameter = new Parameter(name);
		this.parameters.add(parameter);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ret = "Assignment: " + this.methodName + ", ID: " + this.getId() + 
		", Parameters: ";
		for (int i = 0; i < this.parameters.size(); i++) {
			ret = ret.concat(parameters.get(i).getName() + ", ");
		}
		ret = ret.substring(0, ret.length()-2);
		return ret;
	}
	
	/**
	 * @return the name of the method's class
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @return the id of the method
	 */
	public int getId() {
		return this.id;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o.getClass() == this.getClass() && ((Method)o).getId() == this.id);
	}
}
