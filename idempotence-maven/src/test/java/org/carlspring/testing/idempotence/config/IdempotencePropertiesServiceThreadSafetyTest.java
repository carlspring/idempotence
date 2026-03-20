package org.carlspring.testing.idempotence.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Verifies that {@link IdempotencePropertiesService#getInstance()} is thread-safe:
 * concurrent callers must all receive the same, fully-initialised singleton.
 *
 * @author carlspring
 */
class IdempotencePropertiesServiceThreadSafetyTest
{

    private static final int THREAD_COUNT = 32;


    @Test
    void testGetInstanceIsThreadSafe()
            throws Exception
    {
        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        List<Future<IdempotencePropertiesService>> futures = new ArrayList<>();
        try
        {
            for (int i = 0; i < THREAD_COUNT; i++)
            {
                futures.add(executor.submit(() -> {
                    startGate.await();
                    return IdempotencePropertiesService.getInstance();
                }));
            }

            // Release all threads at once to maximise contention
            startGate.countDown();
        }
        finally
        {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }

        IdempotencePropertiesService expected = futures.get(0).get();
        assertNotNull(expected, "getInstance() must never return null");
        assertNotNull(expected.getIdempotenceProperties(),
                      "getIdempotenceProperties() must be initialised before the instance is published");
        assertNotNull(expected.getIdempotenceProperties().getBasedir(),
                      "getBasedir() must not be null after initialisation");

        for (Future<IdempotencePropertiesService> future : futures)
        {
            IdempotencePropertiesService actual = future.get();
            assertNotNull(actual, "getInstance() must never return null");
            assertNotNull(actual.getIdempotenceProperties(),
                          "getIdempotenceProperties() must not be null in any thread");
            assertSame(expected, actual, "All threads must receive the same singleton instance");
        }
    }

}
