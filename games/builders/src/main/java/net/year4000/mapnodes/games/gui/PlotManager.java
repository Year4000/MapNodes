/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.games.PlayerPlot;
import net.year4000.mapnodes.messages.MessageManager;
import net.year4000.utilities.bukkit.gui.AbstractGUI;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlotManager extends AbstractGUI {
    GamePlayer gamePlayer;
    PlayerPlot plot;
    private final int size = 5;

    public PlotManager(GamePlayer gamePlayer, PlayerPlot plot) {
        this.gamePlayer = checkNotNull(gamePlayer);
        this.plot = checkNotNull(plot);
        populateMenu(locale -> {
            generate(locale);
            return gamePlayer.getPlayer().getName();
        }, size);
    }

    @Override
    public Locale[] getLocales() {
        Set<Locale> locales = MessageManager.get().getLocales().keySet();
        return locales.toArray(new Locale[locales.size()]);
    }

    @Override
    public Locale getLocale(Player player) {
        return MapNodes.getCurrentGame().getPlayer(player).getLocale();
    }

    @Override
    public IconView[][] generate(Locale locale) {
        IconView[][] view = new IconView[size][InventoryGUI.COLS];

        view[0][0] = new PlotFloorView(this, plot);

        return view;
    }
}
