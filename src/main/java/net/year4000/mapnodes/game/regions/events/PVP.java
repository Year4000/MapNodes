package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@EventType(EventTypes.PVP)
public class PVP extends RegionEvent implements RegionListener {
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player)) return;

        if (!region.inZone(new Point(event.getEntity().getLocation().toVector().toBlockVector()))) return;

        GamePlayer player = region.getGame().getPlayer((Player) event.getDamager());

        if (applyToPlayer(player)) {
            if (!isAllow()) {
                event.setCancelled(true);
            }

            runGlobalEventTasks(player);
        }
    }
}
