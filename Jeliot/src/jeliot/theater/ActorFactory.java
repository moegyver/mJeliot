package jeliot.theater;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import jeliot.lang.ArrayInstance;
import jeliot.lang.Instance;
import jeliot.lang.MethodFrame;
import jeliot.lang.ObjectFrame;
import jeliot.lang.StringInstance;
import jeliot.lang.Value;
import jeliot.lang.Variable;
import jeliot.mcode.MCodeUtilities;
import jeliot.util.ResourceBundles;
import jeliot.util.UserProperties;
import jeliot.util.Util;

/**
 * This class handles the centralized creation of the actors. This enables the centralized
 * appearance handling.
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 */
public class ActorFactory {

    /**
     * The resource bundle for theater package
     */
    static private UserProperties propertiesBundle = ResourceBundles
            .getTheaterUserProperties();

    /**
     * The resource bundle for theater package
     */
    static private ResourceBundle messageBundle = ResourceBundles
            .getTheaterMessageResourceBundle();

    //  DOC: document!
    /**
     *  
     */
    private Component dummy = new Panel();

    /**
     *  
     */
    private ImageLoader iLoad;

    /**
     *  
     */
    private Image shadowImage;

    /**
     *  
     */
    private Image messageImage;

    /**
     *  
     */
    private Font valueFont;

    /**
     *  
     */
    private Font variableFont;

    /**
     *  
     */
    private Font stageFont;

