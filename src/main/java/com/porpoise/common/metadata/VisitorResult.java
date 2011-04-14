package com.porpoise.common.metadata;

public enum VisitorResult {
    CONTINUE {
        @Override
        public boolean isSkipOrStop() {
            return false;
        }
    },
    STOP {
        @Override
        public boolean isStop() {
            return true;
        }
    },
    SKIP {
        /**
         * @return
         */
        @Override
        public boolean isSkip() {
            return true;
        }
    };

    public boolean isStop() {
        return false;
    }

    public boolean isSkipOrStop() {
        return true;
    }

    /**
     * @return
     */
    public boolean isSkip() {
        return false;
    }
}