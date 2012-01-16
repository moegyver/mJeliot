/*
 * Copyright (c) 2004 Roland Küstermann. All Rights Reserved.
 */
package jeliot.mcode.input;

/**
 * Created by IntelliJ IDEA.
 * User: roku
 * Date: 04.08.2004
 * Time: 17:20:02
 * To change this template use File | Settings | File Templates.
 */
public class InputHandlerFactory {
    public static InputHandler createInputHandler (Class aClass) {
        if (aClass.getName().equals("Prog1Tools.IOTools")) {
            return new Prog1ToolsInputHandler();
        }
        if (aClass.getName().equals("easyIn.EasyIn")){
        	return new EasyInInputHandler();
        }
        if (aClass.getName().equals("jeliot.io.Input")) {
            return new JeliotInputHandlerImpl();
        }
        if (aClass.getName().equals("jeliot.io.Lue")) {
            return new LueInputHandlerImpl();
        }
        if (aClass.getName().equals("java.util.Scanner")) {
            return new JavaScannerHandler();
        }
        return null;
    }
}
