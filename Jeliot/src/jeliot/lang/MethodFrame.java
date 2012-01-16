package jeliot.lang;

import java.util.Stack;

import jeliot.theater.*;

/**
  * MethodFrame is an instance of a method under execution.
  * A method frame is created runtime each time a method is called.
  *
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class MethodFrame {

//  DOC: document!

    /**
	 *
	 */
	private MethodStage stage;
    /**
	 *
	 */
	//private int depth;

    private Stack vars;
    /**
	 *
	 */
	private int vcount = 0;

    /**
	 *
	 */
	private String name;

    /**
	 * @param name
	 */
	public MethodFrame(String name) {
        this.name = name;
        vars = new Stack();
    }

    /**
	 * @param var
	 * @return
	 */
	//public PMethod getMethod() {
        //return method;
    //}

    public Variable declareVariable(Variable var) {
        vcount++;
        vars.push(var);
        return var;
    }

    /**
	 * @param name
	 * @return
	 */
	public Variable getVariable(String name) {
        int size = vars.size();
        for (int i = 0; i < size; ++i) {
            if (vars.elementAt(i) != null) {
                if (((Variable) vars.elementAt(i)).getName().equals(name)) {
                    return (Variable) vars.elementAt(i);
                }
            }
        }
        throw new LocalVariableNotFoundException("There is no local variable " + name);
    }

    /**
	 * 
	 */
	public void openScope() {
        vars.push(null);
        stage.openScope();
    }

    /**
	 * 
	 */
	public void closeScope() {

        while (vars.pop() != null);
        stage.closeScope();
    }

    /**
	 * @return
	 */
	public String getMethodName() {
        return name;
    }

    /**
	 * @return
	 */
	public int getVarCount() {
        return vcount;
    }

    /**
	 * @return
	 */
	public MethodStage getMethodStage() {
        return stage;
    }

    /**
	 * @param stage
	 */
	public void setMethodStage(MethodStage stage) {
        this.stage = stage;
    }
}
