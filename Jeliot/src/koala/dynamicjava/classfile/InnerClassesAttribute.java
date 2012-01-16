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

package koala.dynamicjava.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents an inner class attribute
 *
 * @author Stephane Hillion
 * @version 1.0 - 1999/06/04
 * @see ClassFile
 */

public class InnerClassesAttribute extends AttributeInfo {
    /**
     * The classes
     */
    private List classes;

    /**
     * Creates a new innerclasses attribute
     * @param cp the constant pool
     */
    public InnerClassesAttribute(ConstantPool cp) {
	super(cp, "InnerClasses");
	length  = 2;
	classes = new LinkedList();
    }

    /**
     * Writes this attribute to the given output stream.
     */
    public void write(DataOutputStream out) throws IOException {
	out.writeShort(nameIndex);
	out.writeInt(length);
	
	out.writeShort(classes.size());

	Iterator it = classes.iterator();
	while (it.hasNext()) {
	    ((InnerClassesEntry)it.next()).write(out);
	}
    }

    /**
     * Adds an innerclasses entry to this attribute
     */
    public InnerClassesEntry addInnerClassesEntry() {
	InnerClassesEntry ice = new InnerClassesEntry(constantPool);
	classes.add(ice);
	length += 8;
	return ice;
    }
}
