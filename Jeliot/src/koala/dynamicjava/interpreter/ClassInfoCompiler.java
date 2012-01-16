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

package koala.dynamicjava.interpreter;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import jeliot.mcode.*;
import koala.dynamicjava.classfile.InnerClassesEntry;
import koala.dynamicjava.classinfo.ClassInfo;
import koala.dynamicjava.classinfo.ClassInfoUtilities;
import koala.dynamicjava.classinfo.ConstructorInfo;
import koala.dynamicjava.classinfo.FieldInfo;
import koala.dynamicjava.classinfo.JavaClassInfo;
import koala.dynamicjava.classinfo.MethodInfo;
import koala.dynamicjava.classinfo.TreeClassInfo;
import koala.dynamicjava.classinfo.TreeConstructorInfo;
import koala.dynamicjava.interpreter.context.VariableContext;
import koala.dynamicjava.interpreter.error.CatchedExceptionError;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.AddAssignExpression;
import koala.dynamicjava.tree.AddExpression;
import koala.dynamicjava.tree.AndExpression;
import koala.dynamicjava.tree.ArrayAccess;
import koala.dynamicjava.tree.ArrayAllocation;
import koala.dynamicjava.tree.ArrayType;
import koala.dynamicjava.tree.BinaryExpression;
import koala.dynamicjava.tree.BitAndAssignExpression;
import koala.dynamicjava.tree.BitAndExpression;
import koala.dynamicjava.tree.BitOrAssignExpression;
import koala.dynamicjava.tree.BitOrExpression;
import koala.dynamicjava.tree.BlockStatement;
import koala.dynamicjava.tree.BooleanLiteral;
import koala.dynamicjava.tree.CastExpression;
import koala.dynamicjava.tree.ClassInitializer;
import koala.dynamicjava.tree.ComplementExpression;
import koala.dynamicjava.tree.ConditionalExpression;
import koala.dynamicjava.tree.ConstructorDeclaration;
import koala.dynamicjava.tree.ConstructorInvocation;
import koala.dynamicjava.tree.DivideAssignExpression;
import koala.dynamicjava.tree.DivideExpression;
import koala.dynamicjava.tree.DoubleLiteral;
import koala.dynamicjava.tree.EqualExpression;
import koala.dynamicjava.tree.ExclusiveOrAssignExpression;
import koala.dynamicjava.tree.ExclusiveOrExpression;
import koala.dynamicjava.tree.Expression;
import koala.dynamicjava.tree.FieldDeclaration;
import koala.dynamicjava.tree.FloatLiteral;
import koala.dynamicjava.tree.FormalParameter;
import koala.dynamicjava.tree.GreaterExpression;
import koala.dynamicjava.tree.GreaterOrEqualExpression;
import koala.dynamicjava.tree.Identifier;
import koala.dynamicjava.tree.IdentifierToken;
import koala.dynamicjava.tree.InstanceInitializer;
import koala.dynamicjava.tree.InstanceOfExpression;
import koala.dynamicjava.tree.IntegerLiteral;
import koala.dynamicjava.tree.LessExpression;
import koala.dynamicjava.tree.LessOrEqualExpression;
import koala.dynamicjava.tree.Literal;
import koala.dynamicjava.tree.LongLiteral;
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
import koala.dynamicjava.tree.PrimitiveType;
import koala.dynamicjava.tree.QualifiedName;
import koala.dynamicjava.tree.ReferenceType;
import koala.dynamicjava.tree.RemainderAssignExpression;
import koala.dynamicjava.tree.RemainderExpression;
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
import koala.dynamicjava.tree.ThisExpression;
import koala.dynamicjava.tree.TreeUtilities;
import koala.dynamicjava.tree.TypeDeclaration;
import koala.dynamicjava.tree.TypeExpression;
import koala.dynamicjava.tree.UnaryExpression;
import koala.dynamicjava.tree.UnsignedShiftRightAssignExpression;
import koala.dynamicjava.tree.UnsignedShiftRightExpression;
import koala.dynamicjava.tree.VoidType;
import koala.dynamicjava.tree.visitor.VisitorObject;
import koala.dynamicjava.util.AmbiguousFieldException;
import koala.dynamicjava.util.ImportationManager;

/**
 * This class translates a class info into a Class object
 *
 * @author  Stephane Hillion
 * @version 1.1 - 1999/11/28
 */

public class ClassInfoCompiler {

    /**
     * The class info to compile
     */
    protected TreeClassInfo classInfo;

    /**
     * The tree of the class to compile
     */
    protected TypeDeclaration typeDeclaration;

    /**
     * The class factory
     */
    protected ClassFactory classFactory;

    /**
     * The class finder
     */
    protected TreeClassFinder classFinder;

    /**
     * The interpreter
     */
    protected TreeInterpreter interpreter;

    /**
     * Is the class info represents an interface ?
     */
    protected boolean isInterface;

    /**
     * Is the underlying class contain an abstract method?
     */
    protected boolean hasAbstractMethod;

    /**
     * The class initializer expressions
     */
    protected List classInitializer = new LinkedList();

    /**
     * The instance initializer expressions
     */
    protected List instanceInitializer = new LinkedList();

    /**
     * The members visitor
     */
    protected MembersVisitor membersVisitor = new MembersVisitor();

    /**
     * The importation manager
     */
    protected ImportationManager importationManager;

    /**
     * Creates a new compiler
     * @param ci the class info to compile
     */
    public ClassInfoCompiler(ClassInfo ci) {
        classInfo = (TreeClassInfo) ci;
        typeDeclaration = classInfo.getTypeDeclaration();
        classFinder = (TreeClassFinder) classInfo.getClassFinder();
        importationManager = (ci.getDeclaringClass() != null) ? classFinder
                .getImportationManager() : (ImportationManager) classFinder
                .getImportationManager().clone();
        interpreter = (TreeInterpreter) classFinder.getInterpreter();
        isInterface = classInfo.isInterface();
    }

