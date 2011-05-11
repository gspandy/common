package com.porpoise.common.collect.tree;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.porpoise.common.collect.tree.Tree.Node;
import com.porpoise.common.collect.tree.TreeVisitors.LeafCollectionVisitor;
import com.porpoise.common.collect.tree.TreeVisitors.SizeVisitor;
import com.porpoise.common.collect.tree.TreeVisitors.TreeVisitorAdapter;

/**
 * Similar to scala traits, this class provides a richer tree implementation by providing addition methods from the
 * basic {@link TreeNode} interface
 * 
 */
public enum TreeTrait {
    ;// uninstantiable
    /**
     * the delimiter to use when parsing tree paths
     */
    public static final String TREE_PATH_DELIM = "/";

    /**
     * @param node
     * @return a string for the given node's path
     */
    public static String toPathString(final TreeNode<?> node) {
        return toPathString(node, TREE_PATH_DELIM);
    }

    /**
     * @param <T>
     *            the node type
     * @param node
     *            the input node
     * @param toString
     *            the toString function
     * @return a string for the given node's path using the toString function for each node in the path
     */
    public static <T> String toPathString(final TreeNode<T> node, final Function<? super TreeNode<T>, String> toString) {
        return toPathString(node, toString, TREE_PATH_DELIM);
    }

    /**
     * @param node
     *            the node whos path will be calculated
     * @param separator
     *            the path separator
     * @return a string for the given node
     */
    public static String toPathString(final TreeNode<?> node, final String separator) {
        if (node == null) {
            return "null";
        }
        final Function<Object, String> toString = Functions.toStringFunction();
        return toPathString(node, toString, separator);
    }

    /**
     * @param node
     *            the node for which a string should be returned
     * @param toString
     *            a function used to convert the node to a string
     * @param separator
     *            the separator to use between nodes
     * @return a string containing all parent nodes using the given separator and to-string function
     */
    public static <T> String toPathString(final TreeNode<T> node, final Function<? super TreeNode<T>, String> toString,
            final String separator) {
        final Collection<TreeNode<T>> path = getPath(node);
        final Collection<String> names = Collections2.transform(path, toString);
        return Joiner.on(separator).join(names);
    }

    /**
     * @param node
     *            the input node for which its depth will be computed
     * @return the depth of the given node
     */
    public static int getDepth(final TreeNode<?> node) {
        if (node instanceof Node<?>) {
            final Node<?> impl = (Node<?>) node;
            return impl.getDepth();
        }

        return calculateDepth(node);
    }

    /**
     * similar to {@link #getDepth(TreeNode)}, though without any additional caching or side-effects
     * 
     * @return the depth of the given node
     */
    static int calculateDepth(final TreeNode<?> node) {
        if (node == null || node.getParent() == null) {
            return 0;
        }
        return 1 + getDepth(node.getParent());
    }

    /**
     * @param <T>
     * @param <N>
     * @param node
     *            the node to visit depth-first
     * @param visitor
     *            the visitor used to 'visit' the nodes
     * @param depth
     *            the depth 'delta' to process. i.e., if set to one (1), it will proceed to a maximum of 1 depth further
     * @return the visitor
     */
    public static <T, N extends TreeNode<T>, V extends TreeVisitor<N>> V depthFirstPlusDepth(final N node,
            final V visitor, final int depth) {
        Preconditions.checkArgument(depth > 0, "Depth must be positive: " + depth);
        final Predicate<N> condition = plusDepthCondition(node, depth);
        depthFirstConditional(node, visitor, condition);
        return visitor;
    }

    /**
     * traverse the node tree with the given visitor, depth-first
     * 
     * @param node
     *            the input node
     * @param <T>
     *            the node data type
     * @param <N>
     *            the node type
     * @param <V>
     *            the visitor type
     * 
     * @param visitor
     * @return the visitor
     */
    public final static <T, N extends TreeNode<T>, V extends TreeVisitor<N>> V depthFirst(final N node, final V visitor) {
        final Predicate<N> condition = Predicates.alwaysTrue();
        depthFirstConditional(node, visitor, condition);
        return visitor;
    }

