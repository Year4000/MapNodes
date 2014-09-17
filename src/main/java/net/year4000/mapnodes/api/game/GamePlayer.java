package net.year4000.mapnodes.api.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public interface GamePlayer {
    public Player getPlayer();

    public GameTeam getTeam();

    public boolean isSpectator();

    public boolean isPlaying();

    public boolean isEntering();

    public void sendMessage(String message, Object... args);

    public List<BukkitTask> getPlayerTasks();

    public String getPlayerColor();

    public Inventory getInventory();
}
