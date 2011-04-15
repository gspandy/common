package com.porpoise.common.metadata;

/**
 * Instruction hint for visiting object graphs
 */
public enum VisitorResult {
    /**  */
    CONTINUE {
        @Override
        public boolean isSkipOrStop() {
            return false;
        }
    },
    /**  */
    STOP {
        @Override
        public boolean isStop() {
            return true;
        }
    },
    /**  */
    SKIP {
        /**
         * @return true
         */
        @Override
        public boolean isSkip() {
            return true;
        }
    };

    /**
     * @return true if the condition represents a stop condition
     */
    public boolean isStop() {
        return false;
    }

    /**
     * @return true if the condition represents a stop OR skip condition
     */
    public boolean isSkipOrStop() {
        return true;
    }

    /**
     * @return true if this is a skip condition
     */
    public boolean isSkip() {
        return false;
    }
}