    /**
     * @param <T>
     * @param <N>
     * @param node
     *            the node on which to operate
     * @param logic
     *            the predicate logic
     * @return true if the predicate returns 'true' for all the nodes
     */
    public final static <T, N extends TreeNode<T>> boolean all(final N node, final Predicate<? super N> logic) {
        final AtomicBoolean allTrue = new AtomicBoolean(true);
        final TreeVisitor<N> visitor = new TreeVisitorAdapter<N>();
        final Predicate<N> continuePredicate = new Predicate<N>() {
            @Override
            public boolean apply(final N nodeParam) {
                final boolean result = logic.apply(nodeParam);
                allTrue.compareAndSet(true, result);
                return result;
            }
        };
        depthFirstConditional(node, visitor, continuePredicate);
        return allTrue.get();
    }

    /**
     * @param <T>
     * @param <N>
     * @param node
     *            the node on which to operate
     * @param logic
     *            the predicate logic
     * @return true if the predicate returns 'true' for any nodes
     */
    public final static <T, N extends TreeNode<T>> boolean any(final N node, final Predicate<? super N> logic) {
        final AtomicBoolean anyTrue = new AtomicBoolean(false);
        final TreeVisitor<N> visitor = new TreeVisitorAdapter<N>();
        final Predicate<N> continuePredicate = new Predicate<N>() {
            @Override
            public boolean apply(final N nodeParam) {
                final boolean result = logic.apply(nodeParam);
                anyTrue.compareAndSet(false, result);
                // continue as long as we still don't have a result
                return !result;
            }
        };
        depthFirstConditional(node, visitor, continuePredicate);
        return anyTrue.get();
    }

    /**
     * a depth-first traversal of the tree, stopping once the predicate returns 'false'
     * 
     * @param <T>
     *            the data type of the tree node
     * @param <N>
     *            the tree node type
     * @param <V>
     *            the visitor type
     * @param node
     * @param visitor
     * @param continuePredicate
     * @return the visitor
     */
    public static <T, N extends TreeNode<T>, V extends TreeVisitor<N>> V depthFirstConditional(final N node,
            final V visitor, final Predicate<N> continuePredicate) {
        depthFirstRecursive(0, node, visitor, continuePredicate);
        return visitor;
    }

    private static <T, N extends TreeNode<T>> void depthFirstRecursive(final int depth, final N node,
            final TreeVisitor<N> visitor, final Predicate<N> continuePredicate) {
        if (!continuePredicate.apply(node)) {
            return;
        }

        visitor.onNode(depth, node);

        for (final TreeNode<T> genericChild : node.getChildren()) {
            @SuppressWarnings("unchecked")
            final N child = (N) genericChild;
            depthFirstRecursive(depth + 1, child, visitor, continuePredicate);
        }
    }

    /**
     * traverse the node tree with the given visitor, depth-first
     * 
     * @param node
     *            the input node
     * @param <T>
     *            the node's data type
     * @param <N>
     *            the node type
     * @param <V>
     *            the visitor type
     * 
     * @param visitor
     *            the visitor
     * @return the visitor
     */
    public final static <T, N extends TreeNode<T>, V extends TreeVisitor<N>> V breadthFirst(final N node,
            final V visitor) {
        final Predicate<N> condition = Predicates.alwaysTrue();
        breadthFirstConditional(node, visitor, condition);
        return visitor;
    }

    /**
     * @param <T>
     *            the node's data type
     * @param <N>
     *            the node type
     * @param node
     *            the node to visit breadth-first
     * @param visitor
     *            the visitor used to 'visit' the nodes
     * @param depth
     *            the depth 'delta' to process. i.e., if set to one (1), it will proceed to a maximum of 1 depth further
     * @return the visitor
     */
    public static <T, N extends TreeNode<T>, V extends TreeVisitor<N>> V breadthFirstPlusDepth(final N node,
            final V visitor, final int depth) {
        Preconditions.checkArgument(depth > 0, "Depth must be positive: " + depth);
        final Predicate<N> condition = plusDepthCondition(node, depth);
        breadthFirstConditional(node, visitor, condition);
        return visitor;
    }

    private static <T, N extends TreeNode<T>> Predicate<N> plusDepthCondition(final N node, final int depth) {
        final int startingDepth = TreeTrait.getDepth(node);
        final int maxDepth = startingDepth + depth;
        final Predicate<N> condition = new Predicate<N>() {
            @Override
            public boolean apply(final N next) {
                final int d = TreeTrait.getDepth(next);
                return d < maxDepth;
            }
        };
        return condition;
    }

