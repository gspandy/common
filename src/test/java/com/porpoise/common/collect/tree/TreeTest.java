package com.porpoise.common.collect.tree;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.porpoise.common.collect.tree.Tree.Node;

/**
 * Test for {@link Tree}
 */
public class TreeTest {

    /**
     * Test for {@link Node#multiply(int)}
     */
    @Test
    public void test_multiply() {
        final Node<Object> tree = Tree.parse("root/a/seven").getRoot();

        // call the method under test
        final Node<Object> a = tree.getChildByName("a").multiply(3);
        final Collection<String> paths = TreeTrait.getLeafPaths(a.getRoot(), Tree.name());
        final Iterator<String> iter = paths.iterator();
        Assert.assertEquals("root/a/seven", iter.next());
        Assert.assertEquals("root/a1/seven", iter.next());
        Assert.assertEquals("root/a2/seven", iter.next());
        Assert.assertEquals("root/a3/seven", iter.next());
        Assert.assertFalse(iter.hasNext());

    }

    /**
     * Test for {@link Tree.Node#addAll(Node)}
     */
    @Test
    public void test_addAll() {
        final Node<Object> treeOne = Tree.parse("a/b/c/d").getRoot();
        final Node<Object> treeTwo = Tree.parse("1/2/3/4").getRoot();
        treeOne.addAll(treeTwo);
        treeOne.findByName("c").addAll(treeTwo);

        final Collection<String> paths = TreeTrait.getLeafPaths(treeOne, Tree.name());
        System.out.println(paths);
        final Iterator<String> iter = paths.iterator();
        Assert.assertEquals("a/1/2/3/4", iter.next());
        Assert.assertEquals("a/b/c/1/2/3/4", iter.next());
        Assert.assertEquals("a/b/c/d", iter.next());
        Assert.assertFalse(iter.hasNext());

        // check the nodes aren't actually the same
        Assert.assertFalse(treeOne.findByPath("a/b/c/1/2/3/4").equals(treeTwo.findByName("4")));
    }

    /**
     * Test for {@link Tree#newTree(String)}
     */
    @Test
    public void test_newTreeWithSimpleName() {
        final Node<Object> root = Tree.newTree("some root");
        Assert.assertEquals(root.getName(), "some root");
        assertRoot(root);
        Assert.assertTrue(!root.hasChildren());
    }

    /**
     * Test for {@link Node#copy()}
     */
    @Test
    public void test_copy() {
        final Node<Object> a = Tree.parse("a/b/c/d").getRoot();
        Assert.assertEquals(4, a.size());
        final Node<Object> g = a.parse("e/f/g");
        Assert.assertEquals(7, a.size());

        // call the method under test - create a copy
        final Node<Object> copy = a.copy();

        // assert a node is NOT the same instance, but DOES have the same name
        Assert.assertNotSame(g, copy.findByPath("e/f/g"));
        Assert.assertEquals(g.getName(), copy.findByPath("e/f/g").getName());
        Assert.assertEquals("The copy should be the same 'size' as the original", 7, copy.size());
        Assert.assertEquals(a.toString(), copy.toString());

        // alter the copy - the original should be unchanged
        final Node<Object> m = copy.parse("h/k/m");
        Assert.assertEquals("The copy should be changed", 10, copy.size());
        Assert.assertEquals("The original node should be unchanged", 7, a.size());
        Assert.assertFalse(a.toString().equals(copy.toString()));

        Assert.assertSame("The new 'm' node should exist in the copy", m, copy.findByPath("h/k/m"));
        Assert.assertNull("The 'm' node should not be in the original", a.findByPath("h/k/m"));
    }

    /**
     * Test for {@link TreeTrait#getDepth(TreeNode)}
     */
    @Test
    public void test_getDepth() {
        final Node<Object> node = Tree.parse("root/grandparent1/parent1/child1/grandchild1");
        Assert.assertEquals(4, node.getDepth());
        Assert.assertEquals(4, TreeTrait.getDepth(node));
        Assert.assertEquals(4, TreeTrait.calculateDepth(node));
    }

