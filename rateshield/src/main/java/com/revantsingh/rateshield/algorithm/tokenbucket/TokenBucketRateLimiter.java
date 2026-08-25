package com.revantsingh.rateshield.algorithm.tokenbucket;
import com.revantsingh.rateshield.algorithm.RateLimiter;
import com.revantsingh.rateshield.model.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TokenBucketRateLimiter implements RateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long bucketExpirationNanos;
    private static final long DEFAULT_BUCKET_EXPIRATION_MINUTES = 10;
    private final long capacity;
    private final double refillRate;
    private final ScheduledExecutorService cleanupExecutor=Executors.newSingleThreadScheduledExecutor();

    public TokenBucketRateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.bucketExpirationNanos = TimeUnit.MINUTES.toNanos(DEFAULT_BUCKET_EXPIRATION_MINUTES);
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredBuckets,1,1,TimeUnit.MINUTES);
    }

    @Override
    public boolean allowRequest(String clientId) {
        // Your implementation
        Bucket bucket = buckets.computeIfAbsent(clientId,id->new Bucket(capacity,capacity,System.nanoTime()));
        synchronized (bucket){
            long currentTime = System.nanoTime();
            bucket.setLastAccessTime(currentTime);
            long elapsedTime = currentTime - bucket.getLastRefillTime();
            double newTokens = refillRate * elapsedTime / 1000000000.0;
            double tokens = newTokens + bucket.getTokens();
            tokens = Math.min(tokens, capacity);
            bucket.setTokens(tokens);
            bucket.setLastRefillTime(currentTime);
            if (tokens >= 1) {
                tokens--;
                bucket.setTokens(tokens);
                return true;
            }
            return false;
        }
    }

    @Override
    public void cleanupExpiredBuckets() {
        long currentTime = System.nanoTime();

        for (Map.Entry<String, Bucket> entry : buckets.entrySet()) {
            String clientId = entry.getKey();
            Bucket bucket = entry.getValue();

            synchronized (bucket) {
                long inactiveTime =
                        currentTime - bucket.getLastAccessTime();

                if (inactiveTime >= bucketExpirationNanos) {
                    buckets.remove(clientId, bucket);
                }
            }
        }
    }
}