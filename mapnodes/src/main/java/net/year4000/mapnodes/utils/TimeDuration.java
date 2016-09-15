/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import java.util.concurrent.TimeUnit;

public class TimeDuration {
    private final TimeUnit unit;
    private final long time;
    private final boolean infinite;

    @java.beans.ConstructorProperties({"unit", "time", "infinite"})
    public TimeDuration(TimeUnit unit, long time, boolean infinite) {
        this.unit = unit;
        this.time = time;
        this.infinite = infinite;
    }

    /** Return the time duration to ticks */
    public int toTicks() {
        return MathUtil.ticks((int) TimeUnit.SECONDS.convert(time, unit));
    }

    /** Return the time duration in secs */
    public int toSecs() {
        return MathUtil.sec(toTicks());
    }

    public boolean isInfinite() {
        return this.infinite;
    }
}
