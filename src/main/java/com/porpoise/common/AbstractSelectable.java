package com.porpoise.common;

/**
 * @author Aaron
 */
public abstract class AbstractSelectable implements ISelectable {
    private boolean selected;

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.ISelectable#isSelected()
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.porpoise.common.ISelectable#setSelected(boolean)
     */
    @Override
    public void setSelected(final boolean value) {
        selected = value;
    }

}