    /**
     * Creates a Class object from the classInfo attribute
     * @return the created class
     */
    public Class compile() {
        // Create a class factory
        ClassInfo dc = classInfo.getDeclaringClass();
        String outer = (dc != null) ? dc.getName() : null;
        int af = typeDeclaration.getAccessFlags();
        String name = classInfo.getName();

        if (isInterface) {
            af |= Modifier.INTERFACE;
        }

        classFactory = new ClassFactory(af, name, classInfo.getSuperclass()
                .getName(), interpreter.getClass(), interpreter
                .getExceptionClass(), interpreter.getClassLoader().toString());

        // Add the innerclass attributes
        if (dc != null) {
            addInnerClassesAttribute(classInfo);
        }

        ClassInfo[] inners = classInfo.getDeclaredClasses();
        for (int i = 0; i < inners.length; i++) {
            String ciname = inners[i].getName();

            InnerClassesEntry ice = classFactory.addInnerClassesEntry();
            ice.setInnerClassInfo(ciname);
            ice.setOuterClassInfo(name);
            ice.setInnerName(ciname.substring(name.length() + 1, ciname
                    .length()));
            ice.setInnerClassAccessFlags((short) inners[i].getModifiers());
        }

        // Add the interfaces
        ClassInfo[] ci = classInfo.getInterfaces();
        for (int i = 0; i < ci.length; i++) {
            classFactory.addInterface(ci[i].getName());
        }

        // Check and create the members
        Iterator it = typeDeclaration.getMembers().iterator();
        while (it.hasNext()) {
            ((Node) it.next()).acceptVisitor(membersVisitor);
        }

        if (!isInterface && hasAbstractMethod && !Modifier.isAbstract(af)) {
            typeDeclaration.setProperty(NodeProperties.ERROR_STRINGS,
                    new String[] { name });
            throw new ExecutionError("misplaced.abstract", typeDeclaration);
        }

        // Create the constructor(s)
        if (!isInterface) {
            ConstructorInfo[] cons = classInfo.getConstructors();
            for (int i = 0; i < cons.length; i++) {
                ClassInfo[] cia = cons[i].getParameterTypes();
                String[] params = new String[cia.length];

                for (int j = 0; j < cia.length; j++) {
                    params[j] = cia[j].getName();
                }

                MCodeUtilities.write("" + Code.CONSTRUCTOR + Code.DELIM
                        + MCodeGenerator.arrayToString(params));
                addConstructor((TreeConstructorInfo) cons[i]);
            }
        }

        // Create the class initializer
        if (classInitializer.size() > 0) {
            interpreter.registerMethod(classFactory.createClassInitializer(),
                    new MethodDeclaration(Modifier.PUBLIC, new VoidType(),
                            "<clinit>", new LinkedList(), new LinkedList(),
                            new BlockStatement(classInitializer)),
                    importationManager);
        }

        // Define the class
        TreeClassLoader classLoader = (TreeClassLoader) interpreter
                .getClassLoader();
        return classLoader.defineClass(name, classFactory.getByteCode());
    }

    /**
     * Adds a constructor to the current class
     * @param ci the constructor info
     */
    protected void addConstructor(TreeConstructorInfo ci) {
        // Get the parameter types
        ClassInfo[] cinf = ci.getParameterTypes();
        String[] params = new String[cinf.length];
        for (int i = 0; i < cinf.length; i++) {
            params[i] = cinf[i].getName();
        }

        // Get the exceptions
        cinf = ci.getExceptionTypes();
        String[] ex = new String[cinf.length];
        for (int i = 0; i < cinf.length; i++) {
            ex[i] = cinf[i].getName();
        }

        String sig = ClassFactory.getMethodIdentifier(classInfo.getName(),
                "<init>", params, interpreter.getClassLoader().toString());
        ConstructorDeclaration cd = ci.getConstructorDeclaration();

        // Check the constructor's name
        if (!cd.getName().equals(typeDeclaration.getName())) {
            cd.setProperty(NodeProperties.ERROR_STRINGS, new String[] { cd
                    .getName() });
            throw new ExecutionError("constructor.name", cd);
        }

        // Register the constructor
        ConstructorInvocation civ = cd.getConstructorInvocation();
        ConstructorVisitor cv = new ConstructorVisitor();

        if (civ != null) {
            Iterator it = cd.getParameters().iterator();
            while (it.hasNext()) {
                ((Node) it.next()).acceptVisitor(cv);
            }
            civ.acceptVisitor(cv);

            interpreter.registerConstructorArguments(sig, cd.getParameters(),
                    civ.getArguments(), importationManager, cd); //jeliot 3
        } else {
            interpreter.registerConstructorArguments(sig, new LinkedList(),
                    new LinkedList(), importationManager, cd); //jeliot 3
        }

        MethodDeclaration md = new MethodDeclaration(cd.getAccessFlags(),
                new VoidType(), "<init>", cd.getParameters(), new LinkedList(),
                new BlockStatement(cd.getStatements()), cd.getFilename(), cd.getBeginLine(), cd.getBeginColumn(), cd.getEndLine(), cd.getEndColumn());
        interpreter.registerMethod(sig, md, importationManager);

        // Add the instance initialization statement to the constructor statement
        if (!cv.superConstructor.equals(classInfo.getName())) {
            ListIterator lit = cd.getStatements().listIterator();
            Iterator it = instanceInitializer.iterator();
            while (it.hasNext()) {
                lit.add(it.next());
            }
        }

        // Create the constructor
        classFactory.addConstructor(cd.getAccessFlags(), params, ex,
                cv.superConstructor, cv.constructorParameters);
    }

    /**
     * Adds an inner class attribute to the given class
     */
    protected void addInnerClassesAttribute(ClassInfo ci) {
        ClassInfo dc = ci.getDeclaringClass();

        while (dc != null) {
            String ciname = ci.getName();
            String dcname = dc.getName();

            InnerClassesEntry ice = classFactory.addInnerClassesEntry();
            ice.setInnerClassInfo(ciname);
            ice.setOuterClassInfo(dcname);
            ice.setInnerName(ciname.substring(dcname.length() + 1, ciname
                    .length()));
            ice.setInnerClassAccessFlags((short) ci.getModifiers());

            ci = dc;
            dc = dc.getDeclaringClass();
        }
    }

    /**
     * Adds a statement to the class initializer
     * @param n the statement to add
     */
    protected void addToClassInitializer(Node n) {
        classInitializer.add(n);
    }

    /**
     * Adds a statement to the instance initializer
     * @param n the statement to add
     */
    protected void addToInstanceInitializer(Node n) {
        instanceInitializer.add(n);
    }

    /**
     * To build the constructors
     */
    protected class ConstructorVisitor extends VisitorObject {

        String superConstructor;

        String[] constructorParameters = new String[0];

        VariableContext context = new VariableContext();

        /**
         * Visits a ConstructorInvocation
         * @param node the node to visit
         */
        public Object visit(ConstructorInvocation node) {
            Expression exp = node.getExpression();
            if (exp == null) {
                ClassInfo sc = classInfo.getSuperclass();
                ClassInfo sdc = sc.getDeclaringClass();
                ClassInfo dc = classInfo.getDeclaringClass();

                if (dc != null && dc.equals(sdc)
                        && !Modifier.isStatic(sc.getModifiers())) {
                    List l = new LinkedList();
                    l.add(new Identifier("param$0"));
                    exp = new QualifiedName(l);
                    node.setExpression(exp);
                } else if (sdc != null
                        && sdc.equals(classInfo.getAnonymousDeclaringClass())
                        && !Modifier.isStatic(sc.getModifiers())) {
                    List l = new LinkedList();
                    l.add(new Identifier("param$0"));
                    exp = new QualifiedName(l);
                    node.setExpression(exp);
                }
            }

            List args = node.getArguments();
            if (exp != null) {
                if (args == null) {
                    args = new LinkedList();
                    node.setArguments(args);
                }
                args.add(0, exp);
            }

            if (args != null) {
                ListIterator it = args.listIterator();
                while (it.hasNext()) {
                    Object o = ((Expression) it.next()).acceptVisitor(this);
                    if (o != null) {
                        if (o instanceof Expression) {
                            it.set(o);
                        } else {
                            throw new ExecutionError("malformed.argument", node);
                        }
                    }
                }

                ConstructorInfo cons = null;
                try {
                    ClassInfo[] params = null;
                    it = args.listIterator();

                    int i = 0;
                    params = new ClassInfo[args.size()];

                    while (it.hasNext()) {
                        params[i++] = NodeProperties
                                .getClassInfo((Expression) it.next());
                    }
                    if (node.isSuper()) {
                        ClassInfo sc = classInfo.getSuperclass();
                        cons = ClassInfoUtilities.lookupConstructor(sc, params);
                        superConstructor = sc.getName();
                    } else {
                        cons = ClassInfoUtilities.lookupConstructor(classInfo,
                                params);
                        superConstructor = classInfo.getName();
                    }
                } catch (NoSuchMethodException e) {
                    throw new CatchedExceptionError(e, node);
                }

                ClassInfo[] pt = cons.getParameterTypes();
                constructorParameters = new String[pt.length];
                for (int i = 0; i < pt.length; i++) {
                    constructorParameters[i] = pt[i].getName();
                }
            }

            if (superConstructor == null) {
                //Jeliot 3
                //Changed to fix a bug in DJava
                ConstructorInfo cons = null;
                if (node.isSuper()) {
                    ClassInfo sc = classInfo.getSuperclass();
                    superConstructor = sc.getName();
                } else {
                    superConstructor = classInfo.getName();
                }

                //ClassInfo sc = classInfo.getSuperclass();
                //superConstructor = sc.getName();
            }
            return null;
        }

