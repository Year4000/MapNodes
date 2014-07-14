package net.year4000.mapnodes;

import com.ewized.utilities.bukkit.util.MessageUtil;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.minecraft.util.commands.*;
import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.addons.modules.mapnodes.Internals;
import net.year4000.mapnodes.addons.modules.spectator.*;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.WorldManager;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class MapNodesPlugin extends JavaPlugin implements Plugin {
    @Getter
    private static MapNodesPlugin inst = null;
    private CommandsManager commands = new BukkitCommandsManager();
    private Addons addons = new Addons();

    @Override
    public void onLoad() {
        inst = this;
        MapNodes.init(inst);

        // Set debug mode
        String value = System.getProperty("debug");
        if (value != null) {
            LogUtil.debug = Boolean.parseBoolean(value);
        }
        else {
            LogUtil.debug = Settings.get().isDebug();
        }

        // Clean out old maps
        WorldManager.removeStrayMaps();

        // Load new maps
        new MapFactory();
    }

    @Override
    public void onEnable() {
        List<Node> maps = NodeFactory.get().getAllGames();

        // Disable if no loaded maps
        if (maps.size() == 0) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Generate all the games
        maps.forEach(node -> LogUtil.log(
            Msg.util("debug.map.ready"),
            node.getMatch().getGame().getMap().getName(),
            node.getMatch().getGame().getMap().getVersion()
        ));

        // Addons (The internal system that loads addons)
        // The order is the dependency list
        addons.builder()
            .add(Internals.class)
            .add(GameMenu.class)
            .add(PlayerMenu.class)
            .add(GameServers.class)
            .add(MapBook.class)
            .add(OpenInventories.class)
            .add(DeathMessages.class)
            .register();
    }

    @Override
    public void onDisable() {
        // Tasks that must happen when the plugin loaded with maps
        if (NodeFactory.get().getAllGames().size() > 0) {
            MapNodes.getCurrentGame().getPlayers().forEach(p -> {
                p.getPlayer().kickPlayer(MessageUtil.message(Msg.locale(p, "clocks.restart.last")));
                LogUtil.log(p.getPlayer().getName() + " " + Msg.locale(p, "clocks.restart.last"));
            });
            NodeFactory.get().getCurrentGame().unregister();


            addons.builder().unregister();
        }

        // Tasks that can be ran with out plugin loaded
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.shutdown();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandName, String[] args) {
        List<String> msg = new ArrayList<>();
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            msg.add(MessageUtil.message(Msg.locale(sender, "error.cmd.permission")));
        } catch (MissingNestedCommandException e) {
            msg.add(MessageUtil.message(Msg.locale(sender, "error.cmd.usage"), e.getUsage()));
        } catch (CommandUsageException e) {
            msg.add(MessageUtil.message(Msg.util("global.error"), e.getMessage()));
            msg.add(MessageUtil.message(Msg.locale(sender, "error.cmd.usage"), e.getUsage()));
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                msg.add(MessageUtil.message(Msg.locale(sender, "error.cmd.number")));
            }
            else {
                msg.add(MessageUtil.message(Msg.locale(sender, "error.cmd.error")));
                e.printStackTrace();
            }
        } catch (CommandException e) {
            msg.add(MessageUtil.message(Msg.util("global.error"), e.getMessage()));
        } finally {
            Iterator<String> line = msg.listIterator();
            if (line.hasNext()) {
                sender.sendMessage(MessageUtil.message(Msg.util("global.warring"), line.next()));
                while (line.hasNext()) {
                    sender.sendMessage(MessageUtil.message(line.next()));
                }
            }
        }

        return true;
    }

    /*//----------------------------//
         Current Node Quick Methods
    *///----------------------------//

    @Override
    public GameManager getCurrentGame() {
        return NodeFactory.get().getCurrentGame().getMatch().getGame();
    }

    @Override
    public World getCurrentWorld() {
        return NodeFactory.get().getCurrentGame().getWorld().getWorld();
    }
}
