/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */

package koala.dynamicjava.interpreter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import jeliot.mcode.Code;
import jeliot.mcode.MCodeGenerator;
import jeliot.mcode.MCodeUtilities;
import jeliot.mcode.input.InputHandler;
import jeliot.mcode.input.InputHandlerFactory;
import jeliot.util.DebugUtil;
import koala.dynamicjava.interpreter.context.Context;
import koala.dynamicjava.interpreter.context.GlobalContext;
import koala.dynamicjava.interpreter.error.CatchedExceptionError;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.interpreter.modifier.ArrayModifier;
import koala.dynamicjava.interpreter.modifier.LeftHandSideModifier;
import koala.dynamicjava.interpreter.modifier.ObjectFieldModifier;
import koala.dynamicjava.interpreter.throwable.BreakException;
import koala.dynamicjava.interpreter.throwable.ContinueException;
import koala.dynamicjava.interpreter.throwable.ReturnException;
import koala.dynamicjava.interpreter.throwable.ThrownException;
import koala.dynamicjava.tree.AddAssignExpression;
import koala.dynamicjava.tree.AddExpression;
import koala.dynamicjava.tree.AndExpression;
import koala.dynamicjava.tree.ArrayAccess;
import koala.dynamicjava.tree.ArrayAllocation;
import koala.dynamicjava.tree.ArrayInitializer;
import koala.dynamicjava.tree.BitAndAssignExpression;
import koala.dynamicjava.tree.BitAndExpression;
import koala.dynamicjava.tree.BitOrAssignExpression;
import koala.dynamicjava.tree.BitOrExpression;
import koala.dynamicjava.tree.BlockStatement;
import koala.dynamicjava.tree.BreakStatement;
import koala.dynamicjava.tree.CastExpression;
import koala.dynamicjava.tree.CatchStatement;
import koala.dynamicjava.tree.ClassAllocation;
import koala.dynamicjava.tree.ComplementExpression;
import koala.dynamicjava.tree.ConditionalExpression;
import koala.dynamicjava.tree.ContinueStatement;
import koala.dynamicjava.tree.DivideAssignExpression;
import koala.dynamicjava.tree.DivideExpression;
import koala.dynamicjava.tree.DoStatement;
import koala.dynamicjava.tree.EqualExpression;
import koala.dynamicjava.tree.ExclusiveOrAssignExpression;
import koala.dynamicjava.tree.ExclusiveOrExpression;
import koala.dynamicjava.tree.Expression;
import koala.dynamicjava.tree.ForStatement;
import koala.dynamicjava.tree.FormalParameter;
import koala.dynamicjava.tree.FunctionCall;
import koala.dynamicjava.tree.GreaterExpression;
import koala.dynamicjava.tree.GreaterOrEqualExpression;
import koala.dynamicjava.tree.IfThenElseStatement;
import koala.dynamicjava.tree.IfThenStatement;
import koala.dynamicjava.tree.InnerAllocation;
import koala.dynamicjava.tree.InstanceOfExpression;
import koala.dynamicjava.tree.LabeledStatement;
import koala.dynamicjava.tree.LessExpression;
import koala.dynamicjava.tree.LessOrEqualExpression;
import koala.dynamicjava.tree.Literal;
import koala.dynamicjava.tree.MethodCall;
import koala.dynamicjava.tree.MethodDeclaration;
import koala.dynamicjava.tree.MinusExpression;
import koala.dynamicjava.tree.MultiplyAssignExpression;
import koala.dynamicjava.tree.MultiplyExpression;
import koala.dynamicjava.tree.Node;
import koala.dynamicjava.tree.NotEqualExpression;
import koala.dynamicjava.tree.NotExpression;
import koala.dynamicjava.tree.ObjectFieldAccess;
import koala.dynamicjava.tree.ObjectMethodCall;
import koala.dynamicjava.tree.OrExpression;
import koala.dynamicjava.tree.PlusExpression;
import koala.dynamicjava.tree.PostDecrement;
import koala.dynamicjava.tree.PostIncrement;
import koala.dynamicjava.tree.PreDecrement;
import koala.dynamicjava.tree.PreIncrement;
import koala.dynamicjava.tree.QualifiedName;
import koala.dynamicjava.tree.RemainderAssignExpression;
import koala.dynamicjava.tree.RemainderExpression;
import koala.dynamicjava.tree.ReturnStatement;
import koala.dynamicjava.tree.ShiftLeftAssignExpression;
import koala.dynamicjava.tree.ShiftLeftExpression;
import koala.dynamicjava.tree.ShiftRightAssignExpression;
import koala.dynamicjava.tree.ShiftRightExpression;
import koala.dynamicjava.tree.SimpleAllocation;
import koala.dynamicjava.tree.SimpleAssignExpression;
import koala.dynamicjava.tree.StaticFieldAccess;
import koala.dynamicjava.tree.StaticMethodCall;
import koala.dynamicjava.tree.SubtractAssignExpression;
import koala.dynamicjava.tree.SubtractExpression;
import koala.dynamicjava.tree.SuperFieldAccess;
import koala.dynamicjava.tree.SuperMethodCall;
import koala.dynamicjava.tree.SwitchBlock;
import koala.dynamicjava.tree.SwitchStatement;
import koala.dynamicjava.tree.SynchronizedStatement;
import koala.dynamicjava.tree.ThrowStatement;
import koala.dynamicjava.tree.TryStatement;
import koala.dynamicjava.tree.TypeExpression;
import koala.dynamicjava.tree.UnsignedShiftRightAssignExpression;
import koala.dynamicjava.tree.UnsignedShiftRightExpression;
import koala.dynamicjava.tree.VariableDeclaration;
import koala.dynamicjava.tree.WhileStatement;
import koala.dynamicjava.tree.visitor.Visitor;
import koala.dynamicjava.tree.visitor.VisitorObject;
import koala.dynamicjava.util.Constants;
import koala.dynamicjava.util.ImportationManager;

/**
 * This tree visitor evaluates each node of a syntax tree
 *
 * @author Stephane Hillion
 * @version 1.2 - 2001/01/23
 */

public class EvaluationVisitor extends VisitorObject {

    /**
     * Sets the initial values to the different flags used in EvaluationVisitor
     *
     * @see counter
     * @see evaluating
     * @see preparing
     * @see constructorCallNames
     * @see superClasses
     * @see constructorCallNumbers
     */
    public void initialize() {
        counter = 1;
        evaluating = true;
        preparing = new Stack();
        constructorCallNames = new Stack();
        superClasses = new Stack();
        constructorCallNumbers = new Stack();
        constructorInfoNames = new Stack();
        constructorInfoParamTypes = new Stack();
        MCodeUtilities.superClassesStack = new Stack();
        domesticStack = new Stack();
        returnExpressionCounterStack = new Stack();
    }

    /**
     * Identifies each expression
     */
    private static long counter = 1;

    /**
     * When set to true it means it is evaluating the expression. It is used
     * mainly when the production of MCode needs to re-visit a node and thus it
     * is set to FALSE so not to re evaluate the node.
     */
    private boolean evaluating = true;

    /**
     * Indicates when ArrayModifier is preparing the array, and and thus we
     * don't want info
     */
    private static Stack preparing = new Stack();

    /**
     * Used in Simple Allocation and TreeInterpreter.interpretMethod Used to
     * identify the name of the constructor and distinguish it from its super
     * constructors
     */
    private static Stack constructorCallNames = new Stack();

    /**
     * Contains the superClasses
     */
    private static Stack superClasses = new Stack();

    /**
     * Contains the all the constructorCallNumbers that are used to track the
     * constructor invocations.
     */
    private static Stack constructorCallNumbers = new Stack();

    /**
     * Method to get the counter, also called Instruction Reference
     *
     * @return actual value of counter
     */
    public static long getCounter() {
        return counter;
    }

    /**
     * Increments the instruction counter. To be used when it has already been
     * asigned to an instruction
     */
    public static void incrementCounter() {
        counter++;
    }

    /**
     * Set preparing value to true. So ArrayModifier is preparing the array to
     * visit
     */
    public static void setPreparing() {
        preparing.push(new Boolean(true));
    }

    /**
     * Unset preparing value to false. So ArrayModifier has finished preparing
     * the array to visit
     */
    public static void unsetPreparing() {
        preparing.pop();
    }

    /**
     * Returns preparing value
     */
    public static boolean isSetPreparing() {
        return !preparing.isEmpty()
                && ((Boolean) preparing.peek()).booleanValue();
    }

    /**
     * shortCircuit will indicate if we evaluate && and || in shor circuit
     * TODO: place it somewhere more reasonable
     */
    public boolean shortCircuit = true;

    /**
     * Used in Simple Allocation and TreeInterpreter.interpretMethod Used to
     * identify the name of the constructor and compare it with the actual method interpreted
     */
    private static Stack constructorInfoNames = new Stack();

    /**
     * Contains the list of parameters of the current interpreted constructors
     */
    private static Stack constructorInfoParamTypes = new Stack();

    /**
     * Returns constructorCall value
     */
    public static boolean isSetConstructorInfo() {
        return !constructorInfoNames.empty();
    }

    /**
     * Adds the info of the current constructor to the stack.
     * Used to solve the "this" method call problem
     *
     * @param name       Name of the constructor
     * @param paramTypes List of constructor parameters types
     */
    public static void pushConstructorInfo(String name, Class[] paramTypes) {
        MCodeUtilities.constructorParametersStack.push(paramTypes);
        MCodeUtilities.constructorNameStack.push(name);
    }

    /**
     * Returns constructorCall value
     */
    public static boolean isSetConstructorCall() {
        return !constructorCallNames.empty();
    }

    public static String getConstructorCallName() {
        return (String) constructorCallNames.peek();
    }


    /**
     * 
     * @param name
     * @param superClassesNames
     * @param consCallNumber
     */
    public static void newConstructorCall(String name,
            Vector superClassesNames, long consCallNumber) {
        Long ccn = new Long(consCallNumber);
        constructorCallNumbers.push(ccn);
        constructorCallNames.push(name);
        superClasses.push(superClassesNames);
    }

    /**
     * 
     *
     */
    public static void constructorCallFinished() {
        constructorCallNumbers.pop();
        constructorCallNames.pop();
        superClasses.pop();
    }

    public static Vector getSuperClasses() {
        return (Vector) superClasses.peek();
    }

    public static long getConstructorCallNumber() {
        return ((Long) constructorCallNumbers.peek()).longValue();
    }

    public Vector extractSuperClasses(Class c) {
        Vector v = new Vector();
        do {
            v.add(c.getName());
            c = c.getSuperclass();
        } while (c != null);
        return v;
    }

    /**
     * Set inside value to true. So we have the information required (MD and
     * PARAMETER) in a static method call
     */
    public static void setInside() {
        //inside = true;
        domesticStack.push(returnExpressionCounterStack.peek());
    }

    /**
     * Unset inside value to false.
     */
    public static void unsetInside() {
        //inside = false;
        domesticStack.pop();
    }

    /**
     * Returns preparing value
     */
    public static boolean isSetInside() {

        boolean emptyStacks = (returnExpressionCounterStack.isEmpty() || (domesticStack
                .isEmpty()));
        boolean inside = false;
        if (!emptyStacks)
            inside = ((Long) returnExpressionCounterStack.peek())
                    .equals((Long) domesticStack.peek());
        return inside;
    }

    /**
     * Stack to keep track of the references of the return expression. Used to
     * allow method calls inside them. Public so TreeInterpreter can push a value when invoking a
     * constructor.
     */
    public static Stack returnExpressionCounterStack = new Stack();

    /**
     * Stack with the references of those methods where we can access to its
     * source ("domestic")
     */
    private static Stack domesticStack = new Stack();

    /**
     * Stack to keep cell numbers of arrays. Each element stores the cell number
     * of one array in one list
     */

    private Stack arrayCellNumbersStack = new Stack();

    /**
     * Stack to keep the references of the cell expressions
     */
    private Stack arrayCellReferencesStack = new Stack();

    /**
     * The current context
     */
    private Context context;

    /**
     * Creates a new visitor
     *
     * @param ctx the current context
     */
    public EvaluationVisitor(Context ctx) {
        context = ctx;
    }

