package com.revantsingh.rateshield.algorithm.tokenbucket;
import com.revantsingh.rateshield.algorithm.RateLimiter;
import com.revantsingh.rateshield.model.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TokenBucketRateLimiter implements RateLimiter {

    final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long bucketExpirationNanos;
    private static final long DEFAULT_BUCKET_EXPIRATION_MINUTES = 10;
    private final long capacity;
    private final double refillRate;
    private final ScheduledExecutorService cleanupExecutor=Executors.newSingleThreadScheduledExecutor();

    public TokenBucketRateLimiter(long capacity, double refillRate) {
        this(capacity,refillRate,10,1,TimeUnit.MINUTES);
    }

    public TokenBucketRateLimiter(long capacity, double refillRate, long bucketExpiration, long cleanupInterval, TimeUnit timeUnit) {
        if(capacity <= 0){
            throw new IllegalArgumentException( "Capacity must be greater than 0");
        }
        if(refillRate < 0){
            throw new IllegalArgumentException("Refill rate cannot be negative");
        }

        if(bucketExpiration <= 0){
            throw new IllegalArgumentException("Bucket expiration must be greater than 0");
        }

        if(cleanupInterval <= 0){
            throw new IllegalArgumentException("Cleanup interval must be greater than 0");
        }
        this.capacity = capacity;
        this.refillRate = refillRate;

        this.bucketExpirationNanos = timeUnit.toNanos(bucketExpiration);

        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredBuckets, cleanupInterval, cleanupInterval, timeUnit);
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

    public int getBucketCount() {
        return buckets.size();
    }
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}