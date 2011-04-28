package com.porpoise.common.collect.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.porpoise.common.collect.tree.TreeVisitors.CollectionVisitor;

/**
 * Simple implementation of a tree, with an emphasis on simple (easy) branch creation/navigation.
 * 
 * Also, all tree nodes have a 'name', allowing for easier string parsing
 * 
 * @author Aaron
 */
public class Tree {

    /**
     * parse a slash (/) separated path to get or create a child node.
     * 
     * @param node
     *            the source node
     * @param subTree
     *            the slash-separated path
     * @return the child node
     */
    public static <T> Node<T> parse(final Node<T> node, final String subTree) {
        return parse(node, TreeTrait.split(subTree));
    }

    /**
     * create a new tree with a 'hidden' root node
     * 
     * @param tree
     *            the initial, slash-separated path
     * @return the (hidden) root node
     */
    public static <T> Node<T> parseWithHiddenRoot(final String tree) {
        final Node<T> root = newTree("Hidden Root Node");
        parse(root, TreeTrait.split(tree));
        return root;
    }

    /**
     * create a new tree with a single root node of the given name
     * 
     * @param rootNodeName
     *            the name of the root node
     * @return the single root node with the given name
     */
    public static <T> Node<T> newTree(final String rootNodeName) {
        if (rootNodeName.indexOf(TreeTrait.TREE_PATH_DELIM) >= 0) {
            throw new IllegalArgumentException("slashes are not allowed in the root name. Use parse for creating trees with initial child nodes");
        }
        return new Node<T>(null, rootNodeName);
    }

    /**
     * 
     * create a new tree with the given hierarchy, interpreting the tree string as a slash-separated path
     * 
     * @param tree
     *            a slash-separated node path
     * @return the last leaf node of the tree from the given path
     */
    public static <T> Node<T> parse(final String tree) {
        final Iterable<String> nodeNames = TreeTrait.split(tree);
        final Node<T> root = new Node<T>(null, nodeNames.iterator().next());
        final Iterable<String> theRest = Iterables.skip(nodeNames, 1);
        return parse(root, theRest);
    }

    /**
     * append child nodes to the given node as created from the parts of the subTree path
     * 
     * @param node
     * @param subTree
     * @return the last node added
     */
    @SuppressWarnings("synthetic-access")
    private static <T> Node<T> parse(final Node<T> node, final Iterable<String> subTree) {
        Node<T> lastParent = node;
        for (final String name : subTree) {
            lastParent = lastParent.getOrCreate(name);
        }
        return lastParent;
    }

    /**
     * @return a function which will return the node's name for the given ITreeNode
     */
    public static final Function<TreeNode<?>, String> name() {
        return GetNodeName.INSTANCE;
    }

    private final static class GetNodeName implements Function<TreeNode<?>, String> {
        public static final Function<TreeNode<?>, String> INSTANCE = new GetNodeName();

        @Override
        public String apply(final TreeNode<?> node) {
            return ((Node<?>) node).getName();
        }
    }

    /**
     * A node of the tree
     * 
     * @author Aaron
     */
    public static class Node<T> implements Iterable<Node<T>>, TreeNode<T> {

        private final String               name;

        private T                          data;

        private final Node<T>              parent;

        private final Map<String, Node<T>> childrenByName;

        private Integer                    cachedDepth;

        protected Node(final Node<T> parentNode, final String nodeName) {
            this(parentNode, nodeName, null);
        }

        protected Node(final Node<T> parentNode, final String nodeName, final T info) {
            this.name = nodeName;
            this.data = info;
            final Comparator<String> keyName = Ordering.natural();
            this.childrenByName = Maps.newTreeMap(keyName);
            this.parent = parentNode;
        }

        /**
         * similar to parse, but without creating nodes along the way. If at any point a node in the tree does not exist, a null node is returned
         * 
         * @param path
         *            the slash-separated path for the node
         * @return the node at the given location or null if no node exists
         */
        @SuppressWarnings({ "static-method" })
        public Node<T> findByPath(final String pathParam) {
            return (Node<T>) TreeTrait.findByPath(this, GetNodeName.INSTANCE, pathParam);
        }

        /**
         * find the first sub-node with the given name which exists under this node
         * 
         * @param nodeName
         * @return the node with the given name or null if none was found
         */
        public Node<T> findByName(final String nodeName) {
            if (getName().equals(nodeName)) {
                return this;
            }
            for (final Node<T> child : getChildren()) {
                final Node<T> found = child.findByName(nodeName);
                if (found != null) {
                    return found;
                }
            }
            return null;
        }

        /**
         * @param nodeName
         * @return true if the node currently had a child node with the given name
         */
        public boolean hasChildByName(final String nodeName) {
            return getChildByName(nodeName) != null;
        }

        /**
         * @param childName
         * @return the child node with the given name
         */
        public Node<T> getChildByName(final String childName) {
            return this.childrenByName.get(childName);
        }

        private Node<T> getOrCreate(final String childName) {
            Node<T> child = getChildByName(childName);
            if (child == null) {
                child = addChild(childName, null);
            }
            return child;
        }

        protected Node<T> newNode(final Node<T> p, final String childName, final T value) {
            return new Node<T>(p, childName, value);
        }

        /**
         * @param childName
         * @param value
         * @return the new child
         */
        public Node<T> addChild(final String childName, final T value) {
            return addChild(newNode(this, childName, value));
        }

