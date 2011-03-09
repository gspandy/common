package com.porpoise.common.collect;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Objects;

/**
 * A basic implementation of {@link Delayed} which can be set to expire after a given amount of time. The expiry will be
 * computed from the point that the delayed object is created.
 * 
 * @param <T>
 *            The payload type
 */
public class DelayedImpl<T> implements Delayed {
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final int DEFAULT_TIMEOUT = 2;

    /**
     * @return the payload
     */
    public final T getPayload() {
        return this.payload;
    }

    private final long expiryTimestampInMilliconds;
    private final T payload;

    /**
     * @param data
     * @param timeTilExpiry
     * @param unit
     */
    public DelayedImpl(final T data, final long timeTilExpiry, final TimeUnit unit) {
        final long delta = TimeUnit.MILLISECONDS.convert(timeTilExpiry, unit);
        this.expiryTimestampInMilliconds = System.currentTimeMillis() + delta;
        this.payload = data;
    }

    /**
     * @param <T>
     * @param object
     * @return a new delayed object
     */
    public static <T> DelayedImpl<T> newDelayed(final T object) {
        return newDelayed(object, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    /**
     * @param <T>
     * @param obj
     * @param timeUntilExpiry
     * @param unit
     * @return a new delayed object
     */
    public static <T> DelayedImpl<T> newDelayed(final T obj, final long timeUntilExpiry, final TimeUnit unit) {
        return new DelayedImpl<T>(obj, timeUntilExpiry, unit);
    }

    /**
     * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
     */
    @Override
    public long getDelay(final TimeUnit unit) {
        final long timeTilExpiry = this.expiryTimestampInMilliconds - System.currentTimeMillis();
        final long timeUntilExpiry = unit.convert(timeTilExpiry, TimeUnit.MILLISECONDS);

        return timeUntilExpiry;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final Delayed other) {
        if (other instanceof DelayedImpl) {
            final DelayedImpl<?> blockedMessage = (DelayedImpl<?>) other;
            return (int) (this.expiryTimestampInMilliconds - blockedMessage.expiryTimestampInMilliconds);
        }
        return 0;
    }

    /**
     * @see java.lang.Comparable#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DelayedImpl) {
            final DelayedImpl<?> delayed = (DelayedImpl<?>) obj;
            return compareTo(delayed) == 0 && Objects.equal(getPayload(), delayed.getPayload());
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(Long.valueOf(this.expiryTimestampInMilliconds), getPayload());
    }

    /**
     * Constructs a <code>String</code> with all attributes in name = value format.
     * 
     * @return a <code>String</code> representation of this object.
     */
    @Override
    public String toString() {
        return "DelayedImpl ( " + this.payload + " )";
    }
}