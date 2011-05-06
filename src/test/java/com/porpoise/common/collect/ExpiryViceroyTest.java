package com.porpoise.common.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.porpoise.common.collect.ExpiryViceroy.IExpiryAction;
import com.porpoise.common.core.Pair;

/**
 * Tests for the ExpiryViceroy class
 */
public class ExpiryViceroyTest {
    /**
     * test individual tasks may be given custom time-out values, and those individual time-outs are obeyed.
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_customTimeOutValues() throws InterruptedException {
        //
        // keep track of each task started, along with the timeout it was given
        // and time it started
        //
        final Map<String, Pair<Long, Long>> timeoutAndStartTimeByMessage = new ConcurrentHashMap<String, Pair<Long, Long>>();

        //
        // store all errors in a list, as we shouldn't make our assertions in a
        // separate thread
        //
        final List<String> errors = new ArrayList<String>();

        //
        // As all the tasks will timeout, keep a latch to ensure they've all
        // been processed
        // so as not to end the test too soon. This should be preferable to
        // simply sleeping
        // for a given amount of time
        //
        final int numberOfTasksToStart = 5;
        final CountDownLatch numberOfTasksLatch = new CountDownLatch(numberOfTasksToStart);

        //
        // each task will be started with a different timeout value, which will
        // be in increments of this value
        //
        final long timeoutInterval = 50;

        //
        // we will assert each timeout does not occur before it's timeout, but
        // also no later than a given margin for error. This is done because
        // this test would also pass if all tasks were given the same large
        // expiry value (i.e. if we start tasks to expire at 10, 20 and 30
        // milliseconds, they would all have expired by 1 second, but that isn't
        // what would be expected).
        //
        // The margin for error is given to be the same as the timeout interval
        //
        final long marginForError = timeoutInterval / 2;

        //
        // the callback will populate the errors collection (potentially)
        //
        final IExpiryAction<String> timeoutAction = new IExpiryAction<String>() {

            @Override
            public void onTimeout(final String payload) {
                final long now = System.currentTimeMillis();
                numberOfTasksLatch.countDown();

                final Pair<Long, Long> value = timeoutAndStartTimeByMessage.get(payload);
                if (value == null) {
                    errors.add("No value found for " + payload);
                } else {
                    final Long timeExpiry = value.getFirst();
                    final Long timeStarted = value.getSecond();

                    final long duration = now - timeStarted.longValue();

                    //
                    // assert the duration is within an expected period
                    //
                    final long minExpected = timeExpiry.longValue() - marginForError;
                    final long maxExpected = timeExpiry.longValue() + marginForError;

                    if (duration < minExpected) {
                        errors.add(String.format("%s expired after %d ms", payload, Long.valueOf(duration)));
                    } else {
                        if (duration > maxExpected) {
                            errors.add(String.format("%s took too long to expire after %d ms", payload,
                                    Long.valueOf(duration)));
                        }
                    }
                }
            }
        };
        final ExpiryViceroy<String> taskDelegate = ExpiryViceroy.start(timeoutAction, 1, TimeUnit.SECONDS);

        for (int taskCount = numberOfTasksToStart; taskCount >= 1; taskCount--) {
            final long timeout = taskCount * timeoutInterval;
            final String message = "interval " + timeout;

            //
            // no tasks will be acknowledged as complete, so they should all
            // time out
            //
            taskDelegate.onStartTask(message, timeout, TimeUnit.MILLISECONDS);

            final Pair<Long, Long> value = new Pair<Long, Long>(Long.valueOf(timeout), Long.valueOf(System
                    .currentTimeMillis()));
            timeoutAndStartTimeByMessage.put(message, value);
        }

        //
        // ensure all tasks have completed
        //
        numberOfTasksLatch.await(1, TimeUnit.MINUTES);
        taskDelegate.stop();

        //
        // assert no infractions have occurred within the callback
        //
        // TODO
        // Assert.assertTrue(Lists.toString(Consts.NEW_LINE, errors), errors.isEmpty());
    }

    /**
     * simple case, testing that tasks which complete on time will not result in the callback being invoked, but tasks
     * which DO time-out will have the callback notified
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_callbackInvokedWhenATaskExpires() throws InterruptedException {
        final List<String> timeouts = new ArrayList<String>();
        final IExpiryAction<String> action = new IExpiryAction<String>() {
            @Override
            public void onTimeout(final String payload) {
                timeouts.add(payload);
            }
        };
        final int taskTimeout = 100;
        final ExpiryViceroy<String> viceroy = ExpiryViceroy.start(action, taskTimeout, TimeUnit.MILLISECONDS);

        //
        // start/complete a load of tasks which won't expire,
        // unless for some reason the code below manages to take longer
        // than a second to complete!
        //
        final long start = System.currentTimeMillis();
        final Collection<Delayed> tasks = new ArrayList<Delayed>();
        for (int i = 10; --i >= 0;) {
            final Delayed task = viceroy.onStartTask("Task " + i);
            tasks.add(task);
        }
        for (final Delayed task : tasks) {
            viceroy.onTaskComplete(task);
        }
        final long duration = System.currentTimeMillis() - start;

        if (duration >= taskTimeout) {
            final String errorFormat = "PRECONDITION FAILED: The test code was expected to take less than %dms, but it took %dms";
            final String message = String.format(errorFormat, Long.valueOf(taskTimeout), Long.valueOf(duration));
            Assert.assertTrue(message, duration < taskTimeout);
        }
        Assert.assertTrue("No tasks should've timed out", timeouts.isEmpty());

        //
        // now start a task which will expire
        //
        final String timeoutPayload = "this will timeout!";
        final Delayed task = viceroy.onStartTask(timeoutPayload);

        Thread.sleep(taskTimeout * 2); // allow it to time-out

        // confirm the task complete, but it's too late.
        viceroy.onTaskComplete(task);

        Assert.assertEquals("A timeout was expected", Integer.valueOf(1), Integer.valueOf(timeouts.size()));
        Assert.assertEquals(timeoutPayload, timeouts.get(0));

        viceroy.stop();
    }

    /**
     * test the stop method stops cleanly
     * 
     * @throws InterruptedException
     */
    @Test
    public void test_stop() throws InterruptedException {
        final CountDownLatch expectedTimeoutReceivedLatch = new CountDownLatch(1);
        final CountDownLatch timeoutWhichShouldNeverBeReceivedLatch = new CountDownLatch(2);
        final IExpiryAction<Long> timeoutAction = new IExpiryAction<Long>() {
            @Override
            public void onTimeout(final Long payload) {
                //
                // count-down our latches. The first latch only requires one
                // invocation, the second expects two (though the second should
                // never be received)
                //
                expectedTimeoutReceivedLatch.countDown();
                timeoutWhichShouldNeverBeReceivedLatch.countDown();
            }
        };

        final int taskTimeout = 100;
        final ExpiryViceroy<Long> timeoutManager = ExpiryViceroy.start(timeoutAction, taskTimeout,
                TimeUnit.MILLISECONDS);

        //
        // ensure it's running - add a task which will expire
        //
        timeoutManager.onStartTask(Long.valueOf(1));
        final boolean timeoutReached = expectedTimeoutReceivedLatch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue("timeout reached", timeoutReached);

        //
        // now kill the timeout manager ...
        //
        timeoutManager.stop();

        //
        // ... and submit another task.
        //
        timeoutManager.onStartTask(Long.valueOf(2));
        final boolean timeoutNeverReceived = timeoutWhichShouldNeverBeReceivedLatch.await(taskTimeout + 100,
                TimeUnit.MILLISECONDS);
        Assert.assertFalse("a second timeout should never have been called", timeoutNeverReceived);
    }
}
