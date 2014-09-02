package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class RestartClock extends Clocker {
    public RestartClock() {
        super(20); // Restart the server with 20 delay.
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(String.format(Messages.get("clock-restart"), position));
        gm.getPlayers().values().parallelStream().forEach(player -> {
            if (position <= 5)
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);

            BossBar.removeBar(player.getPlayer());
            BossBar.setMessage(
                player.getPlayer(),
                String.format(Messages.get(player.getPlayer().getLocale(), "clock-restart"), position),
                (float) ((double) position / (double) getTime()) * 100
            );
        });
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(Messages.get("clock-restart-last"));
        gm.getPlayers().values().parallelStream().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            BossBar.removeBar(player.getPlayer());
            BossBar.setMessage(player.getPlayer(), Messages.get(player.getPlayer().getLocale(), "clock-restart-last"), 1);
        });

        Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> gm.setStage(GameStage.ENDED));
    }
}