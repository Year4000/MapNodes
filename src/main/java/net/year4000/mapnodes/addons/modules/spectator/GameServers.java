package net.year4000.mapnodes.addons.modules.spectator;

import com.ewized.utilities.bukkit.util.ItemUtil;
import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.game.components.NodeKit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AddonInfo(
    name = "Game Servers",
    version = "1.0",
    description = "The spectator start menu where players can pick their team and ect.",
    listeners = {GameServers.class}
)
public class GameServers extends Addon implements Listener {
    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();

        kit.getItems().set(4, ItemUtil.makeItem("nether_star", "{'display':{'name':'&aGame Servers'}}"));
    }
}