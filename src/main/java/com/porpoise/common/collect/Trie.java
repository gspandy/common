package com.porpoise.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.porpoise.common.collect.tree.TreeNode;
import com.porpoise.common.collect.tree.TreeTrait;
import com.porpoise.common.collect.tree.TreeVisitor;
import com.porpoise.common.strings.StringIterator;

/**
 * Prefix Trie implementation, where each the {@code Trie} object is itself a node in the prefix tree
 * 
 * @param <T>
 *            the data type held in the trie
 */
public final class Trie<T> implements TreeNode<Optional<T>> {

	private char	                  key;
	private Trie<T>	                  parent;
	private final Collection<Trie<T>>	children	= Lists.newArrayList();
	private Optional<T>	              value;

	/**
	 * factory method - create a new prefix trie with the given strings
	 * 
	 * @param <T>
	 * @param first
	 *            an initial key
	 * @param second
	 *            another key (having two initial strings followed by a varargs
	 * @param values
	 * @return a new prefix trie
	 */
	public static <T> Trie<T> valueOf(final String first, final String second, final String... values) {
		final Trie<T> root = Trie.valueOf(first);
		root.put(second);
		for (final String next : values) {
			root.put(next);
		}
		return root;
	}

	/**
	 * factory method - create a new prefix trie populated with the given values
	 * 
	 * @param <T>
	 * @param values
	 *            the values to populate the initial trie
	 * @return a trie containing the given values
	 */
	public static <T> Trie<T> valueOf(final Iterable<String> values) {
		final Iterator<String> iter = values.iterator();
		if (!iter.hasNext()) {
			return valueOf("");
		}
		final Trie<T> root = Trie.valueOf(iter.next());
		while (iter.hasNext()) {
			root.put(iter.next());
		}
		return root;
	}

	/**
	 * factory method to construct a trie from a single string
	 * 
	 * @param <T>
	 * @param string
	 * @return a trie with the single initial string
	 */
	public static <T> Trie<T> valueOf(final String string) {
		return valueOfWithValue(string, (T) null);
	}

	/**
	 * factory method to create a trie which also stores a value against the key. This name is 'valueOfWithValue' to
	 * differentiate between the other 'valueOf' factory methods to guard against tries which also store {@link String}s
	 * as their values
	 * 
	 * @param <T>
	 * @param string
	 * @param value
	 * @return a trie with the given initial value
	 */
	public static <T> Trie<T> valueOfWithValue(final String string, final T value) {
		final Optional<T> leafValue = Optional.fromNullable(value);

		// though we use an underscore here for the 'root' of the trie, it could be anything (just
		// because each node requires a char key). We don't (and shouldn't) depend on it for any side-effects
		final Trie<T> root = new Trie<T>('_');
		final Iterator<Character> stringIterator = new StringIterator(string);
		root.put(stringIterator, leafValue);

		return root;
	}

	private Trie(final char character) {
		this(null, character, Optional.<T> absent());
	}

	private Trie(final Trie<T> owner, final char keyValue, final Optional<T> charValue) {
		super();
		this.parent = owner;
		this.key = keyValue;
		this.value = charValue;
	}

	/**
	 * returns the closest match for the given string.
	 * 
	 * If, for example, we have a trie that contains:
	 * 
	 * <pre>
	 * a + b + c | +d
	 * </pre>
	 * 
	 * The following will be returned for each given string:
	 * <ul>
	 * <li>"abcdefg" => "abc"</li>
	 * <li>"alpha" => "a"</li>
	 * <li>"beta" => "" (root node)</li>
	 * <li>"" => "" (root node)</li>
	 * <li>null => "" (root node)</li>
	 * <li>"abd" => "abd"</li>
	 * </ul>
	 * 
	 * @param keyString
	 *            the key to find
	 * @return the closest match for the given string
	 */
	public Trie<T> findClosest(final String keyString) {
		if (Strings.isNullOrEmpty(keyString)) {
			return root();
		}

		if (isRoot()) {
			for (final Trie<T> child : getChildren()) {
				final Trie<T> closest = child.findClosest(keyString);
				if (closest != this) {
					return closest;
				}
			}
			return root();
		}

		final StringIterator iter = new StringIterator(keyString);
		assert iter.hasNext();
		if (this.key != iter.next().charValue()) {
			return root();
		}
		return closest(iter);
	}

