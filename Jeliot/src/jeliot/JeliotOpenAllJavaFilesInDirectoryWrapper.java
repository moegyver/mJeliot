/*
 * Created on 29.4.2006
 */
package jeliot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jeliot.gui.JavaFileFilter;

/**
 * @author nmyller
 */
public class JeliotOpenAllJavaFilesInDirectoryWrapper {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            File directory = new File(args[0]);
            if (directory.exists() && directory.isDirectory()) {
                File[] javaFiles = directory.listFiles(new JavaFileFilter());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < javaFiles.length; i++) {
                    if (javaFiles[i].isFile()) {
                        sb.append("//");
                        sb.append(javaFiles[i].getName());
                        sb.append("\n");
                        sb.append("\n");
                        BufferedReader in = null;
                        try {
                            in = new BufferedReader(
                                    new FileReader(javaFiles[i]));
                            String str = null;
                            while ((str = in.readLine()) != null) {
                                sb.append(str);
                                sb.append("\n");
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (in != null) {
                                    in.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        sb.append("\n");
                        sb.append("\n");
                    }
                }
                Jeliot.start(new String[] { sb.toString() });
            }
        }
    }
}
