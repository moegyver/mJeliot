package jeliot.calltree;

import java.util.ListIterator;

/**
 * @author Niko Myller
 */
interface TreeNode {
    
    /**
     * gets the node that contains this node
     * @return
     */
    TreeNode getParent();
    
    /**
     * 
     * @param p
     */
    void setParent(TreeNode p);
    
    /**
     * 
     * @param child
     */
    void addChild(TreeNode child);
    
    /**
     * 
     * @param index
     * @param child
     */
    void addChild(int index, TreeNode child);
    
    /**
     * 
     * @param index
     */
    void removeChildAt(int index);
    
    /**
     * 
     * @param child
     */
    void removeChild(TreeNode child);
    
    /**
     * 
     * @param index
     * @return
     */
    TreeNode getChild(int index);
    
    /**
     * 
     * @return
     */
    int getChildCount();
    
    /**
     * 
     * @return
     */
    ListIterator getChildIterator();

    /**
     * 
     * @param key
     * @param value
     */
    void setProperty(Object key, Object value);
    
    /**
     * 
     * @param key
     * @return
     */
    Object getProperty(Object key);
    
    /**
     * 
     * @param key
     */
    void destroyProperty(Object key);
    
    /**
     * node is the root of the tree
     * @return
     */
    boolean isRoot();
    
    /**
     * node contains no children
     * @return
     */
    boolean isLeaf(); 
    /**
     * node contains children and is not root
     * @return
     */
    boolean isInternalNode();

    /**
     * returns the distance from the root node
     * @return
     */
    int getDepth();
    
    /**
     * returns the size of this sub tree - counts all nodes including this one
     * @return
     */
    int getSize();
}