package com.porpoise.common.collect.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.porpoise.common.collect.tree.Tree.Node;

/**
 * Test for {@link TreeTrait}
 */
public class TreeTraitTest {
    private Node<Object>              root;
    private TreeVisitor<Node<Object>> visitor;
    private Collection<String>        paths;
    private Node<Object>              b2;
    private Node<Object>              b1;
    private Node<Object>              alpha;
    private Node<Object>              beta;
    private Node<Object>              c1;
    private Node<Object>              c2;
    private Node<Object>              a;

    /**
     * setup the test data
     */
    @Before
    public void setUp() {
        this.root = Tree.parse("/a/b1/c1").getRoot();
        this.root.parse("/a/b1/c2");
        this.root.parse("/a/b2/alpha");
        this.root.parse("/a/b2/beta");

        this.a = this.root.findByName("a");
        this.b1 = this.root.findByName("b1");
        this.b2 = this.root.findByName("b2");
        this.c1 = this.root.findByName("c1");
        this.c2 = this.root.findByName("c2");
        this.alpha = this.root.findByName("alpha");
        this.beta = this.root.findByName("beta");

        this.paths = Lists.newArrayList();
        this.visitor = new TreeVisitor<Node<Object>>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void onNode(final int depth, final Node<Object> node) {
                TreeTraitTest.this.paths.add(node.getPath());
            }
        };
    }

    /**
     * test for {@link TreeTrait#sizeOf(TreeNode)}
     */
    @Test
    public void test_sizeOf() {
        Assert.assertEquals(7, TreeTrait.sizeOf(this.root));
        Assert.assertEquals(3, TreeTrait.sizeOf(this.root.findByName("b1")));
        Assert.assertEquals(3, TreeTrait.sizeOf(this.root.findByName("b2")));
        Assert.assertEquals(1, TreeTrait.sizeOf(this.root.findByName("c1")));
    }

    /**
     * test for {@link TreeTrait#depthFirstPlusDepth(TreeNode, TreeVisitor, int)}
     */
    @Test
    public void test_depthFirstConditionalToDepthTwo() {
        Assert.assertTrue("Precondition failed, the paths should not have been populated", this.paths.isEmpty());
        TreeTrait.depthFirstPlusDepth(this.root, this.visitor, 2);
        final Iterator<String> iterator = this.paths.iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("a/b1", iterator.next());
        Assert.assertEquals("a/b2", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * test for {@link TreeTrait#depthFirstPlusDepth(TreeNode, TreeVisitor, int)}
     */
    @Test
    public void test_depthFirstConditionalToDepthOne() {
        Assert.assertTrue("Precondition failed, the paths should not have been populated", this.paths.isEmpty());
        TreeTrait.depthFirstPlusDepth(this.root, this.visitor, 1);
        final Iterator<String> iterator = this.paths.iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * test for {@link TreeTrait#depthFirst(TreeNode, TreeVisitor) }
     */
    @Test
    public void test_depthFirst() {
        Assert.assertTrue("Precondition failed, the paths should not have been populated", this.paths.isEmpty());
        TreeTrait.depthFirst(this.root, this.visitor);
        final Iterator<String> iterator = this.paths.iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("a/b1", iterator.next());
        Assert.assertEquals("a/b1/c1", iterator.next());
        Assert.assertEquals("a/b1/c2", iterator.next());
        Assert.assertEquals("a/b2", iterator.next());
        Assert.assertEquals("a/b2/alpha", iterator.next());
        Assert.assertEquals("a/b2/beta", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * test for {@link TreeTrait#breadthFirst(TreeNode, TreeVisitor)}
     */
    @Test
    public void test_breadthFirst() {
        TreeTrait.breadthFirst(this.root, this.visitor);
        final Iterator<String> iterator = this.paths.iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("a/b1", iterator.next());
        Assert.assertEquals("a/b2", iterator.next());
        Assert.assertEquals("a/b1/c1", iterator.next());
        Assert.assertEquals("a/b1/c2", iterator.next());
        Assert.assertEquals("a/b2/alpha", iterator.next());
        Assert.assertEquals("a/b2/beta", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * test for {@link TreeTrait#findFirst(TreeNode, com.google.common.base.Predicate)}
     */
    @Test
    public void test_findFirst() {
        final Predicate<Node<Object>> withTwo = new Predicate<Node<Object>>() {
            @Override
            public boolean apply(final Node<Object> node) {
                return node.getName().contains("2");
            }
        };
        final Node<Object> found = TreeTrait.findFirst(this.root, withTwo);
        Assert.assertEquals("c2", found.getName());

        final Predicate<Node<Object>> first = Predicates.alwaysTrue();
        final Node<Object> aNode = TreeTrait.findFirst(this.root, first);
        Assert.assertEquals("a", aNode.getName());
    }

    /**
     * test for {@link TreeTrait#findFirst(TreeNode, com.google.common.base.Predicate)}
     */
    @Test
    public void test_findFirstNoResults() {
        final Predicate<Node<Object>> filter = Predicates.alwaysFalse();
        final Node<Object> found = TreeTrait.findFirst(this.root, filter);
        Assert.assertNull(found);
    }

    /**
     * Test for {@link TreeTrait#findByPath(TreeNode, com.google.common.base.Function, String)}
     */
    @Test
    public void test_findByPath() {
        final Node<Object> c = Tree.parse("/a/b/c");
        Assert.assertSame(c.getParent(), TreeTrait.findByPath(c.getRoot(), Tree.name(), "/a/b"));
        Assert.assertSame(c, TreeTrait.findByPath(c.getRoot(), Tree.name(), "a/b/c"));
        Assert.assertNull(TreeTrait.findByPath(c.getRoot(), Tree.name(), "d"));
        Assert.assertNull(TreeTrait.findByPath(c.getRoot(), Tree.name(), "a/B/c"));
    }

    /**
     * Test for {@link TreeTrait#getRoot(TreeNode)}
     */
    @Test
    public void test_getRoot() {
        final Node<Object> newRoot = Tree.newTree("ROOT_NODE");
        final Node<Object> two = newRoot.addChild("one").addChild("two");
        Assert.assertEquals("precondition failed: the root node does not have the expected children",
                "ROOT_NODE/one/two", two.getPath());

        Assert.assertTrue("precondition failed - our root somehow isn't actually the root(?)",
                TreeTrait.isRoot(newRoot));
        Assert.assertTrue("precondition failed - our root somehow isn't actually the root(?)", newRoot.isRoot());

        // call the method under test
        Assert.assertSame(newRoot, TreeTrait.getRoot(newRoot));
        Assert.assertSame(newRoot, TreeTrait.getRoot(two));
        Assert.assertSame(newRoot, TreeTrait.getRoot(newRoot.addChild("three")));
    }

    /**
     * test for TreeTrait#getLeafNodes(TreeNode)
     */
    @Test
    public void test_getLeafNodes() {
        final Collection<TreeNode<Object>> leaves = TreeTrait.getLeafNodes(this.root);
        final Iterator<TreeNode<Object>> leafIter = leaves.iterator();
        Assert.assertEquals("c1", Tree.name().apply(leafIter.next()));
        Assert.assertEquals("c2", Tree.name().apply(leafIter.next()));
        Assert.assertEquals("alpha", Tree.name().apply(leafIter.next()));
        Assert.assertEquals("beta", Tree.name().apply(leafIter.next()));
        Assert.assertFalse(leafIter.hasNext());
    }

    /**
     * Test for {@link TreeTrait#getLeafPaths(TreeNode, com.google.common.base.Function)}
     */
    @Test
    public void test_getLeafPaths() {
        final Collection<String> leaves = TreeTrait.getLeafPaths(this.root, Tree.name());
        assertCollection(leaves, "a/b1/c1",//
                "a/b1/c2",//
                "a/b2/alpha",//
                "a/b2/beta"//
        );

        // only get a subset of the leaves
        final Collection<String> b1Leaves = TreeTrait.getLeafPaths(this.root.findByName("b1"), Tree.name());
        assertCollection(b1Leaves, "a/b1/c1",//
                "a/b1/c2"//
        );

        // create a new tree from the b1 node -- the 'a/' root prefix should no
        // longer appear
        final Collection<String> newTreeLeaves = TreeTrait.getLeafPaths(this.root.findByName("b1").copy(), Tree.name());
        assertCollection(newTreeLeaves, "b1/c1",//
                "b1/c2"//
        );
    }

    private <T> void assertCollection(final Collection<T> collection, final T... args) {
        final boolean equals = Arrays.equals(collection.toArray(), args);
        Assert.assertTrue(String.format("%s != %s", collection, Arrays.asList(args)), equals);
    }

    /**
     * Test for {@link TreeTrait#toPathString(TreeNode, String)}
     */
    @Test
    public void test_toPathString() {
        final String pathString = TreeTrait.toPathString(this.root.parse("l/m/n/o"), Tree.name(), "-");
        Assert.assertEquals("a-l-m-n-o", pathString);
    }

    /**
     * test for {@link TreeTrait#getMaxDepth(TreeNode)}
     */
    @Test
    public void test_getMaxDepth() {
        Assert.assertEquals(2, TreeTrait.getMaxDepth(this.root));
        this.root.parse("1/2/3/4/5/6/7/8");
        Assert.assertEquals("The max depth should now be eight", 8, TreeTrait.getMaxDepth(this.root));
        Assert.assertEquals("The max depth down the 1/2/3/4... path should still be 8", 8,
                TreeTrait.getMaxDepth(this.root.findByName("4")));
        Assert.assertEquals("The max depth for the alpha node should be two", 2,
                TreeTrait.getMaxDepth(this.root.findByName("alpha")));
    }

    /**
     * Test for {@link TreeTrait#getChildByNameDeep(TreeNode, com.google.common.base.Function, String)}
     */
    @Test
    public void test_getChildByNameDeep() {
        final Node<Object> b1C1 = Tree.parse("/A/B1/C1");
        final Node<Object> aNode = b1C1.getRoot();
        final Node<Object> bOne = aNode.getChildByName("B1");
        final Node<Object> b1C2 = bOne.addChild("C2");
        final Node<Object> bTwo = aNode.parse("B2");
        final Node<Object> b2C1 = bTwo.addChild("C1");
        final Node<Object> b2C2 = aNode.parse("B2/C2");

        // should have the tree:
        // /A/B1/C1
        // /A/B1/C2
        // /A/B2/C1
        // /A/B2/C2

        final TreeNode<Object> actual = TreeTrait.getChildByNameDeep(aNode, Tree.name(), "C1");
        Assert.assertSame(b1C1, actual);
        Assert.assertSame(b1C2, TreeTrait.getChildByNameDeep(aNode, Tree.name(), "C2"));

        Assert.assertSame(b1C1, TreeTrait.getChildByNameDeep(bOne, Tree.name(), "C1"));
        Assert.assertSame(b1C2, TreeTrait.getChildByNameDeep(bOne, Tree.name(), "C2"));

        Assert.assertSame(b2C1, TreeTrait.getChildByNameDeep(bTwo, Tree.name(), "C1"));
        Assert.assertSame(b2C2, TreeTrait.getChildByNameDeep(bTwo, Tree.name(), "C2"));
    }

    /**
     * Test for TreeTrait#
     */
    @Test
    public void test_findCommonAncester() {
        Assert.assertEquals(this.b2, TreeTrait.findCommonAncester(this.beta, this.alpha));
        Assert.assertEquals(this.b1, TreeTrait.findCommonAncester(this.c2, this.c1));
        Assert.assertEquals(this.a, TreeTrait.findCommonAncester(this.c2, this.beta));

        final Collection<TreeNode<Object>> leaves = TreeTrait.getLeafNodes(this.root);
        Assert.assertEquals(this.a, TreeTrait.findCommonAncester(leaves));
    }

    /**
     * Test for {@link TreeTrait#findNextSibling(TreeNode)}
     */
    @Test
    public void test_findNextSibling() {
        Assert.assertSame(this.c2, TreeTrait.findNextSibling(this.c1));
        Assert.assertSame(this.alpha, TreeTrait.findNextSibling(this.c2));
        Assert.assertSame(this.beta, TreeTrait.findNextSibling(this.alpha));
        Assert.assertNull(TreeTrait.findNextSibling(this.beta));
        Assert.assertSame(this.b2, TreeTrait.findNextSibling(this.b1));

        // also test a tree with varying levels, and a gap between children:
        /**
         * <pre>
         * 
         * +--(0) 1
         *     +--(1) 2
         *         +--(2) 3
         *             +--(3) 4
         *                 +--(4) 5
         *                     +--(5) 6
         *     +--(1) some shallow node
         *         +--(2) 3
         *     +--(1) this is a different node
         *         +--(2) 3
         *             +--(3) 4
         *                 +--(4) 5
         *                     +--(5) 6
         * </pre>
         */
        final Node<Object> six1 = Tree.parse("/1/2/3/4/5/6");
        six1.getRoot().parse("some shallow node/3");
        final Node<Object> six2 = six1.getRoot().parse("this is a different node/3/4/5/6");
        Assert.assertSame(six2, TreeTrait.findNextSibling(six1));
    }

    /**
     * test for {@link TreeTrait#getNodesAtDepth(TreeNode, int)}
     */
    @Test
    public void test_getNodesAtDepth() {
        // depth 0
        Collection<TreeNode<Object>> nodes = TreeTrait.getNodesAtDepth(this.root, 0);
        Assert.assertTrue(nodes.contains(this.root));
        Assert.assertEquals(1, nodes.size());

        // depth 1
        nodes = TreeTrait.getNodesAtDepth(this.root, 1);
        Assert.assertTrue(nodes.contains(this.b1));
        Assert.assertTrue(nodes.contains(this.b2));
        Assert.assertEquals(2, nodes.size());

        // depth 2
        nodes = TreeTrait.getNodesAtDepth(this.root, 2);
        Assert.assertTrue(nodes.contains(this.alpha));
        Assert.assertTrue(nodes.contains(this.beta));
        Assert.assertTrue(nodes.contains(this.c1));
        Assert.assertTrue(nodes.contains(this.c2));
        Assert.assertEquals(4, nodes.size());

        // there are no nodes at depth 3
        nodes = TreeTrait.getNodesAtDepth(this.root, 3);
        Assert.assertTrue(nodes.isEmpty());

        // add some (unevent) leaf nodes at depth 3
        final Node<Object> alphaChild = this.alpha.parse("child");
        final Node<Object> c1Child = this.c2.parse("child");

        nodes = TreeTrait.getNodesAtDepth(this.root, 3);
        Assert.assertTrue(nodes.contains(alphaChild));
        Assert.assertTrue(nodes.contains(c1Child));
        Assert.assertEquals(2, nodes.size());
    }

    /**
     * Test for {@link TreeTrait#all(TreeNode, Predicate)} and Test for {@link TreeTrait#any(TreeNode, Predicate)}
     */
    @Test
    public void test_allAndAnyForAlwaysTrue() {
        final Predicate<Node<Object>> logic = Predicates.alwaysTrue();
        Assert.assertTrue(TreeTrait.all(this.root, logic));
        Assert.assertTrue(TreeTrait.any(this.root, logic));
    }

    /**
     */
    @Test
    public void test_toString() {
        final String actual = TreeTrait.toString(this.root);
        final String expected = //
        /*    */String.format("+-a%n") + //
                String.format("  | %n") + //
                String.format("  +-b1%n") + //
                String.format("  | | %n") + //
                String.format("  | +-c1%n") + //
                String.format("  | | %n") + //
                String.format("  | +-c2%n") + //
                String.format("  | %n") + //
                String.format("  +-b2%n") + //
                String.format("    | %n") + //
                String.format("    +-alpha%n") + //
                String.format("    | %n") + //
                String.format("    +-beta%n");

        final Splitter splitter = Splitter.on(String.format("%n"));
        final Iterator<String> e = splitter.split(expected).iterator();
        final Iterator<String> a = splitter.split(actual).iterator();
        int i = 0;
        while (a.hasNext() && e.hasNext()) {
            Assert.assertEquals("line " + i, a.next(), e.next());
            i++;
        }
        Assert.assertFalse(a.hasNext());
        Assert.assertFalse(e.hasNext());
    }

    /**
     * Test for {@link TreeTrait#all(TreeNode, Predicate)} and Test for {@link TreeTrait#any(TreeNode, Predicate)}
     */
    @Test
    public void test_allAndAny() {
        final Predicate<Node<Object>> logic = new Predicate<Node<Object>>() {
            @Override
            public boolean apply(final Node<Object> arg0) {
                return arg0.getName().equals("b2");
            }
        };
        Assert.assertFalse(TreeTrait.all(this.root, logic));
        Assert.assertTrue(TreeTrait.any(this.root, logic));
    }
}