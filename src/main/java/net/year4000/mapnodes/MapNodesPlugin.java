package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.addons.modules.mapnodes.Internals;
import net.year4000.mapnodes.addons.modules.spectator.*;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.WorldManager;
import net.year4000.mapnodes.game.NodeModeFactory;
import net.year4000.mapnodes.game.components.regions.types.*;
import net.year4000.mapnodes.game.components.regions.RegionManager;
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
            .add(net.year4000.mapnodes.game.components.regions.types.Void.class)
            .add(Global.class)
            .add(Sphere.class)
            .add(Cylinder.class)
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
            Bukkit.getPluginManager().disablePlugin(this);
            enable = false;
            return;
        }

        // Generate all the games
        maps.forEach(node -> log(Msg.util("debug.map.ready",
            node.getMatch().getGame().getMap().getName(),
            node.getMatch().getGame().getMap().getVersion()
        )));

        // Addons (The internal system that loads addons)
        // The order is the dependency list
        addons.builder()
            .add(Internals.class)
            .add(GameMenu.class)
            .add(PlayerMenu.class)
            .add(GameServers.class)
            .add(MapBook.class)
            .add(OpenInventories.class)
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
