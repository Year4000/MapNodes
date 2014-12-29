package net.year4000.mapnodes.utils;

import static com.google.common.base.Preconditions.checkArgument;

public final class MathUtil {
    private static final int TICKS = 20;

    /** Converts ticks to secs. */
    public static int sec(int ticks) {
        checkArgument(ticks >= 0);

        return ticks / TICKS;
    }

    /** Convert secs to ticks */
    public static int ticks(int sec) {
        checkArgument(sec >= 0);

        return sec * TICKS;
    }

    /** Convert integer to a float */
    public static float percent(int total, int position) {
        return (float) ((double) position / (double) total) * 100;
    }
}
