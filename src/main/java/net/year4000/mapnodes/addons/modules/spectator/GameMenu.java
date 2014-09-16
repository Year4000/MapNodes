package net.year4000.mapnodes.addons.modules.spectator;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AddonInfo(
    name = "Game Menu",
    version = "1.0",
    description = "The spectator start menu where players can pick their team and ect.",
    listeners = {GameMenu.class}
)
public class GameMenu extends Addon implements Listener {
    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();

        kit.getItems().set(0, ItemUtil.makeItem("eye_of_ender", "{'display':{'name':'&6Game Menu'}}"));
    }
}
