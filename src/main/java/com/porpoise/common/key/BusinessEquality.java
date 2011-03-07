package com.porpoise.common.key;

/**
 * 
 */
public interface BusinessEquality {
    /**
     * @param type
     * @param other
     * @return true if the other object is equal-
     */
    public boolean businessEquals(String type, Object other);

    /**
     * @param type
     * @return the hash code for this type
     */
    public int businessHashCode(String type);

    /**
     * @param type
     * @return string for the type
     */
    public String businessToString(String type);

}
