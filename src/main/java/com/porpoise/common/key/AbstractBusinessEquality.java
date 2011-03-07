package com.porpoise.common.key;

public abstract class AbstractBusinessEquality<T> implements BusinessEquality {

    private BusinessKeys<T> cachedKey;

    protected BusinessKeys<T> key() {
        if (this.cachedKey == null) {
            this.cachedKey = (BusinessKeys<T>) BusinessKeys.valueOf(getClass());
        }
        return this.cachedKey;
    }

    /**
     * @see com.tullettprebon.dms.key.BusinessEquality#businessEquals(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean businessEquals(final String type, final Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass().isAssignableFrom(getClass())) {
            return key().equals(type, get(), (T) other);
        }
        return false;
    }

    @Override
    public int businessHashCode(final String type) {
        return key().hashCode(type, get());
    }

    @Override
    public String businessToString(final String type) {
        return key().toString(type, get());
    }

    private T get() {
        return (T) this;
    }

}