    /**
     * @param <T>
     * @param <N>
     * @param node
     *            the node to visit breadth-first
     * @param visitor
     *            the visitor used to 'visit' the nodes
     * @param continuePredicate
     *            the predicate used to determine whether the processing should continue
     * @return the last processed node
     */
    public static <T, N extends TreeNode<T>> N breadthFirstConditional(final N node, final TreeVisitor<N> visitor,
            final Predicate<N> continuePredicate) {
        final Deque<N> queue = Lists.newLinkedList();
        queue.add(node);
        return breadthFirstConditional(queue, visitor, continuePredicate);
    }

    /**
     * @param node
     * @return true if the given node is the root node
     */
    public static boolean isRoot(final TreeNode<?> node) {
        return node.getParent() == null;
    }

    /**
     * @param node
     * @return true if the node is a leaf node
     */
    public static boolean isLeaf(final TreeNode<?> node) {
        return Iterables.isEmpty(node.getChildren());
    }

    /**
     * @param node
     *            the node for which to find the root node
     * @return the root node
     */
    @SuppressWarnings("unchecked")
    public static <T, N extends TreeNode<T>> N getRoot(final N node) {
        if (isRoot(node)) {
            return node;
        }
        return (N) getRoot(node.getParent());
    }

    /**
     * @param <T>
     * @param node
     * @return the number of nodes under the given node
     */
    public static <T> int sizeOf(final TreeNode<T> node) {
        final SizeVisitor<T> sizeVisitor = new SizeVisitor<T>();
        return sizeVisitor.sizeOf(node);
    }

    private static <T, N extends TreeNode<T>> N breadthFirstConditional(final Deque<N> queue,
            final TreeVisitor<N> visitor, final Predicate<N> continuePredicate) {
        N node = null;
        while (!queue.isEmpty()) {
            node = queue.removeFirst();

            if (!continuePredicate.apply(node)) {
                return node;
            }

            // TODO - this could get really expensive, depending on the
            // implementation
            final int depth = getDepth(node);

            visitor.onNode(depth, node);

            @SuppressWarnings("unchecked")
            final Iterable<N> children = (Iterable<N>) node.getChildren();

            for (final N child : children) {
                queue.addLast(child);
            }
        }

        return node;
    }

