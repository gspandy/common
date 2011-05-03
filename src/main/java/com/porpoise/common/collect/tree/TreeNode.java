package com.porpoise.common.collect.tree;

/**
 * Basic interface which should be sufficient for any tree implementation
 * 
 * @param <T>
 */
public interface TreeNode<T> {
	/**
	 * @return the node's parent, or null if it is the root node
	 */
	TreeNode<T> getParent();

	/**
	 * @return an iterable for this node's children, or an empty list if the node has no children
	 */
	Iterable<? extends TreeNode<T>> getChildren();

	/**
	 * A tree node may optionally hold node data. This method returns the node data for this node, or optionally null.
	 * 
	 * @return the data associated with this tree node
	 */
	T getData();
}