package com.porpoise.common.collect.tree;

import java.util.Collections;

/**
 * @param <T>
 */
public class LeafNode<T> implements TreeNode<T> {

    private final TreeNode<T> parent;
    private final T data;

    /**
     * @return the tree as a string
     */
    @Override
    public String toString() {
        return this.data == null ? "null" : this.data.toString();
    }

    /**
     * @param data
     */
    public LeafNode(final T data) {
        this(null, data);
    }

    /**
     * @param parent
     * @param data
     */
    public LeafNode(final TreeNode<T> parent, final T data) {
        this.parent = parent;
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.collect.tree.TreeNode#getParent()
     */
    @Override
    public TreeNode<T> getParent() {
        return this.parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.collect.tree.TreeNode#getChildren()
     */
    @Override
    public Iterable<? extends TreeNode<T>> getChildren() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.collect.tree.TreeNode#getData()
     */
    @Override
    public T getData() {
        return this.data;
    }

}
