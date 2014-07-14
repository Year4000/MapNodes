package net.year4000.mapnodes;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.minecraft.util.commands.*;
import lombok.Getter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import net.year4000.mapnodes.addons.KillStreak;
import net.year4000.mapnodes.addons.OpenInventories;
import net.year4000.mapnodes.addons.PlayerDeathMessages;
import net.year4000.mapnodes.configs.MainConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.clocks.NodeClock;
import net.year4000.mapnodes.clocks.WorldClock;
import net.year4000.mapnodes.listeners.*;
import net.year4000.mapnodes.game.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MapNodes extends JavaPlugin {
    @Getter
    @SuppressWarnings("all")
    private static MapNodes inst;
    @Getter
    private CommandsManager commands;

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
        new MapListener();

        // Enable the commands.
        commands = new BukkitCommandsManager();
        new JoinCommand();
        new MapsCommand();
        new GameCommand();
        new NodeBaseCommand();
        new MatchBaseCommand();

    }

    @Override
    public void onDisable() {
        // Delete worlds created by MapNodes
        FileUtils.deleteQuietly(WorldManager.get().getCurrentGame().getWorld().getWorldFolder());
        WorldManager.get().getGames().forEach(w -> WorldManager.get().removeGame(w));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        List<String> msg = new ArrayList<>();
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            msg.add(MessageUtil.replaceColors("&cYou don't have permission to use this command."));
        } catch (MissingNestedCommandException e) {
            msg.add(MessageUtil.replaceColors("&6Usage&7: &c" + e.getUsage()));
        } catch (CommandUsageException e) {
            msg.add(MessageUtil.replaceColors("&c" + e.getMessage()));
            msg.add(MessageUtil.replaceColors("&6Usage&7: &c" + e.getUsage()));
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                msg.add(MessageUtil.replaceColors("&cNumber expected, string received instead."));
            }
            else {
                msg.add(MessageUtil.replaceColors("&cAn error has occurred. See console."));
                e.printStackTrace();
            }
        } catch (CommandException e) {
            msg.add(MessageUtil.replaceColors("&c" + e.getMessage()));
        } finally {
            if (!msg.isEmpty()) {
                boolean first = true;

                for (String line : msg) {
                    sender.sendMessage(MessageUtil.replaceColors(first ? " &7[&e⚠&7] " : "") + line);
                    first = false;
                }
            }
        }

        return true;
    }

    /** Log a message to the console. */
    public static void log(String message) {
        getInst().getLogger().log(Level.INFO, message);
    }
}
