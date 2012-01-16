/*
 * Created on 8.12.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.printing;

import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;

import jeliot.util.DebugUtil;

/**
 * @author Niko Myller
 */
public class PrintingUtil {

    public static void printComponent(JComponent component, Rectangle area) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        JComponentVista vista = new JComponentVista(component, new PageFormat(), area);
        pj.setPageable(vista);
        try {
            if (pj.printDialog()) {
                pj.print();
            }
        } catch (PrinterException e) {
            if (DebugUtil.DEBUGGING) {
                DebugUtil.handleThrowable(e);
            }
        }
    }

}