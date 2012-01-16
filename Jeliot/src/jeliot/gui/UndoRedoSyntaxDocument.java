/*
 * Created on 17.2.2006
 */
package jeliot.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import jeliot.util.ResourceBundles;

import org.syntax.jeliot_jedit.JEditTextArea;
import org.syntax.jeliot_jedit.SyntaxDocument;

/**
 * A document implementation that can be tokenized by the
 * syntax highlighting system.
 *
 * @author Slava Pestov
 * <br>Revised for JSource 2002 Panagiotis Plevrakis
 * <br>Email: pplevrakis@hotmail.com
 * <br>URL:   http://jsource.sourceforge.net
 * 
 * Revised for Jeliot 3
 * @author Niko Myller
 */
public class UndoRedoSyntaxDocument extends SyntaxDocument {

    static private ResourceBundle messageBundle = ResourceBundles
            .getGuiMessageResourceBundle();

    private static final int CLOSED = 0;
    private static final int LOADING = 1;
    private static final int SAVING = 2;
    private static final int NEW_FILE = 3;
    private static final int UNTITLED = 4;
    private static final int AUTOSAVE_DIRTY = 5;
    private static final int DIRTY = 6;
    private static final int READ_ONLY = 7;
    private static final int SYNTAX = 8;
    private static final int UNDO_IN_PROGRESS = 9;
    private static final int TEMPORARY = 10;

    private int flags = 0;

    private int currentLocation = 0;

    private UndoableEdit saveUndo = null;

    private CompoundEdit compoundEdit = null;

    private boolean compoundEditNonEmpty = false;

    private int compoundEditCount = 0;

    private Toolkit mToolKit = Toolkit.getDefaultToolkit();

    /**
     * 
     */
    private UndoRedo undoredo = new UndoRedo();

    /**
     * 
     */
    public UndoRedoSyntaxDocument() {
        super();
        addUndoableEditListener(undoredo.myundoable);
    }

    /**
     * 
     * @return
     */
    public UndoRedo getUndoredo() {
        return undoredo;
    }

    private void setFlag(int flag, boolean value) {
        if (value)
            flags |= (1 << flag);
        else
            flags &= ~(1 << flag);
    }

    private boolean getFlag(int flag) {
        int mask = (1 << flag);
        return (flags & mask) == mask;
    }

    /**
     * Undoes the most recent edit. Returns true if the undo was successful.
     */
    public boolean undo() {
        if (undoredo.undo == null)
            return false;

        try {
            setFlag(UNDO_IN_PROGRESS, true);
            if (undoredo.undo.canUndo())
                undoredo.undo.undo();
            else if (mToolKit != null)
                mToolKit.beep();
        } catch (CannotUndoException cu) {
            cu.printStackTrace();
            return false;
        } finally {
            setFlag(UNDO_IN_PROGRESS, false);
        }

        UndoableEdit toUndo = undoredo.undo.editToBeUndone();

        return true;
    }

    /**
     * Redoes the most recently undone edit. Returns true if the redo was successful.
     */
    public boolean redo() {
        if (undoredo.undo == null)
            return false;

        try {
            setFlag(UNDO_IN_PROGRESS, true);
            if (undoredo.undo.canRedo())
                undoredo.undo.redo();
            else if (mToolKit != null)
                mToolKit.beep();
        } catch (CannotRedoException cr) {
            cr.printStackTrace();
            return false;
        } finally {
            setFlag(UNDO_IN_PROGRESS, false);
        }

        UndoableEdit toUndo = undoredo.undo.editToBeUndone();

        return true;
    }

    /**
     * Adds an undoable edit to this document. This is non-trivial
     * mainly because the text area adds undoable edits every time
     * the caret is moved. First of all, undos are ignored while
     * an undo is already in progress. This is no problem with Swing
     * Document undos, but caret undos are fired all the time and
     * this needs to be done. Also, insignificant undos are ignored
     * if the redo queue is non-empty to stop something like a caret
     * move from flushing all redos.
     * @param edit The undoable edit
     */
    public void addUndoableEdit(UndoableEdit edit) {
        if (undoredo.undo == null || getFlag(UNDO_IN_PROGRESS)
                || getFlag(LOADING))
            return;

        // Ignore insignificant edits if the redo queue is non-empty.
        // This stops caret movement from killing redos.
        if ((undoredo.undo.canRedo() && !edit.isSignificant()) || edit instanceof JEditTextArea.CaretUndo)
            return;

        if (compoundEdit != null) {
            compoundEditNonEmpty = true;
            compoundEdit.addEdit(edit);
            //System.out.println("Added into compoundEdit: " + edit);
        } else {
            undoredo.undo.addEdit(edit);
            //System.out.println("Added: " + edit);
            
            //the listener didn't catch this sod we need to update the actions manually
            undoredo.redoAction.updateRedoState();
            undoredo.undoAction.updateUndoState();
        }
    }

