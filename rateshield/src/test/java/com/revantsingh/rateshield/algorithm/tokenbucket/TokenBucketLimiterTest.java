package com.revantsingh.rateshield.algorithm.tokenbucket;
import org.junit.jupiter.api.Test;

import  static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
import com.revantsingh.rateshield.model.Bucket;
class TokenBucketLimiterTest{
    @Test
    void allowRequest(){
        TokenBucketRateLimiter limiter=new TokenBucketRateLimiter(6,10);
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertFalse(limiter.allowRequest("user1"));
    }

    @Test
    void shouldRejectWhenBucketIsEmpty(){
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1, 0);
        assertTrue(limiter.allowRequest("user1"));
        assertFalse(limiter.allowRequest("user1"));
        limiter.shutdown();
    }

    @Test
    void shouldNotExceedCapacity() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 100);
        assertTrue(limiter.allowRequest("user1"));
        Thread.sleep(100);
        Bucket bucket = limiter.buckets.get("user1");
        assertNotNull(bucket);
        assertTrue(bucket.getTokens() <= 5);
        limiter.shutdown();
    }

    @Test
    void shouldRefillToken() throws  InterruptedException{
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 10);
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertTrue(limiter.allowRequest("user1"));
        assertFalse(limiter.allowRequest("user1"));
        Thread.sleep(1100);
        assertTrue(limiter.allowRequest("user1"));

    }

    @Test
    void shouldRejectInvalidCapacity() {
        assertThrows(
                IllegalArgumentException.class, () -> new TokenBucketRateLimiter(0, 1)
        );

        assertThrows(
                IllegalArgumentException.class, () -> new TokenBucketRateLimiter(-1, 1)
        );
    }

    @Test
    void shouldRejectNegativeRefillRate() {
        assertThrows(
                IllegalArgumentException.class, () -> new TokenBucketRateLimiter(5, -1)
        );
    }

    @Test
    void shouldHandleConcurrentRequests() throws InterruptedException{
        TokenBucketRateLimiter limiter=new TokenBucketRateLimiter(1,0);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger accepted = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    start.await();

                    if (limiter.allowRequest("user1")) {
                        accepted.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(1, accepted.get());
    }
    @Test
    void shouldHandleDifferentClientsConcurrently() throws InterruptedException{
        TokenBucketRateLimiter limiter=new TokenBucketRateLimiter(1,0);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger user1accepted = new AtomicInteger();
        AtomicInteger user2accepted = new AtomicInteger();
        for (int i = 0; i < 20; i++) {
            final String clientId =i < 10 ? "user1" : "user2";
            executor.submit(() -> {
                try {
                    start.await();
                    if (limiter.allowRequest(clientId)) {
                        if(clientId.equals("user1")){
                            user1accepted.incrementAndGet();
                        }
                        else{
                            user2accepted.incrementAndGet();
                        }
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, user1accepted.get());
        assertEquals(1, user2accepted.get());
    }

    @Test
    void shouldAutomaticallyCleanupInactiveBuckets() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5,1, 100,50, TimeUnit.MILLISECONDS);
        assertTrue(limiter.allowRequest("user1"));
        assertEquals(1, limiter.getBucketCount());
        Thread.sleep(200);
        assertEquals(0, limiter.getBucketCount());
        limiter.shutdown();
    }
}
