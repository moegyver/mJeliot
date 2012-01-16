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

package koala.dynamicjava.interpreter.modifier;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import jeliot.mcode.Code;
import jeliot.mcode.MCodeGenerator;
import jeliot.mcode.MCodeUtilities;
import koala.dynamicjava.interpreter.EvaluationVisitor;
import koala.dynamicjava.interpreter.NodeProperties;
import koala.dynamicjava.interpreter.context.Context;
import koala.dynamicjava.interpreter.error.CatchedExceptionError;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.ObjectFieldAccess;
import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This interface represents the objets that modify an object field
 *
 * @author Stephane Hillion
 * @version 1.1 - 1999/11/28
 */

public class ObjectFieldModifier extends LeftHandSideModifier {
    /**
     * The field
     */
    protected Field field;

    /**
     * The node
     */
    protected ObjectFieldAccess node;

    /**
     * The field
     */
    protected Object fieldObject;

    /**
     * The list used to manage recursive calls
     */
    protected List fields = new LinkedList();

    /**
     * Creates a new field modifier
     * @param f the field to modify
     * @param n the field access node
     */
    public ObjectFieldModifier(Field f, ObjectFieldAccess n) {
        field = f;
        node = n;
    }

    /**
     * Prepares the modifier for modification
     */
    public Object prepare(Visitor v, Context ctx) {
        fields.add(0, fieldObject);

        if (v instanceof EvaluationVisitor) {
            EvaluationVisitor ve = (EvaluationVisitor) v;

            Class c = NodeProperties.getType(node.getExpression());

            long fieldCounter = EvaluationVisitor.getCounter();
            EvaluationVisitor.incrementCounter();

            long objectCounter = EvaluationVisitor.getCounter();

            // Evaluate the object
            Object obj = node.getExpression().acceptVisitor(v);
            Object value;
            Field f = (Field) node.getProperty(NodeProperties.FIELD);
            // Relax the protection for members
            if (ctx.getAccessible()) {
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
            
            fieldObject = obj;
            
            return value;

        } else {
            fieldObject = node.getExpression().acceptVisitor(v);
            try {
                return field.get(fieldObject);
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }
        }
    }

    /**
     * Sets the value of the underlying left hand side expression
     */
    public void modify(Context ctx, Object value) {
        try {
            field.set(fieldObject, value);
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        } finally {
            fieldObject = fields.remove(0);
        }
    }
}
