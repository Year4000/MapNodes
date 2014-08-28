package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.Settings;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeStage;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;

import java.util.Arrays;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class StartGame extends Clocker {
    private Integer[] ticks = {
        ticks(5),
        ticks(4),
        ticks(3),
        ticks(2),
        ticks(1)
    };

    public StartGame() {
        this(30);
    }

    public StartGame(int time) {
        super(MathUtil.ticks(MapNodesPlugin.getInst().getLog().isDebug() ? 10 : time));
    }

    public void runFirst(int position) {
        ((NodeGame)MapNodes.getCurrentGame()).setStage(NodeStage.STARTING);
        GameMap map = MapNodes.getCurrentGame().getMap();

        MapNodesPlugin.log(MessageUtil.message(Msg.util("clocks.start.first"), map.getName(), sec(position) - 1));
        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> FunEffectsUtil.playSound(
            player.getPlayer(),
            Sound.ORB_PICKUP
        ));
    }

    public void runTock(int position) {
        GameMap map = MapNodes.getCurrentGame().getMap();

        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
            if (Arrays.asList(ticks).contains(position)) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            }

            BossBar.setMessage(
                player.getPlayer(),
                Msg.locale(player, "clocks.start.tock", map.getName(), Common.colorNumber(sec(position), sec(getTime()))),
                percent(getTime(), position)
            );
        });
    }

    public void runLast(int position) {
        MapNodesPlugin.log(Msg.locale(Message.DEFAULT_LOCALE, "clocks.start.last"));

        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);
            BossBar.setMessage(player.getPlayer(), Msg.locale(player, "clocks.start.last"), 1);
        });

        ((NodeGame)MapNodes.getCurrentGame()).start();
    }
}
