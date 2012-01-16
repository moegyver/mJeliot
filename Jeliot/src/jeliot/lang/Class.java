package jeliot.lang;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import jeliot.theater.ClassActor;


/**
 * @author nmyller
 */
public class Class {
    
    /**
     * 
     */
    private LinkedList vars = new LinkedList();
    
    /**
     * 
     */
    private String name; 
    
    /**
     * 
     */
    private ClassActor classActor;
    
    /**
     * 
     * @param name
     */
    public Class(String name) {
        this.name = name;
    }

    /**
     * 
     * @param var
     */
    public void declareVariable(Variable var) {
        vars.addLast(var);
    }
    
    /**
     * 
     * @param varName
     * @return
     */
    public Variable getVariable(String varName) {
        Iterator i = vars.iterator();
        while (i.hasNext()) {
            Variable var = (Variable) i.next();
            
            if (var.getName().equals(varName)) {
                return var;
            }
        }
        throw new StaticVariableNotFoundException("Static variable " + varName + " was not found from the class " + this.name + ".");
    }
    
    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Returns the classActor.
     */
    public ClassActor getClassActor() {
        return classActor;
    }
    /**
     * @param classActor The classActor to set.
     */
    public void setClassActor(ClassActor classActor) {
        this.classActor = classActor;
    }
    
    /**
     * @return
     */
    public int getVariableCount() {
        return vars.size();
    }

    /**
     * @return
     */
    public ListIterator getVariables() {
        return vars.listIterator();
    }
    
}
