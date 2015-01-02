package net.year4000.mapnodes.gamemodes.skywars;

import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeClass;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.gamemodes.elimination.Elimination;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

@GameModeInfo(
    name = "Skywars",
    version = "1.1",
    config = SkywarsConfig.class
)
public class Skywars extends Elimination {
    private Map<String, Integer> kills = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoadSkyWars(GameLoadEvent event) {
        NodeGame game = ((NodeGame) event.getGame());
        game.loadClass("skywars_enderman", new NodeClass(game, "Enderman", Material.ENDER_PEARL, "skywars.enderman", "skywars_enderman"));
        game.loadClass("skywars_archer", new NodeClass(game, "Archer", Material.BOW, "skywars.archer", "skywars_archer"));
        game.loadClass("skywars_runner", new NodeClass(game, "Runner", Material.GOLD_BOOTS, "skywars.runner", "skywars_runner"));
        game.loadClass("skywars_heavy", new NodeClass(game, "Heavy", Material.LEATHER_CHESTPLATE, "skywars.heavy", "skywars_heavy"));
        game.loadClass("skywars_demoman", new NodeClass(game, "Demoman", Material.TNT, "skywars.demoman", "skywars_demoman"));
        game.loadClass("skywars_jumper", new NodeClass(game, "Jumper", Material.SLIME_BALL, "skywars.jumper", "skywars_jumper"));
    }

    // todo After x time go to sudden death and start blowing up islands

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFire(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFire(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            kills.put(killer.getName(), kills.getOrDefault(killer.getName(), 0) + 1);
        }
    }

    /** Build the sidebar and send it to the players */
    @Override
    public void buildAndSendList() {
        game.getSidebarGoals().clear();
        int total = alive.size() + dead.size();

        if (total > 15) {
            game.addDynamicGoal("alive", MessageUtil.replaceColors("&6Alive&7:"), alive.size());
            game.addDynamicGoal("dead", MessageUtil.replaceColors("&6Dead&7:"), dead.size());
        }
        else {
            alive.stream()
                .sorted((l, r) -> kills.getOrDefault(l, 0) < kills.getOrDefault(r, 0) ? 1 : -1)
                .forEach(name -> game.addStaticGoal(name, "&7(&e" + kills.getOrDefault(name, 0) + "&7) &a" + name));
            dead.forEach(name -> game.addStaticGoal(name, "&7(&e" + kills.getOrDefault(name, 0) + "&7) &c&m" + name));
        }
    }
}
