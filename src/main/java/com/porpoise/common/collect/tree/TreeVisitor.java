package com.porpoise.common.collect.tree;

/**
 * interface to support the visitor pattern when traversing a tree structure
 * 
 * @author Aaron
 * 
 * @param <T>
 */
public interface TreeVisitor<T> {

	/**
	 * @param depth
	 * @param node
	 */
	void onNode(int depth, T node);
}