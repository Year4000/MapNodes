package net.year4000.mapnodes.api.game;

import org.bukkit.Material;

import java.util.List;

public interface GameClass extends GameComponent {
    public String getName();

    public Material getIcon();

    public String getDescription();

    public List<String> getMultiLineDescription(String locale, int size);

    public GameKit getKit();
}
