package net.year4000.mapnodes;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.minecraft.util.commands.*;
import lombok.Getter;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.config.Settings;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MapNodesPlugin extends JavaPlugin implements MapNodes {
    @Getter
    private static MapNodesPlugin inst = null;

    @Getter
    private CommandsManager commands = new BukkitCommandsManager();

    @Getter
    private Settings config = new Settings();

    @Override
    public void onEnable() {
        inst = this;
        LogUtil.debug = config.isDebug();

        MapFactory maps = new MapFactory();
    }

    @Override
    public void onDisable() {
        // Delete worlds created by MapNodes
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
}
