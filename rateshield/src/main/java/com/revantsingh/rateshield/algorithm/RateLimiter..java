package com.revantsingh.rateshield.algorithm;
public interface  RateLimiter{
    boolean allowRequest(String clinetId);
}