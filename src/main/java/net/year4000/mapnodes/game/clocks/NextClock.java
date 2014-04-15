package net.year4000.mapnodes.game.clocks;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameKit;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.utils.BarAPI;
import net.year4000.mapnodes.world.WorldManager;
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
        String msg = String.format(Messages.get("clock.next"), next, position);

        Bukkit.getConsoleSender().sendMessage(msg);
        for (GamePlayer player : gm.getPlayers().values()) {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(
                player.getPlayer(),
                msg,
                (float) ((double)position / (double)getTime()) * 100
            );
        }
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        WorldManager wm = WorldManager.get();
        GameManager gm = wm.getCurrentGame();
        wm.setCurrentIndex(wm.getCurrentIndex() + 1);
        String msg = Messages.get("clock.next.last");

        Bukkit.getConsoleSender().sendMessage(msg);
        for (GamePlayer player : gm.getPlayers().values()) {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(player.getPlayer(), msg, 1);

            try {
                GamePlayer.join(player.getPlayer());

                if (player.getPlayer().isDead()) {
                    player.getPlayer().kickPlayer(Messages.get("clock.dead"));
                }
            } catch (Exception e) {/*Left Blank*/}
        }

        new ChunkUnloadClock();
        gm.setStage(GameStage.WAITING);

        wm.unLoadMap(wm.getLastGame().getWorld());
    }
}