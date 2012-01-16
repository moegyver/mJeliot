package jeliot.calltree;

/**
 * @author Niko Myller
 */
public class TreeBuilder {

    /**
     * Comment for <code>nodeNumber</code>
     */
    protected int nodeNumber = 0;

    /**
     * Comment for <code>tree</code>
     */
    protected Tree tree;

    /**
     * Comment for <code>currentPosition</code>
     */
    protected TreeNode currentPosition = null;

    /**
     * Builds a random tree.  The build method does the work.
     * @return
     */
    public Tree buildTree() {
        // Create a random binary tree with n external nodes
        nodeNumber = 0;
        tree = new Tree();
        currentPosition = new Node();
        tree.setRoot(currentPosition);
        return tree;
    }

    /**
     * @param node
     * @return
     */
    public boolean insertNode(String node) {
        if (nodeNumber == 0) {
            currentPosition = tree.getRoot();
            currentPosition.setProperty("element", node);
            currentPosition.setProperty("current", new Boolean(true));
        } else {
            if (currentPosition == null) { return false; }

            TreeNode pos = new Node();
            pos.setProperty("element", node);
            currentPosition.addChild(pos);
            currentPosition.destroyProperty("current");
            pos.setProperty("current", new Boolean(true));
            currentPosition = pos;
        }
        nodeNumber++;
        return true;
    }

    /**
     * 
     * @param returnValue
     * @return
     */
    public boolean returnNode(String returnValue) {
        if (nodeNumber == 0 || currentPosition == null) {
            return false;
        }
        
        if (currentPosition == null) {
            return false;
        }

        if (currentPosition.isRoot()) {
            currentPosition.destroyProperty("current");
            currentPosition.setProperty("return", returnValue);
            currentPosition = null;
        } else {
            TreeNode pos = currentPosition.getParent();
            currentPosition.destroyProperty("current");
            currentPosition.setProperty("return", returnValue);
            pos.setProperty("current", new Boolean(true));
            currentPosition = pos;
        }

        return true;
    }
}