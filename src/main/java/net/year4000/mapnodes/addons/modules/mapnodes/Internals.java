package net.year4000.mapnodes.addons.modules.mapnodes;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.commands.CommandBuilder;
import net.year4000.mapnodes.commands.mapnodes.MapNodesBase;
import net.year4000.mapnodes.commands.maps.MapCommands;
import net.year4000.mapnodes.commands.match.MatchBase;
import net.year4000.mapnodes.commands.misc.MenuCommands;
import net.year4000.mapnodes.commands.node.NodeBase;
import net.year4000.mapnodes.listeners.GameListener;
import net.year4000.mapnodes.listeners.MapNodesListener;
import net.year4000.mapnodes.listeners.WorldListener;

@AddonInfo(
    name = "Internals",
    version = "1.0",
    description = "This is the add on that load's the internal listeners and commands.",
    listeners = {
        GameListener.class,
        MapNodesListener.class,
        WorldListener.class
    }
)
public class Internals extends Addon {
    public void start() {
        // Register Commands
        new CommandBuilder()
            .add(MapNodesBase.class)
            .add(MapCommands.class)
            .add(MatchBase.class)
            .add(MenuCommands.class)
            .add(NodeBase.class)
            .register();
    }
}
