package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockVector;

@EventType(EventTypes.ENTER)
public class Enter extends RegionEvent implements RegionListener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        BlockVector vector = event.getTo().toVector().toBlockVector();

        // Skip check if not a new block
        if (event.getFrom().toVector().toBlockVector().equals(vector)) {
            return;
        }

        Point point = new Point(vector);

        if (!region.inZone(point)) {
            return;
        }

        GamePlayer player = region.getGame().getPlayer(event.getPlayer());

        // Should we deny entrance
        if (applyToPlayer(player)) {
            if (isAllowSet() && !isAllow()) {
                event.setTo(event.getFrom());
            }

            runGlobalEventTasks(player);
            runGlobalEventTasks(event.getTo());
        }
    }
}
