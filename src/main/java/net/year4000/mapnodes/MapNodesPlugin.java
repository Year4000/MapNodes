package net.year4000.mapnodes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.addons.modules.misc.GameMech;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.commands.CommandBuilder;
import net.year4000.mapnodes.commands.mapnodes.MapNodesBase;
import net.year4000.mapnodes.commands.maps.MapCommands;
import net.year4000.mapnodes.commands.match.MatchBase;
import net.year4000.mapnodes.commands.misc.MenuCommands;
import net.year4000.mapnodes.commands.node.NodeBase;
import net.year4000.mapnodes.game.*;
import net.year4000.mapnodes.game.regions.EventManager;
import net.year4000.mapnodes.game.regions.RegionManager;
import net.year4000.mapnodes.game.regions.events.*;
import net.year4000.mapnodes.game.regions.types.*;
import net.year4000.mapnodes.gamemodes.capture.Capture;
import net.year4000.mapnodes.gamemodes.deathmatch.Deathmatch;
import net.year4000.mapnodes.gamemodes.destory.Destroy;
import net.year4000.mapnodes.gamemodes.skywars.Skywars;
import net.year4000.mapnodes.gamemodes.tntwars.TntWars;
import net.year4000.mapnodes.listeners.*;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.PacketInjector;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.LogUtil;
import net.year4000.utilities.bukkit.BukkitPlugin;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Getter
public class MapNodesPlugin extends BukkitPlugin implements Plugin {
    @Getter
    private static MapNodesPlugin inst = null;
    private Addons addons = new Addons();
    private boolean enable = true;

    // Fancy Title
    private Set<String> shimmer = ImmutableSet.of("3", "b", "8", "7", "2", "a", "4", "c", "5", "d", "6", "e", "1", "9");
    private final String NAME = "Year4000";
    private Iterable<String> forever = Iterables.cycle(shimmer);
    private Iterator<String> color = forever.iterator();

    @Override
    public void onLoad() {
        inst = this;
        log = new LogUtil(getLogger());
        MapNodes.init(inst);
        PacketInjector.inject();

        // Register region types to be used during map.json parsing
        RegionManager.get()
            .add(Point.class)
            .add(Cuboid.class)
            .add(Cube.class)
            .add(net.year4000.mapnodes.game.regions.types.Void.class)
            .add(Global.class)
            .add(Sphere.class)
            .add(Cylinder.class)
            .add(Chunk.class)
            .build();

        // Register event types
        EventManager.get()
            .add(Enter.class)
            .add(Exit.class)
            .add(Build.class)
            .add(net.year4000.mapnodes.game.regions.events.Destroy.class)
            .add(Bow.class)
            .add(Chest.class)
            .add(TNT.class)
            .add(EntityDamage.class)
            .add(CreatureSpawn.class)
            .add(PlayerDrop.class)
            .add(BlockDrop.class)
            .add(KillPlayer.class)
            .build();

        // Register game modes that MapNodes can support
        NodeModeFactory.get()
            .add(Capture.class)
            .add(Deathmatch.class)
            .add(Destroy.class)
            .add(Skywars.class)
            .add(TntWars.class)
            .build();

        // Clean out old maps
        Node.removeStrayMaps();

        // Load new maps
        new MapFactory();
    }

    @Override
    public void onEnable() {
        List<Node> maps = NodeFactory.get().getAllGames();

        // Disable if no loaded maps
        if (maps.size() == 0) {
            enable = false;
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Generate all the games
        maps.forEach(node -> log(Msg.util("debug.map.ready",
            node.getGame().getMap().getName(),
            node.getGame().getMap().getVersion()
        )));

        // Register built in listeners
        new ListenerBuilder()
            .add(GameListener.class)
            .add(MapNodesListener.class)
            .add(WorldListener.class)
            .add(SpectatorListener.class)
            .register();

        // Register built in commands
        new CommandBuilder()
            .add(MapNodesBase.class)
            .add(MapCommands.class)
            .add(MatchBase.class)
            .add(MenuCommands.class)
            .add(NodeBase.class)
            .register();

        // Addons (The internal system that loads addons)
        // The order is the dependency list
        addons.builder()
            .add(DeathMessages.class)
            .add(GameMech.class)
            .register();

        SchedulerUtil.repeatAsync(() -> {
            if (!MapNodes.getCurrentGame().getStage().isEnded()) {
                String b = "&" + color.next() + "&l";
                String name = b + "   [&" + color.next() + "&l" + NAME + b + "]   ";

                Stream.concat(MapNodes.getCurrentGame().getSpectating(), MapNodes.getCurrentGame().getEntering())
                    .parallel()
                    .forEach(player -> ((NodeGame) MapNodes.getCurrentGame()).getScoreboardFactory().setPersonalSidebar((NodePlayer) player, name));
            }
        }, 20L);
    }

    @Override
    public void onDisable() {
        // Tasks that must happen when the plugin loaded with maps
        if (enable) {
            MapNodes.getCurrentGame().getPlayers().forEach(p -> {
                p.getPlayer().kickPlayer(MessageUtil.message(Msg.locale(p, "clocks.restart.last")));
                log(p.getPlayer().getName() + " " + Msg.locale(p, "clocks.restart.last"));
            });

            if (NodeFactory.get().getAllGames().size() != 0) {
                NodeFactory.get().getCurrentGame().unregister();
            }

            addons.builder().unregister();
        }

        // Tasks that can be ran with out plugin loaded
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.shutdown();
    }

    /*//----------------------------//
         Current Node Quick Methods
    *///----------------------------//

    @Override
    public GameManager getCurrentGame() {
        return NodeFactory.get().getCurrentGame().getGame();
    }

    @Override
    public World getCurrentWorld() {
        return NodeFactory.get().getCurrentGame().getWorld();
    }
}
