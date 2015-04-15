package com.github.bandwidthlimiter.bucket;

import com.github.bandwidthlimiter.bucket.BandwidthAdjuster.ImmutableCapacity;

import static com.github.bandwidthlimiter.bucket.BucketExceptions.*;

public class BandwidthDefinition {

    final long capacity;
    final BandwidthAdjuster adjuster;
    final long initialCapacity;
    final long period;
    final boolean guaranteed;
    final boolean limited;

    public BandwidthDefinition(long capacity, long initialCapacity, long period, boolean guaranteed) {
        this(validateCapacity(capacity), null, initialCapacity, period, guaranteed);
    }

    public BandwidthDefinition(BandwidthAdjuster adjuster, long initialCapacity, long period, boolean guaranteed) {
        this(0l, validateAdjuster(adjuster), initialCapacity, period, guaranteed);
    }

    private BandwidthDefinition(long capacity, BandwidthAdjuster adjuster, long initialCapacity, long period, boolean guaranteed) {
        if (initialCapacity < 0) {
            throw nonPositiveInitialCapacity(initialCapacity);
        }
        if (period <= 0) {
            throw nonPositivePeriod(period);
        }
        this.capacity = capacity;
        this.adjuster = adjuster;
        this.initialCapacity = initialCapacity;
        this.period = period;
        this.guaranteed = guaranteed;
        this.limited = !guaranteed;
    }

    public Bandwidth createBandwidth(int offset) {
        BandwidthAdjuster bandwidthAdjuster = adjuster != null ? adjuster : new ImmutableCapacity(capacity);
        return new Bandwidth(offset, bandwidthAdjuster, initialCapacity, period, guaranteed);
    }

    boolean hasDynamicCapacity() {
        return adjuster != null;
    }

    public double getTimeUnitsPerToken() {
        return (double) period / (double) capacity;
    }

    public double getTokensPerTimeUnit() {
        return (double) capacity / (double) period;
    }

    private static long validateCapacity(long capacity) {
        if (capacity <= 0) {
            throw nonPositiveCapacity(capacity);
        }
        return capacity;
    }

    private static BandwidthAdjuster validateAdjuster(BandwidthAdjuster adjuster) {
        if (adjuster == null) {
            throw nullBandwidthAdjuster();
        }
        return adjuster;
    }

}
