/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.utils;

public class WorldTime {
    private final int time;

    @java.beans.ConstructorProperties({"time"})
    public WorldTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }
}
