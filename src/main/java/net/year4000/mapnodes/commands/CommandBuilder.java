package net.year4000.mapnodes.commands;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.messages.Msg;

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
            MapNodesPlugin.debug(Msg.util("debug.command.register"), command.getSimpleName());
            MapNodesPlugin.getInst().registerCommand(command);
        });
    }

}
