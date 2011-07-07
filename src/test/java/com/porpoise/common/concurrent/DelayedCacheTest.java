package com.porpoise.common.concurrent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests for the {@link DelayedCache} class
 */
public class DelayedCacheTest {

    // our delayed cache under test
    private DelayedCache<String, Integer> cache;

    // a latch to simulate a long computation. The compute function will wait on this latch
    private CountDownLatch pauseComputationLatch;

    // a latch which our listener will release, signalling that the test can safely proceed
    private CountDownLatch notifiedLatch;

    // a map which stores the results from the delayed cache
    private ConcurrentMap<String, Integer> resultMap;

    /**
     * Create a {@link DelayedCache} to test
     */
    @Before
    public void setup() {

        // create a latch which we can control to simulate waiting on a computed response
        this.pauseComputationLatch = new CountDownLatch(1);

        this.cache = new DelayedCache<String, Integer>() {
            @SuppressWarnings({ "boxing", "synthetic-access" })
            @Override
            protected Integer computeValue(final String key) {
                try {
                    DelayedCacheTest.this.pauseComputationLatch.await();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return Integer.parseInt(key);
            }
        };

        // create another latch which our test will wait on. This latch will signal that it's safe for the
        // test to proceed and assert the delayed cache has completed
        this.notifiedLatch = new CountDownLatch(1);
        final CallableListener<String, Integer> listener = new CallableListener<String, Integer>() {

            @Override
            public boolean onException(final String key, final Exception exp) {
                return false;
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void onComplete(final String key, final Integer result) {
                DelayedCacheTest.this.resultMap.put(key, result);
                DelayedCacheTest.this.notifiedLatch.countDown();
            }
        };
        this.cache.addListener(listener);

        // use another map to store the computed results (as a check)
        this.resultMap = Maps.newConcurrentMap();
    }

    /**
     * @throws InterruptedException
     */
    @Test
    public void testGetComputedValue() throws InterruptedException {
        // make a request for the "123" key
        final AtomicReference<Integer> ref = this.cache.get("123");

        Assert.assertNull("The computed result should be initially empty", ref.get());
        Assert.assertTrue(this.resultMap.isEmpty());

        // release the latch, allowing the result to be computed
        this.pauseComputationLatch.countDown();

        final boolean success = this.notifiedLatch.await(100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(success);
        Assert.assertEquals(123, ref.get().intValue());
        Assert.assertEquals(123, Iterables.getOnlyElement(this.resultMap.values()).intValue());
    }

}
