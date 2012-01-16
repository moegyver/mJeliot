package jeliot.calltree;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Niko Myller
 */
public class Node implements TreeNode {

    /**
     * Comment for <code>properties</code>
     */
    private Hashtable properties = null;
    
    /**
     * Comment for <code>parent</code>
     */
    private TreeNode parent = null;

    /**
     * Comment for <code>children</code>
     */
    private List children = null;

    /**
     * 
     */
    public Node() {
        children = new LinkedList();
        properties = new Hashtable();
    }

    /* (non-Javadoc)
     * @see TreeNode#getParent()
     */
    public TreeNode getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see TreeNode#addChild(TreeNode)
     */
    public void addChild(TreeNode child) {
        child.setParent(this);
        children.add(child);
    }

    /* (non-Javadoc)
     * @see TreeNode#addChild(int, TreeNode)
     */
    public void addChild(int index, TreeNode child) {
        child.setParent(this);
        children.add(index, child);
    }

    /* (non-Javadoc)
     * @see TreeNode#removeChildAt(int)
     */
    public void removeChildAt(int index) {
        TreeNode node = (TreeNode) children.remove(index);
        node.setParent(null);
    }

    /* (non-Javadoc)
     * @see TreeNode#removeChild(TreeNode)
     */
    public void removeChild(TreeNode child) {
        if (children.remove(child)) {
            child.setParent(null);
        }
    }

    /* (non-Javadoc)
     * @see TreeNode#getChild(int)
     */
    public TreeNode getChild(int index) {
        return (TreeNode) children.get(index);
    }

    /* (non-Javadoc)
     * @see TreeNode#getChildCount()
     */
    public int getChildCount() {
        return children.size();
    }

    /* (non-Javadoc)
     * @see TreeNode#setProperty(java.lang.Object, java.lang.Object)
     */
    public void setProperty(Object key, Object value) {
        if (value == null) {
            value = Util.nullObject;
        }
        properties.put(key, value);
    }

    /* (non-Javadoc)
     * @see TreeNode#getProperty(java.lang.Object)
     */
    public Object getProperty(Object key) {
        Object o = properties.get(key);
        return o;
    }

    /* (non-Javadoc)
     * @see TreeNode#isRoot()
     */
    public boolean isRoot() {
        return (parent == null);
    }

    /* (non-Javadoc)
     * @see TreeNode#isLeaf()
     */
    public boolean isLeaf() {
        return (children.size() == 0);
    }

    /* (non-Javadoc)
     * @see TreeNode#isInternalNode()
     */
    public boolean isInternalNode() {
        return !isRoot() && !isLeaf();
    }

    /* (non-Javadoc)
     * @see TreeNode#getDepth()
     */
    public int getDepth() {
        int depth = 0;
        if (!isRoot()) {
            depth++;
            TreeNode tn = parent;
            while (!tn.isRoot()) {
                tn = tn.getParent();
                depth++;
            }
        }
        return depth;
    }

    /* (non-Javadoc)
     * @see TreeNode#getSize()
     */
    public int getSize() {
        int size = 0;
        if (!isLeaf()) {
            ListIterator li = children.listIterator();
            while (li.hasNext()) {
                TreeNode node = (TreeNode) li.next();
                size += node.getSize();
            }
            size++;
        } else {
            return 1;
        }
        return size;
    }

    /* (non-Javadoc)
     * @see TreeNode#getChildIterator()
     */
    public ListIterator getChildIterator() {
        return children.listIterator();
    }

    /* (non-Javadoc)
     * @see TreeNode#setParent()
     */
    public void setParent(TreeNode p) {
        this.parent = p;
    }

    /* (non-Javadoc)
     * @see TreeNode#destroyProperty(java.lang.Object)
     */
    public void destroyProperty(Object key) {
        properties.remove(key);
    }

}