	/**
	 * @return the root of this trie
	 */
	public Trie<T> root() {
		return TreeTrait.getRoot(this);
	}

	/**
	 * add the given string to the current trie node, putting the optional value
	 * 
	 * @param next
	 *            the key to insert
	 * @param leafValue
	 *            the leaf value to put
	 * @return the replaced value option (none if a previous value was not set)
	 */
	public Optional<T> put(final String next, final T leafValue) {
		final StringIterator iter = new StringIterator(next);
		final Trie<T> closest;
		if (!next.startsWith(Character.toString(this.key))) {
			closest = this;
		} else {
			closest = closest(iter);
		}
		return closest.put(iter, Optional.<T> fromNullable(leafValue));
	}

	/**
	 * put a new key into this trie node
	 * 
	 * @param next
	 *            the key to insert
	 * @return the previous value held at this key (will either be 'none' or some(T))
	 */
	public Optional<T> put(final String next) {
		return put(next, null);
	}

	private Optional<T> put(final Iterator<Character> chars, final Optional<T> leafValue) {
		if (!chars.hasNext()) {
			final Optional<T> old = this.value;
			this.value = leafValue;
			return old;
		}

		final char charValue = chars.next().charValue();
		Trie<T> child = child(charValue);
		if (child == null) {
			if (!chars.hasNext()) {
				child = new Trie<T>(this, charValue, leafValue);
				this.children.add(child);
				return Optional.absent();
			}
			child = new Trie<T>(this, charValue, Optional.<T> absent());
			this.children.add(child);
		}

		return child.put(chars, leafValue);
	}

	Trie<T> closest(final Iterator<Character> chars) {
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
	public Optional<T> getData() {
		return this.value;
	}

	/**
	 * @return the prefix string for this node
	 */
	public String prefix() {
		return append(new StringBuffer()).reverse().toString();
	}

	private StringBuffer append(final StringBuffer b) {
		if (!isRoot()) {
			b.append(this.key);
			this.parent.append(b);
		}
		return b;
	}

	private boolean isRoot() {
		return TreeTrait.isRoot(this);
	}

	@Override
	public String toString() {
		final Function<Trie<T>, String> function = new Function<Trie<T>, String>() {
			@SuppressWarnings("synthetic-access")
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
		if (isLeaf() || this.children.size() != 1) {
			return prefix();
		}
		final Trie<T> onlyChild = Iterables.getOnlyElement(this.children, null);
		return onlyChild.longestPrefixRecursive();
	}

	/**
	 * Convenience method for determining the longest prefix of a series of strings
	 * 
	 * @param first
	 *            the first string
	 * @param values
	 *            the remaining strings
	 * @return the longest shared prefix for all input strings
	 */
	public static String longestPrefix(final String first, final String... values) {
		final Trie<String> trie = valueOfWithValue(first, first);
		for (final String value : values) {
			trie.put(value, value);
		}
		return trie.longestPrefix();
	}

	/**
	 * @return a map between the leaves of this tree and their values
	 */
	public Map<String, T> leaves() {
		final Map<String, T> leaves = Maps.newHashMap();
		TreeTrait.depthFirst(this, new TreeVisitor<Trie<T>>() {
			@Override
			public void onNode(final int depth, final Trie<T> node) {
				if (node.hasValue()) {
					leaves.put(node.prefix(), node.getData().get());
				} else if (TreeTrait.isLeaf(node)) {
					leaves.put(node.prefix(), null);
				}
			}
		});
		return leaves;
	}

	boolean isLeaf() {
		return hasValue() || TreeTrait.isLeaf(this);
	}

	boolean hasValue() {
		return getData().isPresent();
	}
}
