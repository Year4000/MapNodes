package net.year4000.mapnodes.games.gui;

import net.year4000.mapnodes.api.MapNodes;
import net.year4000.utilities.bukkit.gui.AbstractGUI;
import net.year4000.utilities.bukkit.gui.IconView;
import org.bukkit.entity.Player;

import java.util.Locale;

public class PlotManager extends AbstractGUI {
    @Override
    public Locale[] getLocales() {
        return null;
    }

    @Override
    public Locale getLocale(Player player) {
        return MapNodes.getCurrentGame().getPlayer(player).getLocale();
    }


    @Override
    public IconView[][] generate(Locale locale) {
        return new IconView[0][];
    }
}
