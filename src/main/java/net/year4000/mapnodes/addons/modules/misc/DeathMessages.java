package net.year4000.mapnodes.addons.modules.misc;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

@AddonInfo(
    name = "Death Messages",
    version = "1.2",
    description = "Show death messages that is better than vanilla.",
    listeners = {DeathMessages.class}
)
public class DeathMessages extends Addon implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    /** Only show the message to the player involved. */
    public void onPlayerDeath(PlayerDeathEvent event) {
        String message = MessageUtil.message("&7&o%s", event.getDeathMessage());
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Player killer = player.getKiller();

        // Check if your death was null
        if (player.getLastDamageCause() == null) return;

        // Send messages
        if (killer != null) {
            killer.sendMessage(getMessage(killer, player, killer));
            player.sendMessage(getMessage(player, player, killer));
        }
        else {
            GamePlayer gPlayer = MapNodes.getCurrentGame().getPlayer(player);
            gPlayer.sendMessage(message.replaceAll(player.getName(), gPlayer.getPlayerColor() + "&7&o") + ".");
        }
    }

    /** Add the players names with their team color. */
    public String replace(Player k, Player d, String message, Object... args) {
        GameManager gm = MapNodes.getCurrentGame();

        return MessageUtil.message(
            "&7&o" + message,
            gm.getPlayer(d).getPlayerColor() + "&7&o",
            gm.getPlayer(k).getPlayerColor() + "&7&o",
            args
        );
    }

    /** Format the item so humans can read it. */
    public String humanItem(ItemStack item) {
        // TODO: Locale support from client
        String name = item.getType().name().replaceAll("_", " ").toLowerCase();

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            return "enchanted " + name;
        }

        return name;
    }

    /** Messages based on death. */
    public String getMessage(Player viewer, Player d, Player k) {
        switch (d.getLastDamageCause().getCause()) {
            case BLOCK_EXPLOSION:
                return replace(k, d, Msg.locale(viewer, "death.block_explosion"));
            case DROWNING:
                return replace(k, d, Msg.locale(viewer, "death.drowing"));
            case FALL:
                return replace(k, d, Msg.locale(viewer, "death.fall"));
            case ENTITY_ATTACK:
                return replace(k, d, Msg.locale(viewer, "death.entity_attack"), humanItem(k.getItemInHand()));
            case ENTITY_EXPLOSION:
                return replace(k, d, Msg.locale(viewer, "death.entity_explosion"));
            case FALLING_BLOCK:
                return replace(k, d, Msg.locale(viewer, "death.falling_block"));
            case FIRE:
            case FIRE_TICK:
                return replace(k, d, Msg.locale(viewer, "death.fire"));
            case LAVA:
                return replace(k, d, Msg.locale(viewer, "death.lava"));
            case POISON:
                return replace(k, d, Msg.locale(viewer, "death.poison"));
            case LIGHTNING:
                return replace(k, d, Msg.locale(viewer, "death.lighting"));
            case PROJECTILE:
                int distance = (int)Math.sqrt(k.getLocation().distanceSquared(d.getLocation()));
                return replace(k, d, Msg.locale(viewer, "death.projectile"), distance);
            case MAGIC:
                return replace(k, d, Msg.locale(viewer, "death.magic"));
            case SUFFOCATION:
                return replace(k, d, Msg.locale(viewer, "death.suffocation"));
            case THORNS:
                return replace(k, d, Msg.locale(viewer, "death.thorns"));
            case WITHER:
                return replace(k, d, Msg.locale(viewer, "death.wither"));
            case VOID:
                return replace(k, d, Msg.locale(viewer, "death.void"));
            default:
                return replace(k, d, Msg.locale(viewer, "death.default"));
        }
    }
}
