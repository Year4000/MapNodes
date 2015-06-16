package net.year4000.mapnodes.api.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Locale;

public interface GamePlayer {
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
}
