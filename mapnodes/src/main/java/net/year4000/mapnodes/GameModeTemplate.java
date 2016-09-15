/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes;

import net.year4000.mapnodes.api.game.modes.GameModeConfig;

public abstract class GameModeTemplate {
    private transient GameModeConfig config;

    public GameModeConfig getRawConfig() {
        return getConfig();
    }

    public <T extends GameModeConfig> T getConfig() {
        return (T) config;
    }

    public void setConfig(GameModeConfig config) {
        this.config = config;
    }
}
