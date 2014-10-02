package net.year4000.mapnodes.game.regions.events;

import com.google.gson.annotations.SerializedName;
import net.year4000.mapnodes.game.regions.EventType;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.typewrappers.DamageCauseList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

@EventType(EventTypes.ENTITY_DAMAGE)
public class EntityDamage extends RegionEvent implements RegionListener {
    @SerializedName("damage_causes")
    private DamageCauseList<EntityDamageEvent.DamageCause> damageCauses = DamageCauseList.createAllDamageCauses();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!region.inZone(new Point(event.getEntity().getLocation().toVector().toBlockVector()))) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (isAllow() && damageCauses.contains(cause) || !isAllow() && !damageCauses.contains(cause)) {
            event.setCancelled(false);
        }
        else if (!isAllow() && damageCauses.contains(cause)) {
            event.setCancelled(true);
        }
    }
}
