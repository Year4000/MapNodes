package net.year4000.mapnodes.game.clocks;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.utils.BarAPI;
import org.bukkit.Sound;

public class DelayJoin extends Clocker {
    GamePlayer player;

    public DelayJoin(GamePlayer gPlayer, int time) {
        super(time); // Enter the player in the game after 10 secs.
        player = gPlayer;
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {
        String msg = String.format(Messages.get(player.getPlayer().getLocale(), "clock-delay"), position);

        FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);

        BarAPI.removeBar(player.getPlayer());
        BarAPI.setMessage(
            player.getPlayer(),
            msg,
            (float) ((double)position / (double)getTime()) * 100
        );
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        String msg = Messages.get(player.getPlayer().getLocale(), "clock-delay-last");

        FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

        BarAPI.removeBar(player.getPlayer());
        BarAPI.setMessage(player.getPlayer(), msg, 1);

        player.start();
    }
}
