package net.year4000.mapnodes.addons.modules.misc;

import net.year4000.mapnodes.addons.Addon;
import net.year4000.mapnodes.addons.AddonInfo;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.player.GamePlayerDeathEvent;
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

        GamePlayerDeathEvent deathEvent = new GamePlayerDeathEvent(event);
        deathEvent.call();

        // Send messages
        if (killer != null) {
            // Show messages to others depending on event
            deathEvent.getViewers().stream()
                .filter(v -> !v.getPlayer().getName().equals(player.getName())) // not player
                .filter(v -> !v.getPlayer().getName().equals(killer.getName())) // not killer
                .forEach(v -> v.sendMessage(getMessage(v.getPlayer(), player, killer)));

            killer.sendMessage(getMessage(killer, player, killer));
            player.sendMessage(getMessage(player, player, killer));
        }
        else {
            GamePlayer gPlayer = MapNodes.getCurrentGame().getPlayer(player);
            String formattedMessage = message.replaceAll(player.getName(), gPlayer.getPlayerColor() + "&7&o") + ".";

            // Show messages to others depending on event
            deathEvent.getViewers().stream()
                .filter(v -> !v.getPlayer().getName().equals(player.getName())) // not player
                .forEach(v -> v.sendMessage(formattedMessage));

            gPlayer.sendMessage(formattedMessage);
        }
    }

    public String replace(Player k, Player d, Player viewer, String message) {
        GameManager gm = MapNodes.getCurrentGame();

        return MessageUtil.message(
            "&7&o" + Msg.locale(viewer, message,
            gm.getPlayer(d).getPlayerColor() + "&7&o",
            gm.getPlayer(k).getPlayerColor() + "&7&o"
        ));
    }

    public String replace(Player k, Player d, Player viewer, String message, Object args) {
        GameManager gm = MapNodes.getCurrentGame();

        return MessageUtil.message(
            "&7&o" + Msg.locale(viewer, message,
            gm.getPlayer(d).getPlayerColor() + "&7&o",
            gm.getPlayer(k).getPlayerColor() + "&7&o",
            String.valueOf(args)
        ));
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
                return replace(k, d, viewer, "death.block_explosion");
            case DROWNING:
                return replace(k, d, viewer, "death.drowing");
            case FALL:
                return replace(k, d, viewer, "death.fall");
            case ENTITY_ATTACK:
                return replace(k, d, viewer, "death.entity_attack", humanItem(k.getItemInHand()));
            case ENTITY_EXPLOSION:
                return replace(k, d, viewer, "death.entity_explosion");
            case FALLING_BLOCK:
                return replace(k, d, viewer, "death.falling_block");
            case FIRE:
            case FIRE_TICK:
                return replace(k, d, viewer, "death.fire");
            case LAVA:
                return replace(k, d, viewer, "death.lava");
            case POISON:
                return replace(k, d, viewer, "death.poison");
            case LIGHTNING:
                return replace(k, d, viewer, "death.lighting");
            case PROJECTILE:
                int distance = (int) Math.sqrt(k.getLocation().distanceSquared(d.getLocation()));
                return replace(k, d, viewer, "death.projectile", String.valueOf(distance));
            case MAGIC:
                return replace(k, d, viewer, "death.magic");
            case SUFFOCATION:
                return replace(k, d, viewer, "death.suffocation");
            case THORNS:
                return replace(k, d, viewer, "death.thorns");
            case WITHER:
                return replace(k, d, viewer, "death.wither");
            case VOID:
                return replace(k, d, viewer, "death.void");
            default:
                return replace(k, d, viewer, "death.default");
        }
    }
}
