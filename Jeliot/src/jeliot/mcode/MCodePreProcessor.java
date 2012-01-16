/*
 * Created on 23.10.2006
 */
package jeliot.mcode;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * It is made sure that all the registered MCodePreProcessors will execute their interpretation before
 * the primary MCInterpreter is processing the same line of MCode thus the implementations of this interface
 * <b>should not</b> do any time consuming computations. 
 * 
 * @author nmyller
 */
public interface MCodePreProcessor {

    public PrintWriter getMCodeInputWriter();
    
    public BufferedReader getMCodeOutputReader();
    
    public void closeMCodeOutputReader();
    
}
