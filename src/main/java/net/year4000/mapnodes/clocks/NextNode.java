package net.year4000.mapnodes.clocks;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class NextNode extends Clocker {
    private Integer[] ticks = {
        ticks(5),
        ticks(4),
        ticks(3),
        ticks(2),
        ticks(1)
    };

    public NextNode() {
        this(20);
    }

    public NextNode(int time) {
        super(MathUtil.ticks(MapNodesPlugin.getInst().getLog().isDebug() ? 10 : time));
    }

    public void runFirst(int position) {
        GameMap map = NodeFactory.get().peekNextQueued().getMatch().getGame().getMap();

        MapNodesPlugin.log(Msg.util("clocks.next.first", map.getName(), String.valueOf(sec(position) - 1)));

        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> FunEffectsUtil.playSound(
            player.getPlayer(),
            Sound.ORB_PICKUP
        ));

        // Load and register map in its own thread
        NodeFactory.get().peekNextQueued().register();
    }

    public void runTock(int position) {
        try {
            GameMap map = NodeFactory.get().peekNextQueued().getMatch().getGame().getMap();

            MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
                if (Arrays.asList(ticks).contains(position)) {
                    FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
                }

                BossBar.setMessage(
                    player.getPlayer(),
                    Msg.locale(player, "clocks.next.tock", map.getName(), Common.colorNumber(sec(position), sec(getTime()))),
                    percent(getTime(), position)
                );
            });
        } catch (NullPointerException e) {
            MapNodesPlugin.log(e, false);
            this.getClock().task.cancel();
        }
    }

    public void runLast(int position) {
        try {
            GameMap map = NodeFactory.get().peekNextQueued().getMatch().getGame().getMap();

            MapNodesPlugin.log(Msg.locale(Message.DEFAULT_LOCALE, "clocks.next.last", map.getName()));

            Deque<Player> move = new ArrayDeque<>();

            MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);
                BossBar.setMessage(
                    player.getPlayer(),
                    Msg.locale(player, "clocks.next.last", map.getName()),
                    1
                );
                move.add(player.getPlayer());
            });

            Node next = NodeFactory.get().loadNextQueued();

            move.parallelStream().forEach(player -> next.getMatch().getGame().join(player));
        } catch (NullPointerException e) {
            MapNodesPlugin.log(e, false);
            this.getClock().task.cancel();
        }
    }
}
