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
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.TimeUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class NextNode extends Clocker {
    private static boolean running = false;
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
        super(MathUtil.ticks(MapNodesPlugin.getInst().getLog().isDebug() ? 15 : time));
    }

    public void runFirst(int position) {
        if (running) {
            getClock().task.cancel();
        }

        running = true;
        GameMap map = NodeFactory.get().peekNextQueued().getGame().getMap();

        MapNodesPlugin.log(Msg.util("clocks.next.first", map.getName(), (new TimeUtil(sec(position) - 1, TimeUnit.SECONDS)).rawOutput()));

        MapNodes.getCurrentGame().getPlayers().parallel().forEach(player -> FunEffectsUtil.playSound(
            player.getPlayer(),
            Sound.ORB_PICKUP
        ));

        // Load and register map in its own thread
        NodeFactory.get().peekNextQueued().register();
    }

    public void runTock(int position) {
        GameMap map = NodeFactory.get().peekNextQueued().getGame().getMap();
        int pos = sec(position);
        String color = Common.chatColorNumber(pos, sec(getTime()));
        String time = color + (new TimeUtil(pos, TimeUnit.SECONDS)).prettyOutput("&7:" + color);


        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            if (Arrays.asList(ticks).contains(position)) {
                FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
            }

            if (PacketHacks.isTitleAble(player.getPlayer())) {
                PacketHacks.countTitle(player.getPlayer(), map.getName(), time, percent(getTime(), position));
            }
            else {
                PacketHacks.title(
                    player.getPlayer(),
                    Msg.locale(player, "clocks.next.tock", map.getName(), time),
                    percent(getTime(), position)
                );
            }
        });
    }

    public void runLast(int position) {
        GameMap map = NodeFactory.get().peekNextQueued().getGame().getMap();

        MapNodesPlugin.log(Msg.locale(Message.DEFAULT_LOCALE, "clocks.next.last", map.getName()));

        Deque<Player> move = new ConcurrentLinkedDeque<>();

        MapNodes.getCurrentGame().getPlayers().forEach(player -> {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            if (PacketHacks.isTitleAble(player.getPlayer())) {
                PacketHacks.setTitle(player.getPlayer(), "&a" + map.getName(), Msg.locale(player, "map.created") + map.author(player.getPlayer().getLocale()));
            }
            else {
                PacketHacks.title(
                    player.getPlayer(),
                    Msg.locale(player, "clocks.next.last", map.getName()),
                    1
                );
            }

            BossBar.removeBar(player.getPlayer());
            move.add(player.getPlayer());
        });

        Node next = NodeFactory.get().loadNextQueued();

        while (move.peek() != null) {
            next.getGame().join(move.poll());
        }
        running = false;
    }
}
