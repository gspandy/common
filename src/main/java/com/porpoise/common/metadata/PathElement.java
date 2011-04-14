package com.porpoise.common.metadata;

import com.google.common.base.Preconditions;
import com.porpoise.common.strings.Trim;

/**
 * @param <T>
 */
public class PathElement<T> {
    private final Metadata<T>    metadata;
    private final T              left;
    private final T              right;
    private final PathElement<?> parent;
    private static int           MAX = 5;

    /**
     * @param parent
     * @param prop
     * @param leftValue
     * @param rightValue
     * 
     */
    public PathElement(final PathElement<?> parent, final Metadata<T> prop, final T leftValue, final T rightValue) {
        this.parent = parent;
        this.metadata = Preconditions.checkNotNull(prop);
        this.left = leftValue;
        this.right = rightValue;
    }

    /**
     * @return the propertyName
     */
    public Metadata<T> getProperty() {
        return this.metadata;
    }

    /**
     * @return the left
     */
    public T getLeft() {
        return this.left;
    }

    /**
     * @return the right
     */
    public T getRight() {
        return this.right;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getPathValueString();
    }

    /**
     * @return the path string for this element
     */
    public String getPathString() {
        if (this.parent != null) {
            return String.format("%s.%s", this.parent.getPathString(), getProperty().propertyName());
        }
        return getPropertyName();
    }

    /**
     * @return the property name
     */
    public String getPropertyName() {
        return getProperty().propertyName();
    }

    /**
     * @return the path string for this element
     */
    public String getPathValueString() {
        String prefix = "";
        if (this.parent != null) {
            prefix = String.format("%s.", this.parent.getPathValueString());
        }
        return String.format("%s%s{%s<=>%s}", prefix, getProperty().propertyName(), Trim.right(getLeft(), MAX), Trim.right(getRight(), MAX));
    }

    /**
     * @return the parent
     */
    public PathElement<?> getParent() {
        return this.parent;
    }

    /**
     * @return the root path element parent
     */
    public PathElement<?> getRoot() {
        if (this.parent == null) {
            return this;
        }
        return this.parent.getRoot();
    }
}
