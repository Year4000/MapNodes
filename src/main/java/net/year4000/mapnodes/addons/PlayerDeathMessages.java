package net.year4000.mapnodes.addons;

import com.ewized.utilities.bukkit.util.MessageUtil;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class PlayerDeathMessages implements Listener {
    public PlayerDeathMessages() {
        Bukkit.getPluginManager().registerEvents(this, MapNodes.getInst());
    }

    @EventHandler
    /** Only show the message to the player involved. */
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = MessageUtil.replaceColors("&7&o" + event.getDeathMessage());
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Player killer = player.getKiller();

        // Check if your death was null
        if (player.getLastDamageCause() == null) return;

        // Send messages
        if (killer != null) {
            killer.sendMessage(getMessage(player, killer));
            player.sendMessage(getMessage(player, killer));
        }
        else {
            GameManager gm = WorldManager.get().getCurrentGame();
            player.sendMessage(message.replaceAll(
                player.getName(),
                MessageUtil.replaceColors(gm.getPlayer(player).getPlayerColor() + "&7&o")
            ) + ".");
        }
    }

    /** Add the players names with their team color. */
    public String replace(Player k, Player d, String message) {
        GameManager gm = WorldManager.get().getCurrentGame();

        return MessageUtil.replaceColors("&7&o" + String.format(
                message,
                gm.getPlayer(d).getPlayerColor() + "&7&o",
                gm.getPlayer(k).getPlayerColor() + "&7&o"
        ));
    }

    /** Format the item so humans can read it. */
    public String humanItem(ItemStack item) {
        return item.getType().name().replaceAll("_", " ").toLowerCase();
    }

    /** Messages based on death. */
    public String getMessage(Player d, Player k) {
        switch (d.getLastDamageCause().getCause()) {
            case BLOCK_EXPLOSION:
                return replace(k, d, "%s was exploded by %s.");
            case DROWNING:
                return replace(k, d, "%s was doomed to drown by %s.");
            case FALL:
                return replace(k, d, "%s was pushed by %s to their death.");
            case ENTITY_ATTACK:
                return replace(k, d, "%s was killed by %s's " + humanItem(k.getItemInHand()) + ".");
            case ENTITY_EXPLOSION:
                return replace(k, d, "%s exploded by %s.");
            case FALLING_BLOCK:
                return replace(k, d, "%s was hit on the head by %s.");
            case FIRE:
            case FIRE_TICK:
                return replace(k, d, "%s was messing with %s's fire.");
            case LAVA:
                return replace(k, d, "%s jumped in lava because of %s.");
            case POISON:
                return replace(k, d, "%s was poisoned by %s.");
            case LIGHTNING:
                return replace(k, d, "%s was shocked by %s's thor hammer.");
            case PROJECTILE:
                int distance = (int)Math.sqrt(k.getLocation().distanceSquared(d.getLocation()));
                return replace(k, d, "%s was shot by %s at a distance of " + distance + " blocks.");
            case MAGIC:
                return replace(k, d, "%s played with %s using unknown forces.");
            case SUFFOCATION:
                return replace(k, d, "%s got stuck in a wall by %s.");
            case THORNS:
                return replace(k, d, "%s tried to hug %s but was pricked to death.");
            case WITHER:
                return replace(k, d, "%s withered away from %s.");
            case VOID:
                return replace(k, d, "%s fell into the void by %s.");
            default:
                return replace(k, d, "%s has been killed by %s.");
        }
    }

}
