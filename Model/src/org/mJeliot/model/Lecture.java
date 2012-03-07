package org.mJeliot.model;

import java.util.HashMap;
import java.util.Random;
import org.mJeliot.model.predict.Method;


/**
 * A Lecture object represents all the important data of a lecture in the
 * ICT-system. It is used to store identifying information and the state of the
 * lecture.
 * 
 * @author Moritz Rogalli
 * 
 */
public class Lecture {
	/**
	 * The lecture's name
	 */
	private String name = null;
	/**
	 * The lecture's id to identify it
	 */
	private int id;
	/**
	 * The current method to predict
	 */
	private Method method = null;
	/**
	 * A password for a client that should be able to send out assignments.
	 */
	private String adminPassword = null;
	/**
	 * A password for all basic clients that should not be able to hand out
	 * assignments.
	 */
	private String clientPassword = null;
	/**
	 * the users that are connected.
	 */
	private HashMap<Integer, User> users = new HashMap<Integer, User>();

	/**
	 * Creates a lecture with a randomly selected id.
	 * 
	 * @param name
	 *            the lecture's name
	 */
	public Lecture(String name) {
		this(new Random().nextInt(), name);
	}

	/**
	 * Creates a lecture with a predefined id.
	 * 
	 * @param id
	 *            the id
	 * @param name
	 *            the lecture's name
	 */
	public Lecture(int id, String name) {
		this.name = name;
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a method as the new method, the method's id is random generated.
	 * 
	 * @param className
	 *            the name of the method's class
	 * @param methodName
	 *            the method's name
	 */
	public void newMethod(String className, String methodName) {
		this.method = new Method(className, methodName);
	}

	/**
	 * Set a method with a known id as the new method.
	 * 
	 * @param className
	 *            the name of the method's class
	 * @param methodName
	 *            the method's name
	 * @param id
	 *            the method's id
	 */
	public void newMethod(String className, String methodName, int id) {
		this.method = new Method(className, methodName, id);
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * @return the method's id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param adminPassword
	 *            the adminPassword to set
	 */
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	/**
	 * @return the adminPassword
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * @param clientPassword
	 *            the clientPassword to set
	 */
	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

	/**
	 * @return the clientPassword
	 */
	public String getClientPassword() {
		return clientPassword;
	}

	/**
	 * sets the current method to null
	 */
	public void removeCurrentMethod() {
		this.method = null;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * @return a copy of the users connected to this lecture.
	 */
	public User[] getUsers() {
		return (User[]) this.users.values().toArray();
	}

	public User getUser(int id) {
		return this.users.get(id);
	}
	
	/**
	 * Adds a user to the lecture's user list. Does not do anything if the user
	 * is already in the list.
	 * 
	 * @param user
	 *            the user to add
	 */
	public void addUser(User user) {
		if (!this.users.containsKey(user.getId())) {
			this.users.put(user.getId(), user);
		}
	}

	/**
	 * Removes a user from the user list.
	 * 
	 * @param user
	 *            the user to remove
	 */
	public void removeUser(User user) {
		this.users.remove(user);
	}

	public boolean containsUser(User user) {
		return this.users.containsKey(user.getId());
	}
}
