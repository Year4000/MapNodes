/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.addons.modules.misc.GameMech;
import net.year4000.mapnodes.addons.modules.misc.VIPEffects;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.backend.Backend;
import net.year4000.mapnodes.commands.CommandBuilder;
import net.year4000.mapnodes.commands.mapnodes.MapNodesBase;
import net.year4000.mapnodes.commands.maps.MapCommands;
import net.year4000.mapnodes.commands.match.MatchBase;
import net.year4000.mapnodes.commands.misc.MenuCommands;
import net.year4000.mapnodes.commands.node.NodeBase;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.NodeModeFactory;
import net.year4000.mapnodes.game.regions.EventManager;
import net.year4000.mapnodes.game.regions.RegionManager;
import net.year4000.mapnodes.game.regions.events.*;
import net.year4000.mapnodes.game.regions.types.*;
import net.year4000.mapnodes.listeners.*;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.LogUtil;
import net.year4000.utilities.bukkit.BukkitPlugin;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.MessagingChannel;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Getter
public class MapNodesPlugin extends BukkitPlugin implements Plugin {
    @Getter
    private static MapNodesPlugin inst = null;
    private Addons addons = Addons.get();
    private UserCache usercache;
    private boolean enable = true;
    private MessagingChannel connector;
    private Network network;
    private Backend api;

    @Override
    public void onLoad() {
        inst = this;
        log = new LogUtil(getLogger());
        usercache = new UserCache();
        api = new Backend();
        MapNodes.init(inst);
        //ProtocolInjector.inject();

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
            .add(ItemDrop.class)
            .add(KillPlayer.class)
            .add(FallingBlock.class)
            .build();

        // Register game modes that MapNodes can support
        getGames().build();

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
            .add(VIPEffects.class)
            .register();

        // Register the bungee plugin message channel
        connector = MessagingChannel.get();
        network = new Network();
    }

    @Override
    public void onDisable() {
        // Kick all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(MessageUtil.message(Msg.locale(player, "clocks.restart.last")));
            log(player.getName() + " " + Msg.locale(player, "clocks.restart.last"));
        });

        // Tasks that must happen when the plugin loaded with maps
        if (enable) {
            if (NodeFactory.get().getAllGames().size() != 0) {
                NodeFactory.get().getCurrentGame().unregister();
            }

            addons.builder().unregister();
        }

        // Tasks that can be ran with out plugin loaded
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.shutdown();
    }

    public NodeModeFactory getGames() {
        try {
            NodeModeFactory factory = NodeModeFactory.get();
            JarFile appJar = new JarFile(new File(getDataFolder().getParentFile(), "MapNodes.jar"));

            String tmpPackageName = MapNodesPlugin.class.getPackage().getName();
            String packageName = tmpPackageName.substring(tmpPackageName.indexOf(' ') + 1).replaceAll("\\.", "/");
            MapNodesPlugin.debug(tmpPackageName);
            MapNodesPlugin.debug(packageName);
            List<Class<? extends GameMode>> pendingRegister = new LinkedList<>();
            List<JarEntry> entries = appJar.stream()
                            .filter(jar -> jar.toString().startsWith(packageName))
                            .collect(Collectors.toList());

            for (JarEntry entry : entries) {
                boolean routes = entry.toString().startsWith(packageName + "/gamemodes/");
                String entryName = Common.formatPath(entry.getName());

                    if (routes && !entry.isDirectory()) {
                        Class<?> clazz = Class.forName(entryName, false, MapNodesPlugin.class.getClassLoader());

                        // If its a class that is an route handle add it to the list to be sorted
                        if (clazz != null && GameMode.class.isAssignableFrom(clazz)) {
                            MapNodesPlugin.debug("Adding instance: " + clazz.getCanonicalName());
                            pendingRegister.add((Class<? extends GameMode>) clazz);
                        }
                    }
            }

            pendingRegister.forEach(factory::add);

            return factory;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public void getGameClasses(File file, Set<Class<? extends GameMode>> games) throws ClassNotFoundException {
        checkArgument(checkNotNull(file).exists());
        checkNotNull(games);

        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                getGameClasses(file, games);
            }
            else {
                Class<?> clazz = MapNodesPlugin.class.getClassLoader().loadClass(child.getName());

                if (clazz.isAssignableFrom(GameMode.class) && clazz.isAnnotationPresent(GameModeInfo.class)) {
                    games.add((Class<? extends GameMode>) clazz);
                    MapNodesPlugin.log(Msg.util("games.class.load", clazz.toString()));
                }
            }
        }
    }*/

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

    @Override
    public LogUtil getLogUtil() {
        return log;
    }
}
