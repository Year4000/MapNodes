package net.year4000.mapnodes.game.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.utilities.ChatColor;
import net.year4000.utilities.bukkit.MessageUtil;

@Data
@AllArgsConstructor
public class SidebarGoal {
    /** The Goal Type for the score board */
    public enum GoalType {
        STATIC,
        DYNAMIC,
        ;
    }

    private GoalType type;
    private String display;
    private Integer score;
    private String owner;

    public String getTeamDisplay(GamePlayer player) {
        if (((NodeTeam) player.getTeam()).getId().equals(owner)) {
            return MessageUtil.replaceColors(display).replaceAll(ChatColor.COLOR_CHAR + "([0-9a-fA-F])", "&$1&o");
        }
        else {
            return display;
        }
    }
}
