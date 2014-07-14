package net.year4000.mapnodes.api.game;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public interface GamePlayer {
    public Player getPlayer();

    public GameTeam getTeam();

    public boolean d G();

    public boolean isPlaying();

    public boolean isEntering();

    public void sendMessage(String message, Object... args);

    public List<BukkitTask> getPlayerTasks();

    public String getPlayerColor();
}