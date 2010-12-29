package com.porpoise.common;

/**
 * An object which implements ISelectable has a notion of being able to be selected. It has a mutable 'selected' state
 * which may be checked or set.
 * 
 * @author Aaron
 */
public interface ISelectable {
    /**
     * Set the current selected state
     * 
     * @param selected
     *            the selected state
     */
    public void setSelected(boolean selected);

    /**
     * @return true if the object is currently selected, false otherwise
     */
    public boolean isSelected();

    /**
     * @return true if the object can be selected
     */
    public boolean isSelectAllowed();
}