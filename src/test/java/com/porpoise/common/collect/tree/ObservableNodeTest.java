package com.porpoise.common.collect.tree;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.porpoise.common.collect.tree.ObservableNode.NodeListener;
import com.porpoise.common.collect.tree.Tree.Node;

/**
 * Tests for {@link ObservableNode}
 * 
 */
public class ObservableNodeTest {
    private final ObservableNode<Object> root = ObservableNode.create();

    private Node<Object> lastDataAssociated;

    private Node<Object> lastNodeAdded;

    private final NodeListener<Object> listener = new NodeListener<Object>() {
        @SuppressWarnings("synthetic-access")
        @Override
        public void onDataAssociated(final Node<Object> node) {
            ObservableNodeTest.this.lastDataAssociated = node;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void onNodeAdded(final Node<Object> parent, final Node<Object> child) {
            Assert.assertSame(parent, child.getParent());
            ObservableNodeTest.this.lastNodeAdded = child;
        }
    };

    /**
     * prepare the test data
     */
    @Before
    public void setUp() {
        this.lastDataAssociated = null;
        this.lastNodeAdded = null;
        this.root.addListener(this.listener);
        Assert.assertNull("precondition failed", this.lastDataAssociated);
        Assert.assertNull("precondition failed", this.lastNodeAdded);
    }

    /**
     * Test that listeners are notified when nodes are added
     * 
     * The listener should be notified, but no data should be associated
     */
    @Test
    public void test_listenerNotifiedWhenNodeAdded() {
        final Node<Object> child = this.root.addChild("child");
        Assert.assertSame(child, this.lastNodeAdded);
        Assert.assertNull("no data is associated with the child node", this.lastDataAssociated);
    }

    /**
     * Test that listeners are notified when nodes are added
     * 
     * The listener should be notified, but no data should be associated
     */
    @Test
    public void test_listenerNotifiedWhenDataAssociated() {
        final Node<Object> child = this.root.addChild("child");
        Assert.assertSame(child, this.lastNodeAdded);
        Assert.assertNull("no data is associated with the child node", this.lastDataAssociated);
        child.setData("data");
        Assert.assertSame(child, this.lastDataAssociated);
        Assert.assertEquals("data", this.lastDataAssociated.getData());
    }
}