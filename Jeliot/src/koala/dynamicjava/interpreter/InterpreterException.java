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

import jeliot.mcode.*;
import koala.dynamicjava.interpreter.error.CatchedExceptionError;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.interpreter.throwable.ThrownException;
import koala.dynamicjava.parser.wrapper.ParseError;
import koala.dynamicjava.tree.Node;

/**
 * This exception is thrown when an error append while
 * interpreting a statement
 *
 * @author Stephane Hillion
 * @version 1.0 - 1999/11/14
 */

public class InterpreterException extends ThrownException {
    /**
     * The source code information
     */
    protected SourceInformation sourceInformation;

    /**
     * The detailed message
     */
    protected String message;

    /**
     * Constructs an <code>InterpreterException</code> from a ParseError
     */
    public InterpreterException(ParseError e) {
        super(e);

        String m = e.getMessage();

        m = MCodeUtilities.replace(m, "<", "&lt;");
        m = MCodeUtilities.replace(m, ">", "&gt;");

        m = MCodeUtilities.replace(m,"\n","<BR>");
        m = MCodeUtilities.replace(m,"\r","");

        if (e.getLine() != -1) {
            sourceInformation = new SourceInformation(e.getFilename(),
                                                      e.getLine(),
                                                      e.getColumn());

            message = "<H2>Syntax Error</H2><P><B>Line " + e.getLine() +
                      ", Column " + e.getColumn() + ":</P><P>" + m + "</B></P>";

        } else {

            message = "<H2>Syntax Error</H2><P>" + m + "</P>";

            int line = 0;
            int column = 0;
            String file = "buffer";

            line = MCodeUtilities.findNumber(m, "line");
            column = MCodeUtilities.findNumber(m, "column");

            //System.out.println(message);
            sourceInformation = new SourceInformation(file,
                                                      line,
                                                      column);
        }
    }

    /**
     * Constructs an <code>InterpreterException</code> from a ExecutionError
     */
    public InterpreterException(ExecutionError e) {

        super(e);

        boolean sourceGot = false;
        Node n = e.getNode();

        if (n != null && n.getFilename() != null) {
            message = "<H2>Execution Error</H2><P><B>Line "+
            n.getBeginLine()+", Column "+n.getBeginColumn()+
            ":</B></P>";

            sourceInformation = new SourceInformation(n.getFilename(),
                                                      n.getBeginLine(),
                                                      n.getBeginColumn());
            sourceGot = true;
        } else {

            message = "<H2>Execution Error</H2>";

        }

        if (e instanceof CatchedExceptionError) {

            String m = ((CatchedExceptionError)e).getException().toString();

            m = MCodeUtilities.replace(m, "<", "&lt;");
            m = MCodeUtilities.replace(m, ">", "&gt;");

            m = MCodeUtilities.replace(m,"\n","<BR>");
            m = MCodeUtilities.replace(m,"\r","");

            message += "<P>" + m + "</P>";

            if (!sourceGot) {

                int line = 0;
                int column = 0;
                String file = "buffer";

                line = MCodeUtilities.findNumber(m, "line");
                column = MCodeUtilities.findNumber(m, "column");

                //System.out.println(message);
                sourceInformation = new SourceInformation(file,
                                                      line,
                                                      column);
            }

        } else if (e instanceof ThrownException) {

            String m = ((ThrownException)e).getException().toString();

            m = MCodeUtilities.replace(m, "<", "&lt;");
            m = MCodeUtilities.replace(m, ">", "&gt;");

            m = MCodeUtilities.replace(m,"\n","<BR>");
            m = MCodeUtilities.replace(m,"\r","");

            message += "<P>" + m + "</P>";

            if (!sourceGot) {

                int line = 0;
                int column = 0;
                String file = "buffer";

                line = MCodeUtilities.findNumber(m, "line");
                column = MCodeUtilities.findNumber(m, "column");

                //System.out.println(message);
                sourceInformation = new SourceInformation(file,
                                                          line,
                                                          column);
            }
        } else {

            String m = e.getMessage();

            m = MCodeUtilities.replace(m, "<", "&lt;");
            m = MCodeUtilities.replace(m, ">", "&gt;");

            m = MCodeUtilities.replace(m,"\n","<BR>");
            m = MCodeUtilities.replace(m,"\r","");

            message += "<P>" + m + "</P>";

            if (!sourceGot) {

                int line = 0;
                int column = 0;
                String file = "buffer";

                line = MCodeUtilities.findNumber(m, "line");
                column = MCodeUtilities.findNumber(m, "column");

                //System.out.println(message);
                sourceInformation = new SourceInformation(file,
                                                          line,
                                                          column);
            }
        }
    }

    public Throwable getError() {
        return thrown;
    }

    /**
     * Returns the source code information if available, or null
     */
    public SourceInformation getSourceInformation() {
        return sourceInformation;
    }

    /**
     * Returns the detailed message
     */
    public String getMessage() {
        return message;
    }

    /**
     * To represent the source code informations
     */
    public static class SourceInformation {
        // The fields
        private String filename;
        private int    line;
        private int    column;

        /**
        * Creates a source information
        */
        public SourceInformation(String filename, int line, int column) {
            this.filename = filename;
            this.line     = line;
            this.column   = column;
        }

        /**
        * Returns the filename
        */
        public String getFilename() {
            return filename;
        }

        /**
        * Returns the line where the error occurs
        */
        public int getLine() {
            return line;
        }

        /**
         * Returns the column where the error occurs
         */
        public int getColumn() {
            return column;
        }
    }
}