    /**
     * We can use copy to create a new subtree
     */
    @Test
    public void test_copySubnode() {
        final Node<Object> root = Tree.parse("root/grandparent1/parent1/child1/grandchild1").getRoot();
        root.parse("grandparent1/parent1/child1/grandchild2");
        root.parse("grandparent1/parent1/child2");
        root.parse("grandparent2");

        // copy a node to create a new tree at that node
        final Node<Object> newParentRoot = root.findByPath("grandparent1/parent1").copy();
        Assert.assertEquals(0, newParentRoot.getDepth());
        Assert.assertNull(newParentRoot.getParent());
        Assert.assertTrue(newParentRoot.isRoot());
        Assert.assertNotNull(newParentRoot.findByPath("child1/grandchild1"));
        Assert.assertEquals(5, newParentRoot.size());

    }

    /**
     * Test for {@link Node#findByPath(String)}
     */
    @Test
    public void test_findByPath() {
        final Node<Object> root = Tree.parse("a/b/c").getRoot();
        final Node<Object> c2 = root.parse("/a/b/c2");
        final Node<Object> c3 = root.parse("/a/b2/c3");
        Assert.assertSame(c2, root.findByPath("/a/b/c2"));
        Assert.assertSame(c3, root.findByPath("/a/b2").findByPath("c3"));
        Assert.assertNull(root.findByPath("/does/not/exist"));
    }

    /**
     * Test for {@link Node#findByPath(String)}
     */
    @Test
    public void test_findByName() {
        final Node<Object> root = Tree.parse("a/b/c").getRoot();
        final Node<Object> c2 = root.parse("/a/b/c2");
        Assert.assertSame(c2, root.findByName("c2"));
        Assert.assertSame(root.findByPath("/a/b"), root.findByName("b"));
    }

    /**
     * Test for {@link Tree#newTree(String)}
     */
    @Test
    public void test_newTreeWithSlashInTheNameThrows() {
        try {
            Tree.newTree("some/root");
            Assert.fail("names with slashes should not be allowed");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test for {@link Tree#parse(String)}
     * 
     * Test we can create a simple tree with parse
     */
    @Test
    public void test_parse() {
        assertAbc();
    }

    /**
     * Test for {@link Tree#parse(String)}
     * 
     * Test we can find nodes using parse
     */
    @Test
    public void test_parseToFindNodes() {
        final Node<Object> root = assertAbc();
        final Node<Object> b = Iterables.getOnlyElement(root.getChildren());
        Assert.assertEquals(1, root.getChildCount());

        // if we parse a node which exists, a new node is NOT created but
        // rather the same node is returned
        final Node<Object> findB = root.parse("b");
        Assert.assertEquals(1, root.getChildCount());
        Assert.assertSame(b, findB);
    }

    /**
     * Test for {@link Tree#parse(String)}
     * 
     * Test we can create new children using parse
     * 
     */
    @Test
    public void test_parseToCreateNodes() {
        final Node<Object> root = assertAbc();
        Assert.assertEquals(1, root.getChildCount());

        // if we parse a node which exists, a new node is NOT created but
        // rather the same node is returned
        final Node<Object> newNode = root.parse("new");
        Assert.assertEquals(2, root.getChildCount());
        Assert.assertEquals("new", newNode.getName());
        Assert.assertSame(root, newNode.getParent());
        Assert.assertFalse(newNode.hasChildren());
    }

    /**
     * test {@link Tree.Node#getPath()}
     */
    @Test
    public void test_getPath() {
        final String path = "8/6/seven/fi/ve/30/9";
        final Node<Object> tree = Tree.parse(path);
        Assert.assertEquals(path, tree.getPath());

    }

    private Node<Object> assertAbc() {
        final Node<Object> root = Tree.parse("a/b/c").getRoot();
        Assert.assertEquals(root.getName(), "a");
        assertRoot(root);
        final Node<Object> b = Iterables.getOnlyElement(root.getChildren());
        Assert.assertEquals("b", b.getName());
        Assert.assertSame(root, b.getParent());

        final Node<Object> c = Iterables.getOnlyElement(b.getChildren());
        Assert.assertEquals("c", c.getName());
        Assert.assertSame(b, c.getParent());
        Assert.assertSame(root, c.getRoot());
        return root;
    }

    private void assertRoot(final Node<Object> root) {
        Assert.assertNull(root.getData());
        Assert.assertNull(root.getParent());
        Assert.assertSame(root, root.getRoot());
    }

}