        /**
         * add the given node as a deep copy
         * 
         * @param nodes
         */
        public void addAll(final Node<T> node) {
            final Node<T> copy = node.copyRecursive(this);
            addChild(copy);
        }

        /**
         * add all nodes
         * 
         * @param nodes
         */
        public void addAll(final Iterable<Node<T>> nodes) {
            for (final Node<T> node : nodes) {
                final Node<T> copy = node.copyRecursive(this);
                addChild(copy);
            }
        }

        /**
         * @param childName
         * @return the new child
         */
        public Node<T> addChild(final String childName) {
            return addChild(childName, null);
        }

        Node<T> addChild(final Node<T> child) {
            return addChildInternal(child);
        }

        private Node<T> addChildInternal(final Node<T> child) {
            assert child.getParent() == this;
            final Node<T> replaced = this.childrenByName.put(child.getName(), child);
            assert replaced == null;
            return child;
        }

        @Override
        public final String toString() {
            return TreeTrait.toString(this, Tree.name());
        }

        public String getName() {
            return this.name;
        }

        /**
         * @return true if the node has any children
         */
        public boolean hasChildren() {
            return !this.childrenByName.isEmpty();
        }

        /**
         * @return the number of children
         */
        public int getChildCount() {
            return this.childrenByName.size();
        }

        @Override
        public Iterable<Node<T>> getChildren() {
            return this.childrenByName.values();
        }

        /**
         * @return the depth of this node in the tree
         */
        public int getDepth() {
            if (this.cachedDepth == null) {
                final int depth = calculateDepth();
                this.cachedDepth = Integer.valueOf(depth);
            }
            return this.cachedDepth.intValue();
        }

        private int calculateDepth() {
            int depth = 0;
            if (isRoot()) {
                depth = 0;
            } else {
                // we are one deeper than our parent
                depth = getParent().getDepth() + 1;
            }
            return depth;
        }

        @Override
        public Node<T> getParent() {
            return this.parent;
        }

        /**
         * get or create parts of the tree from the given slash-separated path.
         * 
         * If the path begins with a slash (/), then the path will be interpreted from the root node
         * 
         * @param path
         * @return the last node as interpreted from the path
         */
        public Node<T> parse(final String pathParam) {
            Node<T> source;
            String path = pathParam;
            if (path.startsWith(TreeTrait.TREE_PATH_DELIM)) {
                source = getRoot();

                // is the current node the same as the beginning of the path?
                // e.g with tree a->b->c, asking the root node a to 'parse'
                // "/a/b/c/",
                // we should ignore the first 'a'
                path = TreeTrait.adjustForRootPath(source, GetNodeName.INSTANCE, path);
            } else {
                source = this;
            }
            return Tree.parse(source, path);
        }

        /***
         * @return the root node
         */
        @SuppressWarnings("static-method")
        public Node<T> getRoot() {
            return (Node<T>) TreeTrait.getRoot(this);
        }

        @Override
        public Iterator<Node<T>> iterator() {
            return getChildren().iterator();
        }

        /**
         * traverse the node tree with the given visitor, depth-first
         * 
         * @param visitor
         */
        public final void depthFirst(final TreeVisitor<Node<T>> visitor) {
            TreeTrait.depthFirst(this, visitor);
        }

        public Collection<String> flatten() {
            return Collections2.transform(flattenNodes(), new Function<Node<T>, String>() {
                @Override
                public String apply(final Node<T> node) {
                    return node.getName();
                }
            });
        }

        private Collection<Node<T>> flattenNodes() {
            final CollectionVisitor<T, Node<T>> collection = TreeVisitors.collection();
            return collection.depthFirst(this);
        }

        public boolean isRoot() {
            return this.parent == null;
        }

        public Node<T> copy() {
            return copyRecursive(null);
        }

        private Node<T> copyRecursive(final Node<T> parentNode) {
            final Node<T> copy = newNode(parentNode, getName(), this.data);
            for (final Node<T> child : getChildren()) {
                copy.addChild(child.copyRecursive(copy));
            }
            return copy;
        }

        /**
         * @return the number of nodes in the tree
         */
        @SuppressWarnings("static-method")
        public int size() {
            return TreeTrait.sizeOf(this);
        }

        @Override
        public T getData() {
            return this.data;
        }

        public T setData(final T newData) {
            if (this.data != null) {
                throw new IllegalStateException("data already set: " + this.data);
            }
            this.data = checkNotNull(newData);
            return this.data;
        }

        /**
         * @return the tree path as a string
         */
        @SuppressWarnings("static-method")
        public String getPath() {
            final Function<TreeNode<?>, String> justName = GetNodeName.INSTANCE;
            return TreeTrait.toPathString(this, justName, TreeTrait.TREE_PATH_DELIM);
        }

        /**
         * Create N multiples of this node (appending the copy number as a suffix to the new node names), then proceeding up the tree, having each parent create N multiples of
         * their children.
         * 
         * 
         * @param numberOfChildrenToAdd
         * @return this current node
         */
        public Node<T> multiply(final int numberOfChildrenToAdd) {
            if (isRoot()) {
                return this;
            }
            final String prefix = getName();
            final Iterable<Node<T>> originalChildren = getChildren();
            for (int i = 1; i <= numberOfChildrenToAdd; i++) {
                final Node<T> child = this.parent.addChild(prefix + i);
                child.addAll(originalChildren);
            }

            this.parent.multiply(numberOfChildrenToAdd);

            return this;
        }
    }

}