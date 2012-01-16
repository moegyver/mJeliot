/*
 * Created on 25.4.2007
 */
package jeliot.lang;

import jeliot.theater.InstanceActor;
import jeliot.theater.StringObjectActor;

/**
 * @author nmyller
 */
public class StringInstance extends Instance {

    private StringObjectActor actor;

    private Value stringValue;

    /**
     * @param hashCode
     */
    public StringInstance(String hashCode) {
        super(hashCode);
    }

    /**
     * @param hashCode
     * @param type
     */
    public StringInstance(String hashCode, String type) {
        super(hashCode, type);
    }
    
    public StringInstance(String hashCode, String type, Value value) {
        super(hashCode, type);
        setStringValue(value);
    }

    public StringObjectActor getStringObjectActor() {
        return actor;
    }

    public InstanceActor getActor() {
        return actor;
    }
    
    public void setActor(StringObjectActor actor) {
        this.actor = actor;
    }

    public Value getStringValue() {
        return stringValue;
    }

    public void setStringValue(Value stringValue) {
        this.stringValue = stringValue;
    }

}
