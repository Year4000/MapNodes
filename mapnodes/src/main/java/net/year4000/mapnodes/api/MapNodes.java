/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api;

import com.comphenix.protocol.ProtocolManager;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.utilities.LogUtil;
import net.year4000.utilities.bukkit.gui.GUIManager;
import org.bukkit.World;

public final class MapNodes {
    private static Plugin inst = null;

    public static void init(Plugin inst) {
        if (MapNodes.inst == null) {
            MapNodes.inst = inst;
        }
    }

    /** Get the current game */
    public static GameManager getCurrentGame() {
        return inst.getCurrentGame();
    }

    /** Get the current game world */
    public static World getCurrentWorld() {
        return inst.getCurrentWorld();
    }

    /** Get the log util to log things */
    public static LogUtil getLogUtil() {
        return inst.getLogUtil();
    }

    /** Grab the protocol manager for MapNodes */
    public static ProtocolManager getProtocolManager() {
        return inst.getProtocolManager();
    }

    /** Get the GUI Manager for MapNodes */
    public static GUIManager getGui() {
        return inst.getGui();
    }
}