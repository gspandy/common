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
    SKIP;

    public boolean isStop() {
        return false;
    }

    public boolean isSkipOrStop() {
        return true;
    }
}