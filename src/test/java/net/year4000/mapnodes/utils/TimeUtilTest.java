/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public final class TimeUtilTest {
    @Test
    public void test() {
        // 1 min
        Assert.assertEquals("1:00", (new TimeUtil(1, TimeUnit.MINUTES).prettyOutput()));
        Assert.assertEquals("00:01:00", (new TimeUtil(1, TimeUnit.MINUTES).rawOutput()));

        // 1 hour
        Assert.assertEquals("1:00:00", (new TimeUtil(1, TimeUnit.HOURS).prettyOutput()));
        Assert.assertEquals("01:00:00", (new TimeUtil(1, TimeUnit.HOURS).rawOutput()));

        // 1.5 hours in 90 min's
        Assert.assertEquals("1:30:00", (new TimeUtil(90, TimeUnit.MINUTES).prettyOutput()));
        Assert.assertEquals("01:30:00", (new TimeUtil(90, TimeUnit.MINUTES).rawOutput()));
    }
}
