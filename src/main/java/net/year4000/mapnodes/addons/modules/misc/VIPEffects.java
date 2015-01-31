package net.year4000.mapnodes.addons.modules.misc;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.utils.Common;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@AddonInfo(
    name = "VIP Effects",
    version = "1.0",
    description = "Give effects to vip as they need to have cool effects.",
    listeners = {VIPEffects.class}
)
public class VIPEffects extends Addon implements Listener {
    /** Blood Effect */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        Location loc = event.getEntity().getLocation().clone().add(0, 1, 0).subtract(Common.randomOffset());

        MapNodes.getCurrentGame().getPlaying()
            .filter(player -> Common.isVIP(player.getPlayer()))
            .filter(player -> player.getPlayer().getLocation().distance(loc) < 50)
            .forEach(player -> {
                Location l = event.getEntity().getLocation().clone().add(0, 1, 0);
                player.getPlayer().playEffect(l.clone().add(Common.randomOffset()), Effect.STEP_SOUND, 152);
                player.getPlayer().playEffect(l.clone().add(Common.randomOffset()), Effect.STEP_SOUND, 152);
                player.getPlayer().playEffect(l.clone().add(Common.randomOffset()), Effect.STEP_SOUND, 152);
            });
    }
}
