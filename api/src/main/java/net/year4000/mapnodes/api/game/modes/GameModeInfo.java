/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game.modes;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameModeInfo {
    /** The name of the game mode */
    public String name();

    /** The version of the game mode */
    public String version();

    /** The config object of this game mode */
    public Class<? extends GameModeConfig> config();

    /** The listeners for this game mode */
    public Class<? extends Listener>[] listeners() default {};
}
