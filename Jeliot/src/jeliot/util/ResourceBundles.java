/*
 * Created on 6.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * @author Niko Myller
 */
public class ResourceBundles {

    /**
     * Comment for <code>callTreeProperties</code>
     */
    private static UserProperties callTreeProperties;

    /**
     * Comment for <code>guiProperties</code>
     */
    private static UserProperties guiProperties;

    /**
     * Comment for <code>mCodeProperties</code>
     */
    private static UserProperties mCodeProperties;

    /**
     * Comment for <code>theaterProperties</code>
     */
    private static UserProperties theaterProperties;

    /**
     * Comment for <code>jeliotUserProperties</code>
     */
    private static UserProperties jeliotUserProperties;
    
    /**
     * Comment for <code>guiMessages</code>
     */
    private static ResourceBundle guiMessages;

    /**
     * Comment for <code>mCodeMessages</code>
     */
    private static ResourceBundle mCodeMessages;

    /**
     * Comment for <code>theaterMessages</code>
     */
    private static ResourceBundle theaterMessages;

    /**
     * 
     */
    private static ResourceBundle avInteractionMessages;
    
    /**
     * User Modelling Resources
     */
    
    private static UserProperties internalUM;
    
    /**
     * @return
     */
    public static UserProperties getJeliotUserProperties() {
        if (jeliotUserProperties == null) {
            jeliotUserProperties = new UserProperties("jeliot",
                    null);
        }

        return jeliotUserProperties;
    }
    
    /**
     * @return
     */
    public static UserProperties getCallTreeUserProperties() {
        if (callTreeProperties == null) {
            callTreeProperties = new UserProperties("calltree",
                    "jeliot.calltree.resources.properties");
        }

        return callTreeProperties;
    }

    /**
     * @return
     */
    public static UserProperties getGuiUserProperties() {
        if (guiProperties == null) {
            guiProperties = new UserProperties("gui",
                    "jeliot.gui.resources.properties");
        }

        return guiProperties;
    }

    /**
     * @return
     */
    public static UserProperties getMCodeUserProperties() {
        if (mCodeProperties == null) {
            mCodeProperties = new UserProperties("mcode",
                    "jeliot.mcode.resources.properties");
        }

        return mCodeProperties;
    }

    /**
     * @return
     */
    public static UserProperties getTheaterUserProperties() {
        if (theaterProperties == null) {
            theaterProperties = new UserProperties("theater",
                    "jeliot.theater.resources.properties");
        }

        return theaterProperties;
    }

    /**
     * @return
     */
    public static ResourceBundle getGuiMessageResourceBundle() {
        if (guiMessages == null) {
            guiMessages = ResourceBundle.getBundle(
                    "jeliot.gui.resources.messages", Locale.getDefault());
        }

        return guiMessages;
    }

    /**
     * @return
     */
    public static ResourceBundle getMCodeMessageResourceBundle() {
        if (mCodeMessages == null) {
            mCodeMessages = ResourceBundle.getBundle(
                    "jeliot.mcode.resources.messages", Locale.getDefault());
        }

        return mCodeMessages;
    }

    /**
     * @return
     */
    public static ResourceBundle getTheaterMessageResourceBundle() {
        if (theaterMessages == null) {
            theaterMessages = ResourceBundle.getBundle(
                    "jeliot.theater.resources.messages", Locale.getDefault());
        }

        return theaterMessages;
    }

    /**
     * 
     */
    public static void saveCallTreeUserProperties() {
        if (callTreeProperties != null) {
            callTreeProperties.save();
        }
    }
    
    /**
     * 
     */
    public static void saveJeliotUserProperties() {
        if (jeliotUserProperties != null) {
            jeliotUserProperties.save();
        }
    }

    /**
     * 
     */
    public static void saveGuiUserProperties() {
        if (guiProperties != null) {
            guiProperties.save();
        }
    }

    /**
     * 
     */
    public static void saveMCodeUserProperties() {
        if (mCodeProperties != null) {
            mCodeProperties.save();
        }
    }

    /**
     * 
     */
    public static void saveTheaterUserProperties() {
        if (theaterProperties != null) {
            theaterProperties.save();
        }
    }
    /**
     * 
     */
    public static void saveProgrammingConceptsProperties() {
        if (internalUM != null) {
            internalUM.save();
        }
    }
    /**
     * 
     */
    public static void saveAllUserProperties() {
        saveCallTreeUserProperties();
        saveGuiUserProperties();
        saveMCodeUserProperties();
        saveTheaterUserProperties();
        saveJeliotUserProperties();
        saveProgrammingConceptsProperties();
    }

    public static ResourceBundle getAvInteractionResourceBundle() {
        if (avInteractionMessages == null) {
            avInteractionMessages = ResourceBundle.getBundle(
                    "jeliot.mcode.resources.questions", Locale.getDefault());
        }

        return avInteractionMessages;
    }
    
    public static UserProperties getUserModelConceptsProperties() {
        if (internalUM == null) {
        	internalUM = new UserProperties("internalUM",
                    "jeliot.adapt.internalUM.properties");
        }

        return internalUM;
    }
}