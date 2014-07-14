package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.game.components.NodeTeam;
import org.bukkit.ChatColor;

public class SpectatorTeam extends NodeTeam {
    public SpectatorTeam() {
        setName("Spectator");

        setColor(ChatColor.GRAY);

        setKit("spectator");

        setSize(-1);

        setUseScoreboard(false);
    }
}
