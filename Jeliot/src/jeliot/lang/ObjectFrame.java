package jeliot.lang;

import java.util.Hashtable;

import jeliot.theater.*;

/**
 * ObjectFrame represents an instance of a class that is created at
 * run-time. 
 *
 * @author Niko Myller
 */
public class ObjectFrame extends Instance {

    //DOC: document!

    /**
	 *
	 */
	private ObjectStage stage;

    /**
	 *
	 */
	private Hashtable vars;
    
    /**
	 *
	 */
	private int vcount = 0;

    /**
	 *
	 */
	private String name;

    /**
	 * @param hashCode
	 * @param type
	 * @param vcount
	 */
	public ObjectFrame(String hashCode, String type, int vcount) {
        super(hashCode, type);

        //Name can be changed to something else.
        this.name = type;
        this.vcount = vcount;
        vars = new Hashtable();
    }

    /**
	 * @param var
	 * @return
	 */
	public Variable declareVariable(Variable var) {
        vars.put(var.getName(), var);
        return var;
    }

    /**
	 * @param name
	 * @return
	 */
	public Variable getVariable(String name) {
        Variable var = (Variable) vars.get(name);
        if (var != null) {
            return var;
        }
        //throw new RuntimeException("No Variable " + name);
        return null;
    }

    /**
	 * @return
	 */
	public String getObjectName() {
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
	public ObjectStage getObjectStage() {
        return stage;
    }

    /**
	 * @param stage
	 */
	public void setObjectStage(ObjectStage stage) {
        this.stage = stage;
        setActor(stage);
    }
}
