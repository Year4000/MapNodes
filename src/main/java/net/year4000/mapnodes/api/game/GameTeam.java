package net.year4000.mapnodes.api.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;

import java.util.List;
import java.util.Queue;

public interface GameTeam extends GameComponent {
    public String getName();

    public ChatColor getColor();

    public Color getRawColor();

    public int getSize();

    public void setSize(int size);

    public boolean isAllowFriendlyFire();

    public void setAllowFriendlyFire(boolean allowFriendlyFire);

    public boolean isCanSeeFriendlyInvisibles();

    public GameKit getKit();

    public String prettyPrint();

    public List<GamePlayer> getPlayers();

    public int getPlaying();

    public List<Location> getSpawns();

    public String getDisplayName();

    public Location getSafeRandomSpawn();

    public Queue<GamePlayer> getQueue();
}