        /**
         * Visits a PrimitiveType
         * @param node the node to visit
         * @return the name of the type
         */
        public Object visit(PrimitiveType node) {
            ClassInfo result = new JavaClassInfo(node.getValue());
            node.setProperty(NodeProperties.TYPE, result);
            return result;
        }

        /**
         * Visits a ReferenceType
         * @param node the node to visit
         * @return the name of the type
         */
        public Object visit(ReferenceType node) {
            // Look for the class represented by this node
            ClassInfo c = null;
            String s = node.getRepresentation();
            try {
                c = classFinder.lookupClass(s, classInfo);
            } catch (ClassNotFoundException e) {
                throw new CatchedExceptionError(e, node);
            }

            // Set the type property of this node
            node.setProperty(NodeProperties.TYPE, c);
            return c;
        }

        /**
         * Visits a ArrayType
         * @param node the node to visit
         * @return the name of the type
         */
        public Object visit(ArrayType node) {
            Node eType = node.getElementType();
            eType.acceptVisitor(this);
            ClassInfo c = NodeProperties.getClassInfo(eType);
            ClassInfo ac;
            if (c instanceof JavaClassInfo) {
                ac = new JavaClassInfo((JavaClassInfo) c);
            } else {
                ac = new TreeClassInfo((TreeClassInfo) c);
            }

            // Set the type property of this node
            node.setProperty(NodeProperties.TYPE, ac);
            return ac;
        }

        /**
         * Visits a FormalParameter
         * @param node the node to visit
         * @return the name of the parameter class
         */
        public Object visit(FormalParameter node) {
            ClassInfo ci = (ClassInfo) node.getType().acceptVisitor(this);
            if (node.isFinal()) {
                context.defineConstant(node.getName(), ci);
            } else {
                context.define(node.getName(), ci);
            }
            return null;
        }

        /**
         * Visits a Literal
         * @param node the node to visit
         */
        public Object visit(Literal node) {
            // Set the properties of the node
            Class c = node.getType();
            node.setProperty(NodeProperties.TYPE, (c == null) ? null
                    : new JavaClassInfo(c));
            return null;
        }

        /**
         * Visits a SimpleAssignExpression
         * @param node the node to visit
         */
        public Object visit(SimpleAssignExpression node) {
            Expression left = node.getLeftExpression();

            // Visit the left expression
            Object o = left.acceptVisitor(this);
            if (o != null) {
                if (o instanceof Expression) {
                    Expression exp = (Expression) o;
                    left = exp;
                    node.setLeftExpression(exp);
                } else {
                    throw new ExecutionError("left.expression", node);
                }
            }

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, NodeProperties
                    .getClassInfo(left));
            return null;
        }

