package com.revantsingh.rateshield.model;

public class Bucket {
    private final long capacity;
    private double tokens;
    private long lastRefillTime;
    private long lastAccessTime;
    public Bucket(long capacity,double tokens,long lastRefillTime) {
        this.capacity = capacity;
        this.tokens = tokens;
        this.lastRefillTime = lastRefillTime;
        this.lastAccessTime = lastRefillTime;
    }
    public long getCapacity(){
        return capacity;
    }
    public double getTokens(){
        return tokens;
    }
    public void setTokens(double tokens) {
        this.tokens = tokens;
    }
    public long getLastRefillTime() {
        return lastRefillTime;
    }
    public void setLastRefillTime(long lastRefillTime) {
        this.lastRefillTime = lastRefillTime;
    }
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}