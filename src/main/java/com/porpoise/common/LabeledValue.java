package com.porpoise.common;

import com.google.common.base.Preconditions;

/**
 * An immutable representation of a label/value pair. The label cannot be null.
 * 
 * @param <T>
 *            the value type
 */
public class LabeledValue<T> {
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (label == null ? 0 : label.hashCode());
        result = prime * result + (value == null ? 0 : value.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabeledValue<?> other = (LabeledValue<?>) obj;
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!label.equals(other.label)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    private final String label;
    private final T      value;

    public LabeledValue(final String labelParam, final T valueParam) {
        label = Preconditions.checkNotNull(labelParam, "Label cannot be null");
        value = valueParam;
    }

    public String getLabel() {
        return label;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getLabel(), getValue());
    }

}