package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.commands.CommandBuilder;
import net.year4000.mapnodes.commands.mapnodes.MapNodesBase;
import net.year4000.mapnodes.commands.maps.MapCommands;
import net.year4000.mapnodes.commands.match.MatchBase;
import net.year4000.mapnodes.commands.misc.MenuCommands;
import net.year4000.mapnodes.commands.node.NodeBase;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.WorldManager;
import net.year4000.mapnodes.game.NodeModeFactory;
import net.year4000.mapnodes.game.regions.EventManager;
import net.year4000.mapnodes.game.regions.events.*;
import net.year4000.mapnodes.game.regions.types.*;
import net.year4000.mapnodes.game.regions.RegionManager;
import net.year4000.mapnodes.gamemodes.arrowtag.ArrowTag;
import net.year4000.mapnodes.gamemodes.bomber.Bomber;
import net.year4000.mapnodes.gamemodes.capture.Capture;
import net.year4000.mapnodes.gamemodes.deathmatch.Deathmatch;
import net.year4000.mapnodes.gamemodes.destory.Destroy;
import net.year4000.mapnodes.gamemodes.endtimes.EndTimes;
import net.year4000.mapnodes.gamemodes.juggernaut.Juggernaut;
import net.year4000.mapnodes.gamemodes.magewars.MageWars;
import net.year4000.mapnodes.gamemodes.paintball.PaintBall;
import net.year4000.mapnodes.gamemodes.skywars.Skywars;
import net.year4000.mapnodes.listeners.*;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.LogUtil;
import net.year4000.utilities.bukkit.BukkitPlugin;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.List;

@Getter
public class MapNodesPlugin extends BukkitPlugin implements Plugin {
    @Getter
    private static MapNodesPlugin inst = null;
    private Addons addons = new Addons();
    private boolean enable = true;

    @Override
    public void onLoad() {
        inst = this;
        log = new LogUtil(getLogger());
        MapNodes.init(inst);

        // Register region types to be used during map.json parsing
        RegionManager.get()
            .add(Point.class)
            .add(Cuboid.class)
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
            .add(PVP.class)
            .add(CreatureSpawn.class)
            .add(PlayerDrop.class)
            .add(BlockDrop.class)
            .add(KillPlayer.class)
            .build();

        // Register game modes that MapNodes can support
        NodeModeFactory.get()
            .add(ArrowTag.class)
            .add(Bomber.class)
            .add(Capture.class)
            .add(Deathmatch.class)
            .add(Destroy.class)
            .add(EndTimes.class)
            .add(Juggernaut.class)
            .add(MageWars.class)
            .add(PaintBall.class)
            .add(Skywars.class)
            .build();

        // Clean out old maps
        WorldManager.removeStrayMaps();

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
            node.getMatch().getGame().getMap().getName(),
            node.getMatch().getGame().getMap().getVersion()
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
            .register();
    }

    @Override
    public void onDisable() {
        // Tasks that must happen when the plugin loaded with maps
        if (enable) {
            MapNodes.getCurrentGame().getPlayers().forEach(p -> {
                p.getPlayer().kickPlayer(MessageUtil.message(Msg.locale(p, "clocks.restart.last")));
                log(p.getPlayer().getName() + " " + Msg.locale(p, "clocks.restart.last"));
            });

            NodeFactory.get().getCurrentGame().unregister();

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
        return NodeFactory.get().getCurrentGame().getMatch().getGame();
    }

    @Override
    public World getCurrentWorld() {
        return NodeFactory.get().getCurrentGame().getWorld().getWorld();
    }
}
