package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

@EventType(EventTypes.KILL_PLAYER)
public class KillPlayer extends RegionEvent implements RegionListener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        if (!region.inZone(new Point(event.getEntity().getKiller().getLocation().toVector().toBlockVector()))) {
            return;
        }

        GamePlayer player = region.getGame().getPlayer(event.getEntity().getKiller());

        if (applyToPlayer(player)) {
            runGlobalEventTasks(player);
            runGlobalEventTasks(event.getEntity().getLocation());
        }
    }
}
