/*
 * Created on 11.1.2006
 */
package jeliot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jeliot.mcode.MCodeUtilities;

public class SourceCodeUtilities {

    /**
     * The resource bundle for gui package
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    private SourceCodeUtilities() {
        super();
    }

    private static final Pattern method1 = Pattern
            .compile("\\s+static\\s+void\\s+main\\s*\\(\\s*String[^,]*\\[\\s*\\][^,]*\\)");

    private static final Pattern method2 = Pattern
            .compile("\\s+static\\s+void\\s+main\\s*\\(\\s*\\)");

    private static final Pattern class1 = Pattern.compile("\\s+class\\s+");

    private static final Pattern class2 = Pattern.compile("\\s");

    /**
     * Tries to find the main method declaration
     * from one of the classes.
     * 
     * @param programCode
     * @return
     */
    public static String findMainMethodCall(String programCode,
            boolean askForMainMethodParameters, JFrame frame,
            boolean useNullInMainMethodCall) {

        String commentsRemoved = removeComments(programCode);
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\n", " ");
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\r", " ");
        commentsRemoved = MCodeUtilities.replace(commentsRemoved, "\t", " ");
        commentsRemoved = " " + commentsRemoved;

        //System.out.println(p.pattern());
        String[] method = method1.split(commentsRemoved, 2);
        //String[] method = programCode.split("\\s+static\\s+void\\s+main\\s*\\(\\s*String[^,]*\\[\\s*\\]\\s[^,]*\\)", 2);
        if (method.length > 1 && method[1].length() > 0) {
            //System.out.println(method[0]);
            //System.out.println(method[1]);
            String[] classes = class1.split(method[0]);
            String[] classNames = class2.split(classes[classes.length - 1]);
            String className = classNames[0].replace('{', ' ');
            className = className.trim();
            if (className.length() > 0) {
                //System.out.println(className + ".main(new String[0]);");
                String methodCallStart = className + ".main(";
                String arrayStart = "new String[] {";
                String arrayEnd = "}";
                String methodCallEnd = ");";
                String parameters = "";
                if (askForMainMethodParameters) {
                    String inputValue = JOptionPane
                            .showInputDialog(
                                    frame,
                                    messageBundle
                                            .getString("dialog.ask_for_main_parameters")
                                            + " " + className, "");
                    if (inputValue != null && inputValue.length() > 0) {
                        StringTokenizer st = new StringTokenizer(inputValue
                                .trim());
                        while (st.hasMoreTokens()) {
                            parameters += "\"" + st.nextToken() + "\"";
                            parameters += (st.hasMoreTokens() ? "," : "");
                        }
                    }
                    return methodCallStart + arrayStart + parameters + arrayEnd
                            + methodCallEnd;
                } else if (useNullInMainMethodCall) {
                    return methodCallStart + "null" + methodCallEnd;
                } else {
                    return methodCallStart + arrayStart + arrayEnd
                            + methodCallEnd;
                }
            }
        }

        method = method2.split(commentsRemoved, 2);

        if (method.length > 1 && method[1].length() > 0) {
            //System.out.println(method[0]);
            //System.out.println(method[1]);
            String[] classes = class1.split(method[0]);
            String[] classNames = class2.split(classes[classes.length - 1]);
            String className = classNames[0].replace('{', ' ');
            className = className.trim();
            if (className.length() > 0) {
                //System.out.println(className + ".main();");
                return className + ".main();";
            }
        }
        return null;
    }

    private static final String COMMENT_ML_START = "/*";

    private static final String COMMENT_ML_END = "*/";

    private static final String COMMENT_SL_START = "//";

    private static final String SPACE = " ";

