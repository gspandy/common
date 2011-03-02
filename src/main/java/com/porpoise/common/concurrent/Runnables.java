package com.porpoise.common.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.porpoise.common.Log;

/**
 * Utility class for {@link Runnable}s
 * 
 * @author Aaron
 */
public enum Runnables {
    ;// uninstantiable

    /**
     * This method provides a bridge/adapter pattern, allowing a {@link Callable} to be treated as a {@link Runnable}
     * Return a {@link Runnable}
     * 
     * @param callable
     *            the non-null callable to return as a runnable
     * @return a runnable which can provide a future for the given callable
     */
    public static <T> CallableRunnable<T> asRunnable(final Callable<T> callable) {
        return new CallableRunnable<T>(callable);
    }

    /**
     * @return a runnable which delegates to the given runnable whilst logging the start/complete/exception status
     */
    public static Runnable proxyWithLogging(final Runnable runnable) {
        Preconditions.checkNotNull(runnable);
        final Runnable wrappedRunnable = new Runnable() {
            @Override
            public void run() {
                Log.debug("Executing Runnable");
                final long start = System.currentTimeMillis();
                try {
                    runnable.run();
                } catch (final RuntimeException e) {
                    Log.debug("Runnable threw an exception: %s", e.getMessage());
                    throw e;
                } finally {
                    final long diff = System.currentTimeMillis() - start;
                    Log.debug("lookup logic took %dms", Long.valueOf(diff));
                }
            }
        };
        return wrappedRunnable;
    }

    /**
     * This is a utility method for wrapping a runnable, returning another runnable which is guaranteed only to run at
     * the given interval. Multiple invocations within that interval will be result in only one invocation of the
     * wrapped Runnable being executed at the next available time slot.
     * 
     * For example, consider some runnable logic A is provided with the interval of 10 seconds, and the returned
     * 'throttled' runnable is invoked:
     * 
     * <pre>
     * Runnable logic = ...;
     * Runnable throttled = Threads.newThrottleJob(logic, 10, TimeUnit.SECONDS);
     * </pre>
     * <ol>
     * <li>12:00:00 => thottled.run() // this invocation will occur immediately</li>
     * <li>12:00:01 => thottled.run() // this invocation will result in the logic being run at 12:00:10</li>
     * <li>12:00:02 => thottled.run() // this invocation will be ignored, as an invocation is already queued for
     * 12:00:10</li>
     * <li>12:00:09 => thottled.run() // this invocation is also ignored</li>
     * <li>12:00:10 => the logic is executed</li>
     * <li>12:00:11 => thottled.run() // As the logic was last run at 12:00:10 and should only be run every 10 seconds,
     * this invocation will result in the logic being run at 12:00:20</li>
     * <li>12:00:20 => the logic is executed</li>
     * <li>12:00:50 => thottled.run() // the logic has not been invoked since 12:00:20, so this invocation will occur
     * immediately</li>
     * </ol>
     * 
     * I
     * 
     * @param runnable
     *            the runnable logic to wrap
     * @param interval
     *            the interval after which the runnable may be invoked again
     * @param timeUnit
     *            the interval time unit
     * @return a runnable which will only be invoked once within the given interval
     */
    public static Runnable throttle(final Runnable runnable, final int interval, final TimeUnit timeUnit) {
        return throttle(Threads.getDefaultThreadPool(), runnable, interval, timeUnit);
    }

    /**
     * @see #throttle(Runnable, int, TimeUnit)
     * @param executor
     *            the executor to use to execute throttled jobs
     * @param runnable
     *            the logic to execute
     * @param interval
     *            the interval after which the runnable may be invoked again
     * @param timeUnit
     *            the interval time unit
     * @return a runnable which will only be invoked once within the given interval
     */
    public static Runnable throttle(final ExecutorService executor, final Runnable runnable, final int interval,
            final TimeUnit timeUnit) {
        return new ThrottledRunnable(executor, interval, timeUnit, runnable);
    }
}