        /**
         * Visits an ObjectFieldAccess
         * @param node the node to visit
         */
        public Object visit(ObjectFieldAccess node) {
            // Visit the expression
            Object o = node.getExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof Expression) {
                    node.setExpression((Expression) o);
                } else {
                    Node result = new StaticFieldAccess((ReferenceType) o, node
                            .getFieldName());
                    result.acceptVisitor(this);
                    return result;
                }
            }

            // Load the field object
            ClassInfo c = NodeProperties.getClassInfo(node.getExpression());
            if (!c.isArray()) {
                FieldInfo f = null;

                try {
                    f = ClassInfoUtilities.getField(c, node.getFieldName());
                } catch (Exception e) {
                    throw new CatchedExceptionError(e, node);
                }
                node.setProperty(NodeProperties.TYPE, f.getType());
            } else {
                if (!node.getFieldName().equals("length")) {
                    String s0 = "length";
                    String s1 = c.getComponentType().getName() + " array";
                    node.setProperty(NodeProperties.ERROR_STRINGS,
                            new String[] { s0, s1 });
                    throw new ExecutionError("no.such.field", node);
                }
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
            }
            return null;
        }

        /**
         * Visits a StaticFieldAccess
         * @param node the node to visit
         */
        public Object visit(StaticFieldAccess node) {
            // Visit the field type
            ClassInfo c = (ClassInfo) node.getFieldType().acceptVisitor(this);

            // Load the field object
            FieldInfo f = null;
            try {
                f = ClassInfoUtilities.getField(c, node.getFieldName());
            } catch (Exception e) {
                try {
                    f = ClassInfoUtilities
                            .getOuterField(c, node.getFieldName());
                } catch (Exception ex) {
                    throw new CatchedExceptionError(e, node);
                }
            }

            node.setProperty(NodeProperties.TYPE, f.getType());
            return null;
        }

        /**
         * Visits a SuperFieldAccess
         * @param node the node to visit
         */
        public Object visit(SuperFieldAccess node) {
            ClassInfo c = classInfo;
            FieldInfo f = null;
            try {
                f = ClassInfoUtilities.getField(c.getSuperclass(), node
                        .getFieldName());
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }
            node.setProperty(NodeProperties.TYPE, f.getType());
            return null;
        }

        /**
         * Visits an ObjectMethodCall
         * @param node the node to visit
         */
        public Object visit(ObjectMethodCall node) {
            // Check the receiver
            if (node.getExpression() != null) {
                Object o = node.getExpression().acceptVisitor(this);
                if (o != null) {
                    if (o instanceof Expression) {
                        node.setExpression((Expression) o);
                    } else {
                        Node result = new StaticMethodCall((ReferenceType) o,
                                node.getMethodName(), node.getArguments(), node
                                        .getFilename(), node.getBeginLine(),
                                node.getBeginColumn(), node.getEndLine(), node
                                        .getEndColumn());
                        result.acceptVisitor(this);
                        return result;
                    }
                }
            } else {
                Identifier t = new Identifier(classInfo.getName());
                List l = new LinkedList();
                l.add(t);
                ReferenceType rt = new ReferenceType(l);
                rt.acceptVisitor(this);
                Node result = new StaticMethodCall(rt, node.getMethodName(),
                        node.getArguments(), node.getFilename(), node
                                .getBeginLine(), node.getBeginColumn(), node
                                .getEndLine(), node.getEndColumn());
                result.acceptVisitor(this);
                return result;
            }

            ClassInfo c = NodeProperties.getClassInfo(node.getExpression());

            if (!c.isArray()
                    || (c.isArray() && !node.getMethodName().equals("clone"))) {
                // Do the type checking of the arguments
                ClassInfo[] cargs = new ClassInfo[0];
                List args = node.getArguments();
                if (args != null) {
                    checkList(args, "malformed.argument", node);

                    cargs = new ClassInfo[args.size()];
                    ListIterator it = args.listIterator();
                    int i = 0;
                    while (it.hasNext()) {
                        cargs[i++] = NodeProperties.getClassInfo((Node) it
                                .next());
                    }
                }
                MethodInfo m = null;
                try {
                    m = ClassInfoUtilities.lookupMethod(c,
                            node.getMethodName(), cargs);
                } catch (NoSuchMethodException e) {
                    /* Jeliot 3 addition begins */
                    String s0 = node.getMethodName();
                    String s1 = c.getName();
                    int n = cargs.length;
                    String params = "(";
                    for (int i = 0; i < n; i++) {
                        params += cargs[i].getName();
                        if (i == n - 1) {
                            break;
                        }
                        params += ",";
                    }
                    params += ")";

                    node.setProperty(NodeProperties.ERROR_STRINGS,
                            new String[] { s0 + params, s1 });

                    //node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { s0, s1 });
                    throw new ExecutionError("no.such.method", node);
                    /* Jeliot 3 addition ends */

                    //throw new CatchedExceptionError(e, node);
                }

                // Set the node properties
                node.setProperty(NodeProperties.TYPE, m.getReturnType());
            } else {
                if (!node.getMethodName().equals("clone")
                        || node.getArguments() != null) {
                    String s0 = "clone";
                    String s1 = c.getComponentType().getName() + " array";
                    node.setProperty(NodeProperties.ERROR_STRINGS,
                            new String[] { s0, s1 });
                    throw new ExecutionError("no.such.method", node);
                }
                node.setProperty(NodeProperties.TYPE, new JavaClassInfo(
                        Object.class));
            }
            return null;
        }

        /**
         * Visits a StaticMethodCall
         * @param node the node to visit
         */
        public Object visit(StaticMethodCall node) {
            // Do the type checking of the arguments
            List args = node.getArguments();
            ClassInfo[] cargs = new ClassInfo[0];
            if (args != null) {
                checkList(args, "malformed.argument", node);

                cargs = new ClassInfo[args.size()];
                ListIterator it = args.listIterator();
                int i = 0;
                while (it.hasNext()) {
                    cargs[i++] = NodeProperties.getClassInfo((Node) it.next());
                }
            }
            MethodInfo m = null;
            Node n = node.getMethodType();
            ClassInfo c = NodeProperties.getClassInfo(n);
            try {
                m = ClassInfoUtilities.lookupMethod(c, node.getMethodName(),
                        cargs);
            } catch (NoSuchMethodException e) {
                if (n.getBeginLine() == n.getEndLine()
                        && n.getBeginColumn() == n.getEndLine()) {
                    // Look for a method in the outerclasses
                    try {
                        m = ClassInfoUtilities.lookupOuterMethod(c, node
                                .getMethodName(), cargs);
                    } catch (NoSuchMethodException ex) {
                        throw new CatchedExceptionError(ex, node);
                    }
                } else {
                    throw new CatchedExceptionError(e, node);
                }
                throw new CatchedExceptionError(e, node);
            }

            // Set the node properties
            node.setProperty(NodeProperties.TYPE, m.getReturnType());
            return null;
        }

        /**
         * Visits a SuperMethodCall
         * @param node the node to visit
         */
        public Object visit(SuperMethodCall node) {
            ClassInfo c = classInfo.getSuperclass();

            List args = node.getArguments();
            ClassInfo[] pt = new ClassInfo[0];
            if (args != null) {
                checkList(args, "malformed.argument", node);

                pt = new ClassInfo[args.size()];
                ListIterator it = args.listIterator();
                int i = 0;
                while (it.hasNext()) {
                    pt[i++] = NodeProperties.getClassInfo((Node) it.next());
                }
            }
            MethodInfo m = null;
            try {
                m = ClassInfoUtilities
                        .lookupMethod(c, node.getMethodName(), pt);
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }

            // Set the node type property
            node.setProperty(NodeProperties.TYPE, m.getReturnType());
            return null;
        }

        /**
         * Visits a QualifiedName
         * @param node the node to visit
         * @return a node that depends of the meaning of this name.
         *         It could be : a QualifiedName, a ReferenceType or a FieldAccess.
         */
        public Object visit(QualifiedName node) {
            List ids = node.getIdentifiers();
            IdentifierToken t = (IdentifierToken) ids.get(0);

            if (context.isDefinedVariable(t.image())
                    || fieldExists(classInfo, t.image())) {
                // The name starts with a reference to a local variable,
                // the end of the name is a sequence of field access
                Expression result = null;
                if (context.isDefinedVariable(t.image())) {
                    if (ids.size() == 1) {
                        ClassInfo c = (ClassInfo) context.get(t.image());
                        node.setProperty(NodeProperties.TYPE, c);
                        return null;
                    }
                    List l = new LinkedList();
                    l.add(t);
                    result = new QualifiedName(l);
                } else {
                    result = new StaticFieldAccess(new ReferenceType(classInfo
                            .getName()), t.image());
                }

                Iterator it = ids.iterator();
                it.next();

                IdentifierToken t2;
                while (it.hasNext()) {
                    result = new ObjectFieldAccess(result,
                            (t2 = (IdentifierToken) it.next()).image(), node
                                    .getFilename(), t.beginLine(), t
                                    .beginColumn(), t2.endLine(), t2
                                    .endColumn());
                }
                result.acceptVisitor(this);
                return result;
            }

            // The name must be, or starts with, a class name
            List l = (List) ((LinkedList) ids).clone();
            boolean b = false;

            while (l.size() > 0) {
                String s = TreeUtilities.listToName(l);
                try {
                    classFinder.lookupClass(s, classInfo);
                    b = true;
                    break;
                } catch (ClassNotFoundException e) {
                }
                l.remove(l.size() - 1);
            }
            if (!b) {
                // It is an error if no matching class or field was found
                node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { t
                        .image() });
                throw new ExecutionError("undefined.class", node);
            }

            // Creates a ReferenceType node
            IdentifierToken t2 = (IdentifierToken) l.get(l.size() - 1);
            ReferenceType rt = new ReferenceType(l, node.getFilename(), t
                    .beginLine(), t.beginColumn(), t2.endLine(), t2.endColumn());

            if (l.size() != ids.size()) {
                // The end of the name is a sequence of field access
                ListIterator it = ids.listIterator(l.size());
                Expression result = new StaticFieldAccess(rt,
                        (t2 = (IdentifierToken) it.next()).image(), node
                                .getFilename(), t.beginLine(), t.beginColumn(),
                        t2.endLine(), t2.endColumn());
                while (it.hasNext()) {
                    result = new ObjectFieldAccess(result,
                            (t2 = (IdentifierToken) it.next()).image(), node
                                    .getFilename(), t.beginLine(), t
                                    .beginColumn(), t2.endLine(), t2
                                    .endColumn());
                }
                result.acceptVisitor(this);
                return result;
            } else {
                rt.acceptVisitor(this);
                return rt;
            }
        }

        /**
         * Visits a ThisExpression
         * @param node the node to visit
         */
        public Object visit(ThisExpression node) {
            throw new ExecutionError("this.undefined", node);
        }

        /**
         * Visits a SimpleAllocation
         * @param node the node to visit
         */
        public Object visit(SimpleAllocation node) {
            Node type = node.getCreationType();

            node.setProperty(NodeProperties.TYPE, type.acceptVisitor(this));
            return null;
        }

        /**
         * Visits an ArrayAllocation
         * @param node the node to visit
         */
        public Object visit(ArrayAllocation node) {
            Node type = node.getCreationType();
            ClassInfo c = (ClassInfo) type.acceptVisitor(this);

            for (int i = 0; i < node.getDimension(); i++) {
                if (c instanceof JavaClassInfo) {
                    c = new JavaClassInfo((JavaClassInfo) c);
                } else {
                    c = new TreeClassInfo((TreeClassInfo) c);
                }
            }

            node.setProperty(NodeProperties.TYPE, c);
            return null;
        }

        /**
         * Visits an ArrayAccess
         * @param node the node to visit
         */
        public Object visit(ArrayAccess node) {
            // Visits the expression on which this array access applies
            Object o = node.getExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof Expression) {
                    node.setExpression((Expression) o);
                } else {
                    throw new ExecutionError("malformed.expression", node);
                }
            }
            ClassInfo c = NodeProperties.getClassInfo(node.getExpression());
            if (!c.isArray()) {
                node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { c
                        .getName() });
                throw new ExecutionError("array.required", node);
            }

            // Sets the properties of this node
            node.setProperty(NodeProperties.TYPE, c.getComponentType());
            return null;
        }

        /**
         * Visits a TypeExpression
         * @param node the node to visit
         */
        public Object visit(TypeExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.CLASS);
            return null;
        }

        /**
         * Visits a NotExpression
         * @param node the node to visit
         */
        public Object visit(NotExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a ComplementExpression
         * @param node the node to visit
         */
        public Object visit(ComplementExpression node) {
            visitUnaryExpression(node);

            // Check the type
            Node n = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(n);

            if (ci instanceof JavaClassInfo) {
                Class c = ((JavaClassInfo) ci).getJavaClass();
                if (c == char.class || c == byte.class || c == short.class) {
                    node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
                } else if (c == int.class || c == long.class) {
                    node.setProperty(NodeProperties.TYPE, new JavaClassInfo(c));
                } else {
                    throw new ExecutionError("malformed.expression", node);
                }
            } else {
                throw new ExecutionError("malformed.expression", node);
            }
            return null;
        }

        /**
         * Visits a PlusExpression
         * @param node the node to visit
         */
        public Object visit(PlusExpression node) {
            visitUnaryExpression(node);
            visitUnaryOperation(node, "malformed.expression");
            return null;
        }

        /**
         * Visits a MinusExpression
         * @param node the node to visit
         */
        public Object visit(MinusExpression node) {
            visitUnaryExpression(node);
            visitUnaryOperation(node, "malformed.expression");
            return null;
        }

        /**
         * Visits an AddExpression
         * @param node the node to visit
         */
        public Object visit(AddExpression node) {
            visitBinaryExpression(node);

            // Check the types
            Node ln = node.getLeftExpression();
            Node rn = node.getRightExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);
            ClassInfo rci = NodeProperties.getClassInfo(rn);
            Class lc = null;
            Class rc = null;

            if ((lci instanceof JavaClassInfo)
                    && (rci instanceof JavaClassInfo)) {
                lc = lci.getJavaClass();
                rc = rci.getJavaClass();
            } else {
                throw new ExecutionError("addition.type", node);
            }

            if (lc == String.class || rc == String.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.STRING);
            } else {
                visitNumericExpression(node, "addition.type");
            }
            return null;
        }

        /**
         * Visits an AddAssignExpression
         * @param node the node to visit
         */
        public Object visit(AddAssignExpression node) {
            visitBinaryExpression(node);

            Node ln = node.getLeftExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, lci);
            return null;
        }

        /**
         * Visits a SubtractExpression
         * @param node the node to visit
         */
        public Object visit(SubtractExpression node) {
            visitBinaryExpression(node);
            visitNumericExpression(node, "subtraction.type");
            return null;
        }

        /**
         * Visits an SubtractAssignExpression
         * @param node the node to visit
         */
        public Object visit(SubtractAssignExpression node) {
            visitBinaryExpression(node);

            Node ln = node.getLeftExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, lci);
            return null;
        }

        /**
         * Visits a MultiplyExpression
         * @param node the node to visit
         */
        public Object visit(MultiplyExpression node) {
            visitBinaryExpression(node);
            visitNumericExpression(node, "multiplication.type");
            return null;
        }

        /**
         * Visits an MultiplyAssignExpression
         * @param node the node to visit
         */
        public Object visit(MultiplyAssignExpression node) {
            visitBinaryExpression(node);

            // Check the types
            Node ln = node.getLeftExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, lci);
            return null;
        }

        /**
         * Visits a DivideExpression
         * @param node the node to visit
         */
        public Object visit(DivideExpression node) {
            visitBinaryExpression(node);
            visitNumericExpression(node, "division.type");
            return null;
        }

        /**
         * Visits an DivideAssignExpression
         * @param node the node to visit
         */
        public Object visit(DivideAssignExpression node) {
            visitBinaryExpression(node);

            // Check the types
            Node ln = node.getLeftExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, lci);
            return null;
        }

        /**
         * Visits a RemainderExpression
         * @param node the node to visit
         */
        public Object visit(RemainderExpression node) {
            visitBinaryExpression(node);
            visitNumericExpression(node, "remainder.type");
            return null;
        }

        /**
         * Visits an RemainderAssignExpression
         * @param node the node to visit
         */
        public Object visit(RemainderAssignExpression node) {
            visitBinaryExpression(node);

            // Check the types
            Node ln = node.getLeftExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, lci);
            return null;
        }

        /**
         * Visits an EqualExpression
         * @param node the node to visit
         */
        public Object visit(EqualExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a NotEqualExpression
         * @param node the node to visit
         */
        public Object visit(NotEqualExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a LessExpression
         * @param node the node to visit
         */
        public Object visit(LessExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a LessOrEqualExpression
         * @param node the node to visit
         */
        public Object visit(LessOrEqualExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a GreaterExpression
         * @param node the node to visit
         */
        public Object visit(GreaterExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a GreaterOrEqualExpression
         * @param node the node to visit
         */
        public Object visit(GreaterOrEqualExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a BitAndExpression
         * @param node the node to visit
         */
        public Object visit(BitAndExpression node) {
            visitBinaryExpression(node);
            visitBitwiseExpression(node, "bit.and.type");
            return null;
        }

        /**
         * Visits a BitAndAssignExpression
         * @param node the node to visit
         */
        public Object visit(BitAndAssignExpression node) {
            visitBinaryExpression(node);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, NodeProperties
                    .getClassInfo(node.getLeftExpression()));
            return null;
        }

        /**
         * Visits a ExclusiveOrExpression
         * @param node the node to visit
         */
        public Object visit(ExclusiveOrExpression node) {
            visitBinaryExpression(node);
            visitBitwiseExpression(node, "xor.type");
            return null;
        }

        /**
         * Visits a ExclusiveOrAssignExpression
         * @param node the node to visit
         */
        public Object visit(ExclusiveOrAssignExpression node) {
            visitBinaryExpression(node);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, NodeProperties
                    .getClassInfo(node.getLeftExpression()));
            return null;
        }

        /**
         * Visits a BitOrExpression
         * @param node the node to visit
         */
        public Object visit(BitOrExpression node) {
            visitBinaryExpression(node);
            visitBitwiseExpression(node, "bit.or.type");
            return null;
        }

        /**
         * Visits a BitOrAssignExpression
         * @param node the node to visit
         */
        public Object visit(BitOrAssignExpression node) {
            visitBinaryExpression(node);

            // Sets the type property of this node
            node.setProperty(NodeProperties.TYPE, NodeProperties
                    .getClassInfo(node.getLeftExpression()));
            return null;
        }

        /**
         * Visits a ShiftLeftExpression
         * @param node the node to visit
         */
        public Object visit(ShiftLeftExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "shift.left.type");
            return null;
        }

        /**
         * Visits a ShiftLeftAssignExpression
         * @param node the node to visit
         */
        public Object visit(ShiftLeftAssignExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "shift.left.type");
            return null;
        }

        /**
         * Visits a ShiftRightExpression
         * @param node the node to visit
         */
        public Object visit(ShiftRightExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "shift.right.type");
            return null;
        }

        /**
         * Visits a ShiftRightAssignExpression
         * @param node the node to visit
         */
        public Object visit(ShiftRightAssignExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "shift.right.type");
            return null;
        }

        /**
         * Visits a UnsignedShiftRightExpression
         * @param node the node to visit
         */
        public Object visit(UnsignedShiftRightExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "unsigned.shift.right.type");
            return null;
        }

        /**
         * Visits a UnsignedShiftRightAssignExpression
         * @param node the node to visit
         */
        public Object visit(UnsignedShiftRightAssignExpression node) {
            visitBinaryExpression(node);
            visitShiftExpression(node, "unsigned.shift.right.type");
            return null;
        }

        /**
         * Visits an AndExpression
         * @param node the node to visit
         */
        public Object visit(AndExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits an OrExpression
         * @param node the node to visit
         */
        public Object visit(OrExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a InstanceOfExpression
         * @param node the node to visit
         */
        public Object visit(InstanceOfExpression node) {
            node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            return null;
        }

        /**
         * Visits a ConditionalExpression
         * @param node the node to visit
         */
        public Object visit(ConditionalExpression node) {
            Object o = node.getIfTrueExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof ReferenceType) {
                    throw new ExecutionError("malformed.second.operand", node);
                }
                node.setIfTrueExpression((Expression) o);
            }
            o = node.getIfFalseExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof ReferenceType) {
                    throw new ExecutionError("malformed.third.operand", node);
                }
                node.setIfFalseExpression((Expression) o);
            }

            // Determine the type of the expression
            Node n1 = node.getIfTrueExpression();
            Node n2 = node.getIfFalseExpression();
            ClassInfo c1 = NodeProperties.getClassInfo(n1);
            ClassInfo c2 = NodeProperties.getClassInfo(n2);
            ClassInfo ec = null;

            if (c1 == null) {
                ec = c2;
            } else if (c2 == null) {
                ec = c1;
            } else if (c1.equals(c2)) {
                ec = c1;
            } else if (ClassInfoUtilities.isAssignableFrom(c1, c2)) {
                ec = c1;
            } else if (ClassInfoUtilities.isAssignableFrom(c2, c1)) {
                ec = c2;
            } else {
                throw new ExecutionError("conditional.type", node);
            }

            node.setProperty(NodeProperties.TYPE, ec);
            return null;
        }

        /**
         * Visits a PostIncrement
         * @param node the node to visit
         */
        public Object visit(PostIncrement node) {
            visitUnaryExpression(node);

            Node exp = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(exp);

            if (!(ci instanceof JavaClassInfo)) {
                throw new ExecutionError("post.increment.type", node);
            }
            node.setProperty(NodeProperties.TYPE, ci);
            return null;
        }

        /**
         * Visits a PreIncrement
         * @param node the node to visit
         */
        public Object visit(PreIncrement node) {
            visitUnaryExpression(node);

            Node exp = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(exp);

            if (!(ci instanceof JavaClassInfo)) {
                throw new ExecutionError("pre.increment.type", node);
            }
            node.setProperty(NodeProperties.TYPE, ci);
            return null;
        }

        /**
         * Visits a PostDecrement
         * @param node the node to visit
         */
        public Object visit(PostDecrement node) {
            visitUnaryExpression(node);

            Node exp = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(exp);

            if (!(ci instanceof JavaClassInfo)) {
                throw new ExecutionError("post.decrement.type", node);
            }
            node.setProperty(NodeProperties.TYPE, ci);
            return null;
        }

        /**
         * Visits a PreDecrement
         * @param node the node to visit
         */
        public Object visit(PreDecrement node) {
            visitUnaryExpression(node);

            Node exp = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(exp);

            if (!(ci instanceof JavaClassInfo)) {
                throw new ExecutionError("pre.decrement.type", node);
            }
            node.setProperty(NodeProperties.TYPE, ci);
            return null;
        }

        /**
         * Visits a CastExpression
         * @param node the node to visit
         */
        public Object visit(CastExpression node) {
            Node n = node.getTargetType();
            node.setProperty(NodeProperties.TYPE, n.acceptVisitor(this));
            return null;
        }

        /**
         * Visits the subexpression of an UnaryExpression
         */
        protected void visitUnaryExpression(UnaryExpression node) {
            // Visit the subexpression
            Object o = node.getExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof ReferenceType) {
                    throw new ExecutionError("malformed.expression", node);
                }
                node.setExpression((Expression) o);
            }
        }

        /**
         * Visits an unary operation
         */
        protected void visitUnaryOperation(UnaryExpression node, String s) {
            Node n = node.getExpression();
            ClassInfo ci = NodeProperties.getClassInfo(n);

            if (ci.isPrimitive()) {
                Class c = ci.getJavaClass();
                if (c == char.class || c == byte.class || c == short.class
                        || c == int.class) {
                    node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
                } else if (c == long.class || c == float.class
                        || c == double.class) {
                    node.setProperty(NodeProperties.TYPE, new JavaClassInfo(c));
                } else {
                    throw new ExecutionError(s, node);
                }
            } else {
                throw new ExecutionError(s, node);
            }
        }

        /**
         * Visits the subexpressions of a BinaryExpression
         */
        protected void visitBinaryExpression(BinaryExpression node) {
            // Visit the left expression
            Object o = node.getLeftExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof ReferenceType) {
                    throw new ExecutionError("left.operand", node);
                }
                node.setLeftExpression((Expression) o);
            }

            // Visit the right expression
            o = node.getRightExpression().acceptVisitor(this);
            if (o != null) {
                if (o instanceof ReferenceType) {
                    throw new ExecutionError("right.operand", node);
                }
                node.setRightExpression((Expression) o);
            }
        }

        /**
         * Visits a numeric expression
         */
        protected void visitNumericExpression(BinaryExpression node, String s) {
            // Set the type property of the given node
            ClassInfo lci = NodeProperties.getClassInfo(node
                    .getLeftExpression());
            ClassInfo rci = NodeProperties.getClassInfo(node
                    .getRightExpression());
            Class lc = lci.getJavaClass();
            Class rc = rci.getJavaClass();

            if (lc == null || rc == null || lc == boolean.class
                    || rc == boolean.class || !lc.isPrimitive()
                    || !rc.isPrimitive() || lc == void.class
                    || rc == void.class) {
                throw new ExecutionError(s, node);
            } else if (lc == double.class || rc == double.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.DOUBLE);
            } else if (lc == float.class || rc == float.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.FLOAT);
            } else if (lc == long.class || rc == long.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.LONG);
            } else {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
            }
        }

        /**
         * Visits a bitwise expression
         */
        protected void visitBitwiseExpression(BinaryExpression node, String s) {
            // Check the types
            Node ln = node.getLeftExpression();
            Node rn = node.getRightExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);
            ClassInfo rci = NodeProperties.getClassInfo(rn);
            Class lc = null;
            Class rc = null;

            if ((lci instanceof JavaClassInfo)
                    && (rci instanceof JavaClassInfo)) {
                lc = lci.getJavaClass();
                rc = rci.getJavaClass();
            } else {
                throw new ExecutionError(s, node);
            }

            if (lc == null || rc == null || lc == void.class
                    || rc == void.class || lc == float.class
                    || rc == float.class || lc == double.class
                    || rc == double.class
                    || (lc == boolean.class ^ rc == boolean.class)
                    || !lc.isPrimitive() || !rc.isPrimitive()) {
                throw new ExecutionError(s, node);
            } else if (lc == long.class || rc == long.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.LONG);
            } else if (lc == boolean.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.BOOLEAN);
            } else {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
            }
        }

        /**
         * Visits a shift expression
         */
        protected void visitShiftExpression(BinaryExpression node, String s) {
            // Check the types
            Node ln = node.getLeftExpression();
            Node rn = node.getRightExpression();
            ClassInfo lci = NodeProperties.getClassInfo(ln);
            ClassInfo rci = NodeProperties.getClassInfo(rn);
            Class lc = null;
            Class rc = null;

            if ((lci instanceof JavaClassInfo)
                    && (rci instanceof JavaClassInfo)) {
                lc = lci.getJavaClass();
                rc = rci.getJavaClass();
            } else {
                throw new ExecutionError(s, node);
            }

            if (lc == null || rc == null || lc == boolean.class
                    || rc == boolean.class || lc == void.class
                    || rc == void.class || lc == float.class
                    || rc == float.class || lc == double.class
                    || rc == double.class || !lc.isPrimitive()
                    || !rc.isPrimitive()) {
                throw new ExecutionError(s, node);
            } else if (lc == long.class) {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.LONG);
            } else {
                node.setProperty(NodeProperties.TYPE, JavaClassInfo.INT);
            }
        }

        /**
         * Check a list of node
         */
        protected void checkList(List l, String s, Node n) {
            ListIterator it = l.listIterator();
            while (it.hasNext()) {
                Object o = ((Node) it.next()).acceptVisitor(this);
                if (o != null) {
                    if (o instanceof ReferenceType) {
                        throw new ExecutionError(s, n);
                    }
                    it.set(o);
                }
            }
        }

        /**
         * Whether the given name represents a field in this context
         * @param dc the declaring class
         * @param name the field name
         */
        protected boolean fieldExists(ClassInfo dc, String name) {
            boolean result = false;
            try {
                ClassInfoUtilities.getField(dc, name);
                result = true;
            } catch (NoSuchFieldException e) {
                try {
                    ClassInfoUtilities.getOuterField(dc, name);
                    result = true;
                } catch (NoSuchFieldException ex) {
                } catch (AmbiguousFieldException ex) {
                    result = true;
                }
            } catch (AmbiguousFieldException e) {
                result = true;
            }
            return result;
        }
    }

    /**
     * To visit the members of a type declaration
     */
    protected class MembersVisitor extends VisitorObject {

        /**
         * Visits a ClassInitializer
         * @param node the node to visit
         */
        public Object visit(ClassInitializer node) {
            Iterator it = node.getBlock().getStatements().iterator();
            while (it.hasNext()) {
                addToClassInitializer((Node) it.next());
            }
            return null;
        }

        /**
         * Visits a InstanceInitializer
         * @param node the node to visit
         */
        public Object visit(InstanceInitializer node) {
            Iterator it = node.getBlock().getStatements().iterator();
            while (it.hasNext()) {
                addToInstanceInitializer((Node) it.next());
            }
            return null;
        }

        /**
         * Visits a FieldDeclaration
         * @param node the node to visit
         */
        public Object visit(FieldDeclaration node) {
            FieldInfo fi = classInfo.getField(node);
            int af = node.getAccessFlags();
            String rt = fi.getType().getName();
            String fn = node.getName();

            if (isInterface) {
                if (Modifier.isPrivate(af) || Modifier.isProtected(af)) {
                    node.setProperty(NodeProperties.ERROR_STRINGS,
                            new String[] { fn, classInfo.getName() });
                    throw new ExecutionError("interface.field.modifier", node);
                }
                af |= Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
            }

            Expression init = node.getInitializer();

            if (init != null) {
                if ((init instanceof Literal) && Modifier.isFinal(af)
                        && Modifier.isStatic(af)) {
                    if (init instanceof IntegerLiteral) {
                        Integer val = (Integer) ((Literal) init).getValue();
                        if (rt.equals("byte")) {
                            if (val.intValue() != val.byteValue()) {
                                throw new ExecutionError("invalid.constant",
                                        node);
                            }
                            classFactory.addConstantIntField(af, rt, fn, val);
                        } else if (rt.equals("short")) {
                            if (val.intValue() != val.shortValue()) {
                                throw new ExecutionError("invalid.constant",
                                        node);
                            }
                            classFactory.addConstantIntField(af, rt, fn, val);
                        } else if (rt.equals("int")) {
                            classFactory.addConstantIntField(af, rt, fn, val);
                        } else if (rt.equals("long")) {
                            classFactory.addConstantLongField(af, rt, fn,
                                    new Long(val.longValue()));
                        } else if (rt.equals("float")) {
                            classFactory.addConstantFloatField(af, rt, fn,
                                    new Float(val.floatValue()));
                        } else if (rt.equals("double")) {
                            classFactory.addConstantDoubleField(af, rt, fn,
                                    new Double(val.doubleValue()));
                        } else {
                            throw new ExecutionError("invalid.constant", node);
                        }
                    } else if (init instanceof LongLiteral) {
                        Long val = (Long) ((Literal) init).getValue();
                        if (rt.equals("long")) {
                            classFactory.addConstantLongField(af, rt, fn,
                                    new Long(val.longValue()));
                        } else if (rt.equals("float")) {
                            classFactory.addConstantFloatField(af, rt, fn,
                                    new Float(val.floatValue()));
                        } else if (rt.equals("double")) {
                            classFactory.addConstantDoubleField(af, rt, fn,
                                    new Double(val.doubleValue()));
                        } else {
                            throw new ExecutionError("invalid.constant", node);
                        }
                    } else if (init instanceof FloatLiteral) {
                        Float val = (Float) ((Literal) init).getValue();
                        if (rt.equals("float")) {
                            classFactory.addConstantFloatField(af, rt, fn,
                                    new Float(val.floatValue()));
                        } else if (rt.equals("double")) {
                            classFactory.addConstantDoubleField(af, rt, fn,
                                    new Double(val.doubleValue()));
                        } else {
                            throw new ExecutionError("invalid.constant", node);
                        }
                    } else if (init instanceof DoubleLiteral) {
                        Double val = (Double) ((Literal) init).getValue();
                        if (rt.equals("double")) {
                            classFactory.addConstantDoubleField(af, rt, fn,
                                    new Double(val.doubleValue()));
                        } else {
                            throw new ExecutionError("invalid.constant", node);
                        }

                    } else if (init instanceof BooleanLiteral) {
                        Boolean val = (Boolean) ((Literal) init).getValue();
                        if (rt.equals("boolean")) {
                            classFactory.addConstantBooleanField(af, rt, fn,
                                    val);
                        } else {
                            throw new ExecutionError("invalid.constant", node);
                        }
                    } else {
                        String val = (String) ((Literal) init).getValue();
                        classFactory.addConstantStringField(af, rt, fn, val);
                    }
                } else {
                    classFactory.addField(af & ~Modifier.FINAL, rt, fn);

                    Expression var = null;
                    if (Modifier.isStatic(af)) {
                        ReferenceType typ = new ReferenceType(classInfo
                                .getName());
                        var = new StaticFieldAccess(typ, fn);
                    } else {
                        Identifier t = new Identifier("this");
                        List l = new LinkedList();
                        l.add(t);
                        var = new QualifiedName(l);
                        var = new ObjectFieldAccess(var, fn);
                    }
                    Expression exp = new SimpleAssignExpression(var, init);
                    if (Modifier.isStatic(af)) {
                        addToClassInitializer(exp);
                    } else {
                        exp.setProperty(NodeProperties.INSTANCE_INITIALIZER,
                                null);
                        addToInstanceInitializer(exp);
                    }
                }
                if (init instanceof Literal) {
                    MCodeUtilities.write("" + Code.FIELD + Code.DELIM
                            + node.getName() + Code.DELIM
                            + fi.getType().getName() + Code.DELIM + af
                            + Code.DELIM + ((Literal) init).getValue()
                            + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                } else {
                    MCodeUtilities.write("" + Code.FIELD + Code.DELIM
                            + node.getName() + Code.DELIM
                            + fi.getType().getName() + Code.DELIM + af
                            + Code.DELIM + Code.UNKNOWN + Code.DELIM
                            + MCodeGenerator.locationToString(node));
                }

            } else {
                classFactory.addField(af, rt, fn);
                MCodeUtilities.write("" + Code.FIELD + Code.DELIM
                        + node.getName() + Code.DELIM + fi.getType().getName()
                        + Code.DELIM + af + Code.DELIM + Code.UNKNOWN
                        + Code.DELIM + MCodeGenerator.locationToString(node));
            }
            return null;
        }

        /**
         * Visits a MethodDeclaration
         * @param node the node to visit
         */
        public Object visit(MethodDeclaration node) {
            MethodInfo mi = classInfo.getMethod(node);
            int af = mi.getModifiers();
            String mn = node.getName();
            String rt = mi.getReturnType().getName();
            boolean isAbstract;

            // Check the modifiers
            if (isInterface) {
                if (Modifier.isPrivate(af) || Modifier.isProtected(af)
                        || Modifier.isFinal(af) || Modifier.isStatic(af)) {
                    node
                            .setProperty(NodeProperties.ERROR_STRINGS,
                                    new String[] { node.getName(),
                                            classInfo.getName() });
                    throw new ExecutionError("interface.method.modifier", node);
                }
                af |= Modifier.PUBLIC | Modifier.ABSTRACT;
                isAbstract = true;
            } else {
                isAbstract = Modifier.isAbstract(af);
            }
            hasAbstractMethod |= isAbstract;

            // Create the parameter array
            ClassInfo[] cia = mi.getParameterTypes();
            String[] params = new String[cia.length];
            for (int i = 0; i < cia.length; i++) {
                params[i] = cia[i].getName();
            }
            MCodeUtilities.write("" + Code.METHOD + Code.DELIM + node.getName()
                    + Code.DELIM + rt + Code.DELIM + af + Code.DELIM
                    + MCodeGenerator.arrayToString(params));

            // Create the exception array
            cia = mi.getExceptionTypes();
            String[] except = new String[cia.length];
            for (int i = 0; i < cia.length; i++) {
                except[i] = cia[i].getName();
            }

            // Create the method
            classFactory.addMethod(af, rt, mn, params, except);

            // Create the super method accessor
            if (!isInterface && !isAbstract && isRedefinedMethod(mi)) {
                classFactory.addSuperMethodAccessor(af, rt, mn, params, except);
            }

            // Check the method
            Node body = node.getBody();
            if ((isAbstract && body != null) || (isInterface && body != null)) {
                node.setProperty(NodeProperties.ERROR_STRINGS,
                        new String[] { node.getName() });
                throw new ExecutionError("abstract.method.body", node);
            }

            if (!isAbstract && body == null) {
                node.setProperty(NodeProperties.ERROR_STRINGS,
                        new String[] { node.getName() });
                throw new ExecutionError("missing.method.body", node);
            }

            // Register the body
            if (body != null) {
                // Register the method
                String sig = ClassFactory.getMethodIdentifier(classInfo
                        .getName(), mn, params, interpreter.getClassLoader()
                        .toString());
                interpreter.registerMethod(sig, node, importationManager);
            }
            return null;
        }

        /**
         * Whether the given method is a redefinition
         */
        protected boolean isRedefinedMethod(MethodInfo m) {
            ClassInfo sc = classInfo.getSuperclass();
            String name = m.getName();
            ClassInfo[] params = m.getParameterTypes();
            while (sc != null) {
                MethodInfo[] ms = sc.getMethods();
                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().equals(name)) {
                        ClassInfo[] pt = ms[i].getParameterTypes();
                        lengthTest: if (pt.length == params.length) {
                            for (int j = 0; j < pt.length; j++) {
                                if (!pt[j].equals(params[j])) {
                                    break lengthTest;
                                }
                            }
                            return true;
                        }
                    }
                }
                sc = sc.getSuperclass();
            }
            return false;
        }
    }
}