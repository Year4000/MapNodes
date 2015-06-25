/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.gamemodes.tag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.gamemodes.elimination.EliminationConfig;

@Data
@EqualsAndHashCode(callSuper = false)
@GameModeConfigName("arrow_tag")
public class ArrowTagConfig extends EliminationConfig implements GameModeConfig {
}
