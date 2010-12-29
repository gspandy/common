package com.porpoise.common;

/**
 * General interface for objects that need to be disposed at close down.
 * <p>
 * As good practice, implementations should be disposed of as soon as possible, and if not, kept until disposal is
 * necessary at shutdown.
 * </p>
 * 
 * @author james
 */
public interface IDisposable {
    /**
     * Perform any implementation shutdown, cleanup and tidy up on exit.
     */
    void dispose();
}