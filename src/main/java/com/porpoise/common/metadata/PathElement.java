package com.porpoise.common.metadata;

import com.google.common.base.Preconditions;

/**
 * @param <T>
 */
public class PathElement<T> {
    private final Metadata<T> metadata;
    private final T left;
    private final T right;
    private final PathElement<?> parent;

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
        return String.format("%s%s{%s != %s}", prefix, getProperty().propertyName(), getLeft(), getRight());
    }

    /**
     * @return the path string for this element
     */
    public String getLeafValueString() {
        String prefix = "";
        if (this.parent != null) {
            prefix = String.format("%s.", this.parent.getPathString());
        }
        return String.format("%s%s{%s != %s}", prefix, getPropertyName(), getLeft(), getRight());
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
