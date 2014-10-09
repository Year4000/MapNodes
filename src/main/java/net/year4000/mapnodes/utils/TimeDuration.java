package net.year4000.mapnodes.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class TimeDuration {
    private final TimeUnit unit;
    private final long time;
    @Getter
    private final boolean infinite;

    /** Return the time duration to ticks */
    public int toTicks() {
        return MathUtil.ticks((int) TimeUnit.SECONDS.convert(time, unit));
    }

    /** Return the time duration in secs */
    public int toSecs() {
        return MathUtil.sec(toTicks());
    }
}
