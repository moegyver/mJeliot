package jeliot.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import jeliot.mcode.Code;

/**
 * Object of this class contains information about a single Java class.
 * It contains all the fields, methods and constructors of the class. 
 * 
 * @author Niko Myller
 */
public class ClassInfo {

    //DOC: document!

    /**
     *
     */
    private Hashtable methods;

    /**
     *
     */
    private Hashtable fields;

    /**
     *
     */
    private List fieldNamesInDeclarationOrder;

    /**
     *
     */
    private Hashtable constructors;

    /**
     *
     */
    private String extendedClass;

    /**
     *
     */
    private String name;

    private int nonStaticFieldsCount = -1;

    private int staticFieldsCount = -1;

    /**
     * @param name
     */
    public ClassInfo(String name) {
        this.name = name;
        this.methods = new Hashtable();
        this.fields = new Hashtable();
        this.fieldNamesInDeclarationOrder = new LinkedList();
        this.constructors = new Hashtable();
    }

    /**
     * @param declaredClass
     */
    public ClassInfo(java.lang.Class declaredClass) {
        this(declaredClass.getName());
        try {
            setDeclaredConstructors(declaredClass.getDeclaredConstructors());
        } catch (Exception e) {
            this.constructors = new Hashtable();
            setDeclaredConstructors(declaredClass.getConstructors());
        }

        try {
            setDeclaredFields(declaredClass.getDeclaredFields());
        } catch (Exception e) {
            this.fields = new Hashtable();
            setDeclaredFields(declaredClass.getFields());
        }

        try {
            setDeclaredMethods(declaredClass.getDeclaredMethods());
        } catch (Exception e) {
            this.methods = new Hashtable();
            setDeclaredMethods(declaredClass.getMethods());
        }
    }

    /**
     * @param constructors
     */
    public void setDeclaredConstructors(Constructor[] constructors) {
        int n = constructors.length;
        for (int i = 0; i < n; i++) {

            java.lang.Class[] classes = constructors[i].getParameterTypes();
            String typeList = "";
            int m = classes.length;
            for (int j = 0; j < m; j++) {
                typeList += classes[j].getName();
                if (j != (m - 1)) {
                    typeList += ",";
                }
            }

            declareConstructor(constructors[i].getName() + Code.DELIM
                    + typeList, "");
        }
    }

    /**
     * @param fields
     */
    public void setDeclaredFields(Field[] fields) {
        int n = fields.length;
        for (int i = 0; i < n; i++) {

            String name = fields[i].getName();
            String type = fields[i].getType().getName();
            int modifiers = fields[i].getModifiers();
            String value = Code.UNKNOWN;

            declareField(name, "" + modifiers + Code.DELIM + type + Code.DELIM
                    + value);
        }
    }

    /**
     * @param methods
     */
    public void setDeclaredMethods(Method[] methods) {

        int n = methods.length;
        for (int i = 0; i < n; i++) {

            String name = methods[i].getName();
            String returnType = methods[i].getReturnType().getName();
            int modifiers = methods[i].getModifiers();
            java.lang.Class[] classes = methods[i].getParameterTypes();
            String typeList = "";
            int m = classes.length;
            for (int j = 0; j < m; j++) {
                typeList += classes[j].getName();
                if (j != (m - 1)) {
                    typeList += ",";
                }
            }

            declareMethod(name + Code.DELIM + typeList, "" + modifiers
                    + Code.DELIM + returnType);
        }
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param key
     * @param info
     */
    public void declareMethod(String key, String info) {
        methods.put(key, info);
    }

    /**
     * @param key
     * @param info
     */
    public void declareField(String key, String info) {
        fields.put(key, info);
        fieldNamesInDeclarationOrder.add(key);
    }

    /**
     * @param key
     * @param info
     */
    public void declareConstructor(String key, String info) {
        constructors.put(key, info);
    }

    /**
     * @param key
     * @return
     */
    public String getMethodInfo(String key) {
        return (String) methods.get(key);
    }

    /**
     * @param key
     * @return
     */
    public String getFieldInfo(String key) {
        return (String) fields.get(key);
    }

    /**
     * @param key
     * @return
     */
    public String getConstructorInfo(String key) {
        return (String) constructors.get(key);
    }

    /**
     * @return
     */
    public Hashtable getMethods() {
        return methods;
    }

    /**
     * @return
     */
    public Hashtable getFields() {
        return fields;
    }

    /**
     * @return
     */
    public Hashtable getConstructors() {
        return constructors;
    }

    /**
     * @return
     */
    public int getStaticFieldAmount() {
        if (this.staticFieldsCount < 0) {
            int size = 0;
            for (Iterator i = fields.entrySet().iterator(); i.hasNext();) {
                Entry entry = (Map.Entry) i.next();
                String fieldName = (String) entry.getKey();
                String info = (String) entry.getValue();
                StringTokenizer st = new StringTokenizer(info, Code.DELIM);
                String mods = st.nextToken();
                if (Modifier.isStatic(Integer.parseInt(mods))
                        && fieldName.indexOf("$") < 0) {
                    size++;
                }
            }
            this.staticFieldsCount = size;
        }
        return this.staticFieldsCount;
    }

    public int getNonStaticFieldsAmount() {
        if (this.nonStaticFieldsCount < 0) {
            int size = 0;
            for (Iterator i = fields.entrySet().iterator(); i.hasNext();) {
                Entry entry = (Map.Entry) i.next();
                String fieldName = (String) entry.getKey();
                String info = (String) entry.getValue();
                StringTokenizer st = new StringTokenizer(info, Code.DELIM);
                String mods = st.nextToken();
                if (!Modifier.isStatic(Integer.parseInt(mods))
                        && fieldName.indexOf("$") < 0) {
                    size++;
                }
            }
            this.nonStaticFieldsCount = size;
        }
        return this.nonStaticFieldsCount;
    }

    /**
     * @param ci
     */
    public void extendClass(ClassInfo ci) {

        extendedClass = ci.getName();

        //Firstly the fields
        Hashtable hf = ci.getFields();
        //Enumeration enum = hf.keys();
        ListIterator i = ci.getFieldNamesInDeclarationOrder().listIterator();

        //while (enum.hasMoreElements()) {
        while (i.hasNext()) {
            //String name = (String) enum.nextElement();
            String name = (String) i.next();
            String info = (String) hf.get(name);
            if (name != null && info != null) {
                declareField(name, info + Code.DELIM + "<E>");
            }
        }

        //Secondly the methods
        Hashtable hm = ci.getMethods();
        Enumeration enumeration = hm.keys();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String info = (String) hm.get(name);
            if (name != null && info != null) {
                declareMethod(name, info + Code.DELIM + "<E>");
            }
        }
    }

    /**
     * @return Returns the fieldNamesInDeclarationOrder.
     */
    public List getFieldNamesInDeclarationOrder() {
        return fieldNamesInDeclarationOrder;
    }

    /**
     * @param fieldNamesInDeclarationOrder The fieldNamesInDeclarationOrder to set.
     */
    public void setFieldNamesInDeclarationOrder(
            List fieldNamesInDeclarationOrder) {
        this.fieldNamesInDeclarationOrder = fieldNamesInDeclarationOrder;
    }

    public String getExtendedClassName() {
        return extendedClass;
    }
}