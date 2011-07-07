package com.porpoise.common.collect.tree;

import java.util.Collection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Convenience methods /implementations of {@link TreeVisitor}s
 */
public enum TreeVisitors {
    ; // uninstantiable

    /**
     * @param <T>
     * @return a new collection visitor
     */
    public static <T, N extends TreeNode<T>> CollectionVisitor<T, N> collection() {
        return new CollectionVisitor<T, N>();
    }

    /**
     * @param <T>
     * @param <N>
     * @return a new visitor which will add all leaf nodes to its collection
     */
    public static <T, N extends TreeNode<T>> LeafCollectionVisitor<T, N> leafCollection() {
        return new LeafCollectionVisitor<T, N>();
    }

    /**
     * A tree visitor adapter pattern for an {@link TreeVisitor}
     * 
     * @param <T>
     */
    public static final class TreeVisitorAdapter<T> implements TreeVisitor<T> {

        @Override
        public void onNode(final int depth, final T node) {
            // no-op
        }
    }

    /**
     * counting visitor
     * 
     * @param <T>
     */
    public static final class SizeVisitor<T> implements TreeVisitor<TreeNode<T>> {
        private int count;

        @Override
        public void onNode(final int depth, final TreeNode<T> node) {
            this.count++;
        }

        /**
         * @return the number of nodes counted
         */
        public int getCount() {
            return this.count;
        }

        /**
         * @param node
         * @return the number of nodes under the given node, including the given node
         */
        public int sizeOf(final TreeNode<T> node) {
            // reset the count
            this.count = 0;
            TreeTrait.depthFirst(node, this);
            return getCount();
        }
    }

    /**
     * @param <T>
     *            the data type held in the node
     * @param <N>
     *            the tree node type
     */
    public static class LeafCollectionVisitor<T, N extends TreeNode<T>> extends CollectionVisitor<T, N> {
        @Override
        public void onNode(final int depth, final N node) {
            if (Iterables.isEmpty(node.getChildren())) {
                super.onNode(depth, node);
            }
        }
    }

    /**
     * @param <T>
     *            the data type held in the node
     * @param <N>
     *            the node type
     */
    public static class CollectionVisitor<T, N extends TreeNode<T>> implements TreeVisitor<N> {
        private final Collection<N> nodes = Lists.newArrayList();

        @Override
        public void onNode(final int depth, final N node) {
            this.nodes.add(node);
        }

        /**
         * @return the nodes found whilst visiting the tree
         */
        public Collection<N> getNodes() {
            return this.nodes;
        }

        /**
         * convenience method to parse the given node depth first using the collection visitor.
         * 
         * NOTE: the internal (mutable) nodes collection will simply append all nodes, so multiple calls of this method
         * will continue to add to the collection!
         * 
         * @param node
         *            the node to parse, depth first
         * @return the node results
         */
        public Collection<N> depthFirst(final N node) {
            TreeTrait.depthFirst(node, this);
            return getNodes();
        }

        /**
         * like {@link #depthFirst(TreeNode)}, but breadth first
         * 
         * @param node
         *            the node to parse, breadth first
         * @return the node results
         */
        public Collection<N> breadthFirst(final N node) {
            TreeTrait.breadthFirst(node, this);
            return getNodes();
        }
    }

    /**
     * @return a no-operation visitor
     */
    public static <T> TreeVisitor<TreeNode<T>> noOp() {
        return new TreeVisitorAdapter<TreeNode<T>>();
    }

}