    /**
     * Visits a WhileStatement
     *
     * @param node the node to visit
     */
    public Object visit(WhileStatement node) {
        long condcounter = counter; //Reference number for the condition
        // expression
        long round = 0; // Number of iterations in the loop
        boolean breakc = false; // Exiting while loop because of break
        try {
            while (((Boolean) node.getCondition().acceptVisitor(this))
                    .booleanValue()) {
                try {
                    MCodeUtilities.write("" + Code.WHI + Code.DELIM
                            + condcounter + Code.DELIM + Code.TRUE + Code.DELIM
                            + round + Code.DELIM
                            + MCodeGenerator.locationToString(node.getBody()));
                    node.getBody().acceptVisitor(this);
                    condcounter = counter;
                    round++;
                } catch (ContinueException e) {
                    MCodeUtilities.write("" + Code.CONT + Code.DELIM + Code.WHI
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node.getBody()));
                    condcounter = counter;
                    // 'continue' statement management
                    if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                        throw e;
                    }
                }
            }
        } catch (BreakException e) {
            // 'break' statement management
            MCodeUtilities.write("" + Code.BR + Code.DELIM + Code.WHI
                    + Code.DELIM
                    + MCodeGenerator.locationToString(node.getBody()));
            breakc = true;
            if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                throw e;
            }
        }
        if (!breakc)
            MCodeUtilities.write("" + Code.WHI + Code.DELIM + condcounter
                    + Code.DELIM + Code.FALSE + Code.DELIM + round + Code.DELIM
                    + MCodeGenerator.locationToString(node.getBody()));

        return null;
    }

    /**
     * Visits a ForStatement
     *
     * @param node the node to visit
     */
    public Object visit(ForStatement node) {
        long condcounter = counter;
        long round = 0; // Number of iterations
        boolean breakc = false; // Exiting while loop because of break

        try {
            Set vars = (Set) node.getProperty(NodeProperties.VARIABLES);
            //Open scope for the declaration of the loop indexes
            MCodeUtilities.write(Code.SCOPE + Code.DELIM + "1");
            context.enterScope(vars);

            // Interpret the initialization expressions
            if (node.getInitialization() != null) {
                Iterator it = node.getInitialization().iterator();
                while (it.hasNext()) {
                    ((Node) it.next()).acceptVisitor(this);
                }
            }

            // Interpret the loop
            try {

                Expression cond = node.getCondition();
                List update = node.getUpdate();
                condcounter = counter;
                while (cond == null
                        || ((Boolean) cond.acceptVisitor(this)).booleanValue()) {
                    try {
                        MCodeUtilities.write(""
                                + Code.FOR
                                + Code.DELIM
                                + condcounter
                                + Code.DELIM
                                + Code.TRUE
                                + Code.DELIM
                                + round
                                + Code.DELIM
                                + MCodeGenerator.locationToString(node
                                        .getBody()));
                        node.getBody().acceptVisitor(this);
                        condcounter = counter;
                        round++;
                    } catch (ContinueException e) {
                        condcounter = counter;
                        // 'continue' statement management
                        if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                            throw e;
                        }
                    }
                    // Interpret the update statements
                    if (update != null) {
                        Iterator it = update.iterator();
                        while (it.hasNext()) {
                            ((Node) it.next()).acceptVisitor(this);
                        }
                    }
                }
            } catch (BreakException e) {
                // 'break' statement management
                MCodeUtilities.write("" + Code.BR + Code.DELIM + Code.FOR
                        + Code.DELIM
                        + MCodeGenerator.locationToString(node.getBody()));
                breakc = true;
                if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                    throw e;
                }
            }
        } finally {
            // Always leave the current scope
            context.leaveScope();
            if (!breakc) {
                MCodeUtilities.write("" + Code.FOR + Code.DELIM + condcounter
                        + Code.DELIM + Code.FALSE + Code.DELIM + round
                        + Code.DELIM
                        + MCodeGenerator.locationToString(node.getBody()));
            }
            MCodeUtilities.write(Code.SCOPE + Code.DELIM + "0");
        }
        return null;
    }

    /**
     * Visits a DoStatement
     *
     * @param node the node to visit
     */
    public Object visit(DoStatement node) {
        long condcounter = counter;
        long round = 0; // Number of iterations
        boolean breakc = false; // Exiting while loop because of break

        try {
            // Interpret the loop
            do {

                MCodeUtilities.write("" + Code.DO + Code.DELIM + condcounter
                        + Code.DELIM + Code.TRUE + Code.DELIM + round
                        + Code.DELIM
                        + MCodeGenerator.locationToString(node.getBody()));

                try {
                    node.getBody().acceptVisitor(this);
                    round++;
                    condcounter = counter;
                } catch (ContinueException e) {
                    // 'continue' statement management
                    if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                        condcounter = counter;
                        throw e;
                    }
                }
            } while (((Boolean) node.getCondition().acceptVisitor(this))
                    .booleanValue());
        } catch (BreakException e) {
            // 'break' statement management
            MCodeUtilities.write("" + Code.BR + Code.DELIM + Code.DO
                    + Code.DELIM
                    + MCodeGenerator.locationToString(node.getBody()));
            breakc = true;

            if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                throw e;
            }
        }
        if (!breakc)
            MCodeUtilities.write("" + Code.DO + Code.DELIM + condcounter
                    + Code.DELIM + Code.FALSE + Code.DELIM + round + Code.DELIM
                    + MCodeGenerator.locationToString(node.getBody()));

        return null;
    }

    /**
     * Visits a SwitchStatement
     *
     * @param node the node to visit
     */
    public Object visit(SwitchStatement node) {

        boolean breakc = false; // Exiting while loop because of break

        try {
            boolean processed = false;

            MCodeUtilities.write("" + Code.SWITCHB + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            long selectorCounter = counter;

            // Evaluate the choice expression
            Object o = node.getSelector().acceptVisitor(this);
            if (o instanceof Character) {
                o = new Integer(((Character) o).charValue());
            }
            Number n = (Number) o;

            // Search for the matching label
            ListIterator it = node.getBindings().listIterator();
            ListIterator dit = null;
            loop: while (it.hasNext()) {
                SwitchBlock sc = (SwitchBlock) it.next();
                Number l = null;

                long switchBlockCounter = counter;
                if (sc.getExpression() != null) {
                    switchBlockCounter = counter;
                    o = sc.getExpression().acceptVisitor(this);
                    if (o instanceof Character) {
                        o = new Integer(((Character) o).charValue());
                    }
                    l = (Number) o;
                } else {
                    dit = node.getBindings().listIterator(it.nextIndex() - 1);
                }

                if (l != null && n.intValue() == l.intValue()) {
                    processed = true;

                    MCodeUtilities.write(""
                            + Code.SWIBF
                            + Code.DELIM
                            + selectorCounter
                            + Code.DELIM
                            + switchBlockCounter
                            + Code.DELIM
                            + MCodeGenerator.locationToString(sc
                                    .getExpression()));

                    // When a matching label is found, interpret all the
                    // remaining statements
                    for (;;) {
                        if (sc.getStatements() != null) {
                            Iterator it2 = sc.getStatements().iterator();
                            while (it2.hasNext()) {
                                ((Node) it2.next()).acceptVisitor(this);
                            }
                        }
                        if (it.hasNext()) {
                            sc = (SwitchBlock) it.next();
                        } else {
                            break loop;
                        }
                    }
                }
            }

            // Default case handling if no matching case was found.
            if (!processed && dit != null) {

                SwitchBlock sc = (SwitchBlock) dit.next();

                MCodeUtilities.write("" + Code.SWIBF + Code.DELIM
                        + selectorCounter + Code.DELIM + "-1" + Code.DELIM
                        + MCodeGenerator.locationToString(sc));

                for (;;) {

                    if (sc.getStatements() != null) {

                        Iterator it2 = sc.getStatements().iterator();
                        while (it2.hasNext()) {
                            ((Node) it2.next()).acceptVisitor(this);
                        }
                    }

                    if (dit.hasNext()) {
                        sc = (SwitchBlock) dit.next();
                    } else {
                        break;
                    }
                }
            }

        } catch (BreakException e) {
            // 'break' statement management
            MCodeUtilities.write("" + Code.BR + Code.DELIM + Code.SWITCH
                    + Code.DELIM + MCodeGenerator.locationToString(node));
            breakc = true;
            if (e.isLabeled()) {
                throw e;
            }
        }

        if (!breakc)
            MCodeUtilities.write("" + Code.SWITCH + Code.DELIM
                    + MCodeGenerator.locationToString(node));

        return null;
    }

    /**
     * Visits a LabeledStatement Not implemented in Jeliot 3
     *
     * @param node the node to visit
     */
    public Object visit(LabeledStatement node) {
        try {
            node.getStatement().acceptVisitor(this);
        } catch (BreakException e) {
            // 'break' statement management
            if (!e.isLabeled() || !e.getLabel().equals(node.getLabel())) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Visits a SynchronizedStatement. Not implemented in Jeliot 3
     *
     * @param node the node to visit
     */
    public Object visit(SynchronizedStatement node) {
        synchronized (node.getLock().acceptVisitor(this)) {
            node.getBody().acceptVisitor(this);
        }
        return null;
    }

    /**
     * Visits a BreakStatement
     *
     * @param node the node to visit
     */
    public Object visit(BreakStatement node) {
        throw new BreakException("unexpected.break", node.getLabel());
    }

    /**
     * Visits a ContinueStatement
     *
     * @param node the node to visit
     */
    public Object visit(ContinueStatement node) {
        throw new ContinueException("unexpected.continue", node.getLabel());
    }

    /**
     * Visits a TryStatement. Not implemented in Jeliot 3
     *
     * @param node the node to visit
     */
    public Object visit(TryStatement node) {

        boolean handled = false;
        try {
            node.getTryBlock().acceptVisitor(this);
        } catch (Throwable e) {
            Throwable t = e;
            if (e instanceof ThrownException) {
                t = ((ThrownException) e).getException();
            } else if (e instanceof CatchedExceptionError) {
                t = ((CatchedExceptionError) e).getException();
            }

            // Find the exception handler
            Iterator it = node.getCatchStatements().iterator();
            while (it.hasNext()) {
                CatchStatement cs = (CatchStatement) it.next();
                Class c = NodeProperties.getType(cs.getException().getType());
                if (c.isAssignableFrom(t.getClass())) {
                    handled = true;

                    // Define the exception in a new scope
                    context.enterScope();
                    context.define(cs.getException().getName(), t);

                    // Interpret the handler
                    cs.getBlock().acceptVisitor(this);
                    break;
                }
            }

            if (!handled) {
                if (e instanceof Error) {
                    throw (Error) e;
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new CatchedExceptionError((Exception) e, node);
                }
            }
        } finally {
            // Leave the current scope if entered
            if (handled) {
                context.leaveScope();
            }

            // Interpret the 'finally' block
            Node n;
            if ((n = node.getFinallyBlock()) != null) {
                n.acceptVisitor(this);
            }
        }
        return null;
    }

    /**
     * Visits a ThrowStatement. Not implemented in Jeliot 3
     *
     * @param node the node to visit
     */
    public Object visit(ThrowStatement node) {
        throw new ThrownException((Throwable) node.getExpression()
                .acceptVisitor(this));
    }

    /**
     * Visits a ReturnStatement
     *
     * @param node the node to visit
     */
    public Object visit(ReturnStatement node) {

        Object o = null; // Object to be returned
        //We get the reference assigned in the call method to this return
        // statement
        Long l = (Long) returnExpressionCounterStack.peek();

        if (node.getExpression() != null) {

            long auxcounter = counter;

            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.R
                    + Code.DELIM + l.toString() + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            o = node.getExpression().acceptVisitor(this); //
            String value = MCodeUtilities.getValue(o);

            String typeString = "";
            Class type = NodeProperties.getType(node.getExpression());
            if (type != null) {
                typeString = type.getName();
            } else {
                typeString = "null";
            }
            MCodeUtilities.write("" + Code.R + Code.DELIM + l.toString()
                    + Code.DELIM + auxcounter + Code.DELIM + value + Code.DELIM
                    + typeString + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            throw new ReturnException("return.statement", o, node);
            // If there is nothing to return we indicate this with the
            // NO_REFERENCE flag
        } else {

            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.R
                    + Code.DELIM + Code.NO_REFERENCE + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            MCodeUtilities.write("" + Code.R + Code.DELIM + (counter++)
                    + Code.DELIM + Code.NO_REFERENCE + Code.DELIM
                    + Code.UNKNOWN + Code.DELIM + Void.TYPE.getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            throw new ReturnException("return.statement", node);
        }
    }

    /**
     * Visits a IfThenStatement
     *
     * @param node the node to visit
     */
    public Object visit(IfThenStatement node) {
        long condcounter = counter;
        if (((Boolean) node.getCondition().acceptVisitor(this)).booleanValue()) {

            MCodeUtilities.write("" + Code.IFT + Code.DELIM + condcounter
                    + Code.DELIM + Code.TRUE + Code.DELIM
                    + MCodeGenerator.locationToString(node.getThenStatement()));

            node.getThenStatement().acceptVisitor(this);
        } else {
            MCodeUtilities.write("" + Code.IFT + Code.DELIM + condcounter
                    + Code.DELIM + Code.FALSE);
        }
        return null;
    }

    /**
     * Visits a IfThenElseStatement
     *
     * @param node the node to visit
     */
    public Object visit(IfThenElseStatement node) {
        long condcounter = counter;
        String value;
        if (((Boolean) node.getCondition().acceptVisitor(this)).booleanValue()) {
            MCodeUtilities.write("" + Code.IFTE + Code.DELIM + condcounter
                    + Code.DELIM + Code.TRUE + Code.DELIM
                    + MCodeGenerator.locationToString(node.getThenStatement()));

            node.getThenStatement().acceptVisitor(this);

        } else {
            MCodeUtilities.write("" + Code.IFTE + Code.DELIM + condcounter
                    + Code.DELIM + Code.FALSE + Code.DELIM
                    + MCodeGenerator.locationToString(node.getElseStatement()));

            node.getElseStatement().acceptVisitor(this);
        }

        return null;
    }

    /**
     * Visits a BlockStatement
     *
     * @param node the node to visit
     */
    public Object visit(BlockStatement node) {
        try {
            // Enter a new scope and define the local variables
            MCodeUtilities.write(Code.SCOPE + Code.DELIM + "1");
            Set vars = (Set) node.getProperty(NodeProperties.VARIABLES);
            context.enterScope(vars);

            // Interpret the statements
            Iterator it = node.getStatements().iterator();
            while (it.hasNext()) {
                ((Node) it.next()).acceptVisitor(this);
            }
        } finally {
            // Always leave the current scope
            MCodeUtilities.write("" + Code.SCOPE + Code.DELIM + "0");
            context.leaveScope();
        }

        return null;
    }

    /**
     * Visits a Literal
     *
     * @param node the node to visit
     */
    public Object visit(Literal node) {

        if (node.getType() == null) {
            MCodeUtilities.write("" + Code.L + Code.DELIM + (counter++)
                    + Code.DELIM + MCodeUtilities.getValue(node.getValue())
                    + Code.DELIM + Code.REFERENCE + Code.DELIM
                    + MCodeGenerator.locationToString(node));
        } else {
            MCodeUtilities.write(Code.L + Code.DELIM + (counter++) + Code.DELIM
                    + MCodeUtilities.getValue(node.getValue()) + Code.DELIM
                    + node.getType().getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
        }

        return node.getValue();
    }

    /**
     * Visits a VariableDeclaration
     *
     * @param node the node to visit
     */
    public Object visit(VariableDeclaration node) {

        Class c = NodeProperties.getType(node.getType());
        int type; //FINAL or NOT_FINAL
        String value = Code.UNKNOWN;

        long auxcounter = Code.NO_REFERENCE;

        Object o;

        if (node.isFinal()) {
            type = Code.FINAL;
        } else {
            type = Code.NOT_FINAL;
        }

        // fixed by rku: use componenttype to resolve later
        String classname = MCodeUtilities.getFullQualifiedClassname(c);

        MCodeUtilities.write("" + Code.VD + Code.DELIM + node.getName()
                + Code.DELIM + auxcounter + Code.DELIM + value + Code.DELIM
                + classname + Code.DELIM + type + Code.DELIM
                + MCodeGenerator.locationToString(node));

        if (node.getInitializer() != null) {
            long assigncounter = counter++;
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A
                    + Code.DELIM + assigncounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            //

            auxcounter = counter;
            o = performCast(c, node.getInitializer().acceptVisitor(this));
            //value=o.toString();

            MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
            long auxcounter2 = counter;

            // fixed by rku: use classname to resolve later
            MCodeUtilities.write("" + Code.QN + Code.DELIM + (counter++)
                    + Code.DELIM + node.getName()
                    //+ node.getRepresentation()
                    + Code.DELIM + value + Code.DELIM + classname // c.getName()
                    + MCodeGenerator.locationToString(node));
            value = MCodeUtilities.getValue(o);

            MCodeGenerator.generateCodeA(assigncounter, auxcounter,
                    auxcounter2, value, classname, MCodeGenerator
                            .locationToString(node));

            /*
             MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
             + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
             + Code.DELIM + value
             //+ o.toString()
             + Code.DELIM + classname + Code.DELIM //c.getName() + Code.DELIM
             + MCodeUtilities.locationToString(node));
             */

            if (node.isFinal()) {
                context.setConstant(node.getName(), o);
            } else {
                context.set(node.getName(), o);
            }
        } else {
            if (node.isFinal()) {

                context.setConstant(node.getName(),
                        UninitializedObject.INSTANCE);
            } else {
                context.set(node.getName(), UninitializedObject.INSTANCE);
            }
        }

        return null;
    }

    /**
     * Visits an ObjectFieldAccess
     *
     * @param node the node to visit
     */
    public Object visit(ObjectFieldAccess node) {
        Class c = NodeProperties.getType(node.getExpression());

        long fieldCounter = counter;
        counter++;

        long objectCounter = counter;

        //Jeliot 3 modification
        if (c.isArray()) {
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AL
                    + Code.DELIM + fieldCounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
        }
        // Evaluate the object
        Object obj = node.getExpression().acceptVisitor(this);
        Object value;
        if (!c.isArray()) {
            Field f = (Field) node.getProperty(NodeProperties.FIELD);
            // Relax the protection for members
            if (context.getAccessible()) {
                f.setAccessible(true);
            }
            try {
                value = f.get(obj);
            } catch (NullPointerException e) {
                throw new ExecutionError("j3.null.pointer.exception", node);
                //throw new CatchedExceptionError(e, node);
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }
            String objectValue = MCodeUtilities.getValue(value);

            // fixed by rku, added Modifiers after type.
            MCodeUtilities.write("" + Code.OFA + Code.DELIM + fieldCounter
                    + Code.DELIM + objectCounter + Code.DELIM + f.getName()
                    + Code.DELIM + objectValue + Code.DELIM
                    + f.getType().getName() + Code.DELIM + f.getModifiers()
                    + Code.DELIM + MCodeGenerator.locationToString(node));
            /*
             * if (value != null) { MCodeUtilities.write("" + Code.OFA +
             * Code.DELIM + fieldCounter + Code.DELIM + objectCounter +
             * Code.DELIM + f.getName() + Code.DELIM + value.toString() +
             * Code.DELIM + f.getType().getName() + Code.DELIM +
             * MCodeUtilities.locationToString(node)); } else {
             * MCodeUtilities.write("" + Code.OFA + Code.DELIM + fieldCounter +
             * Code.DELIM + objectCounter + Code.DELIM + f.getName() +
             * Code.DELIM + Code.UNKNOWN + Code.DELIM + f.getType().getName() +
             * Code.DELIM + MCodeUtilities.locationToString(node)); }
             */
            return value;
        } else {
            // If the object is an array, the field must be 'length'.
            // This field is not a normal field and it is the only
            // way to get it

            Integer integer = null;
            try {
                integer = new Integer(Array.getLength(obj));
            } catch (NullPointerException e) {
                throw new ExecutionError("j3.null.pointer.exception", node);
            }

            MCodeUtilities.write("" + Code.AL + Code.DELIM + fieldCounter
                    + Code.DELIM + objectCounter + Code.DELIM + "length"
                    + Code.DELIM + integer.toString() + Code.DELIM
                    + int.class.getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            return integer;
        }
    }

    /**
     * Visits an ObjectMethodCall
     *
     * @param node the node to visit
     */
    public Object visit(ObjectMethodCall node) {

        //Check for System System output
        if ((node.hasProperty(NodeProperties.METHOD))
                && ((Method) node.getProperty(NodeProperties.METHOD))
                        .getDeclaringClass().getName().equals(
                                "java.io.PrintStream")) {

            Method m = (Method) node.getProperty(NodeProperties.METHOD);
            {
                if (m.getName().equals("println")
                        || m.getName().equals("print")) {

                    List larg = node.getArguments();
                    if (larg != null) {
                        Object[] args = new Object[larg.size()];
                        Class[] typs;
                        Iterator it = larg.iterator();
                        int i = 0;
                        long auxcounter; //Records the previous counter value
                        Object auxarg; //Stores the current argument

                        typs = m.getParameterTypes();

                        //It should only get once in the while loop!!!
                        while (it.hasNext()) {
                            long outputCounter = counter;
                            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM
                                    + Code.OUTPUT + Code.DELIM + outputCounter
                                    + Code.DELIM
                                    + MCodeGenerator.locationToString(node));
                            Expression exp = (Expression) it.next();

                            args[i] = MCodeUtilities
                                    .stringConversion(exp, this);

                            //args[i] = ((Expression) it.next()).acceptVisitor(this);
                            MCodeUtilities.write(""
                                    + Code.OUTPUT
                                    + Code.DELIM
                                    + outputCounter
                                    + Code.DELIM
                                    + "System.out"
                                    + Code.DELIM
                                    + m.getName()
                                    + Code.DELIM
                                    //+ args[i].toString() + Code.DELIM
                                    + MCodeUtilities.getValue(args[i])
                                    + Code.DELIM
                                    + typs[i].getName()
                                    + Code.DELIM
                                    //To indicate newline or not
                                    + (m.getName().equals("println") ? "1"
                                            : "0") + Code.DELIM
                                    + MCodeGenerator.locationToString(node));
                            i++;
                        }
                    } else {
                        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM
                                + Code.OUTPUT + Code.DELIM + Code.NO_REFERENCE
                                + Code.DELIM
                                + MCodeGenerator.locationToString(node));
                        MCodeUtilities.write("" + Code.OUTPUT + Code.DELIM
                                + Code.NO_REFERENCE + Code.DELIM + "System.out"
                                + Code.DELIM + m.getName() + Code.DELIM + ""
                                + Code.DELIM
                                + String.class.getName()
                                + Code.DELIM
                                //To indicate newline or not
                                + (m.getName().equals("println") ? "1" : "0")
                                + Code.DELIM
                                + MCodeGenerator.locationToString(node));
                    }
                }
                return null;
            }
        } else if ((node.hasProperty(NodeProperties.METHOD))
                && ((Method) node.getProperty(NodeProperties.METHOD))
                        .getDeclaringClass().getName().equals(
                                "java.util.Scanner")) {
            Method m = (Method) node.getProperty(NodeProperties.METHOD);
            List larg = node.getArguments();
            Expression exp = node.getExpression();
            long inputCounter = counter++;
            Object obj = exp.acceptVisitor(this);
            return handleScanner(node, m, larg, inputCounter);
        } else {
            Long l = new Long(counter);
            returnExpressionCounterStack.push(l);
            counter++;
            Class[] typs;
            

            Expression exp = node.getExpression();
            long objectCounter = counter;
            // Evaluate the receiver first
            Object obj = exp.acceptVisitor(this);

            if (node.hasProperty(NodeProperties.METHOD)) {
                Method m = (Method) node.getProperty(NodeProperties.METHOD);
                typs = m.getParameterTypes();

                // Relax the protection for members?
                if (context.getAccessible()) {
                    m.setAccessible(true);
                }

                List larg = node.getArguments();
                Object[] args = Constants.EMPTY_OBJECT_ARRAY;
                if (larg != null) {

                    MCodeUtilities.write("" + Code.OMC + Code.DELIM
                            + m.getName() + Code.DELIM + larg.size()
                            + Code.DELIM + objectCounter + Code.DELIM
                            + m.getDeclaringClass().getName() + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                } else {

                    MCodeUtilities.write("" + Code.OMC + Code.DELIM
                            + m.getName() + Code.DELIM + "0" + Code.DELIM
                            + objectCounter + Code.DELIM
                            + m.getDeclaringClass().getName() + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                }

                // Fill the arguments
                if (larg != null) {
                    args = new Object[larg.size()];
                    Iterator it = larg.iterator();
                    int i = 0;
                    long auxcounter; //Records the previous counter value
                    Object auxarg; //Stores the current argument
                    String argType; // Stores the type of the current argument
                    while (it.hasNext()) {

                        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM
                                + Code.P + Code.DELIM + counter + Code.DELIM
                                + MCodeGenerator.locationToString(node));
                        //arguments construction
                        auxcounter = counter;
                        Object p = ((Expression) it.next()).acceptVisitor(this);
                        args[i] = performCast(typs[i], p);
                        //HACK: If we can assure that the type of the argument
                        // is a String then specify it
                        if (args[i] instanceof String) {
                            argType = String.class.getName();
                        } else {
                            argType = typs[i].getName();
                        }
                        MCodeUtilities.write("" + Code.P + Code.DELIM
                                + auxcounter + Code.DELIM
                                + MCodeUtilities.getValue(args[i]) + Code.DELIM
                                + argType);
                        i++;

                        //args[i++] =
                        // ((Expression)it.next()).acceptVisitor(this);
                    }
                }

                // Invoke the method
                try {
                    Object o = null;

                    o = m.invoke(obj, args);

                    if (!isSetInside()) {
                        //visit(o)

                        MCodeUtilities.write(Code.PARAMETERS
                                + Code.DELIM
                                + MCodeGenerator.parameterArrayToString(m
                                        .getParameterTypes()));
                        MCodeUtilities.write(Code.MD + Code.DELIM
                                + MCodeGenerator.locationToString(node));

                        if (!m.getReturnType().getName().equals(
                                Void.TYPE.getName())) {

                            long auxcounter = counter;
                            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM
                                    + Code.R + Code.DELIM + l.toString()
                                    + Code.DELIM
                                    + MCodeGenerator.locationToString(node));

                            // Don't try this with objects, foreign method calls
                            // don't provide enough info to handle them
                            String value = MCodeUtilities.getValue(o);
                            String className = (o == null) ? Code.REFERENCE : o
                                    .getClass().getName();
                            MCodeUtilities.write(Code.L + Code.DELIM
                                    + (counter++) + Code.DELIM + value
                                    + Code.DELIM + className + Code.DELIM
                                    + MCodeGenerator.locationToString(node));

                            MCodeUtilities.write("" + Code.R + Code.DELIM
                                    + l.toString() + Code.DELIM + auxcounter
                                    + Code.DELIM + value + Code.DELIM
                                    + className + Code.DELIM
                                    + MCodeGenerator.locationToString(node));

                        }

                    } else {
                        unsetInside();
                    }

                    MCodeUtilities.write("" + Code.OMCC);
                    //the method call is closed

                    if (((Long) returnExpressionCounterStack.peek()).equals(l)) {

                        returnExpressionCounterStack.pop();
                    }
                    return o;

                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof Error) {
                        throw (Error) e.getTargetException();
                    } else if (e.getTargetException() instanceof NullPointerException
                            && !m.getReturnType().getName().equals(
                                    Void.TYPE.getName())) {
                        node.setProperty(NodeProperties.ERROR_STRINGS,
                                new String[] { m.getName(),
                                        m.getReturnType().getName() });
                        throw new ExecutionError("j3.no.return.exception", node);

                    } else if (e.getTargetException() instanceof RuntimeException) {
                        throw (RuntimeException) e.getTargetException();
                    }
                    throw new ThrownException(e.getTargetException(), node);
                } catch (Exception e) {
                    throw new CatchedExceptionError(e, node);
                }
            } else {
                // If the 'method' property is not set, the object must be
                // an array and the called method must be 'clone'.
                // Since the 'clone' method of an array is not a normal
                // method, the only way to invoke it is to simulate its
                // behaviour.
                try {
                    Class c = NodeProperties.getType(exp);
                    int len = Array.getLength(obj);
                    Object result = Array
                            .newInstance(c.getComponentType(), len);
                    for (int i = 0; i < len; i++) {
                        Array.set(result, i, Array.get(obj, i));
                    }
                    return result;
                } catch (NullPointerException e) {
                    throw new ExecutionError("j3.null.pointer.exception", node);
                }
            }
        }
    }

    private Object handleScanner(MethodCall node, Method m, List largs, long inputCounter) {
        Object o = null;
        o = handleInput(node, m, largs, inputCounter);
        return o;
    }
    

    private Object handleInput(MethodCall node, Method m, List larg, long inputCounter) {

        Object result = null;

        Object[] args = Constants.EMPTY_OBJECT_ARRAY;

        final InputHandler inputHandler = InputHandlerFactory
                .createInputHandler(m.getDeclaringClass());

        if (inputHandler != null) {
            inputHandler.setInputReader(MCodeUtilities.getReader());
            //long inputCounter = counter++;
            String prompt = (larg != null) ? "" : null;

            if (prompt != null) {
                // Fill the arguments
                args = new Object[larg.size()];
                Iterator it = larg.iterator();
                int i = 0;
                while (it.hasNext()) {
                    args[i] = ((Expression) it.next()).acceptVisitor(this);
                    prompt += MCodeUtilities.getValue(args[i]);
                    i++;
                }
            }
            // Hack to get right class for next() method in Java 1.5
            Class returnType = m.getReturnType();
            if (m.getName().equals("next") && returnType.equals(Object.class)) {
                returnType = String.class;
            }
            result = inputHandler.handleInput(returnType, inputCounter, m,
                    node, prompt);

            MCodeUtilities.write("" + Code.INPUTTED + Code.DELIM + inputCounter
                    + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                    + m.getReturnType().getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            /* Taken care of somewhere else, and not needed anymore */
            //if (prompt != null && prompt.length() > 0) {
            //    inputHandler.out(counter, "println", result.toString(), node);
            //}
        }
        return result;

    }

    /**
     * Visits a StaticFieldAccess
     *
     * @param node the node to visit
     */
    public Object visit(StaticFieldAccess node) {
        Field f = (Field) node.getProperty(NodeProperties.FIELD);
        try {
            Object o = f.get(null);
            String value = MCodeUtilities.getValue(o);
            // fixed:  added modifiers to code
            MCodeUtilities.write(Code.SFA + Code.DELIM + (counter++)
                    + Code.DELIM + f.getDeclaringClass().getName() + Code.DELIM
                    + f.getName() + Code.DELIM + value + Code.DELIM
                    + f.getType().getName() + Code.DELIM + f.getModifiers()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            return o;
        } catch (NullPointerException e) {
            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { f
                    .getName() });
            throw new ExecutionError("j3.not.static.field", node);
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a SuperFieldAccess
     *
     * @param node the node to visit
     */
    public Object visit(SuperFieldAccess node) {
        Field f = (Field) node.getProperty(NodeProperties.FIELD);
        try {
            return f.get(context.getHiddenArgument());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a SuperMethodCall
     * 
     * @param node
     *            the node to visit
     */
    public Object visit(SuperMethodCall node) {
        Method m = (Method) node.getProperty(NodeProperties.METHOD);
        List larg = node.getArguments();
        Object[] args = Constants.EMPTY_OBJECT_ARRAY;
        Class[] typs = m.getParameterTypes();

        Long l = new Long(counter);
        returnExpressionCounterStack.push(l);
        counter++;

        Object thisObject = context.getHiddenArgument();
        long objectCounter = counter;

        MCodeUtilities.write(""
                + Code.QN
                + Code.DELIM
                + (counter++)
                + Code.DELIM
                + "this"
                + Code.DELIM
                + MCodeUtilities.getValue(thisObject)
                + Code.DELIM
                + MCodeUtilities.getFullQualifiedClassname(thisObject
                        .getClass()) + Code.DELIM
                + MCodeGenerator.locationToString(node));

        if (larg != null) {

            MCodeUtilities.write(""
                    + Code.OMC
                    + Code.DELIM
                    + "super,"
                    + m.getName() //.substring(m.getName().lastIndexOf("$") + 1)
                    + Code.DELIM + larg.size() + Code.DELIM + objectCounter
                    + Code.DELIM
                    + m.getDeclaringClass().getSuperclass().getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

        } else {

            MCodeUtilities.write("" + Code.OMC + Code.DELIM + "super,"
                    + m.getName().substring(m.getName().lastIndexOf("$") + 1)
                    + Code.DELIM + "0" + Code.DELIM + objectCounter
                    + Code.DELIM
                    + m.getDeclaringClass().getSuperclass().getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));
            //0 arguments
        }
        // Fill the arguments
        if (larg != null) {
            Iterator it = larg.iterator();
            int i = 0;
            args = new Object[larg.size()];
            long auxcounter; //Records the previous counter value

            while (it.hasNext()) {
                MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.P
                        + Code.DELIM + counter + Code.DELIM
                        + MCodeGenerator.locationToString(node));
                //arguments construction
                auxcounter = counter;
                args[i] = ((Expression) it.next()).acceptVisitor(this);

                MCodeUtilities.write("" + Code.P + Code.DELIM + auxcounter
                        + Code.DELIM + MCodeUtilities.getValue(args[i])
                        + Code.DELIM + typs[i].getName());
                i++;
            }
        }

        // Invoke the method
        try {
            Object o = m.invoke(thisObject, args);

            if (!isSetInside()) {
                //visit(o)

                MCodeUtilities.write(Code.PARAMETERS
                        + Code.DELIM
                        + MCodeGenerator.parameterArrayToString(m
                                .getParameterTypes()));
                MCodeUtilities.write(Code.MD + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                if (!m.getReturnType().getName().equals(Void.TYPE.getName())) {

                    long auxcounter = counter;
                    MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.R
                            + Code.DELIM + l.toString() + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                    // Don't try this with objects, foreign method calls
                    // don't provide enough info to handle them

                    String value = MCodeUtilities.getValue(o);
                    String className = (o == null) ? Code.REFERENCE : o
                            .getClass().getName();
                    MCodeUtilities.write(Code.L + Code.DELIM + (counter++)
                            + Code.DELIM + value + Code.DELIM + className
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                    MCodeUtilities.write("" + Code.R + Code.DELIM
                            + l.toString() + Code.DELIM + auxcounter
                            + Code.DELIM + value + Code.DELIM + className
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                }

            } else {
                unsetInside();
            }

            MCodeUtilities.write("" + Code.OMCC); //the method call is closed

            if (((Long) returnExpressionCounterStack.peek()).equals(l)) {
                returnExpressionCounterStack.pop();
            }

            return o;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            } else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a StaticMethodCall
     *
     * @param node the node to visit
     */
    public Object visit(StaticMethodCall node) {

        Method m = (Method) node.getProperty(NodeProperties.METHOD);
        List larg = node.getArguments();
        Object[] args = Constants.EMPTY_OBJECT_ARRAY;

        //INPUT AND OUTPUT FIRST!!!!!!!!!!!!!

        // Check if the static method call is one of our Input methods
        // Hardcoded!!! TO BE CHANGED
        Object result = handleInput(node, m, larg, counter);
        if (result != null) {
        	counter++;
            return result;
        }

        if (m.getDeclaringClass().getName().equals("jeliot.io.Output")) {
            if (m.getName().equals("println")) {
                args = new Object[larg.size()];
                Iterator it = larg.iterator();
                int i = 0;
                long auxcounter; //Records the previous counter value
                Object auxarg; //Stores the current argument
                Class[] typs = m.getParameterTypes();

                //It should only get once in the while loop!!!
                while (it.hasNext()) {
                    long outputCounter = counter;
                    MCodeUtilities.write("" + Code.BEGIN + Code.DELIM
                            + Code.OUTPUT + Code.DELIM + outputCounter
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                    args[i] = ((Expression) it.next()).acceptVisitor(this);
                    MCodeUtilities.write("" + Code.OUTPUT + Code.DELIM
                            + outputCounter + Code.DELIM
                            + m.getDeclaringClass().getName() + Code.DELIM
                            + m.getName() + Code.DELIM
                            + MCodeUtilities.getValue(args[i]) + Code.DELIM
                            + typs[i].getName() + Code.DELIM
                            + "1" //BREAKLINE
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                    i++;
                }
                //Exit output function!!!
                return null;
            }

        }

        /* JELIOT 3 */
        if (larg != null) {

            MCodeUtilities.write("" + Code.SMC + Code.DELIM + m.getName()
                    + Code.DELIM + m.getDeclaringClass().getName() + Code.DELIM
                    + larg.size() + Code.DELIM
                    + MCodeGenerator.locationToString(node));

        } else {

            MCodeUtilities.write("" + Code.SMC + Code.DELIM + m.getName()
                    + Code.DELIM + m.getDeclaringClass().getName() + Code.DELIM
                    + "0" + Code.DELIM + MCodeGenerator.locationToString(node));
            //0 arguments
        }

        Long l = new Long(counter);
        returnExpressionCounterStack.push(l);
        counter++;

        // Fill the arguments
        if (larg != null) {

            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int i = 0;
            long auxcounter; //Records the previous counter value
            Object auxarg; //Stores the current argument
            Class[] typs = m.getParameterTypes();

            while (it.hasNext()) {

                //HACK: If we can assure that the type of the argument is a
                // String then specify it
                // Useful for external method call . Not working completely, or
                // none at all, arg retrieved as null
                String argType;
                if (typs[i].getName().equals(String.class.getName())) {
                    argType = String.class.getName();
                } else {
                    argType = typs[i].getName();
                }
                MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.P
                        + Code.DELIM + counter + Code.DELIM
                        + MCodeGenerator.locationToString(node));
                //arguments construction
                auxcounter = counter;
                args[i] = ((Expression) it.next()).acceptVisitor(this);

                MCodeUtilities.write("" + Code.P + Code.DELIM + auxcounter
                        + Code.DELIM + MCodeUtilities.getValue(args[i])
                        + Code.DELIM + argType);
                i++;
            }
        }

        // Invoke the method
        try {
            Object o = m.invoke(null, args);

            if (!isSetInside()) {
                //visit(o)

                MCodeUtilities.write(Code.PARAMETERS
                        + Code.DELIM
                        + MCodeGenerator.parameterArrayToString(m
                                .getParameterTypes()));
                MCodeUtilities.write(Code.MD + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                if (!m.getReturnType().getName().equals(Void.TYPE.getName())) {

                    long auxcounter = counter;
                    MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.R
                            + Code.DELIM + l.toString() + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                    // Don't try this with objects, foreign method calls don't
                    // provide enough info to handle them
                    //TODO: How to handle objects from foreign method calls.
                    String value = MCodeUtilities.getValue(o);
                    String className = (o == null) ? Code.REFERENCE : o
                            .getClass().getName();
                    MCodeUtilities.write("" + Code.L + Code.DELIM + (counter++)
                            + Code.DELIM + value + Code.DELIM + className
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                    MCodeUtilities.write("" + Code.R + Code.DELIM
                            + l.toString() + Code.DELIM + auxcounter
                            + Code.DELIM + value + Code.DELIM + className
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));

                }
            } else {
                unsetInside();
            }

            MCodeUtilities.write("" + Code.SMCC); //the method call is closed

            if (((Long) returnExpressionCounterStack.peek()).equals(l)) {

                returnExpressionCounterStack.pop();
            }
            return o;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            } else if (e.getTargetException() instanceof NullPointerException
                    && Modifier.isStatic(m.getModifiers())
                    && !m.getReturnType().getName().equals(Void.TYPE.getName())) {
                node.setProperty(NodeProperties.ERROR_STRINGS, new String[] {
                        m.getName(), m.getReturnType().getName() });
                throw new ExecutionError("j3.no.return.exception", node);

            } else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (NullPointerException e) {
            if (!Modifier.isStatic(m.getModifiers())) {
                node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { m
                        .getName() });
                throw new ExecutionError("j3.not.static.method", node);
            } else if (!m.getReturnType().getName()
                    .equals(Void.class.getName())) {
                node.setProperty(NodeProperties.ERROR_STRINGS, new String[] {
                        m.getName(), m.getReturnType().getName() });
                throw new ExecutionError("j3.no.return.exception", node);
            }
            throw new CatchedExceptionError(e, node);
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        } catch (Throwable e1) {
            DebugUtil.handleThrowable(e1);
            throw new CatchedExceptionError(
                    "Throwable in static method invocation", e1, node);
        }

    }

    /**
     * Visits a SimpleAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(SimpleAssignExpression node) {

        long assigncounter = counter++;
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);

        long auxcounter2 = counter;

        /*
         //setPreparing();
         Node ln = node.getLeftExpression();
         LeftHandSideModifier mod = NodeProperties.getModifier(ln);
         mod.prepare(this, context);
         //unsetPreparing();
         */

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object v = prepareLeftHandSideModifier(mod, left);

        /*
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         node.getLeftExpression().acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        long auxcounter = counter;

        Object val = node.getRightExpression().acceptVisitor(this);

        val = performCast(NodeProperties.getType(node), val);
        mod.modify(context, val);
        //String value = MCodeUtilities.getValue(val);

        MCodeGenerator.generateCodeA(assigncounter, auxcounter, auxcounter2,
                val, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
         + Code.DELIM + value +
         //val.toString()+
         Code.DELIM + NodeProperties.getType(node).getName()
         + Code.DELIM + MCodeUtilities.locationToString(node));
         */

        return val;
    }

    /**
     * Displays a QualifiedName if it is declared without worrying about
     * initialization
     *
     * @param node the node to visit
     * @return the value of the local variable represented by this node
     */
    public Object display(QualifiedName node) {

        Object result = null;

        try {
            result = context.get(node.getRepresentation());
        } catch (IllegalStateException e) {
            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { node
                    .getRepresentation() });
            throw new ExecutionError("j3.variable.not.declared", node);
        }

        Class c = NodeProperties.getType(node);
        String value = Code.UNKNOWN;

        if ((result != UninitializedObject.INSTANCE) && (result != null)) {
            value = MCodeUtilities.getValue(result);
        } else if (result == null && c.isPrimitive()) {
            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { node
                    .getRepresentation() });
            throw new ExecutionError("j3.variable.not.declared", node);
        } else if (result == null) {
            value = "null";
        }

        MCodeUtilities.write("" + Code.QN + Code.DELIM + (counter++)
                + Code.DELIM + node.getRepresentation() + Code.DELIM + value
                + Code.DELIM + MCodeUtilities.getFullQualifiedClassname(c)
                + Code.DELIM + MCodeGenerator.locationToString(node));

        return result;
    }

    /**
     * Visits a QualifiedName
     *
     * @param node the node to visit
     * @return the value of the local variable represented by this node
     */
    public Object visit(QualifiedName node) {
        if (evaluating) {
            Object result = context.get(node.getRepresentation());
            Class c = NodeProperties.getType(node);

            if (result == UninitializedObject.INSTANCE) {
                node.setProperty(NodeProperties.ERROR_STRINGS,
                        new String[] { node.getRepresentation() });
                throw new ExecutionError("uninitialized.variable", node);
            }

            MCodeUtilities.write("" + Code.QN + Code.DELIM + (counter++)
                    + Code.DELIM + node.getRepresentation() + Code.DELIM
                    + MCodeUtilities.getValue(result) + Code.DELIM
                    + MCodeUtilities.getFullQualifiedClassname(c) + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            return result;
        } else {
            return display(node);
        }
    }

    /**
     * Visits a TypeExpression
     *
     * @param node the node to visit
     */
    public Object visit(TypeExpression node) {
        return node.getProperty(NodeProperties.VALUE);
    }

    /**
     * Visits a SimpleAllocation
     * 
     * @param node
     *            the node to visit
     */
    public Object visit(SimpleAllocation node) {
        List larg = node.getArguments();
        Object[] args = Constants.EMPTY_OBJECT_ARRAY;
        Constructor cons = (Constructor) node
                .getProperty(NodeProperties.CONSTRUCTOR);
        Class[] paramTypes = cons.getParameterTypes();
        Class[] types;
        if (larg != null) {
            types = new Class[larg.size()];
        } else {
            types = new Class[0];
        }
        String consName = cons.getName();
        String declaringClass = cons.getDeclaringClass().getName();
        // Fill the arguments
        if (declaringClass.equals(String.class.getName())){
        	return handleStringConstructor(node, larg);
        }
        long simpleAllocationCounter = counter++;
        
        if (larg != null) {

            MCodeUtilities.write("" + Code.SA + Code.DELIM
                    + simpleAllocationCounter + Code.DELIM + declaringClass
                    + Code.DELIM + consName + Code.DELIM + larg.size()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

        } else {

            MCodeUtilities.write("" + Code.SA + Code.DELIM
                    + simpleAllocationCounter + Code.DELIM + declaringClass
                    + Code.DELIM + consName + Code.DELIM + "0" + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            //0 arguments
        }

        // Fill the arguments
        if (larg != null) {
            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int i = 0;
            long auxcounter; //Records the previous counter value
            Object auxarg; //Stores the current argument

            Class[] params = null;
            Constructor constructor = (Constructor) node
                    .getProperty(NodeProperties.CONSTRUCTOR);
            if (constructor != null) {
                params = constructor.getParameterTypes();
            }

            while (it.hasNext()) {

                MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.P
                        + Code.DELIM + counter + Code.DELIM
                        + MCodeGenerator.locationToString(node));
                //arguments construction
                auxcounter = counter;
                args[i] = ((Expression) it.next()).acceptVisitor(this);

                // HACK: If we can assure that the type of the argument is a
                // String then specify it. Useful for external method call.
                // Not working completely, or none at all, arg retrieved as null
                String argType;
                if (args[i] instanceof String) {
                    argType = String.class.getName();
                } else {
                    if (params != null) {
                        argType = params[i].getName();
                    } else {
                        argType = args[i].getClass().getName();
                    }
                }

                MCodeUtilities
                        .write("" + Code.P + Code.DELIM + auxcounter
                                + Code.DELIM + MCodeUtilities.getValue(args[i])
                                + Code.DELIM + argType/*args[i].getClass().getName()*/);

                if (params != null) {
                    types[i] = params[i];
                } else {
                    types[i] = args[i].getClass();
                }
                //types[i] = args[i].getClass();
                i++;
            }
        }

        newConstructorCall(consName, extractSuperClasses(cons
                .getDeclaringClass()), simpleAllocationCounter);
        MCodeUtilities.write("" + Code.CONSCN + Code.DELIM
                + EvaluationVisitor.getConstructorCallNumber());

        //If Scanner constructor we hack a simple Scanner constructor that also 
        //skips the special stacks (superClassesStack and so handling)
        //otherwise we do nothing special b
        if (consName.equals("java.util.Scanner")) {
            if (args.length > 1)
                throw new ExecutionError("j3.no.such.Scanner.implemented", node);
            else {
                MCodeUtilities.write("" + Code.CONSCN + Code.DELIM
                        + EvaluationVisitor.getConstructorCallNumber());
                MCodeUtilities.write(Code.PARAMETERS + Code.DELIM + "source");
                MCodeUtilities.write(Code.MD + Code.DELIM + "0,0,0,0");
            }
        } //else {
        //To handle "super" recursive calls
        MCodeUtilities.superClassesStack.push(new Integer(0));

        //To handle "this" method calls we store the constructor name, and its parameters list
        pushConstructorInfo(consName, types);
        MCodeUtilities.previousClassStack.push(consName);
        MCodeUtilities.previousClassParametersStack.push(types);

        //Hack to avoid problems when jeliot is started with a constructor
        returnExpressionCounterStack.push(new Long(0));
        //}
        try { // Jeliot 3
            Object result = context.invokeConstructor(node, args);
            MCodeUtilities.write("" + Code.SAC + Code.DELIM
                    + simpleAllocationCounter + Code.DELIM
                    + MCodeUtilities.getValue(result) + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            //0 arguments
            if (!consName.equals("java.util.Scanner")) {
                MCodeUtilities.popConstructorInfo();
                MCodeUtilities.previousClassStack.pop();
                MCodeUtilities.previousClassParametersStack.pop();
                //MCodeUtilities.superClassesStack.pop();
                returnExpressionCounterStack.pop();

                //This is now done for all the simple allocations but once Java API allocations are allowed this should be changed.
                unsetInside();
            } else {
                EvaluationVisitor.constructorCallFinished();
                MCodeUtilities.popConstructorInfo();
                MCodeUtilities.previousClassStack.pop();
                MCodeUtilities.previousClassParametersStack.pop();
                MCodeUtilities.superClassesStack.pop();
                returnExpressionCounterStack.pop();
            }
            return result;

            /* Jeliot 3 addition begins */
        } catch (NoSuchMethodError e) {
            int n = paramTypes.length;
            String params = "(";
            for (int i = 0; i < n; i++) {
                params += paramTypes[i].getName();
                if (i == n - 1) {
                    break;
                }
                params += ",";
            }
            params += ")";

            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] {
                    consName + params, declaringClass });
            throw new ExecutionError("j3.no.such.constructor", node);
        } /*
         * catch (NoSuchMethodException e) { Class[] paramTypes =
         * cons.getParameterTypes(); int n = paramTypes.length; String params =
         * "("; for (int i = 0; i < n; i++) { params +=
         * paramTypes[i].getName(); if (i == n-1) { break; } params += ","; }
         * params += ")";
         * 
         * node.setProperty(NodeProperties.ERROR_STRINGS, new String[] {
         * consName + params, declaringClass }); throw new
         * ExecutionError("j3.no.such.constructor", node); }
         */
        /* Jeliot 3 addition ends */
    }

    private Object handleStringConstructor(SimpleAllocation node, List larg) {
    	String result;
    	Object[] args = Constants.EMPTY_OBJECT_ARRAY;
    	
    	if (larg != null && larg.size()==1) {
    		args = new Object[larg.size()];
            Iterator it = larg.iterator();
            result = (String) ((Expression) it.next()).acceptVisitor(this);
            
        } else if (larg == null) {
        	 result = (String) context.invokeConstructor(node, args);
        	 MCodeUtilities.write(Code.L + Code.DELIM + (counter++) + Code.DELIM
                     + MCodeUtilities.getValue(result) + Code.DELIM
                     + String.class.getName() + Code.DELIM
                     + MCodeGenerator.locationToString(node));
        } else {
        	//We don't suppport anything else
        	node.setProperty(NodeProperties.ERROR_STRINGS, new String[] {
        	          "String" , "String" }); 
        	throw new ExecutionError("j3.no.such.constructor", node);
        }
    	//Object result = context.invokeConstructor(node, args);
		return result;
	}

	/**
     * Visits an ArrayAllocation
     *
     * @param node the node to visit
     */
    public Object visit(ArrayAllocation node) {

        // Visits the initializer if one
        if (node.getInitialization() != null) {
            return node.getInitialization().acceptVisitor(this);
        }

        long arrayAllocationCounter = counter++;

        // Evaluate the size
        int[] dims = new int[node.getSizes().size()];
        // It will store here the references to the expressions used for every
        // dimension
        Long[] dimExpressionReferences = new Long[node.getSizes().size()];
        Iterator it = node.getSizes().iterator();
        int i = 0;
        String dimensions = "";

        while (it.hasNext()) {
            dimExpressionReferences[i] = new Long(counter);
            Number n = (Number) ((Expression) it.next()).acceptVisitor(this);
            dims[i] = n.intValue();
            dimensions += "" + dims[i] + ",";
            i++;
        }

        dimensions = dimensions.substring(0, dimensions.length() - 1);

        // Create the array
        Object newArray; //Array to be returned
        if (node.getDimension() != dims.length) {
            Class c = NodeProperties.getComponentType(node);
            c = Array.newInstance(c, 0).getClass();
            //Allow a second degree of freedom (new int[4][][]) (Jeliot 3 only supports 3D arrays
            c = (node.getDimension() == (dims.length + 2)) ? Array.newInstance(
                    c, 0).getClass() : c;
            newArray = Array.newInstance(c, dims);
        } else {
            newArray = Array.newInstance(NodeProperties.getComponentType(node),
                    dims);
        }
        //TODO: get all the hashcodes of the different dimensions from the array

        String subArrayHashCodes = MCodeUtilities
                .getSubArrayHashCodes(newArray);

        //System.out.println("Array allocator");

        MCodeUtilities.write("" + Code.AA + Code.DELIM + arrayAllocationCounter
                + Code.DELIM + MCodeUtilities.getValue(newArray) /*Integer.toHexString(newArray.hashCode())*/
                + Code.DELIM + NodeProperties.getComponentType(node).getName()
                + Code.DELIM + dims.length + Code.DELIM
                + MCodeGenerator.arrayToString(dimExpressionReferences)
                + Code.DELIM + dimensions + Code.DELIM + node.getDimension()
                + Code.DELIM + subArrayHashCodes + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return newArray;
    }

    /**
     * Visits a ArrayInitializer
     *
     * @param node the node to visit
     */
    public Object visit(ArrayInitializer node) {
        Object result = Array.newInstance(NodeProperties.getType(node
                .getElementType()), node.getCells().size());

        int size = node.getCells().size();
        long arrayAllocationCounter = counter++;
        //Fake literal with the array size
        long sizeCounter = counter++;
        MCodeUtilities.write("" + Code.L + Code.DELIM + sizeCounter
                + Code.DELIM + size + Code.DELIM + int.class.getName()
                + Code.DELIM + MCodeGenerator.locationToString(node));
        Long[] dimExpressionReferences = { new Long(sizeCounter) };
        String arrayHashCode = Integer.toHexString(System
                .identityHashCode(result));
        String dimensionsReferences = MCodeGenerator
                .arrayToString(dimExpressionReferences);
        String componentType = ((Class) node.getElementType().getProperty(
                "type")).getName();
        int dimensions = 1;
        int index = componentType.indexOf('[');
        while (index >= 0) {
            dimensions++;
            index = componentType.indexOf('[', index + 1);
        }

        //System.out.println("Array initializer");

        MCodeUtilities.write("" + Code.AA + Code.DELIM + arrayAllocationCounter
                + Code.DELIM + arrayHashCode + Code.DELIM + componentType
                + Code.DELIM + dimensions + Code.DELIM + dimensionsReferences
                + Code.DELIM + size + Code.DELIM + dimensions + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.AIBEGIN + Code.DELIM + size + Code.DELIM
                + MCodeGenerator.locationToString(node));

        Iterator it = node.getCells().iterator();
        int i = 0;
        while (it.hasNext()) {
            long elementCounter = counter;
            Expression expression = (Expression) it.next();

            Object o = expression.acceptVisitor(this);
            int isLiteral = (Literal.class.isInstance(expression)) ? 1 : 0;
            String value = MCodeUtilities.getValue(o);
            MCodeUtilities.write("" + Code.AIE + Code.DELIM + arrayHashCode
                    + Code.DELIM + i + Code.DELIM + elementCounter + Code.DELIM
                    + value + Code.DELIM + o.getClass().getName() + Code.DELIM
                    + isLiteral + Code.DELIM
                    + MCodeGenerator.locationToString(expression));
            Array.set(result, i++, o);
        }
        MCodeUtilities.write("" + Code.AI + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return result;
    }

    /**
     * Visits an ArrayAccess
     *
     * @param node
     * the node to visit
     */
    private boolean first = true;

    public Object visit(ArrayAccess node) {
        List arrayCellNumbersList;
        List arrayCellReferencesList;

        long arrayAccessCounter = counter;

        boolean iAmFirst = first;
        first = false;
        long nameCounter = 0; // Not used if not first
        if (iAmFirst) {
            counter++;
            arrayCellNumbersList = new ArrayList();
            arrayCellReferencesList = new ArrayList();
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AAC
                    + Code.DELIM + arrayAccessCounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            nameCounter = counter;
            arrayCellNumbersStack.push(arrayCellNumbersList);
            arrayCellReferencesStack.push(arrayCellReferencesList);
        } else {
            arrayCellNumbersList = (List) arrayCellNumbersStack.peek();
            arrayCellReferencesList = (List) arrayCellReferencesStack.peek();
        }

        //This is for array reference when the qualified name is visited
        long arrayCounter = counter;
        Object t = node.getExpression().acceptVisitor(this);

        long cellCounter = counter;
        // This way we allow array accesses inside cell numbers
        first = true;
        //evaluating = false;
        Object o = node.getCellNumber().acceptVisitor(this);
        //evaluating = true;
        first = false;

        if (o instanceof Character) {
            o = new Integer(((Character) o).charValue());
        }

        arrayCellNumbersList.add(o);
        arrayCellReferencesList.add(new Long(cellCounter));

        /*
         if (!iAmFirst) {
         arrayCellNumbersStack.push(arrayCellNumbersList);
         arrayCellReferencesStack.push(arrayCellReferencesList);
         }
         */

        Object result = null;
        try {
            result = Array.get(t, ((Number) o).intValue());
        } catch (ArrayIndexOutOfBoundsException e) {
            node.setProperty(NodeProperties.ERROR_STRINGS,
                    new String[] { "" + (Array.getLength(t) - 1),
                            "" + ((Number) o).intValue() });
            throw new ExecutionError("j3.array.index.out.of.bounds", node);
        } catch (NullPointerException e) {
            throw new ExecutionError("j3.null.pointer.exception", node);
        }

        String resultString;
        if (result != null) {
            resultString = MCodeUtilities.getValue(result);
        } else {
            resultString = Code.UNKNOWN;
        }

        if (iAmFirst) {
            MCodeUtilities.write(""
                    + Code.AAC
                    + Code.DELIM
                    + arrayAccessCounter
                    + Code.DELIM
                    + arrayCounter
                    + Code.DELIM
                    + arrayCellNumbersList.size()
                    + Code.DELIM
                    + MCodeGenerator.arrayToString(arrayCellReferencesList
                            .toArray())
                    + Code.DELIM
                    + MCodeGenerator.arrayToString(arrayCellNumbersList
                            .toArray()) + Code.DELIM + resultString
                    + Code.DELIM + NodeProperties.getType(node).getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            /*
             * ECodeUtilities.write("Array access of name "+ nameCounter +" with
             * hashcode "+ Integer.toHexString(t.hashCode()) + "element "
             * +arrayCellNumbersList.toString() +" references "+
             * arrayCellReferencesList.toString());
             */
            first = true;
            /* arrayCellNumbersList = (List) */
            arrayCellNumbersStack.pop();
            /* arrayCellReferencesList =(List) */
            arrayCellReferencesStack.pop();

        }

        return result;
    }

    /**
     * Visits a InnerAllocation
     *
     * @param node the node to visit
     */
    public Object visit(InnerAllocation node) {
        Constructor cons = (Constructor) node
                .getProperty(NodeProperties.CONSTRUCTOR);
        Class c = NodeProperties.getType(node);

        List larg = node.getArguments();
        Object[] args = null;

        if (larg != null) {
            args = new Object[larg.size() + 1];
            args[0] = node.getExpression().acceptVisitor(this);

            Iterator it = larg.iterator();
            int i = 1;
            while (it.hasNext()) {
                args[i++] = ((Expression) it.next()).acceptVisitor(this);
            }
        } else {
            args = new Object[] { node.getExpression().acceptVisitor(this) };
        }

        // Invoke the constructor
        try {
            return cons.newInstance(args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error) e.getTargetException();
            } else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a ClassAllocation
     *
     * @param node the node to visit
     */

    public Object visit(ClassAllocation node) {
        List larg = node.getArguments();
        Object[] args = Constants.EMPTY_OBJECT_ARRAY;

        // Fill the arguments
        if (larg != null) {
            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int i = 0;
            while (it.hasNext()) {
                args[i++] = ((Expression) it.next()).acceptVisitor(this);
            }
        }

        return context.invokeConstructor(node, args);
    }

    /**
     * Visits a NotExpression
     *
     * @param node the node to visit
     */
    public Object visit(NotExpression node) {

        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            MCodeUtilities.write("" + Code.NO + Code.DELIM + (counter++)
                    + Code.DELIM + Code.NO_REFERENCE + Code.DELIM
                    + node.getProperty(NodeProperties.VALUE) + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            return node.getProperty(NodeProperties.VALUE);
        } else {
            long notcounter = counter++;
            long auxcounter = counter;
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.NO
                    + Code.DELIM + notcounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

            Boolean b = (Boolean) node.getExpression().acceptVisitor(this);

            MCodeUtilities.write("" + Code.NO + Code.DELIM + notcounter
                    + Code.DELIM + auxcounter + Code.DELIM + !b.booleanValue()
                    + Code.DELIM + NodeProperties.getType(node).getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            if (b.booleanValue()) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
    }

    /**
     * Visits a ComplementExpression
     *
     * @param node the node to visit
     */
    public Object visit(ComplementExpression node) {

        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            MCodeUtilities.write("" + Code.NO + Code.DELIM + (counter++)
                    + Code.DELIM + Code.NO_REFERENCE + Code.DELIM
                    + node.getProperty(NodeProperties.VALUE) + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            return node.getProperty(NodeProperties.VALUE);
        } else {
            long compcounter = counter++;
            long auxcounter = counter;
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.COMP
                    + Code.DELIM + compcounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

            Class c = NodeProperties.getType(node);
            Object o = node.getExpression().acceptVisitor(this);

            if (o instanceof Character) {
                o = new Integer(((Character) o).charValue());
            }

            if (c == int.class) {
                MCodeUtilities.write("" + Code.COMP + Code.DELIM + compcounter
                        + Code.DELIM + auxcounter + Code.DELIM
                        + (~((Number) o).intValue()) + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));
                return new Integer(~((Number) o).intValue());
            } else {
                MCodeUtilities.write("" + Code.COMP + Code.DELIM + compcounter
                        + Code.DELIM + auxcounter + Code.DELIM
                        + (~((Number) o).longValue()) + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));
                return new Long(~((Number) o).longValue());
            }

        }
    }

    /**
     * Visits a PlusExpression
     *
     * @param node the node to visit
     */
    public Object visit(PlusExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            MCodeUtilities.write("" + Code.L + Code.DELIM + (counter++)
                    + Code.DELIM + node.getProperty(NodeProperties.VALUE)
                    + Code.DELIM + NodeProperties.getType(node).getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            return node.getProperty(NodeProperties.VALUE);
        } else {
            long pluscounter = counter++;
            long auxcounter = counter;
            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.PLUS
                    + Code.DELIM + pluscounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

            Class c = NodeProperties.getType(node);
            Object robj = node.getExpression().acceptVisitor(this);
            Object o = InterpreterUtilities.plus(c, robj);
            MCodeUtilities.write("" + Code.PLUS + Code.DELIM + pluscounter
                    + Code.DELIM + auxcounter + Code.DELIM
                    + MCodeUtilities.getValue(o) + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            return o;
        }
    }

    /**
     * Visits a MinusExpression
     *
     * @param node the node to visit
     */
    public Object visit(MinusExpression node) {

        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant

            MCodeUtilities.write("" + Code.L + Code.DELIM + (counter++)
                    + Code.DELIM + node.getProperty(NodeProperties.VALUE)
                    + Code.DELIM + NodeProperties.getType(node).getName()
                    + Code.DELIM + MCodeGenerator.locationToString(node));

            return node.getProperty(NodeProperties.VALUE);

        } else {

            long minuscounter = counter++;
            long auxcounter = counter;

            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.MINUS
                    + Code.DELIM + minuscounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

            Class c = NodeProperties.getType(node);
            Object robj = node.getExpression().acceptVisitor(this);
            Object o = InterpreterUtilities.minus(c, robj);

            MCodeUtilities.write("" + Code.MINUS + Code.DELIM + minuscounter
                    + Code.DELIM + auxcounter + Code.DELIM
                    + MCodeUtilities.getValue(o) + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            return o;
        }
        /*
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.minus (NodeProperties.getType(node),
         * node.getExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits a AddExpression
     *
     * @param node the node to visit
     */
    public Object visit(AddExpression node) {

        Class c = NodeProperties.getType(node);
        if (c.getName().equals(String.class.getName())) {
            return concatenate(node);
        } else {
            long addcounter = counter++;
            long auxcounter = counter;

            MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AE
                    + Code.DELIM + addcounter + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

            Object lobj = node.getLeftExpression().acceptVisitor(this);

            long auxcounter2 = counter;

            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

            Object robj = node.getRightExpression().acceptVisitor(this);

            Object o = InterpreterUtilities.add(c, lobj, robj);

            MCodeUtilities.write("" + Code.AE + Code.DELIM + addcounter
                    + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                    + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
            return o;
        }
    }

    public Object concatenate(AddExpression node) {

        Class c = NodeProperties.getType(node);
        long addcounter = counter++;
        Object lobj, robj;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AE
                + Code.DELIM + addcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        String str1 = "";
        Node exp = node.getLeftExpression();
        long auxcounter = counter;
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + auxcounter);

        str1 = MCodeUtilities.stringConversion(exp, this);

        String str2 = "";
        exp = node.getRightExpression();
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + auxcounter2);

        str2 = MCodeUtilities.stringConversion(node.getRightExpression(), this);

        Object o = InterpreterUtilities.add(c, str1, str2);
        MCodeUtilities.write("" + Code.AE + Code.DELIM + addcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //System.out.println(o.toString());
        return o;
    }

    /**
     * Visits an AddAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(AddAssignExpression node) {
        //Simulate that a+=3-b -->> a= a+ (3-b)

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //Start ad expression
        long addcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AE
                + Code.DELIM + addcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the add expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        long auxcounter2 = counter;
        // Get right hand side for the add expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.add(NodeProperties.getType(node),
                lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.AE + Code.DELIM + addcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //long assignauxcounter2 = counter;
        //MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);

        //boolean evaluatingPreviously = evaluating;
        //evaluating = false;
        //left.acceptVisitor(this);
        //evaluating = evaluatingPreviously;

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;
    }

    /**
     * Visits a SubtractExpression
     *
     * @param node the node to visit
     */
    public Object visit(SubtractExpression node) {

        Class c = NodeProperties.getType(node);
        long substractcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.SE
                + Code.DELIM + substractcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.subtract(c, lobj, robj);

        MCodeUtilities.write("" + Code.SE + Code.DELIM + substractcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;

    }

    /**
     * Visits an SubtractAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(SubtractAssignExpression node) {
        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //Start ad expression
        long subcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.SE
                + Code.DELIM + subcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the subtract expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;

        // Get right hand side for the subtract expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.subtract(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.SE + Code.DELIM + subcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //long assignauxcounter2 = counter;
        //MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
        //evaluatingPreviously = evaluating;
        //evaluating = false;
        //left.acceptVisitor(this);
        //evaluating = evaluatingPreviously;

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;

        /*
         * Node left = node.getLeftExpression(); LeftHandSideModifier mod =
         * NodeProperties.getModifier(left); Object lhs = mod.prepare(this,
         * context);
         *  // Perform the operation Object result =
         * InterpreterUtilities.subtract( NodeProperties.getType(node), lhs,
         * node.getRightExpression().acceptVisitor(this));
         *  // Cast the result result =
         * performCast(NodeProperties.getType(left), result);
         *  // Modify the variable and return mod.modify(context, result);
         *
         * return result;
         */
    }

    /**
     * Visits a MultiplyExpression
     *
     * @param node the node to visit
     */
    public Object visit(MultiplyExpression node) {

        Class c = NodeProperties.getType(node);
        long multiplycounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.ME
                + Code.DELIM + multiplycounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.multiply(c, lobj, robj);

        MCodeUtilities.write("" + Code.ME + Code.DELIM + multiplycounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;

        /*
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.multiply( NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits an MultiplyAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(MultiplyAssignExpression node) {
        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start ad expression
        long multcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.ME
                + Code.DELIM + multcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the add expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;

        // Get right hand side for the add expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.multiply(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.ME + Code.DELIM + multcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //long assignauxcounter2 = counter;
        //MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
        //evaluatingPreviously = evaluating;
        //evaluating = false;
        //left.acceptVisitor(this);
        //evaluating = evaluatingPreviously;

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;

        /*
         * Node left = node.getLeftExpression(); LeftHandSideModifier mod =
         * NodeProperties.getModifier(left); Object lhs = mod.prepare(this,
         * context);
         *  // Perform the operation Object result =
         * InterpreterUtilities.multiply( NodeProperties.getType(node), lhs,
         * node.getRightExpression().acceptVisitor(this));
         *  // Cast the result result =
         * performCast(NodeProperties.getType(left), result);
         *  // Modify the variable and return mod.modify(context, result);
         *
         * return result;
         */
    }

    /**
     * Visits a DivideExpression
     *
     * @param node the node to visit
     */
    public Object visit(DivideExpression node) {
        Class c = NodeProperties.getType(node);
        long dividecounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.DE
                + Code.DELIM + dividecounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.divide(c, lobj, robj);

        MCodeUtilities.write("" + Code.DE + Code.DELIM + dividecounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;

        /*
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.divide( NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits an DivideAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(DivideAssignExpression node) {
        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start divide expression
        long divcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.DE
                + Code.DELIM + divcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the divide expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;

        // Get right hand side for the divide expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.divide(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.DE + Code.DELIM + divcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //long assignauxcounter2 = counter;
        //MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
        //evaluatingPreviously = evaluating;
        //evaluating = false;
        //left.acceptVisitor(this);
        //evaluating = evaluatingPreviously;

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;

        /*
         * Node left = node.getLeftExpression(); LeftHandSideModifier mod =
         * NodeProperties.getModifier(left); Object lhs = mod.prepare(this,
         * context);
         *  // Perform the operation Object result =
         * InterpreterUtilities.divide( NodeProperties.getType(node), lhs,
         * node.getRightExpression().acceptVisitor(this));
         *  // Cast the result result =
         * performCast(NodeProperties.getType(left), result);
         *  // Modify the variable and return mod.modify(context, result);
         *
         * return result;
         */
    }

    /**
     * Visits a RemainderExpression
     *
     * @param node the node to visit
     */
    public Object visit(RemainderExpression node) {

        Class c = NodeProperties.getType(node);
        long remaindercounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.RE
                + Code.DELIM + remaindercounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.remainder(c, lobj, robj);

        MCodeUtilities.write("" + Code.RE + Code.DELIM + remaindercounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;

        /*
         * if (node.hasProperty(NodeProperties.VALUE)) {
         *  // The expression is constant return
         * node.getProperty(NodeProperties.VALUE); } else { return
         * InterpreterUtilities.remainder( NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits an RemainderAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(RemainderAssignExpression node) {
        //Simulate that a%=b -->> a= a%b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start remainder expression
        long addcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.RE
                + Code.DELIM + addcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the remainder expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */
        long auxcounter2 = counter;

        // Get right hand side for the remainder expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.remainder(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.RE + Code.DELIM + addcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        //long assignauxcounter2 = counter;
        //MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
        //evaluatingPreviously = evaluating;
        //evaluating = false;
        //left.acceptVisitor(this);
        //evaluating = evaluatingPreviously;

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;

    }

    /**
     * Visits an EqualExpression
     *
     * @param node the node to visit
     */
    public Object visit(EqualExpression node) {

        long eecounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.EE
                + Code.DELIM + eecounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node leftexp = node.getLeftExpression();
        Object lobj = leftexp.acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Node rightexp = node.getRightExpression();
        Object robj = rightexp.acceptVisitor(this);
        Object o = InterpreterUtilities.equalTo(
                NodeProperties.getType(leftexp), NodeProperties
                        .getType(rightexp), lobj, robj);

        MCodeUtilities.write("" + Code.EE + Code.DELIM + eecounter + Code.DELIM
                + auxcounter + Code.DELIM + auxcounter2 + Code.DELIM
                + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a NotEqualExpression
     *
     * @param node the node to visit
     */
    public Object visit(NotEqualExpression node) {

        long necounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.NE
                + Code.DELIM + necounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node leftexp = node.getLeftExpression();
        Object lobj = leftexp.acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Node rightexp = node.getRightExpression();
        Object robj = rightexp.acceptVisitor(this);
        Object o = InterpreterUtilities
                .notEqualTo(NodeProperties.getType(leftexp), NodeProperties
                        .getType(rightexp), lobj, robj);

        MCodeUtilities.write("" + Code.NE + Code.DELIM + necounter + Code.DELIM
                + auxcounter + Code.DELIM + auxcounter2 + Code.DELIM
                + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a LessExpression
     *
     * @param node the node to visit
     */
    public Object visit(LessExpression node) {

        long lecounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.LE
                + Code.DELIM + lecounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.lessThan(lobj, robj);

        MCodeUtilities.write("" + Code.LE + Code.DELIM + lecounter + Code.DELIM
                + auxcounter + Code.DELIM + auxcounter2 + Code.DELIM
                + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a LessOrEqualExpression
     *
     * @param node the node to visit
     */
    public Object visit(LessOrEqualExpression node) {

        long lqecounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.LQE
                + Code.DELIM + lqecounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.lessOrEqual(lobj, robj);

        MCodeUtilities.write("" + Code.LQE + Code.DELIM + lqecounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a GreaterExpression
     *
     * @param node the node to visit
     */

    public Object visit(GreaterExpression node) {

        long gtcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.GT
                + Code.DELIM + gtcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.greaterThan(lobj, robj);

        MCodeUtilities.write("" + Code.GT + Code.DELIM + gtcounter + Code.DELIM
                + auxcounter + Code.DELIM + auxcounter2 + Code.DELIM
                + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a GreaterOrEqualExpression
     *
     * @param node the node to visit
     */
    public Object visit(GreaterOrEqualExpression node) {
        long gqtcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.GQT
                + Code.DELIM + gqtcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.greaterOrEqual(lobj, robj);

        MCodeUtilities.write("" + Code.GQT + Code.DELIM + gqtcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return o;
    }

    /**
     * Visits a InstanceOfExpression
     *
     * @param node the node to visit
     */
    public Object visit(InstanceOfExpression node) {
        Object v = node.getExpression().acceptVisitor(this);
        Class c = NodeProperties.getType(node.getReferenceType());

        return (c.isInstance(v)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Visits a ConditionalExpression
     *
     * @param node the node to visit
     */
    public Object visit(ConditionalExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            long condcounter = counter;
            Boolean b = (Boolean) node.getConditionExpression().acceptVisitor(
                    this);
            //TODO: There should be a better way to do this. But Assigment is
            // waiting for the initial counter, that condition modifies here, and
            // thus we need to reset it after evaluating the condition
            counter = condcounter;
            if (b.booleanValue()) {
                MCodeUtilities.write(""
                        + Code.IFTE
                        + Code.DELIM
                        + condcounter
                        + Code.DELIM
                        + Code.TRUE
                        + Code.DELIM
                        + MCodeGenerator.locationToString(node
                                .getIfTrueExpression()));

                return node.getIfTrueExpression().acceptVisitor(this);
            } else {
                MCodeUtilities.write(""
                        + Code.IFTE
                        + Code.DELIM
                        + condcounter
                        + Code.DELIM
                        + Code.FALSE
                        + Code.DELIM
                        + MCodeGenerator.locationToString(node
                                .getIfFalseExpression()));

                return node.getIfFalseExpression().acceptVisitor(this);
            }
        }
    }

    /**
     * Visits a PostIncrement
     *
     * @param node the node to visit
     */
    public Object visit(PostIncrement node) {
        long postinccounter = counter++;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.PIE
                + Code.DELIM + postinccounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        long auxcounter = counter;

        Node exp = node.getExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = prepareLeftHandSideModifier(mod, exp);

        /*
         Node exp = node.getExpression();
         LeftHandSideModifier mod = NodeProperties.getModifier(exp);
         Object v = mod.prepare(this, context);

         //This is a hack to prevent increments to be evaluated twice
         if (!evaluating) {
         mod.modify(context, v = InterpreterUtilities.subtract(
         NodeProperties.getType(node), v, InterpreterUtilities.ONE));
         }
         */

        /*
         boolean evaluatingPreviously = evaluating;
         if (mod instanceof ArrayModifier) {
         evaluating = false;
         }
         exp.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        //LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        //Object v = mod.prepare(this, context);
        Object result = InterpreterUtilities.add(NodeProperties.getType(node),
                v, InterpreterUtilities.ONE);

        mod.modify(context, result);

        MCodeUtilities.write("" + Code.PIE + Code.DELIM + postinccounter
                + Code.DELIM + auxcounter + Code.DELIM
                + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return v;
    }

    /**
     * Visits a PreIncrement
     *
     * @param node the node to visit
     */
    public Object visit(PreIncrement node) {
        long preinccounter = counter++;
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.PRIE
                + Code.DELIM + preinccounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        long auxcounter = counter;

        Node exp = node.getExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = prepareLeftHandSideModifier(mod, exp);

        /*
         Node exp = node.getExpression();

         LeftHandSideModifier mod = NodeProperties.getModifier(exp);
         Object v = mod.prepare(this, context);

         //This is a hack to prevent increments to be evaluated twice
         if (!evaluating) {
         mod.modify(context, v = InterpreterUtilities.subtract(
         NodeProperties.getType(node), v, InterpreterUtilities.ONE));
         }
         */

        /*
         boolean evaluatingPreviously = evaluating;
         if (mod instanceof ArrayModifier) {
         evaluating = false;
         }
         exp.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        mod.modify(context, v = InterpreterUtilities.add(NodeProperties
                .getType(node), v, InterpreterUtilities.ONE));

        MCodeUtilities.write("" + Code.PRIE + Code.DELIM + preinccounter
                + Code.DELIM + auxcounter + Code.DELIM
                + MCodeUtilities.getValue(v) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return v;
    }

    /**
     * Visits a PostDecrement
     *
     * @param node the node to visit
     */
    public Object visit(PostDecrement node) {
        long postdeccounter = counter++;
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.PDE
                + Code.DELIM + postdeccounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        long auxcounter = counter;

        Node exp = node.getExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = prepareLeftHandSideModifier(mod, exp);

        /*
         Node exp = node.getExpression();
         LeftHandSideModifier mod = NodeProperties.getModifier(exp);
         Object v = mod.prepare(this, context);
         
         //This is a hack to prevent decrements to be evaluated twice
         if (!evaluating) {
         mod.modify(context, v = InterpreterUtilities.add(NodeProperties
         .getType(node), v, InterpreterUtilities.ONE));
         }

         long auxcounter = counter;
         boolean evaluatingPreviously = evaluating;
         if (mod instanceof ArrayModifier) {
         evaluating = false;
         }
         exp.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        Object result = InterpreterUtilities.subtract(NodeProperties
                .getType(node), v, InterpreterUtilities.ONE);

        mod.modify(context, result);
        MCodeUtilities.write("" + Code.PDE + Code.DELIM + postdeccounter
                + Code.DELIM + auxcounter + Code.DELIM
                + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        return v;
    }

    /**
     * Visits a PreDecrement
     *
     * @param node the node to visit
     */
    public Object visit(PreDecrement node) {
        long predeccounter = counter++;
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.PRDE
                + Code.DELIM + predeccounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        long auxcounter = counter;

        Node exp = node.getExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = prepareLeftHandSideModifier(mod, exp);

        /*
         Node exp = node.getExpression();
         LeftHandSideModifier mod = NodeProperties.getModifier(exp);
         Object v = mod.prepare(this, context);

         //This is a hack to prevent decrements to be evaluated twice
         if (!evaluating) {
         mod.modify(context, v = InterpreterUtilities.add(NodeProperties
         .getType(node), v, InterpreterUtilities.ONE));
         }

         long auxcounter = counter;
         boolean evaluatingPreviously = evaluating;
         if (mod instanceof ArrayModifier) {
         evaluating = false;
         }
         exp.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        mod.modify(context, v = InterpreterUtilities.subtract(NodeProperties
                .getType(node), v, InterpreterUtilities.ONE));

        MCodeUtilities.write("" + Code.PRDE + Code.DELIM + predeccounter
                + Code.DELIM + auxcounter + Code.DELIM
                + MCodeUtilities.getValue(v) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return v;
    }

    /**
     * Visits a CastExpression
     *
     * @param node the node to visit
     */
    public Object visit(CastExpression node) {
        long auxCounter = 0;
        long expCounter = 0;
        if (MCodeUtilities.isPrimitive(NodeProperties.getType(node).getName())) {
            expCounter = counter++;
            auxCounter = counter;
        }
        Object o = performCast(NodeProperties.getType(node), node
                .getExpression().acceptVisitor(this));
        if (MCodeUtilities.isPrimitive(o.getClass().getName())) {
            MCodeUtilities.write("" + Code.CAST + Code.DELIM + expCounter
                    + Code.DELIM + auxCounter + Code.DELIM
                    + MCodeUtilities.getValue(o) + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));
        }
        return o;
    }

    /**
     * Visits a BitAndExpression
     *
     * @param node the node to visit
     */
    public Object visit(BitAndExpression node) {
        Class c = NodeProperties.getType(node);
        long bitAndcounter = counter++;
        long auxcounter = counter;
        int expression;
        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.AND;
        } else
            expression = Code.BITAND;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + bitAndcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.bitAnd(c, lobj, robj);

        MCodeUtilities.write("" + expression + Code.DELIM + bitAndcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
        /*
         *
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.bitAnd( NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits a BitAndAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(BitAndAssignExpression node) {
        //Simulate that a&=b -->> a= a&b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start bit and expression
        long bitAndcounter = counter++;
        long auxcounter = counter;
        int expression;
        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.AND;
        } else
            expression = Code.BITAND;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + bitAndcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the bitand expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;
        // Get right hand side for the bitand expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.bitAnd(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + expression + Code.DELIM + bitAndcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */

        return result;
    }

    /**
     * Visits a ExclusiveOrExpression
     *
     * @param node the node to visit
     */
    public Object visit(ExclusiveOrExpression node) {
        Class c = NodeProperties.getType(node);
        long xOrcounter = counter++;
        long auxcounter = counter;

        int expression;
        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.XOR;
        } else
            expression = Code.BITXOR;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + xOrcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.xOr(c, lobj, robj);

        MCodeUtilities.write("" + expression + Code.DELIM + xOrcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
    }

    /**
     * Visits a ExclusiveOrAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(ExclusiveOrAssignExpression node) {
        //Simulate that a^=b -->> a= a^b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start bit and expression
        long xOrcounter = counter++;
        long auxcounter = counter;
        int expression;
        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.XOR;
        } else
            expression = Code.BITXOR;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + xOrcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the bitand expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;
        // Get right hand side for the XOR expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.xOr(NodeProperties.getType(node),
                lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + expression + Code.DELIM + xOrcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);

        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM + auxcounter //assignauxcounter2
         + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */
        return result;
    }

    /**
     * Visits a BitOrExpression
     *
     * @param node the node to visit
     */
    public Object visit(BitOrExpression node) {
        Class c = NodeProperties.getType(node);
        long bitOrcounter = counter++;
        long auxcounter = counter;
        int expression;

        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.OR;
        } else {
            expression = Code.BITOR;
        }
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + bitOrcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.bitOr(c, lobj, robj);

        MCodeUtilities.write("" + expression + Code.DELIM + bitOrcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
    }

    /**
     * Visits a BitOrAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(BitOrAssignExpression node) {
        //Simulate that a|=b -->> a= a|b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start bitor expression
        long bitOrcounter = counter++;
        long auxcounter = counter;
        int expression;

        if (NodeProperties.getType(node).getName().equals(
                boolean.class.getName())
                || NodeProperties.getType(node).getName().equals(
                        Boolean.class.getName())) {
            expression = Code.OR;

        } else {
            expression = Code.BITOR;
        }

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + expression
                + Code.DELIM + bitOrcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the bitor expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;
        // Get right hand side for the bitor expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.bitOr(
                NodeProperties.getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + expression + Code.DELIM + bitOrcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM
         + auxcounter //assignauxcounter2
         + Code.DELIM
         + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */
        return result;
    }

    /**
     * Visits a ShiftLeftExpression
     *
     * @param node the node to visit
     */
    public Object visit(ShiftLeftExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.LSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.shiftLeft(c, lobj, robj);

        MCodeUtilities.write("" + Code.LSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
    }

    /*
     * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
     * constant return node.getProperty(NodeProperties.VALUE); } else { return
     * InterpreterUtilities.shiftLeft( NodeProperties.getType(node),
     * node.getLeftExpression().acceptVisitor(this),
     * node.getRightExpression().acceptVisitor(this)); } }
     */

    /**
     * Visits a ShiftLeftAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(ShiftLeftAssignExpression node) {
        //Simulate that a<<=b -->> a= a<<b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start shift expression
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.LSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the shift expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;
        // Get right hand side for the shift expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.shiftLeft(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.LSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);
        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM
         + auxcounter //assignauxcounter2
         + Code.DELIM
         + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */
        return result;
    }

    /**
     * Visits a ShiftRightExpression
     *
     * @param node the node to visit
     */
    public Object visit(ShiftRightExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.RSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.shiftRight(c, lobj, robj);

        MCodeUtilities.write("" + Code.RSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
        /*
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.shiftRight( NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits a ShiftRightAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(ShiftRightAssignExpression node) {
        //Simulate that a>>=b -->> a= a>>b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start shift expression
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.RSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the shift expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;

        // Get right hand side for the shift expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.shiftRight(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.RSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM
         + auxcounter //assignauxcounter2
         + Code.DELIM
         + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */
        return result;
    }

    /**
     * Visits a UnsignedShiftRightExpression
     *
     * @param node the node to visit
     */
    public Object visit(UnsignedShiftRightExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.URSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2 = counter;

        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.unsignedShiftRight(c, lobj, robj);

        MCodeUtilities.write("" + Code.URSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(o) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));
        return o;
        /*
         * if (node.hasProperty(NodeProperties.VALUE)) { // The expression is
         * constant return node.getProperty(NodeProperties.VALUE); } else {
         * return InterpreterUtilities.unsignedShiftRight(
         * NodeProperties.getType(node),
         * node.getLeftExpression().acceptVisitor(this),
         * node.getRightExpression().acceptVisitor(this)); }
         */
    }

    /**
     * Visits a UnsignedShiftRightAssignExpression
     *
     * @param node the node to visit
     */
    public Object visit(UnsignedShiftRightAssignExpression node) {
        //Simulate that a>>>=b -->> a= a>>>b

        long assigncounter = counter++;
        long assignauxcounter = counter;
        //Start assignment
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.A + Code.DELIM
                + assigncounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        //
        //Start shift expression
        long shiftcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.URSHIFT
                + Code.DELIM + shiftcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        // Get left hand side for the shift expression
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        Node left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = prepareLeftHandSideModifier(mod, left);

        /*
         Node left = node.getLeftExpression();
         boolean evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;

         LeftHandSideModifier mod = NodeProperties.getModifier(left);
         Object lhs = mod.prepare(this, context);
         */

        long auxcounter2 = counter;

        // Get right hand side for the shift expression
        MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
        Object val = node.getRightExpression().acceptVisitor(this);

        // Perform the operation
        Object result = InterpreterUtilities.unsignedShiftRight(NodeProperties
                .getType(node), lhs, val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        MCodeUtilities.write("" + Code.URSHIFT + Code.DELIM + shiftcounter
                + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                + Code.DELIM + MCodeUtilities.getValue(result) + Code.DELIM
                + NodeProperties.getType(node).getName() + Code.DELIM
                + MCodeGenerator.locationToString(node));

        /*
         long assignauxcounter2 = counter;
         MCodeUtilities.write("" + Code.TO + Code.DELIM + counter);
         evaluatingPreviously = evaluating;
         evaluating = false;
         left.acceptVisitor(this);
         evaluating = evaluatingPreviously;
         */

        // Modify the variable and return
        mod.modify(context, result);

        MCodeGenerator.generateCodeA(assigncounter, assignauxcounter,
                auxcounter /*assignauxcounter2*/, result, node);
        /*
         MCodeUtilities.write("" + Code.A + Code.DELIM + assigncounter
         + Code.DELIM + assignauxcounter + Code.DELIM
         + auxcounter //assignauxcounter2
         + Code.DELIM
         + MCodeUtilities.getValue(result) + Code.DELIM
         + NodeProperties.getType(node).getName() + Code.DELIM
         + MCodeUtilities.locationToString(node));
         */
        return result;
    }

    /**
     * Visits an AndExpression
     *
     * @param node the node to visit
     */
    public Object visit(AndExpression node) {
        long andcounter = counter++;
        long auxcounter = counter;

        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.AND
                + Code.DELIM + andcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));

        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);

        boolean left = ((Boolean) node.getLeftExpression().acceptVisitor(this))
                .booleanValue();

        if (!left && shortCircuit) {
            MCodeUtilities.write("" + Code.AND + Code.DELIM + andcounter
                    + Code.DELIM + auxcounter + Code.DELIM + Code.NO_REFERENCE
                    + Code.DELIM + Code.FALSE + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            return Boolean.FALSE;

        } else {

            long auxcounter2 = counter;
            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
            boolean right = ((Boolean) node.getRightExpression().acceptVisitor(
                    this)).booleanValue();

            if (left && right) {

                MCodeUtilities.write("" + Code.AND + Code.DELIM + andcounter
                        + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                        + Code.DELIM + Code.TRUE + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                return Boolean.TRUE;

            } else {

                MCodeUtilities.write("" + Code.AND + Code.DELIM + andcounter
                        + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                        + Code.DELIM + Code.FALSE + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                return Boolean.FALSE;
            }
        }
    }

    /**
     * Visits an OrExpression
     *
     * @param node the node to visit
     */
    public Object visit(OrExpression node) {

        long orcounter = counter++;
        long auxcounter = counter;
        MCodeUtilities.write("" + Code.BEGIN + Code.DELIM + Code.OR
                + Code.DELIM + orcounter + Code.DELIM
                + MCodeGenerator.locationToString(node));
        MCodeUtilities.write("" + Code.LEFT + Code.DELIM + counter);
        boolean left = ((Boolean) node.getLeftExpression().acceptVisitor(this))
                .booleanValue();

        if (left && shortCircuit) {
            MCodeUtilities.write("" + Code.OR + Code.DELIM + orcounter
                    + Code.DELIM + auxcounter + Code.DELIM + Code.NO_REFERENCE
                    + Code.DELIM + Code.TRUE + Code.DELIM
                    + NodeProperties.getType(node).getName() + Code.DELIM
                    + MCodeGenerator.locationToString(node));

            return Boolean.TRUE;

        } else {
            long auxcounter2 = counter;
            MCodeUtilities.write("" + Code.RIGHT + Code.DELIM + counter);
            boolean right = ((Boolean) node.getRightExpression().acceptVisitor(
                    this)).booleanValue();

            if (left || right) {

                MCodeUtilities.write("" + Code.OR + Code.DELIM + orcounter
                        + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                        + Code.DELIM + Code.TRUE + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                return Boolean.TRUE;

            } else {

                MCodeUtilities.write("" + Code.OR + Code.DELIM + orcounter
                        + Code.DELIM + auxcounter + Code.DELIM + auxcounter2
                        + Code.DELIM + Code.FALSE + Code.DELIM
                        + NodeProperties.getType(node).getName() + Code.DELIM
                        + MCodeGenerator.locationToString(node));

                return Boolean.FALSE;
            }
        }
    }

    /**
     * Visits a FunctionCall
     *
     * @param node the node to visit
     */
    public Object visit(FunctionCall node) {
        MethodDeclaration md;
        md = (MethodDeclaration) node.getProperty(NodeProperties.FUNCTION);

        // Enter a new scope and define the parameters as local variables
        Context c = new GlobalContext(context.getInterpreter());
        if (node.getArguments() != null) {
            Iterator it = md.getParameters().iterator();
            Iterator it2 = node.getArguments().iterator();
            while (it.hasNext()) {
                FormalParameter fp = (FormalParameter) it.next();
                if (fp.isFinal()) {
                    c.setConstant(fp.getName(), ((Node) it2.next())
                            .acceptVisitor(this));
                } else {
                    c.setVariable(fp.getName(), ((Node) it2.next())
                            .acceptVisitor(this));
                }
            }
        }

        // Do the type checking of the body if needed
        Node body = md.getBody();
        if (!body.hasProperty("visited")) {
            body.setProperty("visited", null);
            ImportationManager im = (ImportationManager) md
                    .getProperty(NodeProperties.IMPORTATION_MANAGER);
            Context ctx = new GlobalContext(context.getInterpreter());
            ctx.setImportationManager(im);

            Visitor v = new NameVisitor(ctx);
            Iterator it = md.getParameters().iterator();
            while (it.hasNext()) {
                ((Node) it.next()).acceptVisitor(v);
            }
            body.acceptVisitor(v);

            ctx = new GlobalContext(context.getInterpreter());
            ctx.setImportationManager(im);
            ctx.setFunctions((List) md.getProperty(NodeProperties.FUNCTIONS));

            v = new TypeChecker(ctx);
            it = md.getParameters().iterator();
            while (it.hasNext()) {
                ((Node) it.next()).acceptVisitor(v);
            }
            body.acceptVisitor(v);
        }

        // Interpret the body of the function
        try {
            body.acceptVisitor(new EvaluationVisitor(c));
        } catch (ReturnException e) {
            return e.getValue();
        }
        return null;
    }

    /**
     * Performs a dynamic cast. This method acts on primitive wrappers.
     *
     * @param tc
     *            the target class
     * @param o
     *            the object to cast
     */
    private static Object performCast(Class tc, Object o) {
        Class ec = (o != null) ? o.getClass() : null;

        if (tc != ec && tc.isPrimitive() && ec != null) {
            if (tc != char.class && ec == Character.class) {
                o = new Integer(((Character) o).charValue());
            } else if (tc == byte.class) {
                o = new Byte(((Number) o).byteValue());
            } else if (tc == short.class) {
                o = new Short(((Number) o).shortValue());
            } else if (tc == int.class) {
                o = new Integer(((Number) o).intValue());
            } else if (tc == long.class) {
                o = new Long(((Number) o).longValue());
            } else if (tc == float.class) {
                o = new Float(((Number) o).floatValue());
            } else if (tc == double.class) {
                o = new Double(((Number) o).doubleValue());
            } else if (tc == char.class && ec != Character.class) {
                o = new Character((char) ((Number) o).shortValue());
            }
        }
        return o;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public Stack getArrayCellNumbersStack() {
        return arrayCellNumbersStack;
    }

    public void setArrayCellNumbersStack(Stack arrayCellNumbersStack) {
        this.arrayCellNumbersStack = arrayCellNumbersStack;
    }

    public Stack getArrayCellReferencesStack() {
        return arrayCellReferencesStack;
    }

    public void setArrayCellReferencesStack(Stack arrayCellReferencesStack) {
        this.arrayCellReferencesStack = arrayCellReferencesStack;
    }

    /**
     * This method will handle the preparation of <code>LeftHandSideModifier</code> in cases of
     * assignments and pre/post-increments/decrements
     * 
     * @param mod
     * @param left
     * @return
     */
    private Object prepareLeftHandSideModifier(LeftHandSideModifier mod,
            Node exp) {
        Object lhs;
        if (mod instanceof ArrayModifier) {
            lhs = mod.prepare(this, context);
        } else if (mod instanceof ObjectFieldModifier) {
            lhs = mod.prepare(this, context);
        } else {
            boolean evaluatingPreviously = evaluating;
            evaluating = false;
            exp.acceptVisitor(this);
            evaluating = evaluatingPreviously;
            lhs = mod.prepare(this, context);
        }
        return lhs;
    }
}
