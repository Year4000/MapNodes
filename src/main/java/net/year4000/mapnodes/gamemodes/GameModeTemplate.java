/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.gamemodes;

import lombok.Setter;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;

public abstract class GameModeTemplate {
    @Setter
    private transient GameModeConfig config;

    public GameModeConfig getRawConfig() {
        return getConfig();
    }

    public <T extends GameModeConfig> T getConfig() {
        return (T) config;
    }
}