    /**
     *  
     */
    private Font messageFont = new Font(propertiesBundle
            .getStringProperty("font.message.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.message.size")));

    /**
     *  
     */
    private Font indexFont = new Font(propertiesBundle
            .getStringProperty("font.index.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.index.size")));

    /**
     *  
     */
    private Font CIFont = new Font(propertiesBundle
            .getStringProperty("font.CI.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.CI.size")));

    /**
     *  
     */
    private Font SMIFont = new Font(propertiesBundle
            .getStringProperty("font.SMI.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.SMI.size")));

    /**
     *  
     */
    private Font OMIFont = new Font(propertiesBundle
            .getStringProperty("font.OMI.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.OMI.size")));

    /**
     *  
     */
    private Font ACFont = new Font(propertiesBundle
            .getStringProperty("font.AC.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.AC.size")));

    /**
     *  
     */
    private Font LATFont = new Font(propertiesBundle
            .getStringProperty("font.LAT.family"), Font.BOLD, Integer
            .parseInt(propertiesBundle.getStringProperty("font.LAT.size")));

    /**
     *  
     */
    private int valueHeight;

    /**
     *  
     */
    private Insets variableInsets = new Insets(2, 3, 2, 2);

    /**
     *  
     */
    private int margin = Integer.parseInt(propertiesBundle
            .getStringProperty("actor_factory.margin"));

    /**
     *  
     */
    private Color messagebc = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.message.background"))
            .intValue());

    /**
     *  
     */
    private Color messagefc = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.message.foreground"))
            .intValue());

    /**
     *  
     */
    private Color trueColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.true")).intValue());

    /**
     *  
     */
    private Color falseColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.false")).intValue());

    /**
     *  
     */
    private Color opColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.operator")).intValue());

    /**
     *  
     */
    private Color methodStageColor = new Color(
            Integer
                    .decode(
                            propertiesBundle
                                    .getStringProperty("color.method_stage.background"))
                    .intValue());

    /**
     *  
     */
    private Color objectStageColor = new Color(
            Integer
                    .decode(
                            propertiesBundle
                                    .getStringProperty("color.object_stage.background"))
                    .intValue());

    /**
     *  
     */
    private Color classColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.class.background"))
            .intValue());

    /**
     *  
     */
    private Color SMIColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.SMI.background"))
            .intValue());

    /**
     *  
     */
    private Color variableForegroundColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.variable.foreground"))
            .intValue());

    /**
     *  
     */
    private Color valueForegroundColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.value.foreground"))
            .intValue());

    /**
     *  
     */
    private Color OMIColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.OMI.background"))
            .intValue());

    /**
     *  
     */
    private Color CIColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.CI.background"))
            .intValue());

    /**
     *  
     */
    private Color ACColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.AC.background"))
            .intValue());

    /**
     *  
     */
    private Color bubbleColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.bubble.background"))
            .intValue());

    /**
     *  
     */
    private Color LATForegroundColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.LAT.foreground"))
            .intValue());

    /**
     *  
     */
    private Color LATBackgroundColor = new Color(Integer.decode(
            propertiesBundle.getStringProperty("color.LAT.background"))
            .intValue());

    /**
     *  
     */
    private Color[] valColor = {
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.value.background.boolean"))
                            .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.byte"))
                    .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.short"))
                    .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.int"))
                    .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.long"))
                    .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.char"))
                    .intValue()),
            new Color(Integer.decode(
                    propertiesBundle
                            .getStringProperty("color.value.background.float"))
                    .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.value.background.double"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.value.background.string"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.value.background.reference"))
                            .intValue()) };

    /**
     *  
     */
    private Color[] varColor = {
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.boolean"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.byte"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.short"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.int"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.long"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.char"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.float"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.double"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.string"))
                            .intValue()),
            new Color(
                    Integer
                            .decode(
                                    propertiesBundle
                                            .getStringProperty("color.variable.background.reference"))
                            .intValue()) };

    /**
     *  
     */
    private String[][] binOpImageName = {
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.multiplication"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.multiplication") },
            //multiplication
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.division"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.division") },
            //division
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.remaider"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.remaider") },
            //remaider
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.addition"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.addition") },
            //addition
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.subtraction"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.subtraction") },
            //subtraction
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.left_shift"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.left_shift") },
            //left shift
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.right_shift"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.right_shift") },
            //right shift
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.unsigned_right_shift"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.unsigned_right_shift") },
            //unsigned right shift
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.lesser_than"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.lesser_than") },
            //lesser than
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.greater_than"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.greater_than") },
            //greater than
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.lesser_than_or_equals"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.lesser_than_or_equals") },
            //lesser than or equals
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.greater_than_or_equals"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.greater_than_or_equals") },
            //greater than or equals
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.instanceof"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.instanceof") },
            //instanceof not yet implemented
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.equals"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.equals") },
            //equals
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.not_equals"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.not_equals") },
            //not equals
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.bitwise_and"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.bitwise_and") },
            //bitwise and
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.bitwise_xor"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.bitwise_xor") },
            //bitwise xor
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.bitwise_or"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.bitwise_or") },
            //bitwise or
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.logical_and"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.logical_and") },
            //logical and
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.logical_or"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.logical_or") },
            //logical or
            {
                    propertiesBundle
                            .getStringProperty("image.binary_operator.logical_xor"),
                    propertiesBundle
                            .getStringProperty("image.binary_operator.result.logical_xor") }
    //logical xor
    };

    /**
     *  
     */
    private String[][] unaOpImageName = {
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.plus"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.plus") },
            //plus
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.minus"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.minus") },
            //minus
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.preinc"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.preinc") },
            //preinc
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.predec"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.predec") },
            //predec
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.complement"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.complement") },
            //complement
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.not"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.not") },
            //not
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.postinc"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.postinc") },
            //postinc
            {
                    propertiesBundle
                            .getStringProperty("image.unary_operator.postdec"),
                    propertiesBundle
                            .getStringProperty("image.unary_operator.result.postdec") }
    //postdec
    };

    /**
     *  
     */
    public static int[] typeValWidth;

    /**
     *  
     */
    private static int[] typeWidth;

    /**
     * @param iLoad
     */
    public ActorFactory(ImageLoader iLoad) {
        this.iLoad = iLoad;
        this.shadowImage = iLoad.getImage(propertiesBundle
                .getStringProperty("image.shadow"));
        Actor.setShadowImage(this.shadowImage);
        this.messageImage = iLoad.getImage(propertiesBundle
                .getStringProperty("image.message.background"));
        setValueFont(new Font(propertiesBundle
                .getStringProperty("font.value.family"), Font.BOLD,
                Integer.parseInt(propertiesBundle
                        .getStringProperty("font.value.size"))));
        setVariableFont(new Font(propertiesBundle
                .getStringProperty("font.variable.family"), Font.BOLD, Integer
                .parseInt(propertiesBundle
                        .getStringProperty("font.variable.size"))));
        setStageFont(new Font(propertiesBundle
                .getStringProperty("font.stage.family"), Font.PLAIN,
                Integer.parseInt(propertiesBundle
                        .getStringProperty("font.stage.size"))));
    }

    /**
     * @param font
     */
    public void setValueFont(Font font) {
        this.valueFont = font;
        FontMetrics fm = dummy.getFontMetrics(font);
        valueHeight = fm.getHeight() + margin;
        int m = 4;
        typeValWidth = new int[] {
                m + Math.max(fm.stringWidth("true"), fm.stringWidth("false")),
                m + fm.stringWidth("888"), m + fm.stringWidth("88888"),
                m + fm.stringWidth("8888888"), m + fm.stringWidth("888888888"),
                m + fm.stringWidth("xm"), m + fm.stringWidth("0.00E10"),
                m + fm.stringWidth("0.0000E10"),
                m + fm.stringWidth("Normal string"), 6 };
    }

    /**
     * @param font
     */
    public void setVariableFont(Font font) {
        this.variableFont = font;
        FontMetrics fm = dummy.getFontMetrics(font);
        typeWidth = new int[] { fm.stringWidth("boolean"),
                fm.stringWidth("byte"), fm.stringWidth("short"),
                fm.stringWidth("int"), fm.stringWidth("long"),
                fm.stringWidth("char"), fm.stringWidth("float"),
                fm.stringWidth("double"), fm.stringWidth("String") };
    }

    /**
     * @param font
     */
    public void setStageFont(Font font) {
        this.stageFont = font;
    }

    /**
     * @param n
     * @return
     */
    public static int getTypeValueWidth(int n) {
        if (n >= 0 && n < typeValWidth.length) {
            return typeValWidth[n];
        }
        return 0;
    }

    /**
     * @param n
     * @return
     */
    public static int getTypeWidth(int n) {
        if (n >= 0 && n < typeWidth.length) {
            return typeWidth[n];
        }
        return 0;
    }

    /**
     * @return
     */
    public static int getMaxTypeWidth() {
        int max = 0;
        if (typeWidth != null) {
            int n = typeWidth.length;
            for (int i = 0; i < n; i++) {
                if (typeWidth[i] > max) {
                    max = typeWidth[i];
                }
            }
            return max;
        }
        return 0;
    }

    /**
     * @return
     */
    public static int getMaxMethodStageWidth() {
        if (typeValWidth != null) {
            return getMaxTypeWidth() + typeValWidth[8]
                    + TheaterManager.getMaxMethodInset();
        }
        return 0;
    }

    /**
     * @return
     */
    public static int getMaxObjectStageWidth() {
        return getMaxTypeWidth() + typeValWidth[8] + 20;
    }

    /**
     * @param m
     * @return
     */
    public MethodStage produceMethodStage(MethodFrame m) {
        MethodStage stage = new MethodStage(m.getMethodName());
        stage.setFont(stageFont);
        stage.calculateSize(getMaxMethodStageWidth(), valueHeight + 8
                + variableInsets.top + variableInsets.bottom);
        stage.setBackground(methodStageColor);
        stage.setShadow(6);
        return stage;
    }

    /**
     * @param v
     * @return
     */
    public VariableActor produceVariableActor(Variable v) {
        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = MCodeUtilities.resolveType(type);
        if (typeInfo != MCodeUtilities.REFERENCE
                && !(Util.visualizeStringsAsObjects() && typeInfo == MCodeUtilities.STRING)) {
            actor = new VariableActor();
            ValueActor vact = null;
            ImageValueActor valueActor = new ImageValueActor(iLoad
                    .getImage(propertiesBundle
                            .getStringProperty("image.mystery")));

            valueActor.calculateSize();
            vact = valueActor;
            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex + 1);
            }
            if (v.getName().equals(v.getType())
                    || (v.getName().equals("java.lang.Object") && v.getType()
                            .equals("java.lang.String"))) {
                actor.setName("");
                actor.setType(resolvedType);
                //actor.setName(resolvedType);
            } else {
                actor.setName(v.getName());
                actor.setType(resolvedType);
                //actor.setName(resolvedType + " " + v.getName());
            }
            //actor.setName(resolvedType + " " + v.getName());
            actor.setFont(variableFont);
            actor.setForeground(variableForegroundColor);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.reserve(vact);
            actor.bind();
            //Tracking purposes
            actor.setDescription("local variable: " + actor.getLabel());
            return actor;
        } else if (typeInfo == MCodeUtilities.REFERENCE
                || (Util.visualizeStringsAsObjects() && typeInfo == MCodeUtilities.STRING)) {
            ReferenceVariableActor refAct = new ReferenceVariableActor();
            if (MCodeUtilities.isArray(type)) {
                String ct = MCodeUtilities.resolveComponentType(type);
                if (MCodeUtilities.isPrimitive(ct)) {
                    int ti = MCodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }
                String resolvedType = MCodeUtilities
                        .changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex + 1);
                }
                int dims = MCodeUtilities.getNumberOfDimensions(type);
                String arrayString = "";
                for (int i = 0; i < dims; i++) {
                    arrayString += "[ ]";
                }
                if (v.getName().equals(v.getType())) {
                    refAct.setName("");
                    refAct.setType(resolvedType + arrayString);
                    //refAct.setName(resolvedType + arrayString);
                } else {
                    refAct.setName(v.getName());
                    refAct.setType(resolvedType + arrayString);
                    //refAct.setName(resolvedType + arrayString + " " + v.getName());
                }
                //refAct.setName(resolvedType + arrayString + " " + v.getName());
                //Tracking purposes

            } else {
                String resolvedType = type;
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex + 1);
                }
                if (v.getName().equals(v.getType())
                        || (v.getName().equals("java.lang.Object") && v
                                .getType().equals("java.lang.String"))) {
                    refAct.setName("");
                    refAct.setType(resolvedType);
                    //refAct.setName(resolvedType);
                } else {
                    refAct.setName(v.getName());
                    refAct.setType(resolvedType);
                    //refAct.setName(resolvedType + " " + v.getName());
                }
                //refAct.setName(resolvedType + " " + v.getName());
                refAct.setBackground(varColor[typeInfo]);
                //Tracking purposes

            }
            refAct.setForeground(variableForegroundColor);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
            actor.setDescription("local variable: " + actor.getLabel());
        }
        return actor;
    }

    /**
     * @param v
     * @return
     */
    public VariableActor produceObjectVariableActor(Variable v) {
        String type = v.getType();
        VariableActor actor = null;
        int typeInfo = MCodeUtilities.resolveType(type);
        if (typeInfo != MCodeUtilities.REFERENCE
                && !(Util.visualizeStringsAsObjects() && typeInfo == MCodeUtilities.STRING)) {
            actor = new VariableActor();
            ValueActor vact = produceValueActor(new Value(MCodeUtilities
                    .getDefaultValue(type), type));
            int dotIndex = type.lastIndexOf(".");
            String resolvedType = type;
            if (dotIndex > -1) {
                resolvedType = resolvedType.substring(dotIndex + 1);
            }
            actor.setName(resolvedType + " " + v.getName());
            actor.setName(v.getName());
            actor.setType(resolvedType);
            actor.setFont(variableFont);
            actor.setForeground(variableForegroundColor);
            actor.setInsets(variableInsets);
            actor.setValueDimension(typeValWidth[typeInfo], valueHeight);
            actor.setBackground(varColor[typeInfo]);
            actor.setValueColor(valColor[typeInfo]);
            actor.calculateSize();
            actor.reserve(vact);
            actor.bind();
            //Tracking purposes
            actor.setDescription("object variable: " + actor.getLabel());

            return actor;
        } else if (typeInfo == MCodeUtilities.REFERENCE
                || (Util.visualizeStringsAsObjects() && typeInfo == MCodeUtilities.STRING)) {
            ReferenceVariableActor refAct = new ReferenceVariableActor();
            if (MCodeUtilities.isArray(type)) {
                String ct = MCodeUtilities.resolveComponentType(type);
                if (MCodeUtilities.isPrimitive(ct)) {
                    int ti = MCodeUtilities.resolveType(ct);
                    refAct.setBackground(varColor[ti]);
                } else {
                    //This is not implemented properly
                    refAct.setBackground(varColor[typeInfo]);
                }
                String resolvedType = MCodeUtilities
                        .changeComponentTypeToPrintableForm(ct);
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex + 1);
                }
                int dims = MCodeUtilities.getNumberOfDimensions(type);
                String arrayString = "";
                for (int i = 0; i < dims; i++) {
                    arrayString += "[ ]";
                }
                refAct.setName(v.getName());
                refAct.setType(resolvedType + arrayString);
                //refAct.setName(resolvedType + arrayString + " " + v.getName());
            } else {
                String resolvedType = type;
                int dotIndex = resolvedType.lastIndexOf(".");
                if (dotIndex > -1) {
                    resolvedType = resolvedType.substring(dotIndex + 1);
                }
                refAct.setName(v.getName());
                refAct.setType(resolvedType);
                //refAct.setName(resolvedType + " " + v.getName());
                refAct.setBackground(varColor[typeInfo]);
            }
            refAct.setForeground(variableForegroundColor);
            refAct.setInsets(variableInsets);
            refAct.setFont(variableFont);
            refAct.setValueDimension(6 + 6, valueHeight);
            refAct.calculateSize();
            ReferenceActor ra = new ReferenceActor();
            ra.setBackground(refAct.getBackground());
            ra.calculateSize();
            refAct.setValue(ra);
            actor = refAct;
            //Tracking purposes
            actor.setDescription("object variable " + actor.getLabel());

        }
        return actor;
    }

    public ValueActor produceValueActor(Value val) {
        return produceValueActor(val, false);
    }

    /**
     * @param val
     * @return
     */
    public ValueActor produceValueActor(Value val, boolean primitiveString) {
        String type = val.getType();
        int typeInfo = MCodeUtilities.resolveType(type);
        //System.out.println(type);
        if (MCodeUtilities.isPrimitive(type)
                || (primitiveString && typeInfo == MCodeUtilities.STRING)) {
            ValueActor actor = new ValueActor();
            actor.setForeground(valueForegroundColor);
            if (typeInfo == MCodeUtilities.BOOLEAN) {
                boolean b = Boolean.getBoolean(val.getValue());
                Color tcol = b ? trueColor : falseColor;
                actor.setForeground(tcol);
            }
            actor.setBackground(valColor[typeInfo]);
            String label = val.getValue();
            //String label = valObj instanceof Exception ?
            //                                    "ERROR" :
            //                                    valObj.toString();
            if (typeInfo == MCodeUtilities.DOUBLE) {
                if (label.indexOf('E') == -1) {
                    int dot = label.indexOf('.');
                    if (dot > -1 && dot < label.length() - 5) {
                        label = label.substring(0, dot + 5);
                    }
                    //typeValWidth[type.getIndex()];
                }
            }
            //Stylistic change, not to show the line break char in the animation
            if (label.lastIndexOf("\\n") != -1
                    && label.lastIndexOf("\\n") == (label.length() - 2)) {
                label = label.substring(0, label.length() - 2);
            }

            actor.setLabel(label);
            //Tracking porpuses
            actor.setType(type);
            actor.setDescription("value: (" + val.getType() + ") "
                    + val.getValue() + "");
            //actor.setActor(val.getActor());
            actor.calculateSize();
            return actor;
        } else {
            ReferenceActor actor = null;
            if (val instanceof jeliot.lang.Reference) {
                actor = produceReferenceActor((jeliot.lang.Reference) val);
            } else if (val.getActor() instanceof jeliot.theater.ReferenceActor) {
                actor = produceReferenceActor((ReferenceActor) val.getActor());
            } else {
                actor = new ReferenceActor();
                actor.setBackground(valColor[typeInfo]);
                actor.calculateSize();
            }
            actor.setForeground(valueForegroundColor);

            actor.setDescription("reference: (" + val.getType() + ") "
                    + val.getValue() + "");

            return actor;
        }
    }

    /**
     * @param rf
     * @return
     */
    public ReferenceActor produceReferenceActor(jeliot.lang.Reference rf) {
        Instance inst = rf.getInstance();
        ReferenceActor actor = null;
        int typeInfo = MCodeUtilities.resolveType("null");
        if (inst != null) {
            typeInfo = MCodeUtilities.resolveType(inst.getType());
            actor = new ReferenceActor(inst.getActor());
        } else if (rf.getActor() instanceof ReferenceActor) {
            ReferenceActor rfa = (ReferenceActor) rf.getActor();
            typeInfo = MCodeUtilities.resolveType(rf.getType());
            actor = new ReferenceActor(rfa.getInstanceActor());
        } else {
            actor = new ReferenceActor();
        }
        actor.setBackground(valColor[typeInfo]);
        actor.calculateSize();
        actor.setForeground(valueForegroundColor);

        actor.setDescription("reference: (" + rf.getType() + ") "
                + rf.getValue() + "");

        return actor;
    }

    /**
     * @param cloneActor
     * @return
     */
    public ReferenceActor produceReferenceActor(ReferenceActor cloneActor) {
        ReferenceActor actor = new ReferenceActor(cloneActor.getInstanceActor());
        actor.setBackground(cloneActor.getBackground());
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());
        actor.calculateSize();
        actor.setForeground(valueForegroundColor);

        actor.setDescription("reference: (" + cloneActor.getType() + ") "
                + cloneActor.getValstr() + "");

        return actor;
    }

    /**
     * @param cloneActor
     * @return
     */
    public ValueActor produceValueActor(ValueActor cloneActor) {
        ValueActor actor = new ValueActor();
        actor.setForeground(cloneActor.getForeground());
        actor.setBackground(cloneActor.getBackground());
        actor.setLabel(cloneActor.getLabel());
        actor.calculateSize();
        Point p = cloneActor.getLocation();
        actor.setLocation(new Point(p.x, p.y));
        actor.setParent(cloneActor.getParent());

        actor.setDescription("value: (" + cloneActor.getType() + ") "
                + cloneActor.getValstr() + "");

        return actor;
    }

    /**
     * @param op
     * @return
     */
    public OperatorActor produceBinOpActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][0]);
        return produceOperatorActor(image, getBinOpDescription(op));
    }

    /**
     * @param op
     * @return
     */
    public OperatorActor produceBinOpResActor(int op) {
        Image image = iLoad.getImage(binOpImageName[op][1]);
        return produceOperatorActor(image, "equals");
    }

    /**
     * @return
     */
    public OperatorActor produceEllipsis() {
        Image image = iLoad.getImage(propertiesBundle
                .getStringProperty("image.dots"));
        OperatorActor actor = produceOperatorActor(image, "ellipsis");
        return actor;
    }

    /**
     * @param op
     * @return
     */
    public OperatorActor produceUnaOpActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][0]);
        return produceOperatorActor(image, getUnOpDescription(op));
    }

    /**
     * @param op
     * @return
     */
    public OperatorActor produceUnaOpResActor(int op) {
        Image image = iLoad.getImage(unaOpImageName[op][1]);
        return produceOperatorActor(image, "equals");
    }

    /**
     * @param image
     * @return
     */
    public OperatorActor produceOperatorActor(Image image, String description) {
        OperatorActor actor = new OperatorActor(image, iLoad.darken(image),
                description);
        actor.calculateSize();
        int hh = valueHeight - actor.getHeight();
        if (hh > 0) {
            actor.setInsets(new Insets(hh / 2, 0, (hh + 1) / 2, 0));
            actor.setSize(actor.getWidth(), valueHeight);
        }
        return actor;
    }

    /**
     * Static Method Invocation Actor.
     * 
     * @param name
     * @param paramCount
     * @return
     */
    public SMIActor produceSMIActor(String name, int paramCount) {
        SMIActor actor = new SMIActor(name, paramCount);
        actor.setFont(SMIFont);
        actor.setBackground(SMIColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    /**
     * Object Method Invocation Actor
     * 
     * @param name
     * @param paramCount
     * @return
     */
    public OMIActor produceOMIActor(String name, int paramCount) {
        OMIActor actor = new OMIActor(name, paramCount);
        actor.setFont(OMIFont);
        actor.setBackground(OMIColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    /**
     * Array Creator Actor
     * 
     * @param name
     * @param paramCount
     * @return
     */
    public ACActor produceACActor(String name, int paramCount,
            int emptyBracketsCount) {
        ACActor actor = new ACActor(name, paramCount, emptyBracketsCount);
        actor.setFont(ACFont);
        actor.setBackground(ACColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    //     public SMIActor produceSMIActor(MethodPointer fmp, int n) {
    //         ReferenceType type = fmp.getDeclaringClass();
    //         String name = type.getSimpleName() + "." + fmp.getName();
    //         SMIActor actor = new SMIActor(name, n);
    //         actor.setFont(messageFont);
    //         actor.setBackground(new Color(0xFFEAEA));
    //         actor.setInsets(new Insets(6, 6, 6, 6));
    //         actor.calculateSize();
    //         return actor;
    //     }
    //     public SMIActor produceSMIActor(DomesticMethodPointer dmp, int n) {
    //         ReferenceType type = dmp.getDeclaringClass();
    //         String name = type.getSimpleName() + "." + dmp.getName();
    //         SMIActor actor = new SMIActor(name, n);
    //         actor.setFont(messageFont);
    //         actor.setBackground(new Color(0xFFEAEA));
    //         actor.setInsets(new Insets(6, 6, 6, 6));
    //         actor.calculateSize();
    //         return actor;
    //     }
    /**
     * @param actor
     * @return
     */
    public BubbleActor produceBubble(Actor actor) {
        BubbleActor ba = new BubbleActor(actor);
        ba.setBackground(bubbleColor);
        ba.setInsets(new Insets(8, 8, 8, 8));
        return ba;
    }

    /**
     * @param text
     * @return
     */
    MessageActor produceMessageActor(String[] text) {
        MessageActor ma = new MessageActor();
        ma.setBackground(messageImage);
        ma.setText(text);
        ma.setBackground(messagebc);
        ma.setForeground(messagefc);
        ma.setFont(messageFont);
        ma.setShadow(6);
        if (text != null) {
            ma.calculateSize();
        }
        return ma;
    }

    /**
     * @return
     */
    public ConstantBox produceConstantBox() {
        ConstantBox cbox = new ConstantBox(iLoad.getImage(propertiesBundle
                .getStringProperty("image.constant_box")));
        cbox.calculateSize();
        return cbox;
    }

    /**
     * @return
     */
    public AnimatingActor produceHand() {
        AnimatingActor hand = new AnimatingActor(produceImage("image.hand1"),
                "Output");
        hand.calculateSize();
        return hand;
    }

    /**
     * @param iname
     * @return
     */
    public Image produceImage(String iname) {
        return iLoad.getImage(propertiesBundle.getStringProperty(iname));
    }

    /**
     * @param array
     * @return
     */
    public ArrayActor produceArrayActor(ArrayInstance array) {
        int length = array.length();
        ValueActor[] valueActors = new ValueActor[length];

        for (int i = 0; i < length; i++) {
            Value value = array.getVariableAt(i).getValue();
            ValueActor va = produceValueActor(value);
            value.setActor(va);
            valueActors[i] = va;
        }

        String ctype = array.getComponentType();
        int finalComponentTypeInfo = MCodeUtilities.resolveType(MCodeUtilities
                .resolveComponentType(ctype));
        int typeInfo = MCodeUtilities.resolveType(ctype);

        VariableActor arLengthVarAct = produceVariableActor(array
                .getArrayLenghtVariable());
        array.getArrayLenghtVariable().setActor(arLengthVarAct);
        ValueActor arLenghtValAct = produceValueActor(array
                .getArrayLenghtVariable().getValue());
        arLengthVarAct.setValue(arLenghtValAct);
        array.getArrayLenghtVariable().getValue().setActor(arLenghtValAct);

        ArrayActor aactor = new ArrayActor(valueActors, length, MCodeUtilities
                .isPrimitive(ctype), ctype, arLengthVarAct);

        if (MCodeUtilities.isPrimitive(MCodeUtilities
                .resolveComponentType(ctype))) {
            aactor.setFont(indexFont);
            aactor.setBackground(varColor[finalComponentTypeInfo]);
            aactor.setValueColor(valColor[finalComponentTypeInfo]);
            aactor.calculateSize(typeValWidth[typeInfo], valueHeight);
        } else {
            aactor.setFont(indexFont);
            aactor.setBackground(varColor[finalComponentTypeInfo]);
            aactor.setValueColor(valColor[finalComponentTypeInfo]);
            aactor.calculateSize(typeValWidth[typeInfo], valueHeight);
        }

        for (int i = 0; i < length; i++) {
            VariableActor va = aactor.getVariableActor(i);
            //va.setValueDimension(typeValWidth[typeInfo], valueHeight);
            array.getVariableAt(i).setActor(va);
        }
        array.setActor(aactor);
        aactor.setShadow(6);
        return aactor;
    }

    /**
     *  
     */
    private MessageFormat objectStageTitle = new MessageFormat(messageBundle
            .getString("title.object_stage"));

    /**
     * @param m
     * @return
     */
    public ObjectStage produceObjectStage(ObjectFrame m) {
        ObjectStage stage = new ObjectStage(objectStageTitle
                .format(new String[] { m.getObjectName().substring(
                        m.getObjectName().lastIndexOf(".") + 1) }), m
                .getVarCount());
        stage.setFont(stageFont);
        //The width of the object stage is not correct but we have not found
        // any better.
        stage.calculateSize(getMaxObjectStageWidth(), valueHeight + 8
                + variableInsets.top + variableInsets.bottom);
        stage.setBackground(objectStageColor);
        stage.setShadow(6);
        return stage;
    }

    /**
     * @return
     */
    public LinesAndText produceLinesAndText() {
        LinesAndText lat = new LinesAndText();
        lat.setBackground(LATBackgroundColor);
        lat.setForeground(LATForegroundColor);
        lat.setFont(LATFont);
        return lat;
    }

    /**
     *  
     */
    private MessageFormat classTitle = new MessageFormat(messageBundle
            .getString("title.class"));

    /**
     * 
     * @param c
     * @return
     */
    public ClassActor produceClassActor(jeliot.lang.Class c) {
        ClassActor ca = new ClassActor(classTitle.format(new String[] { c
                .getName().substring(c.getName().lastIndexOf(".") + 1) }), c
                .getVariableCount());
        ca.setFont(stageFont);
        //The width of the object stage is not correct but
        //we have not found any better.
        ca.calculateSize(getMaxObjectStageWidth(), valueHeight + 8
                + variableInsets.top + variableInsets.bottom);
        ca.setBackground(classColor);
        ca.setShadow(6);
        return ca;
    }

    /**
     * 
     * @param op
     * @return
     */
    public String getBinOpDescription(int op) {
        switch (op) {
        case 0:
            return "multiplication";
        case 1:
            return "division";
        case 2:
            return "remainder";
        case 3:
            return "addition";
        case 4:
            return "substration";
        case 5:
            return "left shift";
        case 6:
            return "right shift";
        case 7:
            return "unsigned right shift";
        case 8:
            return "lesser than";
        case 9:
            return "greater than";
        case 10:
            return "lesser than or equals";
        case 11:
            return "greater than or equals";
        case 12:
            return "instanceof";
        case 13:
            return "equals";
        case 14:
            return "not equals";
        case 15:
            return "bitwise and";
        case 16:
            return "bitwise xor";
        case 17:
            return "bitwise or";
        case 18:
            return "logical and";
        case 19:
            return "logical or";
        case 20:
            return "logical xor";
        default:
            return "other";
        }
    }

    /**
     * 
     * @param op
     * @return
     */
    public String getUnOpDescription(int op) {
        switch (op) {
        case 0:
            return "plus";
        case 1:
            return "minus";
        case 2:
            return "preinc";
        case 3:
            return "predec";
        case 4:
            return "complement";
        case 5:
            return "postinc";
        case 6:
            return "postdec";
        default:
            return "other";
        }
    }

    /**
     * 
     * @param name
     * @param paramCount
     * @return
     */
    public CIActor produceCIActor(String name, int paramCount) {
        CIActor actor = new CIActor(name, paramCount);
        actor.setFont(CIFont);
        actor.setBackground(CIColor);
        actor.setInsets(new Insets(6, 6, 6, 6));
        actor.calculateSize();
        return actor;
    }

    /**
     * 
     * @param si
     * @return
     */
    public StringObjectActor produceStringActor(StringInstance si) {
        ValueActor va = produceValueActor(si.getStringValue(), true);
        StringObjectActor stage = new StringObjectActor(objectStageTitle
                .format(new String[] { si.getType().substring(
                        si.getType().lastIndexOf(".") + 1) }), va);
        stage.setFont(stageFont);
        stage.setBackground(varColor[MCodeUtilities.resolveType(si.getType())]);
        stage.setShadow(6);
        stage.calculateSize();
        return stage;
    }
}