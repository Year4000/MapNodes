package net.year4000.mapnodes.api.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public interface GameTeam {
    public String getName();

    public ChatColor getColor();

    public Color getRawColor();

    public int getSize();

    public boolean isAllowFriendlyFire();

    public boolean isCanSeeFriendlyInvisibles();

    public GameKit getKit();

    public boolean isUseScoreboard();
}