    /**
     * @param <T>
     * @param <N>
     * @param node
     * @param predicate
     * @return the first node which matches the predicate
     */
    public static <T, N extends TreeNode<T>> N findFirst(final N node, final Predicate<N> predicate) {
        if (predicate.apply(node)) {
            return node;
        }
        for (final TreeNode<T> child : node.getChildren()) {
            @SuppressWarnings("unchecked")
            final N found = findFirst((N) child, predicate);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * @param <T>
     * @param source
     * @param nodeToName
     * @param name
     * @return the child for the given tree node using the nodeToName function
     */
    public static <T> TreeNode<T> getChildByName(final TreeNode<T> source,
            final Function<? super TreeNode<T>, String> nodeToName, final String name) {
        for (final TreeNode<T> child : source.getChildren()) {
            if (nodeToName.apply(child).equals(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * @param <T>
     * @param source
     * @param nodeToName
     * @param name
     * @return the child for the given tree node using the nodeToName function
     */
    public static <T> TreeNode<T> getChildByNameDeep(final TreeNode<T> source,
            final Function<? super TreeNode<T>, String> nodeToName, final String name) {
        final Predicate<TreeNode<T>> continuePredicate = new Predicate<TreeNode<T>>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public boolean apply(final TreeNode<T> node) {
                final boolean stop = isNameEquals(node, nodeToName, name);
                final boolean keepGoing = !stop;
                return keepGoing;
            }

        };
        final TreeVisitor<TreeNode<T>> visitor = TreeVisitors.noOp();
        final TreeNode<T> f = breadthFirstConditional(source, visitor, continuePredicate);
        if (isNameEquals(f, nodeToName, name)) {
            return f;
        }
        return null;
    }

    private static <T> boolean isNameEquals(final TreeNode<T> node,
            final Function<? super TreeNode<T>, String> nodeToName, final String name) {
        if (node == null) {
            return false;
        }
        final String nodeName = nodeToName.apply(node);
        return nodeName.equals(name);
    }

    /**
     * @param <T>
     * @param source
     * @param pathParam
     * @return the tree node by the given path using 'toString' to determine the node names
     */
    public static <T> TreeNode<T> findByPath(final TreeNode<T> source, final String pathParam) {
        final Function<? super TreeNode<T>, String> function = Functions.toStringFunction();
        return findByPath(source, function, pathParam);
    }

    /**
     * find the given node for the path
     * 
     * @param <T>
     * @param source
     * @param nodeToNameFunction
     * @param pathParam
     *            the path
     * @return the node for the given slash-delimited ('/') node path
     */
    public static <T> TreeNode<T> findByPath(final TreeNode<T> source,
            final Function<? super TreeNode<T>, String> nodeToNameFunction, final String pathParam) {
        // start at the current node
        TreeNode<T> result = source;
        String path = pathParam;
        path = adjustForRootPath(source, nodeToNameFunction, path);
        final Iterable<String> nodes = split(path);

        // walk the 'result' node down the tree (/A/B/C). If it is ever 'null',
        // then a child did not exist
        for (final String nextNodeName : nodes) {
            // a child was not found
            if (result == null) {
                return result;
            }
            // get the next child in the path (node)
            result = getChildByName(result, nodeToNameFunction, nextNodeName);
        }
        return result;
    }

    /**
     * adjust the path based on the given node, using the function for the conversion between the node and node names.
     * 
     * The intent is this:
     * 
     * Given an ITreeNode (who's nodeToName function resolves it as 'A'), then when 'A' is asked to parse the path A/B/C
     * , this resolves to 'B/C'
     * 
     * @param <T>
     * @param source
     * @param nodeToName
     * @param pathParam
     * @return
     */
    static <T> String adjustForRootPath(final TreeNode<T> source,
            final Function<? super TreeNode<T>, String> nodeToName, final String pathParam) {
        String path = pathParam;
        final Iterable<String> nodes = split(path);
        final Iterator<String> iter = nodes.iterator();
        final String nodeName = nodeToName.apply(source);
        if (iter.hasNext() && iter.next().equals(nodeName)) {
            path = join(Iterables.skip(nodes, 1));
        }
        return path;
    }

    /**
     * @param tree
     *            a slash-separated tree path
     * @return the individual parts of the slash-separated tree path
     */
    static Iterable<String> split(final String tree) {
        return Splitter.on(TREE_PATH_DELIM).trimResults().omitEmptyStrings().split(tree);
    }

    private static String join(final Iterable<String> nodes) {
        return Joiner.on(TREE_PATH_DELIM).join(nodes);
    }

    /**
     * @param <T>
     * @param source
     * @return the leaf nodes for the given source
     */
    public static <T> Collection<TreeNode<T>> getLeafNodes(final TreeNode<T> source) {
        final LeafCollectionVisitor<T, TreeNode<T>> leafCollection = TreeVisitors.leafCollection();
        final Collection<TreeNode<T>> leaves = leafCollection.depthFirst(source);
        return leaves;
    }

    /**
     * @param <T>
     * @param source
     * @return the leaf nodes for the given source
     */
    public static <T> Collection<TreeNode<T>> getPath(final TreeNode<T> source) {
        final Collection<TreeNode<T>> parents = Lists.newArrayList();
        getPathRecursive(parents, source);
        return parents;
    }

    private static <T> void getPathRecursive(final Collection<TreeNode<T>> parents, final TreeNode<T> source) {
        if (source == null) {
            return;
        }
        getPathRecursive(parents, source.getParent());
        parents.add(source);
    }

    /**
     * @param <T>
     * @param source
     * @param name
     * @return a collection of the tree leaf 'paths'
     */
    public static <T> Collection<String> getLeafPaths(final TreeNode<T> source,
            final Function<? super TreeNode<?>, String> name) {
        final Collection<TreeNode<T>> nodes = getLeafNodes(source);
        final Function<TreeNode<T>, String> toPath = new Function<TreeNode<T>, String>() {
            @Override
            public String apply(final TreeNode<T> leaf) {
                return toPathString(leaf, name, TREE_PATH_DELIM);
            }
        };
        return Collections2.transform(nodes, toPath);
    }

    /**
     * Get the maximum depth of the tree. The root node is at depth zero, so a tree containing the paths:
     * 
     * <pre>
     * a/b/c1 
     * a/b/c2 
     * a/b1
     * </pre>
     * 
     * would have a max depth of 2
     * 
     * @param <T>
     * @param node
     *            the node under which the max depth will be found
     * @return the (zero-based) maximum depth of the tree
     */
    public static <T> int getMaxDepth(final TreeNode<T> node) {
        final TreeNode<T> firstDeepestNode = getFirstDeepestNode(node);
        return getDepth(firstDeepestNode);
    }

    /**
     * @param <T>
     * @param startNode
     * @param depth
     * @return all nodes at the given depth
     */
    public static <T> Collection<TreeNode<T>> getNodesAtDepth(final TreeNode<T> startNode, final int depth) {
        final TreeNode<T> root = getRoot(startNode);

        final Collection<TreeNode<T>> nodes = Lists.newArrayList();
        final TreeVisitor<TreeNode<T>> visitor = new TreeVisitor<TreeNode<T>>() {
            @Override
            public void onNode(final int d, final TreeNode<T> treeNode) {
                if (d == depth) {
                    nodes.add(treeNode);
                }
            }
        };
        depthFirstPlusDepth(root, visitor, depth + 1);
        return nodes;
    }

    /**
     * filter the leaf nodes from the collection of nodes
     * 
     * @param <T>
     *            the node data type
     * @param nodes
     *            the input nodes
     * @return the node leaves for the given nodes
     */
    public static <T> Collection<TreeNode<T>> filterLeaves(final Collection<TreeNode<T>> nodes) {
        final Predicate<TreeNode<T>> predicate = new Predicate<TreeNode<T>>() {
            @Override
            public boolean apply(final TreeNode<T> node) {
                return TreeTrait.isLeaf(node);
            }
        };
        return Collections2.filter(nodes, predicate);
    }

    /**
     * @param <T>
     * @param node
     *            the parent node under which to search
     * @return the deepest node under the given parent node
     */
    public static <T> TreeNode<T> getFirstDeepestNode(final TreeNode<T> node) {
        int max = -1;
        final Collection<TreeNode<T>> leaves = getLeafNodes(node);
        TreeNode<T> deepest = node;
        for (final TreeNode<T> leaf : leaves) {
            final int depth = getDepth(leaf);
            if (depth > max) {
                deepest = leaf;
                max = depth;
            }
        }
        return deepest;
    }

    /**
     * Find the next sibling to a node.
     * 
     * For example, given the tree:
     * 
     * <pre>
     * A + -B + -C + -D + -E + -F
     * </pre>
     * 
     * The next sibling to node 'C' is 'E', the next sibling to 'D' is 'F', and the next sibling to 'A' is null
     * 
     * @param <T>
     * @param node
     *            the node for whom to find the next sibling
     * @return the next sibling
     */
    public static <T> TreeNode<T> findNextSibling(final TreeNode<T> node) {
        final int nodeIndex = indexOf(node);
        if (nodeIndex == -1) {
            return null;
        }
        final Iterable<? extends TreeNode<T>> children = node.getParent().getChildren();
        final int size = Iterables.size(children);

        // are we the last child? if so, we need to return the first child of
        // our parent's next sibling
        final boolean isLastChildOfOurParent = nodeIndex == size - 1;

        TreeNode<T> next = null;
        if (isLastChildOfOurParent) {
            // find the next sibling of the parent node who has children
            final TreeNode<T> nextParentSiblingWithChildren = findNextSiblingWithChildren(node);
            if (nextParentSiblingWithChildren != null) {
                // return the first child of the first node next to our parent
                // with children
                next = Iterables.get(nextParentSiblingWithChildren.getChildren(), 0);
            }
        } else {
            next = Iterables.get(children, nodeIndex + 1);
        }
        return next;
    }

    private static <T> TreeNode<T> findNextSiblingWithChildren(final TreeNode<T> node) {
        TreeNode<T> nextParentSiblingWithChildren = findNextSibling(node.getParent());
        while (nextParentSiblingWithChildren != null && Iterables.isEmpty(nextParentSiblingWithChildren.getChildren())) {
            nextParentSiblingWithChildren = findNextSibling(nextParentSiblingWithChildren);
        }
        return nextParentSiblingWithChildren;
    }

    /**
     * @param <T>
     * @param node
     * @return the index of the given node as a child of its parent
     */
    public static <T> int indexOf(final TreeNode<T> node) {
        if (isRoot(node)) {
            return -1;
        }
        final TreeNode<T> parent = node.getParent();
        final Iterable<? extends TreeNode<T>> kids = parent.getChildren();
        final Predicate<TreeNode<T>> equals = new Predicate<TreeNode<T>>() {
            @Override
            public boolean apply(final TreeNode<T> n) {
                return n == node;
            }
        };
        return Iterables.indexOf(kids, equals);
    }

    /**
     * @param nodes
     * @return the common parent for the given nodes
     */
    public static <T> TreeNode<T> findCommonAncester(final Collection<? extends TreeNode<T>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        final List<TreeNode<T>> nodesList = ImmutableList.copyOf(nodes);
        final TreeNode<T> firstNode = nodesList.get(0);
        if (nodes.size() == 1) {
            return firstNode;
        }
        return findCommonAncester(firstNode, nodesList.subList(1, nodes.size()));
    }

    /**
     * find the common parent for a collection of nodes
     * 
     * @param nodes
     * @return the common parent from a collection of nodes
     */
    private static <T> TreeNode<T> findCommonAncester(final TreeNode<T> first, final List<? extends TreeNode<T>> theRest) {
        if (theRest == null || theRest.size() < 2) {
            return first;
        }
        TreeNode<T> common = first;
        for (final TreeNode<T> other : theRest.subList(1, theRest.size())) {
            common = findCommonAncester(common, other);
        }
        return common;
    }

    /**
     * @param <T>
     * @param first
     * @param second
     * @return the common ancester of the two nodes
     */
    public static <T> TreeNode<T> findCommonAncester(final TreeNode<T> first, final TreeNode<T> second) {
        if (first == null || second == null) {
            return null;
        }
        // do a quick test before any further work
        if (first == second) {
            return first;
        }

        /**
         * To get to the same depth, we choose to always work in one 'direction'. Either is as good as the other, so
         * lets always assume the left side to be more shallow than the right:
         */
        final int depth = getDepth(first);
        if (depth > getDepth(second)) {
            return findCommonAncester(second, first);
        }

        /**
         * Now, get both nodes to the same depth
         */
        TreeNode<T> right = second;
        final int secondDepth = getDepth(right);
        for (int i = secondDepth; i != depth; i--) {
            right = right.getParent();
        }
        assert depth == getDepth(right);

        /**
         * until a common parent is found. The only way this would NOT work is if the nodes were from two different
         * trees. If that is the case, we could return null, but here we choose rather to just let them NPE, as clients
         * of this class really shouldn't be mixing two navigation trees
         */
        TreeNode<T> left = first;
        while (left != right) {
            left = left.getParent();
            right = right.getParent();
        }

        // either left or right will do
        assert left == right;
        assert left != null;
        return left;
    }

    /**
     * @param <T>
     * @param nodes
     * @return a collection of the type wrapped within a tree node
     */
    public static <T> Collection<T> stripData(final Collection<TreeNode<T>> nodes) {
        final Function<TreeNode<T>, T> transform = asData();
        return Collections2.transform(nodes, transform);
    }

    /**
     * @param <T>
     * @return a function to return the tree node as its contained data type
     */
    public static <T> Function<TreeNode<T>, T> asData() {
        final Function<TreeNode<T>, T> transform = new Function<TreeNode<T>, T>() {
            @Override
            public T apply(final TreeNode<T> node) {
                return node.getData();
            }
        };
        return transform;
    }

    /**
     * Return a String representation of the tree
     * 
     * @param <T>
     * @param node
     *            the input node
     * @return the node as a string
     */
    public static <T, N extends TreeNode<T>> String toString(final N node) {
        final Function<N, String> function = new Function<N, String>() {
            @Override
            public String apply(final N arg0) {
                return arg0.toString();
            }
        };
        return toString(node, function);
    }

    /**
     * Return a String representation of the tree
     * 
     * @param <T>
     * @param node
     * @param toString
     * @return the node as a string
     */
    public static <T, N extends TreeNode<T>> String toString(final N node, final Function<N, String> toString) {
        final String newLine = String.format("%n");
        return toStringRecursive(node, toString, newLine);
    }

    @SuppressWarnings("unchecked")
    private static <T, N extends TreeNode<T>> String toStringRecursive(final N node,
            final Function<N, String> toString, final String newLine) {
        final StringBuilder b = new StringBuilder();

        final int depth = getDepth(node);
        final String nodeString = String.format("(%d) %s", Integer.valueOf(depth), toString.apply(node));

        if (depth > 0) {
            final int indent = 4 * depth;
            b.append(Strings.repeat(" ", indent));
        }

        b.append("+--").append(nodeString).append(newLine);

        for (final TreeNode<T> child : node.getChildren()) {
            b.append(toStringRecursive((N) child, toString, newLine));
        }
        return b.toString();
    }
}