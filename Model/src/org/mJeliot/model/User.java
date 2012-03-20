package org.mJeliot.model;

import java.util.Random;

/**
 * @author Moritz Rogalli
 * A User represents a user by pseudo-uniquely identifying him/her with an id and an
 * alias.
 */
public class User {
	/**
	 * The alias chosen by the user.
	 */
	private String name = null;
	/**
	 * The user id. Usually randomly generated.
	 */
	private int id;
	
	private Lecture lecture = null;

	/**
	 * Creates new user with a randomly generated id.
	 * @param name the alias for the user
	 */
	public User(String name) {
		this(name, new Random().nextInt());
	}
	/**
	 * Creates a user with a non-random id.
	 * @param name the user's alias
	 * @param id the user's id
	 */
	public User(String name, int id) {
		this(name, id, null);
	}
	/**
	 * Creates a user with a non-random id and a lecture connected to it.
	 * @param name the user's alias
	 * @param id the user's id
	 * @param lecture the user's lecture
	 */
	public User(String name, int id, Lecture lecture) {
		this.name = name;
		this.id = id;
		this.lecture = lecture;
	}
	/**
	 * @return the user's alias
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the user's id
	 */
	public int getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o.getClass() == User.class) {
			User user = (User)o;
			if (user.id == this.id) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	/**
	 * @param lecture the lecture to set
	 */
	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
	}
	/**
	 * @return the lecture
	 */
	public Lecture getLecture() {
		return lecture;
	}
	@Override
	public String toString() {
		return "mJeliot User, id=" + this.id + ", username=" + this.name;
		
	}
}
