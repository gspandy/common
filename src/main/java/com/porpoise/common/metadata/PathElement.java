package com.porpoise.common.metadata;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @param <T>
 *            The source object type - the delta type
 * @param <P>
 *            The type returned by this element's metadata's accessor
 */
public final class PathElement<T, P> {
    private final Delta<T>          delta;
    private final PathElement<?, T> parent;

    /**
     * @param parent
     * @param prop
     * @param leftValue
     * @param rightValue
     * 
     */
    PathElement(final PathElement<?, T> parent, final Delta<T> owner) {
        this.parent = parent;
        this.delta = Preconditions.checkNotNull(owner);
    }

    /**
     * @return the propertyName
     */
    @SuppressWarnings("unchecked")
    public Metadata<T, P> getProperty() {
        return (Metadata<T, P>) this.delta.getProperty();
    }

    /**
     * @return the left value of the difference
     */
    public T getLeft() {
        return this.delta.getLeft();
    }

    private String getLeftString() {
        return this.delta.getLeftString();
    }

    private String getRightString() {
        return this.delta.getRightString();
    }

    /**
     * @return the right
     */
    public T getRight() {
        return this.delta.getRight();
    }

    @Override
    public String toString() {
        return getLeafValueString();
    }

    /**
     * @return the path string for this element
     */
    public String getPathString() {
        if (this.parent != null) {
            return String.format("%s.%s", this.parent.getPathString(), getPropertyName());
        }
        return getPropertyName();
    }

    /**
     * @return the property name
     */
    public String getPropertyName() {
        return this.delta.getPropertyName();
    }

    /**
     * Returns the path string for this element, where the object for each element along the path has its 'toString' value displayed.
     * 
     * For example, if the properties for a path were:
     * 
     * <pre>
     * BOOK.CHAPTER.PARAGRAPH.SENTENCE
     * </pre>
     * 
     * The path value string would include the book, chapter, paragraph and sentence objects' toString values.
     * 
     * e.g.
     * 
     * BOOK{Book@123}.CHAPTER{Chapter@456}.PARAGRAPH{Paragraph@789}.SENTENCE{Sentence@101112}
     * 
     * 
     * @return the path string for this element.
     */
    public String getPathValueString() {
        String prefix = "";
        if (this.parent != null) {
            prefix = String.format("%s.", this.parent.getPathValueString());
        }
        final String propertyName = getProperty() == null ? getPropertyName() : getProperty().propertyName();
        return String.format("%s%s{%s != %s}", prefix, propertyName, getLeft(), getRight());
    }

    /**
     * @return the path string for this element, but only the leave's 'toString' methods are used in output
     */
    public String getLeafValueString() {
        String prefix = "";
        if (this.parent != null) {
            prefix = String.format("%s.", this.parent.getPathString());
        }
        return String.format("%s%s{%s != %s}", prefix, getPropertyName(), getLeftString(), getRightString());
    }

    /**
     * @return the parent
     */
    public PathElement<?, ?> getParent() {
        return this.parent;
    }

    /**
     * @return the root path element parent
     */
    public PathElement<?, ?> getRoot() {
        if (this.parent == null) {
            return this;
        }
        return this.parent.getRoot();
    }

    /**
     * @param property
     * @return true if this path element contains the given property within its chain
     */
    public boolean contains(final Metadata<?, ?> property) {
        return getProperty() == property || getParent() != null && getParent().contains(property);
    }
}
