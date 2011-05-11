package com.porpoise.common.collect.tree;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.porpoise.common.collect.tree.Tree.Node;

/**
 * A tree node which supports the 'observer' pattern, allowing listeners to be notified when nodes are added
 * 
 * @param <T>
 */
public class ObservableNode<T> extends Node<T> {
    /**
     * The observer's callback interface
     * 
     * @param <T>
     */
    public static interface NodeListener<T> {
        /**
         * A node has been added
         * 
         * @param parent
         *            the parent node receiving the new child
         * @param child
         *            the child node added
         */
        public void onNodeAdded(Node<T> parent, Node<T> child);

        /**
         * Callback event for when an observable node has had data associated with it
         * 
         * @param node
         */
        public void onDataAssociated(Node<T> node);
    }

    /**
     * @param <T>
     */
    public static abstract class NodeListenerAdapter<T> implements NodeListener<T> {
        @Override
        public void onDataAssociated(final Node<T> node) {
            // no-op
        }

        @Override
        public void onNodeAdded(final Node<T> parent, final Node<T> child) {
            // no-op
        }

    }

    /** the observers */
    private Collection<NodeListener<T>> listeners;

    /**
     * Constructor
     * 
     * @param parentNode
     * @param nodeName
     * @param info
     */
    public ObservableNode(final Node<T> parentNode, final String nodeName, final T info) {
        super(parentNode, nodeName, info);
    }

    /**
     * factory method
     * 
     * @param <T>
     * @param name
     * @param data
     * @return an observable node
     */
    public static <T> ObservableNode<T> create(final String name, final T data) {
        return new ObservableNode<T>(null, name, data);
    }

    /**
     * factory method
     * 
     * @param <T>
     * @param name
     * @return
     */
    public static <T> ObservableNode<T> create(final String name) {
        return create(name, null);
    }

    /**
     * default factory method
     * 
     * @param <T>
     * @return a new observable node
     */
    public static <T> ObservableNode<T> create() {
        return create("root");
    }

    /**
     * add a listener. Regardless of the node on which this method is called, the listener will be added to the root
     * node.
     * 
     * @param listener
     */
    public void addListener(final NodeListener<T> listener) {
        if (listener != null) {
            final ObservableNode<T> root = getRoot();
            synchronized (root) {
                if (root.listeners == null) {
                    root.listeners = Lists.newArrayList();
                }
                root.listeners.add(listener);
            }
        }
    }

    @Override
    public T setData(final T newData) {
        final T data = super.setData(newData);
        final ObservableNode<T> root = getRoot();
        synchronized (root) {
            root.fireOnDataAssociated(this);
        }
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.tree.Tree.Node#newNode(com.porpoise.common.tree.Tree.Node, java.lang.String,
     *      java.lang.Object)
     */
    @Override
    protected ObservableNode<T> newNode(final Node<T> p, final String childName, final T value) {
        final ObservableNode<T> child = new ObservableNode<T>(p, childName, value);
        return child;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.tree.Tree.Node#addChild(com.porpoise.common.tree.Tree.Node)
     */
    @Override
    Node<T> addChild(final Node<T> child) {
        final Node<T> same = super.addChild(child);

        final ObservableNode<T> root = getRoot();
        synchronized (root) {
            root.fireOnChildAdded(this, child);
        }

        return same;
    }

    /**
     * notify the listeners
     * 
     * @param parent
     * @param child
     */
    private void fireOnChildAdded(final Node<T> parent, final Node<T> child) {
        if (this.listeners != null) {
            final Collection<NodeListener<T>> copy = Lists.newArrayList(this.listeners);
            for (final NodeListener<T> listener : copy) {
                listener.onNodeAdded(parent, child);
            }
        }
    }

    /**
     * notify the listeners
     * 
     * @param parent
     * @param child
     */
    private void fireOnDataAssociated(final Node<T> node) {
        if (this.listeners != null) {
            final Collection<NodeListener<T>> copy = Lists.newArrayList(this.listeners);
            for (final NodeListener<T> listener : copy) {
                listener.onDataAssociated(node);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.tree.Tree.Node#addChild(java.lang.String, java.lang.Object)
     */
    @Override
    public ObservableNode<T> addChild(final String childName, final T value) {
        return (ObservableNode<T>) super.addChild(childName, value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.tree.Tree.Node#getRoot()
     */
    @Override
    public ObservableNode<T> getRoot() {
        return (ObservableNode<T>) super.getRoot();
    }

}