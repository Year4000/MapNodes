package net.year4000.mapnodes.game.regions.events;

import net.year4000.mapnodes.api.game.regions.EventType;
import net.year4000.mapnodes.api.game.regions.RegionListener;
import net.year4000.mapnodes.game.regions.EventTypes;
import net.year4000.mapnodes.game.regions.RegionEvent;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.utils.typewrappers.EntityTypeList;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

@EventType(EventTypes.CREATURE_SPAWN)
public class CreatureSpawn extends RegionEvent implements RegionListener {
    private EntityTypeList<EntityType> creatures = new EntityTypeList<>();

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!region.inZone(new Point(event.getLocation().toVector().toBlockVector()))) return;

        EntityType entityType = event.getEntityType();

        if (isAllowSet()) {
            if ((isAllow() && creatures.contains(entityType)) || (!isAllow() && !creatures.contains(entityType))) {
                event.setCancelled(false);
            }
            else if ((isAllow() && !creatures.contains(entityType)) || (!isAllow() && creatures.contains(entityType))) {
                event.setCancelled(true);
            }
        }

        runGlobalEventTasks(event.getLocation());
    }
}
