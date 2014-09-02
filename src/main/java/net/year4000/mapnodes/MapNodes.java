package net.year4000.mapnodes;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.year4000.mapnodes.addons.KillStreak;
import net.year4000.mapnodes.addons.OpenInventories;
import net.year4000.mapnodes.addons.PlayerDeathMessages;
import net.year4000.mapnodes.commands.*;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.clocks.ChunkUnloadClock;
import net.year4000.mapnodes.clocks.NodeClock;
import net.year4000.mapnodes.clocks.WorldClock;
import net.year4000.mapnodes.listeners.*;
import net.year4000.mapnodes.world.WorldManager;
import net.year4000.utilities.bukkit.BukkitPlugin;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;

public class MapNodes extends BukkitPlugin {
    @Getter
    @SuppressWarnings("all")
    private static MapNodes inst;

    @Override
    public void onEnable() {
        if (inst != null) return;

        inst = this;
        // Enable the configs.
        MainConfig.get();

        // Load the maps into the worlds.
        WorldManager.get();

        // If no world's can be loaded disable self
        if (!WorldManager.get().isNextGame()) {
            MapNodes.log(MessageUtil.stripColors(Messages.get("error-games-none")));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Enable the listeners.
        new SpectatorListener();
        new GameListener();
        new MapConfigListener();
        new MapNodesListener();
        new PlayerDeathMessages();
        new KillStreak();
        new OpenInventories();

        // NodeClock
        new NodeClock();
        new WorldClock();
        new ChunkUnloadClock();
        new MapListener();

        // Enable the commands.
        registerCommand(JoinCommand.class);
       registerCommand(MapsCommand.class);
       registerCommand(GameCommand.class);
       registerCommand(NodeBaseCommand.class);
       registerCommand(MatchBaseCommand.class);

    }

    @Override
    public void onDisable() {
        // Delete worlds created by MapNodes
        FileUtils.deleteQuietly(WorldManager.get().getCurrentGame().getWorld().getWorldFolder());
        WorldManager.get().getGames().forEach(w -> WorldManager.get().removeGame(w));
    }
}
