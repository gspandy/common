package com.porpoise.common.collect;

import com.google.common.base.Function;

/**
 * GrapeAccessors
 */
enum GrapeAccessors {
    ; // uninstantiable

    /**
     * ACCESSOR FOR GET_RADIUS
     */
    public static final Function<Grape, Integer> GET_RADIUS;

    /**
     * ACCESSOR FOR GET_RIPENESS
     */
    public static final Function<Grape, Integer> GET_RIPENESS;

    /**
     * ACCESSOR FOR GET_COLOR
     */
    public static final Function<Grape, Color> GET_COLOR;

    static {
        /**
         * GET_RADIUS
         */
        GET_RADIUS = new Function<Grape, Integer>() {
            @SuppressWarnings("boxing")
            @Override
            public Integer apply(final Grape input) {
                return input.getRadius();
            }
        };

        /**
         * GET_RIPENESS
         */
        GET_RIPENESS = new Function<Grape, Integer>() {
            @SuppressWarnings("boxing")
            @Override
            public Integer apply(final Grape input) {
                return input.getRipeness();
            }
        };

        /**
         * GET_COLOR
         */
        GET_COLOR = new Function<Grape, Color>() {
            @Override
            public Color apply(final Grape input) {
                return input.getColor();
            }
        };

    }
}