package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeStage;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class RestartServer extends Clocker {
    private static boolean running = false;
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
        super(MathUtil.ticks(MapNodesPlugin.getInst().getLog().isDebug() ? 10 : time));
    }

    public void runFirst(int position) {
        if (running) {
            getClock().task.cancel();
        }

        running = true;
        MapNodesPlugin.log(Msg.util("clocks.restart.first", (new TimeUtil(sec(position) - 1, TimeUnit.SECONDS)).rawOutput()));
        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> FunEffectsUtil.playSound(
            player.getPlayer(),
            Sound.ORB_PICKUP
        ));
    }

    public void runTock(int position) {
        int pos = sec(position);
        String color = Common.chatColorNumber(pos, sec(getTime()));
        String time = color + (new TimeUtil(pos, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            if (Arrays.asList(ticks).contains(position)) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            }

            PacketHacks.title(
                player.getPlayer(),
                Msg.locale(player, "clocks.restart.tock", time),
                percent(getTime(), position)
            );
        });
    }

    public void runLast(int position) {
        MapNodesPlugin.log(Msg.locale(Message.DEFAULT_LOCALE, "clocks.restart.last"));

        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);
            PacketHacks.title(player.getPlayer(), Msg.locale(player, "clocks.restart.last"), 1);
        });

        ((NodeGame)MapNodes.getCurrentGame()).setStage(NodeStage.ENDED);

        SchedulerUtil.runSync(() -> Bukkit.getPluginManager().disablePlugin(MapNodesPlugin.getInst()), 2);
        running = false;
    }
}
