package net.year4000.mapnodes.game.system;

import net.year4000.mapnodes.game.NodeTeam;
import org.bukkit.ChatColor;

public class SpectatorTeam extends NodeTeam implements Spectator {
    public SpectatorTeam() {
        setName("Spectator");

        setColor(ChatColor.GRAY);

        setKit("spectator");

        setSize(Integer.MAX_VALUE);

        setUseScoreboard(false);
    }
}
