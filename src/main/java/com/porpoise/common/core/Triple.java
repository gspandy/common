package com.porpoise.common.core;

/**
 * @param <A>
 *            the first type
 * @param <B>
 *            the second type
 * @param <C>
 *            the third type
 */
public class Triple<A, B, C> extends Pair<A, B> {

    private final C third;

    /**
     * Factory method for creating new pairs
     * 
     * @param <A>
     * @param <B>
     * @param <C>
     * @param thingOne
     * @param thingTwo
     * @param thingThree
     * @return a new triple
     */
    public static <A, B, C> Triple<A, B, C> valueOf(final A thingOne, final B thingTwo, final C thingThree) {
        return new Triple<A, B, C>(thingOne, thingTwo, thingThree);
    }

    /**
     * @param one
     *            the first thing
     * @param two
     *            the second thing
     * @param three
     *            the third thing
     */
    public Triple(final A one, final B two, final C three) {
        super(one, two);
        this.third = three;
    }

    /**
     * @return the third
     */
    public C getThird() {
        return this.third;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.third == null) ? 0 : this.third.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
        if (this.third == null) {
            if (other.third != null) {
                return false;
            }
        } else if (!this.third.equals(other.third)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see Pair#toString()
     */
    @Override
    public String toString() {
        return String.format("[%s,%s,%s]", getFirst(), getSecond(), getThird());
    }
}
