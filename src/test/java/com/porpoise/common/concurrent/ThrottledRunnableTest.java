package com.porpoise.common.concurrent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * 
 */
public class ThrottledRunnableTest {

    private Runnable   runnable;
    private List<Long> callTimes;

    @Before
    public void setup() {
        this.callTimes = Lists.newArrayList();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                ThrottledRunnableTest.this.callTimes.add(Long.valueOf(System.currentTimeMillis()));
            }
        };
    }

    @Test
    public void testThrottleOnlyCalledAMaximumOfTheAllocatedTime() {
        final int interval = 100; // every 100 ms
        final Runnable throttled = Runnables.throttle(this.runnable, interval, TimeUnit.MILLISECONDS);
        final long start = System.currentTimeMillis();
        final int testDuration = 500; // 1/2 second
        while (System.currentTimeMillis() < start + testDuration) {
            throttled.run();
        }
        // final int expected = testDuration / interval;
        // Assert.assertEquals(expected, this.callTimes.size());

        // TODO - assert all times are > duration apart
        this.callTimes.get(0).longValue();
    }
}
