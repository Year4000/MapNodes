/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Locale;

public interface GamePlayer extends Comparable {
    public Player getPlayer();

    public GameTeam getTeam();

    public boolean isSpectator();

    public boolean isPlaying();

    public boolean isEntering();

    public void sendMessage(String message, Object... args);

    public List<BukkitTask> getPlayerTasks();

    public String getPlayerColor();

    public Inventory getInventory(Locale locale);

    public GameClass getClassKit();

    public void setClassKit(GameClass classKit);

    public void joinTeam(GameTeam gameTeam);

    public void joinSpectatorTeam();

    Locale getLocale();

    String getRawLocale();

    /** Add an object to player data */
    void addPlayerData(Class clazz, Object object);

    /** Add an object to player data */
    <T> T getPlayerData(Class clazz);

    /** Add an object to player data */
    void removePlayerData(Class clazz);

    /** Add one to token and experience multiplier */
    int addMultiplierModifier();
}
