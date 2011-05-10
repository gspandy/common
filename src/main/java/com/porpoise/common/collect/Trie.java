package com.porpoise.common.collect;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.porpoise.common.collect.tree.TreeNode;
import com.porpoise.common.collect.tree.TreeTrait;
import com.porpoise.common.core.Options;
import com.porpoise.common.core.Options.Option;
import com.porpoise.common.strings.StringIterator;

/**
 * Trie implementation, where each the Trie object is itself a node in the Trie
 * 
 * @param <T>
 *            the data type held in the trie
 */
public class Trie<T> implements TreeNode<Option<T>> {

    private char                      key;
    private Trie<T>                   parent;
    private final Collection<Trie<T>> children = Lists.newArrayList();
    private Option<T>                 value;

    public static <T> Trie<T> valueOf(final String string) {
        return valueOf(string, (T) null);
    }

    public static <T> Trie<T> valueOf(final String string, final T value) {
        final Option<T> leafValue = Options.valueOf(value);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(string), "input cannot be empty");

        final Iterator<Character> stringIterator = new StringIterator(string);
        final Trie<T> root = new Trie<T>(stringIterator.next().charValue());

        root.put(stringIterator, leafValue);

        return root;
    }

    private Trie(final char character) {
        this(null, character, Options.<T> none());
    }

    private Trie(final Trie<T> owner, final char keyValue, final Option<T> charValue) {
        super();
        this.parent = owner;
        this.key = keyValue;
        this.value = charValue;
    }

    public Trie<T> findClosest(final String keyString) {
        if (Strings.isNullOrEmpty(keyString)) {
            return root();
        }

        final StringIterator iter = new StringIterator(keyString);
        assert iter.hasNext();
        if (this.key != iter.next().charValue()) {
            return null;
        }
        return closest(iter);
    }

    private Trie<T> root() {
        final Trie<T> root = TreeTrait.getRoot(this);
        return root;
    }

    public Option<T> put(final String next, final T leafValue) {
        final StringIterator iter = new StringIterator(next);
        final Trie<T> closest = closest(iter);
        return closest.put(iter, Options.<T> none());
    }

    public Option<T> put(final String next) {
        return put(next, null);
    }

    private Option<T> put(final Iterator<Character> chars, final Option<T> leafValue) {
        if (!chars.hasNext()) {
            final Option<T> old = this.value;
            this.value = leafValue;
            return old;
        }

        final char charValue = chars.next().charValue();
        Trie<T> child = child(charValue);
        if (child == null) {
            if (!chars.hasNext()) {
                child = new Trie<T>(this, charValue, leafValue);
                this.children.add(child);
                return Options.none();
            }
            child = new Trie<T>(this, charValue, Options.<T> none());
            this.children.add(child);
        }

        return child.put(chars, leafValue);
    }

    protected Trie<T> closest(final Iterator<Character> chars) {
        if (!chars.hasNext()) {
            return this;
        }
        final char charValue = chars.next().charValue();
        final Trie<T> child = child(charValue);
        if (child == null) {
            return this;
        }
        return child.closest(chars);
    }

    private Trie<T> child(final char charValue) {
        final Trie<T> child = Iterables.find(this.children, new Predicate<Trie<T>>() {
            @SuppressWarnings("synthetic-access")
            @Override
            public boolean apply(final Trie<T> input) {
                return input.key == charValue;
            }
        }, null);
        return child;
    }

    /**
     * @return the parent trie node
     */
    @Override
    public Trie<T> getParent() {
        return this.parent;
    }

    @Override
    public Iterable<Trie<T>> getChildren() {
        return this.children;
    }

    /**
     * @return the data held at this node
     */
    @Override
    public Option<T> getData() {
        return this.value;
    }

    /**
     * @return the prefix string for this node
     */
    public String prefix() {
        return append(new StringBuffer()).reverse().toString();
    }

    private StringBuffer append(final StringBuffer b) {
        b.append(this.key);
        if (this.parent != null) {
            this.parent.append(b);
        }
        return b;
    }

    @Override
    public String toString() {
        final Function<Trie<T>, String> function = new Function<Trie<T>, String>() {
            @Override
            public String apply(final Trie<T> arg0) {
                return Character.toString(arg0.key);
            }
        };
        return TreeTrait.toString(this, function);
    }

    /**
     * @return the longest common prefix for all elements held in this trie
     */
    public String longestPrefix() {
        return root().longestPrefixRecursive();
    }

    private String longestPrefixRecursive() {
        if (this.children.size() != 1) {
            return prefix();
        }
        final Trie<T> onlyChild = Iterables.getOnlyElement(this.children, null);
        return onlyChild.longestPrefixRecursive();
    }
}
