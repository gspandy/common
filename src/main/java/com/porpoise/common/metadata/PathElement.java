package com.porpoise.common.metadata;

import com.google.common.base.Preconditions;

/**
 * @param <T>
 */
public class PathElement<T> {
    private final Delta<T> delta;
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
    PathElement(final PathElement<?> parent, final Delta<T> owner, final T leftValue, final T rightValue) {
        this.parent = parent;
        this.delta = Preconditions.checkNotNull(owner);
        this.left = leftValue;
        this.right = rightValue;
    }

    /**
     * @return the propertyName
     */
    @SuppressWarnings("unchecked")
    public Metadata<T> getProperty() {
        return (Metadata<T>) this.delta.getProperty();
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
        return this.delta.getPropertyName();
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
