package com.porpoise.common.key;

/**
 * Interface which allows objects to implement equals and hashCodes which differ for different contexts.
 * 
 * Where 'type' parameters are specified, they refer to an optional grouping type as defined on the annotations e.g.
 * 
 * <pre>
 * @BusinessKey(type={"identity", "logical"})
 * </pre>
 */
public interface BusinessEquality {
    /**
     * @param type
     *            the optional business key grouping type
     * @param other
     * @return true if the other object is equal-
     */
    public boolean businessEquals(String type, Object other);

    /**
     * @param type
     *            the optional business key grouping type
     * @return the hash code for this type
     */
    public int businessHashCode(String type);

    /**
     * @param type
     *            the optional business key grouping type
     * @return string for the type
     */
    public String businessToString(String type);

}