package net.year4000.mapnodes.gamemodes.skywars;

import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.game.NodeClass;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.gamemodes.elimination.Elimination;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@GameModeInfo(
    name = "Skywars",
    version = "1.1",
    config = SkywarsConfig.class
)
public class Skywars extends Elimination {

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
}
