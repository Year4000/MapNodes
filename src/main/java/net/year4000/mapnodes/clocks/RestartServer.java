package net.year4000.mapnodes.clocks;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeStage;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Arrays;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class RestartServer extends Clocker {
    private Integer[] ticks = {
        ticks(5),
        ticks(4),
        ticks(3),
        ticks(2),
        ticks(1)
    };

    public RestartServer() {
        this(20);
    }

    public RestartServer(int time) {
        super(MathUtil.ticks(Settings.get().isDebug() ? 10 : time));
    }
    public void runFirst(int position) {
        LogUtil.log(MessageUtil.message(Msg.util("clocks.restart.first"), sec(position) - 1));
        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> FunEffectsUtil.playSound(
            player.getPlayer(),
            Sound.ORB_PICKUP
        ));
    }

    public void runTock(int position) {
        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
            if (Arrays.asList(ticks).contains(position)) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            }

            BarAPI.setMessage(
                player.getPlayer(),
                MessageUtil.message(
                    Msg.locale(player, "clocks.restart.tock"),
                    Common.colorNumber(sec(position), sec(getTime()))
                ),
                percent(getTime(), position)
            );
        });
    }

    public void runLast(int position) {
        LogUtil.log(MessageUtil.message(Msg.locale(Msg.DEFAULT_LOCALE, "clocks.restart.last")));

        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);
            BarAPI.setMessage(player.getPlayer(), MessageUtil.message(Msg.locale(player, "clocks.restart.last")), 1);
        });

        ((NodeGame)MapNodes.getCurrentGame()).setStage(NodeStage.ENDED);

        SchedulerUtil.runSync(() -> Bukkit.getPluginManager().disablePlugin(MapNodesPlugin.getInst()), 2);
    }
}