    /**
     * Starts a compound edit. All edits from now on until
     * <code>endCompoundEdit()</code> are called will be merged
     * into one. This can be used to make a complex operation
     * undoable in one step. Nested calls to
     * <code>beginCompoundEdit()</code> behave as expected,
     * requiring the same number of <code>endCompoundEdit()</code>
     * calls to end the edit.
     */
    public void beginCompoundEdit() {
        if (getFlag(TEMPORARY))
            return;

        compoundEditCount++;
        if (compoundEdit == null) {
            compoundEditNonEmpty = false;
            compoundEdit = new CompoundEdit();
        }
    }

    /**
     * Ends a compound edit. All edits performed since
     * <code>beginCompoundEdit()</code> was called can now
     * be undone in one step by calling <code>undo()</code>.
     */
    public void endCompoundEdit() {
        if (getFlag(TEMPORARY))
            return;

        if (compoundEditCount == 0)
            return;

        compoundEditCount--;
        if (compoundEditCount == 0) {
            compoundEdit.end();            
            if (compoundEditNonEmpty && compoundEdit.canUndo()) {
                undoredo.undo.addEdit(compoundEdit);
                //System.out.println("Added compoundEdit into manager: " + compoundEdit);
                //the listener didn't catch this sod we need to update the actions manually
                undoredo.redoAction.updateRedoState();
                undoredo.undoAction.updateUndoState();
            }
            compoundEdit = null;
        }
    }

    class UndoRedo {

        UndoAction undoAction = new UndoAction();
        RedoAction redoAction = new RedoAction();
        MyUndoManager undo = new MyUndoManager();       
        MyUndoableEditListener myundoable = new MyUndoableEditListener();
        
        class UndoAction extends AbstractAction {
            
            boolean insideEnabled = false;
            
            public UndoAction() {
                super(messageBundle.getString("menu.edit.undo"));
                setEnabledFromInside(false);
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    undo();
                } catch (CannotUndoException ex) {
                    //System.out.println("Unable to undo: " + ex);
                    ex.printStackTrace();
                }
                updateUndoState();
                redoAction.updateRedoState();
            }

            protected void updateUndoState() {
                //System.out.println("Checking for state change in undo");
                if (undo.canUndo()) {                    
                    setEnabledFromInside(true);
                    putValue(Action.NAME, undo.getUndoPresentationName());
                } else {
                    setEnabledFromInside(false);
                    putValue(Action.NAME, messageBundle
                            .getString("menu.edit.undo"));
                }
            }
            
            private void setEnabledFromInside(boolean e) {
                synchronized (this) {
                    insideEnabled = true;
                    setEnabled(e);
                    insideEnabled = false;
                }
            }
            
            public void setEnabled(boolean e) {
                //System.out.println("Changing state in undo: " + e);
                if (insideEnabled || !e) {
                    super.setEnabled(e);
                } else {
                    updateUndoState();
                }
            }
        }

        class RedoAction extends AbstractAction {
            
            boolean insideEnabled = false;
            
            public RedoAction() {
                super(messageBundle.getString("menu.edit.redo"));
                setEnabledFromInside(false);
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    redo();
                } catch (CannotRedoException ex) {
                    //System.out.println("Unable to redo: " + ex);
                    ex.printStackTrace();
                }
                updateRedoState();
                undoAction.updateUndoState();
            }

            protected void updateRedoState() {
                //System.out.println("Checking for state change in redo");
                if (undo.canRedo()) {
                    setEnabledFromInside(true);
                    putValue(Action.NAME, undo.getRedoPresentationName());
                } else {
                    setEnabledFromInside(false);
                    putValue(Action.NAME, messageBundle
                            .getString("menu.edit.redo"));
                }
            }
            
            private void setEnabledFromInside(boolean e) {
                synchronized (this) {
                    insideEnabled = true;
                    setEnabled(e);
                    insideEnabled = false;
                }
            }
            
            public void setEnabled(boolean e) {
                //System.out.println("Changing state in redo: " + e);
                if (insideEnabled || !e) {
                    super.setEnabled(e);
                } else {
                    updateRedoState();
                }
            }            
        }

        protected class MyUndoableEditListener implements UndoableEditListener {
            public void undoableEditHappened(UndoableEditEvent e) {
                //Remember the edit and update the menus.
                addUndoableEdit(e.getEdit());
                //undo.addEdit(e.getEdit());
                undoAction.updateUndoState();
                redoAction.updateRedoState();
            }
        }

        // we need to call some protected methods, so override this class to make them public
        class MyUndoManager extends UndoManager {
            public UndoableEdit editToBeUndone() {
                return super.editToBeUndone();
            }

            public UndoableEdit editToBeRedone() {
                return super.editToBeRedone();
            }
        }
    }
}
