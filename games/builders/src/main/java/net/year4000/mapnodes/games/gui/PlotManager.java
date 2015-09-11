/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.gui;

import lombok.Getter;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.games.PlayerPlot;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.gui.IconView;
import net.year4000.utilities.bukkit.gui.InventoryGUI;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class PlotManager extends MapNodesLocaleGUI {
    @Getter
    private GamePlayer gamePlayer;
    @Getter
    private PlayerPlot plot;
    private final int size = 1;
    private BiomeManager biomeManager;
    private TimeManager timeManager;
    //private WeatherManager weatherManager;

    public PlotManager(GamePlayer gamePlayer, PlayerPlot plot) {
        this.gamePlayer = checkNotNull(gamePlayer);
        this.plot = checkNotNull(plot);
        populateMenu(locale -> {
            generate(locale);
            return Msg.locale(gamePlayer, "builders.plot.stick") + " - " + gamePlayer.getPlayer().getName();
        }, size);
        registerSubGUI(MapNodes.getGui(), biomeManager = new BiomeManager(this));
        registerSubGUI(MapNodes.getGui(), timeManager = new TimeManager(this));
        //registerSubGUI(MapNodes.getGui(), weatherManager = new WeatherManager(this));
    }

    @Override
    public IconView[][] generate(Locale locale) {
        IconView[][] view = new IconView[size][InventoryGUI.COLS];

        view[0][3] = new PlotFloorView(this);
        view[0][4] = biomeManager;
        view[0][5] = timeManager;
        //view[0][8] = weatherManager;

        return view;
    }
}
