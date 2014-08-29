package net.year4000.mapnodes.game;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.exceptions.WorldLoadException;
import net.year4000.mapnodes.map.MapFolder;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class Node {
    /** The node's id */
    private int id;

    /** Stores the info about the map's json */
    private MatchManager match;

    /** Manages the node's worlds */
    private WorldManager world;

    public Node(int id, MapFolder worldFolder) throws WorldLoadException, InvalidJsonException {
        this.id = id;

        world = new WorldManager(this, worldFolder);

        match = new MatchManager(this, worldFolder);
        match.validate();
    }

    /** Register this node */
    public void register() {
        try {
            initWorld();
        } catch (WorldLoadException e) {
            MapNodesPlugin.debug(e.getMessage());
            NodeFactory.get().loadNextQueued();
        }

        match.register();
    }

    /** Unregister this node */
    public void unregister() {
        if (!Bukkit.getPluginManager().isPluginEnabled(MapNodesPlugin.getInst())) return;
        Preconditions.checkNotNull(world);

        SchedulerUtil.runAsync(() -> {
            int count = 0;
            while (world.getWorld().getPlayers().size() > 0) {
                ++count;

                if (count % 1000000 == 0) {
                    MapNodesPlugin.debug(Msg.util("debug.world.remove", world.getWorld().getName()));
                }

                boolean online = false;

                for (Player player : world.getWorld().getPlayers()) {
                    player.teleport(MapNodes.getCurrentWorld().getSpawnLocation());
                    if (player.isOnline()) {
                        online = true;
                    }
                }

                if (!online) break;
            }

            remove();
        });
    }

    /** Create world */
    public void initWorld() throws WorldLoadException {
        if (world.getWorld() == null) {
            world.unZip();
            world.createWorld();
        }
    }

    /** Remove this node from existing */
    public void remove() {
        world.unloadWorld();
        world.deleteWorld();
    }
}
