package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class NextClock extends Clocker {
    public NextClock() {
        super(25); // Cycle the server with 25 delay.
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();
        String next = WorldManager.get().getNextGame().getMap().getName();

        Bukkit.getConsoleSender().sendMessage(String.format(Messages.get("clock-next"), next, position));
        gm.getPlayers().values().parallelStream().forEach(player -> {
            if (position <= 5)
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);

            BossBar.removeBar(player.getPlayer());
            BossBar.setMessage(
                player.getPlayer(),
                String.format(Messages.get(player.getPlayer().getLocale(), "clock-next"), next, position),
                (float) ((double)position / (double)getTime()) * 100
            );
        });
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        WorldManager wm = WorldManager.get();
        GameManager gm = wm.getCurrentGame();
        wm.nextGame();

        Bukkit.getConsoleSender().sendMessage(String.format(Messages.get("clock-next-last"), position));
        gm.getPlayers().values().parallelStream().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            BossBar.removeBar(player.getPlayer());
            BossBar.setMessage(player.getPlayer(), String.format(Messages.get(player.getPlayer().getLocale(), "clock-next-last"), position), 1);

            try {
                if (player.getPlayer().isDead()) {
                    player.getPlayer().kickPlayer(Messages.get(player.getPlayer().getLocale(), "clock-dead"));
                }
                GamePlayer.join(player.getPlayer());
            } catch (Exception e) {/*Left Blank*/}
        });

        gm.setStage(GameStage.WAITING);
    }
}