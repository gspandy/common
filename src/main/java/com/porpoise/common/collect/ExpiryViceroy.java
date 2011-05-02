package com.porpoise.common.collect;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;

/**
 * <p>
 * An ExpiryViceroy will look after a task on behalf of another thread. Should a task take too long, a callback will be notified that it has timed out.
 * </p>
 * <p>
 * Typical usage:
 * 
 * <pre>
 * 
 * 
 * IExpiryAction&lt;String&gt; action = new IExpiryAction&lt;String&gt;() {
 *     public void onTimeout(String obj) {
 *         System.out.println(&quot;The user took too long to respond to message: &quot; + obj);
 *     }
 * };
 * 
 * ExpiryViceroy&lt;String&gt; taskTimer = ExpiryViceroy.start(action, 1, TimeUnit.SECONDS);
 * 
 * String question = &quot;Input some data:&quot;;
 * 
 * Delayed task = taskTimer.onStartTask(question);
 * try {
 *     System.out.println(question);
 *     int input = System.in.read(); // If the user takes longer than ten seconds to enter some data then the action will
 *                                   // be invoked
 * } finally {
 *     taskTime.onTaskComplete(task);
 * }
 * 
 * </pre>
 * 
 * </p>
 * <p>
 * It is good practice to explicitly stop the expiry mechanism with a call to {@link #stop()} when it is no longer needed, though it should not be strictly necessary
 * </p>
 * 
 * @param <T>
 */
public class ExpiryViceroy<T> {

    /**
     * The PollTask contains the logic which pulls expired objects off the delayed queue and invokes the timeout callback. This was created as an inner class so there would be no
     * public "run" method on the {@link ExpiryViceroy} itself
     */
    private class PollTask implements Runnable {
        private final IExpiryAction<T> timeoutAction;

        public PollTask(final IExpiryAction<T> timeoutAction) {
            this.timeoutAction = Preconditions.checkNotNull(timeoutAction, "timeoutAction");
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void run() {
            while (ExpiryViceroy.this.running.get()) {
                final DelayedImpl<T> expiredAction = ExpiryViceroy.this.taskQueue.poll();
                if (ExpiryViceroy.this.running.get()) {
                    if (expiredAction != null) {
                        final T obj = expiredAction.getPayload();

                        this.timeoutAction.onTimeout(obj);
                    }
                }
            }
        }
    }

    /**
     * our queue of delayed tasks
     */
    private final DelayQueue<DelayedImpl<T>> taskQueue = new DelayQueue<DelayedImpl<T>>();

    /**
     * The task which will read from the delayed queue, notifying the call-back when tasks expire
     */
    private final Runnable                   task;

    /**
     * typical "running" flag. When stopped, this flag will be set and a "poison value" will be inserted into the delayed queue to ensure it does not continue to wait (block) for
     * another message
     */
    private final AtomicBoolean              running   = new AtomicBoolean(true);

    /**
     * The default timeout which tasks will be created with
     */
    private final long                       timeout;

    /**
     * The default time unit applied to the timeout
     */
    private final TimeUnit                   units;

    /**
     * An expiry action will be invoked when a task has taken too long to complete.
     * 
     * @param <T>
     *            A nullable payload for the task
     */
    public static interface IExpiryAction<T> {
        /**
         * @param payload
         */
        public void onTimeout(T payload);
    }

    /**
     * @param timeoutAction
     * @param timeout
     * @param units
     */
    private ExpiryViceroy(final IExpiryAction<T> timeoutAction, final long timeout, final TimeUnit units) {
        Preconditions.checkArgument(timeout > 0);
        this.timeout = timeout;
        this.units = Preconditions.checkNotNull(units, "units");
        this.task = new PollTask(timeoutAction);
    }

    /**
     * Stop the expiry mechanism. This will stop the mechanism immediately - Any tasks currently in progress may or may not be evaluated, and subsequent {@code onStartTask} or
     * {@onTaskComplete} calls will have no effect.
     */
    public void stop() {
        if (this.running.compareAndSet(true, false)) {
            //
            // insert a short-lived "poison" object to ensure the
            // queue doesn't block
            //
            final DelayedImpl<T> impl = DelayedImpl.newDelayed(null, 10, TimeUnit.MILLISECONDS);
            this.taskQueue.add(impl);
        }
    }

    /**
     * start is a factory method used to construct {@link ExpiryViceroy}s. This method will also start the background task required for the expiry notification mechanism
     * 
     * @param <T>
     * @param timeoutAction
     *            The call-back which will be invoked when tasks take longer than a given amount of time to complete
     * @param timeout
     *            the default, positive time-out value. A task-specific time-out value may also be specified
     * @param units
     *            the non-null time units to apply to the timeout value
     * @return the new expiry object
     */
    public static <T> ExpiryViceroy<T> start(final IExpiryAction<T> timeoutAction, final long timeout, final TimeUnit units) {
        final ExpiryViceroy<T> impl = new ExpiryViceroy<T>(timeoutAction, timeout, units);

        startBackgroundExpiryTask(impl);

        return impl;
    }

    private static <T> void startBackgroundExpiryTask(final ExpiryViceroy<T> impl) {
        final ThreadFactory factory = new ThreadFactory() {

            @Override
            public Thread newThread(final Runnable r) {
                final Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("task expiry manager");
                return thread;
            }
        };
        final ExecutorService pool = Executors.newSingleThreadExecutor(factory);
        pool.submit(impl.task);
        pool.shutdown();
    }

    /**
     * onTaskComplete should be called when a task is finished, preferably within a finally block. Failure to notify the onTaskComplete with the correct delayed object will result
     * in the timeout action (callback) being invoked.
     * 
     * @param delayed
     *            the delayed object as returned from an {@code onStartTask} call
     */
    public void onTaskComplete(final Delayed delayed) {
        final boolean removed = this.taskQueue.remove(delayed);
        if (!removed) {
            warn("%s wasn't in the queue - it must've timed out!", delayed);
        }
    }

    private static void warn(final String string, final Object... args) {
        // TODO
        System.out.println(String.format(string, args));
    }

    /**
     * @param payload
     *            the payload associated with the task
     * @return the delayed object associated with the task. This will need to be retained by the caller in order to notify the {@link #onTaskComplete(Delayed)} method.
     */
    public Delayed onStartTask(final T payload) {
        return onStartTask(payload, this.timeout, this.units);
    }

    /**
     * submit a task for completion
     * 
     * @param payload
     *            the payload associated with the task
     * @param timeoutValue
     *            the time given to a particular task
     * @param unitsValue
     *            the time unit applied to the time-out
     * @return the delayed object associated with the task. This will need to be retained by the caller in order to notify the {@link #onTaskComplete(Delayed)} method.
     */
    public Delayed onStartTask(final T payload, final long timeoutValue, final TimeUnit unitsValue) {
        final DelayedImpl<T> delayed = DelayedImpl.newDelayed(payload, timeoutValue, unitsValue);
        this.taskQueue.add(delayed);
        return delayed;
    }
}
