/*
 * Created on 6.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 
 * @author Niko Myller
 */
public class UserProperties {

    /**
     * Comment for <code>userProperties</code>
     */
    Properties userProperties;

    /**
     * Comment for <code>userPropertiesFile</code>
     */
    File userPropertiesFile;

    /**
     * Comment for <code>userPropertiesName</code>
     */
    String userPropertiesName;

    /**
     * @param userPropertiesName
     * @param defaultPropertiesName
     */
    public UserProperties(String userPropertiesName, String defaultPropertiesName) {
        String temp = defaultPropertiesName;
        this.userPropertiesName = userPropertiesName;
        Properties defaultProperties = null;
        File userPath = Util.createUserPath();

        if (defaultPropertiesName != null) {
            String defaultFileSeparator = System.getProperty("file.separator");
            defaultPropertiesName = defaultPropertiesName.replace('.', defaultFileSeparator
                    .charAt(0));
            defaultPropertiesName += ".properties";
            //DebugUtil.printDebugInfo(defaultPropertiesName);
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                    defaultPropertiesName);
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        defaultPropertiesName);
            }
            if (is == null) {
                is = UserProperties.class.getClassLoader().getResourceAsStream(
                        defaultPropertiesName);
            }
            defaultProperties = new Properties();
            try {
                if (is == null) {
                    //This is a hack because even though the resource is found in Eclipse it is not
                    //found in command prompt for some mysterious reason.
                    ResourceBundle rb = null;
                    try {
                        rb = ResourceBundle.getBundle(temp, Locale.getDefault(), this.getClass()
                                .getClassLoader());
                    } catch (Exception e) {
                        try {
                            rb = ResourceBundle.getBundle(temp, Locale.getDefault(), Thread
                                    .currentThread().getContextClassLoader());
                        } catch (Exception e1) {
                            DebugUtil.handleThrowable(e1);
                        }
                    }
                    Enumeration e = rb.getKeys();
                    while (e.hasMoreElements()) {
                        String str = (String) e.nextElement();
                        //System.out.println(str + "=" + rb.getString(str));
                        defaultProperties.setProperty(str, rb.getString(str));
                    }
                } else {
                    defaultProperties.load(is);
                }
            } catch (Exception e) {
                DebugUtil.handleThrowable(e);
            }
        }

        this.userPropertiesFile = new File(userPath, userPropertiesName + ".properties");
        if (defaultProperties != null) {
            userProperties = new Properties(defaultProperties);
        } else {
            userProperties = new Properties();
        }

        if (userPropertiesFile.exists()) {
            try {
                userProperties.load(new FileInputStream(this.userPropertiesFile));
            } catch (FileNotFoundException e) {
                DebugUtil.handleThrowable(e);
            } catch (IOException e) {
                DebugUtil.handleThrowable(e);
            }
        }
    }

    /**
     * 
     */
    public void save() {
        try {
            if (!userPropertiesFile.exists()) {
                userPropertiesFile.createNewFile();
            }
            userProperties.store(new FileOutputStream(userPropertiesFile), "User Properties for "
                    + userPropertiesName);
        } catch (FileNotFoundException e) {
            DebugUtil.handleThrowable(e);
        } catch (IOException e) {
            DebugUtil.handleThrowable(e);
        }
    }

    /**
     * @param key
     * @return
     */
    public String getStringProperty(String key) {
        return userProperties.getProperty(key);
    }

    /**
     * @param key
     * @param value
     */
    public void setStringProperty(String key, String value) {
        userProperties.setProperty(key, value);
    }

    /**
     * @param s
     * @return
     */
    public boolean getBooleanProperty(String s) {
        return Boolean.valueOf(userProperties.getProperty(s)).booleanValue();
    }

    /**
     * @param s
     * @param newValue
     */
    public void setBooleanProperty(String s, boolean newValue) {
        userProperties.setProperty(s, Boolean.toString(newValue));
    }

    /**
     * @param s
     * @return
     */
    public int getIntegerProperty(String s) {
        return Integer.valueOf(userProperties.getProperty(s)).intValue();
    }

    /**
     * @param s
     * @param newValue
     */
    public void setIntegerProperty(String s, int newValue) {
        userProperties.setProperty(s, Integer.toString(newValue));
    }

    /**
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        return userProperties.containsKey(key);
    }

    /**
     * @param key
     * @param newValue
     */
    public void setColorProperty(String key, Color newValue) {
        userProperties.setProperty(key, Integer.toHexString(newValue.getRGB()));
    }

    /**
     * @param key
     * @return
     */
    public Color getColorProperty(String key) {
        return new Color(Integer.decode(userProperties.getProperty(key)).intValue());
    }

    /**
     * @param key
     * @param newValue
     */
    public void setFontProperty(String key, Font newValue) {
        userProperties.setProperty(key + ".family", newValue.getFamily());
        setIntegerProperty(key + ".style", newValue.getStyle());
        setIntegerProperty(key + ".size", newValue.getSize());
    }

    /**
     * @param key
     * @return
     */
    public Font getFontProperty(String key) {
        String family = userProperties.getProperty(key + ".family");
        int style = Font.PLAIN;
        try {
            style = getIntegerProperty(key + ".style");
        } catch (Exception e) {
        }
        int size = getIntegerProperty(key + ".size");

        return new Font(family, style, size);
    }

}