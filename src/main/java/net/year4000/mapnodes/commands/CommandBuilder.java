package net.year4000.mapnodes.commands;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    private List<Class<?>> commands = new ArrayList<>();

    public CommandBuilder add(Class<?> commandClass) {
        commands.add(commandClass);
        return this;
    }

    public void register() {
        MapNodesPlugin plugin = MapNodesPlugin.getInst();
        commands.forEach(command -> {
            LogUtil.debug(Msg.util("debug.command.register"), command.getSimpleName());
            new CommandsManagerRegistration(plugin, plugin.getCommands()).register(command);
        });
    }

}
