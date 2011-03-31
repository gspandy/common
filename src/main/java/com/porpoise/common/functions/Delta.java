package com.porpoise.common.functions;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 
 */
public class Delta {

    private static class DeltaContext {
        public DeltaContext(final Lookup lookup) {
            this.diffLookup = lookup;
        }

        private Delta workingDelta;
        private final Lookup diffLookup;
        private final Set<Object> visitedSet = Sets.newIdentityHashSet();

        /**
         * @return
         */
        public Delta makeDelta() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * 
         */
        public void reverse() {
            // TODO Auto-generated method stub

        }
    }

    /**
     * @param <T>
     * @param input
     * @param propByName
     * @param diffLookup
     * @return
     */
    public static <T> Delta valueOf(final T left, final T right, final Lookup diffLookup) {
        final DeltaContext ctxt = new DeltaContext(diffLookup);
        if (left == null) {
            if (right == null) {
                return null;
            }
            valueOfInternal(right, left, ctxt);
            ctxt.reverse();
            return ctxt.makeDelta();
        }
        // final Map<String, Function<T, ?>> propByName
        valueOfInternal(left, right, ctxt);
        return ctxt.makeDelta();
    }

    private static <T> DeltaContext valueOfInternal(final T left, final T right, final DeltaContext ctxt) {

        if (left == null) {
            if (right != null) {
                ctxt.addDiff(left, right);
            } else {
                return ctxt;
            }
        } else if (isIterable(left)) {
            return diffIterable(left, right, ctxt);
        } else if (isMap(left)) {
            return diffMap(left, right, ctxt);
        } else {
            return diff(left, right, ctxt);
        }
    }

}
