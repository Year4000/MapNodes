package net.year4000.mapnodes.addons.modules.spectator;

import com.ewized.utilities.bukkit.util.ItemUtil;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.components.NodeKit;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

@AddonInfo(
    name = "Map Book",
    version = "1.0",
    description = "The book and map that spectators of the maps info.",
    listeners = {MapBook.class}
)
public class MapBook extends Addon implements Listener {
    public ItemStack book(GamePlayer player) {
        GameManager gm = MapNodes.getCurrentGame();

        return ItemUtil.createBook(
            "&b" + gm.getMap().getName(),
            "&5&o" + gm.getMap().getMainAuthor(),
            ((NodeGame) gm).getBookPages(player.getPlayer())
        );
    }

    @EventHandler
    public void onTeamJoin(GamePlayerJoinTeamEvent e) {
        GameManager gm = MapNodes.getCurrentGame();

        gm.getSpectating().forEach(player -> {
            Inventory inv = player.getPlayer().getInventory();

            inv.setItem(8, book(player));
        });
    }

    @EventHandler
    public void onGameStart(GameStartEvent e) {
        e.getGame().getSpectating().forEach(player -> {
            Inventory inv = player.getPlayer().getInventory();

            inv.setItem(8, book(player));
        });
    }

    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();
        GameManager gm = MapNodes.getCurrentGame();

        // Book
        kit.getItems().set(8, book(e.getPlayer()));

        // Map icon
        if (NodeFactory.get().getCurrentGame().getMatch().getIcon() != null) {
            MapView view = Bukkit.createMap(MapNodes.getCurrentWorld());
            view.addRenderer(new MapRenderer() {
                @Override
                public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                    mapView.setCenterX(Integer.MAX_VALUE);
                    mapView.setCenterZ(Integer.MAX_VALUE);
                    mapCanvas.drawImage(0,0, MapPalette.resizeImage(NodeFactory.get().getCurrentGame().getMatch().getIconImage()));
                }
            });
            short damage = view.getId();
            ItemStack item = ItemUtil.makeItem("map", 1, damage);
            item.setItemMeta(ItemUtil.addMeta(item, "{'display':{'name':'&3" + gm.getMap().getName() + "'}}"));
            kit.getItems().set(7, item);
        }
    }
}