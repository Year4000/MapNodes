/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.utilities.bukkit.gui.AbstractGUI;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public abstract class MapNodesLocaleGUI extends AbstractGUI {
    @Override
    public Locale[] getLocales() {
        Set<Locale> locales = MessageManager.get().getLocales().keySet();
        return locales.toArray(new Locale[locales.size()]);
    }

    @Override
    public Locale getLocale(Player player) {
        return MapNodes.getCurrentGame().getPlayer(player).getLocale();
    }
}
