package com.revantsingh.rateshield.algorithm.tokenbucket;

import com.revantsingh.rateshield.algorithm.RateLimiter;
import com.revantsingh.rateshield.model.Bucket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucket implements RateLimiter {

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
        return false;
    }
}