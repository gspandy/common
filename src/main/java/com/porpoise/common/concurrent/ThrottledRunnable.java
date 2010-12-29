package com.porpoise.common.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A 'throttled' runnable will only allow one invocation within a given time limit.
 * 
 * Although a general purpose Runnable, it was originally written to queue several 'refresh' requests sent to the UI in
 * quick succession. In that instance, it didn't matter if some requests were ignored, just that after invoking 'N'
 * calls to 'refresh' within a certain time frame that at least ONE would be invoked before the deadline.
 * 
 * 
 * Note: Due to this class implementing Delayed, it has a natural ordering that is inconsistent with equals.
 * 
 * @author Aaron
 */
class ThrottledRunnable implements Runnable, Delayed {
    /** The runnable logic we are attempting to throttle */
    private final Runnable                      job;

    /** The timestamp of the last time the job was run - updated each time the 'job' is invoked */
    private long                                lastRunTime = -1;

    /**
     * the throttle interval, preventing more than one invocation every so often. The 'so often' is determined by the
     * inteval and timeUnit
     */
    private final int                           interval;

    /** the time unit to apply to the throttle interval */
    private final TimeUnit                      timeUnit;

    /**
     * the maximum amount of 'extra' invocations to queue. For example, if the throttle is set to one invocation every
     * second, is called 10 times in one second, and the maxCapacity is set to five, then only the first 5 of the 10
     * invocations will be queued. The remaining 4 invocations (1 being executed, 5 are queued) will be handled by the
     * overflow handler
     */
    private int                                 maxCapacity = 1;

    /** handler which deals with invocations which are NOT queued */
    private final IOverflowHandler              overflowHandler;

    /**
     * Our delay queue onto which surplus calls are added. They will be taken off the queue by the executor service
     */
    private final DelayQueue<ThrottledRunnable> queue;

    /**
     * An executor service which will submit queued jobs
     */
    private final ExecutorService               executor;

    /**
     * An {@link IOverflowHandler} is invoked when the queue 'overflows' with calls.
     * 
     * For example, consider the scenario:
     * 
     * <ol>
     * <li>A throttle is set to only allow 5 invocations/second</li>
     * <li>A the queue limit is set to only queue 10 invocations</li>
     * <li>The throttle is invoked 20 times in one second</li>
     * </ol>
     * 
     * Now, the first invocation is executed, the next 5 are queued as per the queue limit. The remaining 14
     * 'overflowing' invocations are sent to the overflow handler
     * 
     * @author Aaron
     */
    public static interface IOverflowHandler {
        public void onInvocationExceedsThreshold(int threshold, Runnable job);
    }

    /**
     * Handler which will ignore multiple 'overflow' invocations within the interval
     * 
     * @author Aaron
     */
    private static class IgnoreHandler implements IOverflowHandler {
        @Override
        public void onInvocationExceedsThreshold(final int threshold, final Runnable job) {
            // ignore
        }
    }

    /**
     * Constructor - uses the 'default'
     * 
     * @param executorService
     * @param minInterval
     * @param intervalTimeUnit
     * @param runnable
     */
    public ThrottledRunnable(final ExecutorService executorService, final int minInterval,
            final TimeUnit intervalTimeUnit, final Runnable runnable) {
        this(executorService, minInterval, intervalTimeUnit, runnable, 1);
    }

    /**
     * 
     * @param executorService
     * @param minInterval
     * @param intervalTimeUnit
     * @param runnable
     * @param jobThreshold
     */
    public ThrottledRunnable(final ExecutorService executorService, final int minInterval,
            final TimeUnit intervalTimeUnit, final Runnable runnable, final int jobThreshold) {
        this(executorService, minInterval, intervalTimeUnit, runnable, jobThreshold, new IgnoreHandler());
    }

    public ThrottledRunnable(final ExecutorService executorPool, final int minInterval,
            final TimeUnit intervalTimeUnit, final Runnable runnable, final int jobThreshold,
            final IOverflowHandler handler) {
        checkArgument(minInterval > 0);
        interval = minInterval;
        timeUnit = checkNotNull(intervalTimeUnit);
        job = checkNotNull(runnable);
        maxCapacity = jobThreshold;
        overflowHandler = checkNotNull(handler);
        executor = checkNotNull(executorPool);
        queue = new DelayQueue<ThrottledRunnable>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        final long throttleDelay = TimeUnit.MILLISECONDS.convert(interval, timeUnit);

        final boolean invokeImmediately = now >= lastRunTime + throttleDelay;
        if (invokeImmediately) {
            lastRunTime = now;
            job.run();
        } else {
            if (maxCapacity <= queue.size()) {
                overflowHandler.onInvocationExceedsThreshold(maxCapacity, job);
            } else {
                // queue up another call
                queue.add(this);
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ThrottledRunnable task = queue.take();
                            task.run();
                        } catch (final InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        final long now = System.currentTimeMillis();
        final long throttleDelay = TimeUnit.MILLISECONDS.convert(interval, timeUnit);

        final long threshold = lastRunTime + throttleDelay;
        final long delayInMillis = threshold - now;

        final long delay = unit.convert(delayInMillis, TimeUnit.MILLISECONDS);
        return delay;
    }

    @Override
    public int compareTo(final Delayed other) {
        final long delay1 = getDelay(timeUnit);
        final long delay2 = other.getDelay(timeUnit);

        if (delay1 == delay2) {
            return 0;
        }
        if (delay1 > delay2) {
            return 1;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + interval;
        result = prime * result + (job == null ? 0 : job.hashCode());
        result = prime * result + maxCapacity;
        result = prime * result + (timeUnit == null ? 0 : timeUnit.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ThrottledRunnable other = (ThrottledRunnable) obj;
        if (interval != other.interval) {
            return false;
        }
        if (job == null) {
            if (other.job != null) {
                return false;
            }
        } else if (!job.equals(other.job)) {
            return false;
        }
        if (maxCapacity != other.maxCapacity) {
            return false;
        }
        if (timeUnit == null) {
            if (other.timeUnit != null) {
                return false;
            }
        } else if (!timeUnit.equals(other.timeUnit)) {
            return false;
        }
        return true;
    }
}