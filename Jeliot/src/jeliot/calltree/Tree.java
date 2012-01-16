package jeliot.calltree;

import java.util.ListIterator;

/**
 * @author Niko Myller
 */
public class Tree {
    
    /**
     * Comment for <code>root</code>
     */
    TreeNode root;
    
    /**
     * 
     */
    public Tree() { }
    
    /**
     * @return
     */
    public TreeNode getRoot() {
        return root;
    }
    
    /**
     * @param r
     */
    public void setRoot(TreeNode r) {
        this.root = r;
    }
    
    /**
     * @param tn
     * @param child
     */
    public void addChild(TreeNode tn, TreeNode child) {
        tn.addChild(child);
    }

    /**
     * @param tn
     * @param in
     * @param child
     */
    public void addChild(TreeNode tn, int in, TreeNode child) {
        tn.addChild(in, child);
    }
    
    /**
     * @param tn
     * @return
     */
    public ListIterator getChildIterator(TreeNode tn) {
        return tn.getChildIterator();
    }
    
    /**
     * @param tn
     * @param from
     * @return
     */
    public TreeNode getChild(TreeNode tn, int from) {
        return tn.getChild(from);
    }

    /**
     * @param tn
     * @param child
     */
    public void removeChild(TreeNode tn, TreeNode child) {
        tn.removeChild(child);
    }

    /**
     * @param tn
     * @param in
     */
    public void removeChild(TreeNode tn, int in) {
        tn.removeChildAt(in);
    }
    
    /**
     * @return
     */
    public int getSize() {
        if (root != null) {
            return root.getSize();
        }
        return 0;
    }
    
    /**
     * @return
     */
    public int depth() {
        if (root != null) {
            return root.getDepth();
        }
        return 0;
    }
}
