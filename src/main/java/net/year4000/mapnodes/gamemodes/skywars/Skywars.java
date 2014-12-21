package net.year4000.mapnodes.gamemodes.skywars;

import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeClass;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.gamemodes.elimination.Elimination;
import net.year4000.mapnodes.utils.Common;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStart(GameStartEvent event) {
        alive.forEach(player -> kills.put(player, 0));
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            kills.put(killer.getName(), kills.get(killer.getName()) + 1);
        }
    }

    /** Build the sidebar and send it to the players */
    @Override
    public void buildAndSendList() {
        game.getSidebarGoals().clear();

        if (alive.size() + dead.size() > 16) {
            game.addDynamicGoal("alive", MessageUtil.replaceColors("&6Alive&7:"), alive.size());
            game.addDynamicGoal("dead", MessageUtil.replaceColors("&6Dead&7:"), dead.size());
        }
        else {
            int total = alive.size() + dead.size();
            alive.forEach(name -> game.addStaticGoal(name, "&7(" + Common.chatColorNumber(kills.get(name), total) + "&7) &a" + name));
            dead.forEach(name -> game.addStaticGoal(name, "&7(" + Common.chatColorNumber(kills.get(name), total) + "&7) &c&m" + name));
        }

        game.getPlaying().forEach(player -> game.getScoreboardFactory().setGameSidebar((NodePlayer) player));
    }
}