    /**
     * Removes the comments from the source code.
     * 
     * @param programCode
     *            the source code
     * @return the source code without comments
     */
    public static String removeComments(String programCode) {

        if (programCode == null) {
            return null;
        }

        programCode = programCode + "\n";

        StringBuffer output = new StringBuffer();
        BufferedReader in = new BufferedReader(new StringReader(programCode));
        boolean inMultiLine = false;

        try {
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                boolean lineCanStillContainComments = true;
                while (lineCanStillContainComments) {

                    lineCanStillContainComments = false;

                    if (!inMultiLine) {
                        int mlcstart = line.indexOf(COMMENT_ML_START);
                        int cstart = line.indexOf(COMMENT_SL_START);
                        if (cstart >= 0 && (mlcstart < 0 || cstart < mlcstart)) {
                            line = line.substring(0, cstart);
                        }
                    }

                    if (!inMultiLine) {

                        // We are not in a multi-line comment, check for a start
                        int cstart = line.indexOf(COMMENT_ML_START);
                        if (cstart >= 0) {
                            // This could be a MLC on one line ...
                            int cend = line.indexOf(COMMENT_ML_END, cstart
                                    + COMMENT_ML_START.length());
                            if (cend >= 0) {
                                // A comment that starts and ends on one line
                                line = line.substring(0, cstart)
                                        + SPACE
                                        + line.substring(cend
                                                + COMMENT_ML_END.length());
                                lineCanStillContainComments = true;
                            } else {
                                // A real multi-line comment
                                inMultiLine = true;
                                line = line.substring(0, cstart) + SPACE;
                            }
                        } else {
                            // We are not in a multi line comment and we haven't
                            // started one so we are going to ignore closing
                            // comments even if they exist.
                        }
                    } else {
                        // We are in a multi-line comment, check for the end
                        int cend = line.indexOf(COMMENT_ML_END);
                        if (cend >= 0) {
                            // End of comment
                            line = line.substring(cend
                                    + COMMENT_ML_END.length());
                            inMultiLine = false;
                            lineCanStillContainComments = true;
                        } else {
                            // The comment continues
                            line = SPACE;
                        }
                    }
                }

                output.append(line);
                output.append('\n');

            }

        } catch (IOException ex) {
            throw new RuntimeException("IOExecption unexpected."); //$NON-NLS-1$
        }

        return output.toString();

        /*
         int index = programCode.indexOf(beginningComment);

         while (index > -1) {
         int endIndex = programCode.indexOf(endingComment, index);
         programCode = programCode.substring(0, index)
         + programCode.substring(endIndex, programCode.length());
         index = programCode.indexOf(beginningComment);
         }

         index = programCode.indexOf(lineComment);

         while (index > -1) {
         int endIndex = programCode.indexOf('\n', index);
         programCode = programCode.substring(0, index)
         + programCode.substring(endIndex, programCode.length());
         index = programCode.indexOf(lineComment);
         }

         return programCode;
         */
    }

    public static String convertNative2Ascii(String nativeString) {
        if (DebugUtil.DEBUGGING) {
            System.out.println("Converting Native to Ascii");
        }
        File tempDir = new File(Util.createUserPath(), "temp" + File.separator);
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        
        File tempFile = null;
        File tempFileAscii = null;
        do {
            tempFile = new File(tempDir, "tempFile"
                    + (int) (Math.random() * 10000) + ".txt");
        } while (tempFile == null && tempFile.exists());
        OutputStreamWriter w = null;
        try {
            w = new OutputStreamWriter(new FileOutputStream(
                    tempFile), Charset.forName("UTF-8"));
            w.write(nativeString);
            w.flush();
            w.close();
            
            Process process = Runtime.getRuntime().exec("native2ascii -encoding UTF-8 \"" + tempFile.getAbsolutePath() + "\" \"" + tempFile.getAbsolutePath() + ".asc\"");
            int returnValue = process.waitFor();
            tempFileAscii = new File(tempFile.getAbsolutePath() + ".asc");
            if (DebugUtil.DEBUGGING) {
                System.out.println("return value " + returnValue);
            }
            if (DebugUtil.DEBUGGING) {
                System.out.println("Ascii temp file exists: " + tempFileAscii.exists());
            }

            if (returnValue == 0 && tempFileAscii.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(tempFileAscii));
                String str = null;
                StringBuffer sb = new StringBuffer();
                while ((str = in.readLine()) != null) {
                    sb.append(str);
                    sb.append("\n");
                }                    
                in.close();
                
                if (DebugUtil.DEBUGGING) {
                    System.out.println("Converted Native to Ascii");
                }
                
                return sb.toString().trim();
            }
        } catch (IOException e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            if (DebugUtil.DEBUGGING) {
                e.printStackTrace();
            }
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            if (tempFileAscii != null && tempFileAscii.exists()) {
                tempFileAscii.delete();
            }
        }
        if (DebugUtil.DEBUGGING) {
            System.out.println("Error in convertion from Native to Ascii");
        }
        return nativeString;
    }
}
