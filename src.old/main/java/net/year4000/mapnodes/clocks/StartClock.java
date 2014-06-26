package net.year4000.mapnodes.clocks;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.utils.BarAPI;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class StartClock extends Clocker {
    public StartClock() {
        super(15); // Starts the game with 15 sec clock-
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(String.format(Messages.get("clock-start"), gm.getMap().getName(), position));
        gm.getPlayers().values().parallelStream().forEach(player -> {
            if (position <= 5)
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            else if (position == getTime())
                FunEffectsUtil.playSound(player.getPlayer(), Sound.ORB_PICKUP);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(
                player.getPlayer(),
                String.format(Messages.get(player.getPlayer().getLocale(), "clock-start"), gm.getMap().getName(), position),
                (float) ((double)position / (double)getTime()) * 100
            );
        });
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(Messages.get("clock-start-last"));
        gm.getPlayers().values().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(player.getPlayer(), Messages.get(player.getPlayer().getLocale(), "clock-start-last"), 1);

            if (player.getPlayer().isDead()) {
                player.getPlayer().kickPlayer(Messages.get(player.getPlayer().getLocale(), "clock-dead"));
            }

            if (!player.isSpecatator()) {
                player.start();
            }
        });

        gm.setStage(GameStage.PLAYING);
        gm.setStartTime(System.currentTimeMillis());
    }
}
