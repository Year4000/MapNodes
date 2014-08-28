package net.year4000.mapnodes.addons.modules.spectator;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.game.components.NodeKit;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AddonInfo(
    name = "Player Menu",
    version = "1.0",
    description = "A spectator item that show all game player's in a gui.",
    listeners = {PlayerMenu.class}
)
public class PlayerMenu extends Addon implements Listener {
    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();

        kit.getItems().set(1, ItemUtil.makeItem("Compass", "{'display':{'name':'&5Player Menu'}}"));
    }
}
