/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.utilities.bukkit.gui.AbstractGUI;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import org.bukkit.entity.Player;

import java.util.Iterator;
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

    /** Create a 2D Icon View with row size and iterator to populate with */
    public IconView[][] populate(int size, Iterator<? extends IconView> views) {
        IconView[][] view = new IconView[size][InventoryGUI.COLS];
        boolean broken = false;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < InventoryGUI.COLS; j++) {
                if (views.hasNext()) {
                    view[i][j] = views.next();
                }
                else {
                    broken = true;
                    break;
                }
            }

            if (broken) break;
        }

        return view;
    }
}
