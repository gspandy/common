package com.porpoise.common.metadata;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.porpoise.common.collect.tree.TreeNode;

/**
 * @param <T>
 * @param <V>
 */
public class MetadataNode<T, V> implements TreeNode<Object> {

    private final TreeNode<? extends Object> parentNode;
    private final Collection<TreeNode<Object>> children = Lists.newArrayList();
    private final Metadata<T, V> metadata;
    private final T instance;

    /**
     * @param parent
     * @param metadata
     * @param instance
     */
    public MetadataNode(final TreeNode<?> parent, final Metadata<T, V> metadata, final T instance) {
        this.parentNode = parent;
        this.metadata = metadata;
        this.instance = instance;
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public <N extends TreeNode<?>> N addChild(final N child) {
        this.children.add((TreeNode<Object>) child);
        return child;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.collect.tree.TreeNode#getParent()
     */
    @SuppressWarnings("unchecked")
    @Override
    public TreeNode<Object> getParent() {
        return (TreeNode<Object>) this.parentNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.porpoise.common.collect.tree.TreeNode#getChildren()
     */
    @Override
    public Iterable<TreeNode<Object>> getChildren() {
        return this.children;
    }

    /**
     * @see com.porpoise.common.collect.tree.TreeNode#getData()
     */
    @Override
    public V getData() {
        return this.metadata.accessor().apply(this.instance);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s = %s", this.metadata, getData());
    }

}
