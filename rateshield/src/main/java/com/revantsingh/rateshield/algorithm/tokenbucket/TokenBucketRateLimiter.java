package com.revantsingh.rateshield.algorithm.tokenbucket;
import com.revantsingh.rateshield.algorithm.RateLimiter;
import com.revantsingh.rateshield.model.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final long capacity;
    private final double refillRate;

    public TokenBucketRateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    @Override
    public boolean allowRequest(String clientId) {
        // Your implementation
        long currentTime = System.nanoTime();
        Bucket bucket = buckets.get(clientId);
        if (bucket == null) {
            bucket = new Bucket(capacity,capacity,currentTime);
            buckets.put(clientId, bucket);
        }
        long elapsedtime=currentTime-bucket.getLastRefillTime();
        double newTokens = refillRate * elapsedtime / 1000000000.0;
        double tokens=newTokens+bucket.getTokens();
        tokens=Math.min(tokens,capacity);
        bucket.setTokens(tokens);
        bucket.setLastRefillTime(currentTime);
        if(tokens>=1) {
            tokens--;
            bucket.setTokens(tokens);
            return true;
        }
        return false;
    }
}