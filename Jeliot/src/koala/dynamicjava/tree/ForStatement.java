/*
 * DynamicJava - Copyright (C) 1999-2001
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL DYADE BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Dyade shall not be
 * used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization from
 * Dyade.
 *
 */

package koala.dynamicjava.tree;

import java.util.LinkedList;
import java.util.List;

import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This class represents the for statement nodes of the syntax tree
 *
 * @author  Stephane Hillion
 * @version 1.0 - 1999/05/23
 */

public class ForStatement extends Statement implements ContinueTarget {
    /**
     * The initialization property name
     */
    public final static String INITIALIZATION = "initialization";

    /**
     * The condition property name
     */
    public final static String CONDITION = "condition";

    /**
     * The update property name
     */
    public final static String UPDATE = "update";

    /**
     * The body property name
     */
    public final static String BODY = "body";

    /**
     * The initialization statements
     */
    private List initialization;

    /**
     * The condition to evaluate at each loop
     */
    private Expression condition;

    /**
     * The update statements
     */
    private List update;

    /**
     * The body of this statement
     */
    private Node body;

    /**
     * The labels
     */
    private List labels;

    /**
     * Creates a new for statement
     * @param init  the initialization statements
     * @param cond  the condition to evaluate at each loop
     * @param updt  the update statements
     * @param body  the body
     * @exception IllegalArgumentException if body is null
     */
    public ForStatement(List init, Expression cond, List updt, Node body) {
	this(init, cond, updt, body, null, 0, 0, 0, 0);
    }

    /**
     * Creates a new for statement
     * @param init  the initialization statements
     * @param cond  the condition to evaluate at each loop
     * @param updt  the update statements
     * @param body  the body
     * @param fn    the filename
     * @param bl    the begin line
     * @param bc    the begin column
     * @param el    the end line
     * @param ec    the end column
     * @exception IllegalArgumentException if body is null
     */
    public ForStatement(List init, Expression cond, List updt, Node body,
			String fn, int bl, int bc, int el, int ec) {
	super(fn, bl, bc, el, ec);

	if (body == null) throw new IllegalArgumentException("body == null");

	initialization = init;
	condition      = cond;
	update         = updt;
	this.body      = body;
	labels         = new LinkedList();
    }
    
    /**
     * Gets the initialization statements
     */
    public List getInitialization() {
	return initialization;
    }

    /**
     * Sets the initialization statements
     */
    public void setInitialization(List l) {
	firePropertyChange(INITIALIZATION, initialization, initialization = l);
    }

    /**
     * Gets the condition to evaluate at each loop
     */
    public Expression getCondition() {
	return condition;
    }

    /**
     * Sets the condition to evaluate
     */
    public void setCondition(Expression e) {
	firePropertyChange(CONDITION, condition, condition = e);
    }

    /**
     * Gets the update statements
     */
    public List getUpdate() {
	return update;
    }

    /**
     * Sets the update statements
     */
    public void setUpdate(List l) {
	firePropertyChange(UPDATE, update, update = l);
    }

    /**
     * Returns the body of this statement
     */
    public Node getBody() {
	return body;
    }

    /**
     * Sets the body of this statement
     * @exception IllegalArgumentException if node is null
     */
    public void setBody(Node node) {
	if (node == null) throw new IllegalArgumentException("node == null");

	firePropertyChange(BODY, body, body = node);
    }

    /**
     * Adds a label to this statement
     * @param label the label to add
     * @exception IllegalArgumentException if label is null
     */
    public void addLabel(String label) {
	if (label == null) throw new IllegalArgumentException("label == null");

	labels.add(label);
    }

    /**
     * Test whether this statement has the given label
     * @return true if this statement has the given label
     */
    public boolean hasLabel(String label) {
	return labels.contains(label);
    }

    /**
     * Allows a visitor to traverse the tree
     * @param visitor the visitor to accept
     */
    public Object acceptVisitor(Visitor visitor) {
	return visitor.visit(this);
    }    
}
