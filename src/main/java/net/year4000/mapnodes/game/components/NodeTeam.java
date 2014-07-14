package net.year4000.mapnodes.game.components;

import com.ewized.utilities.bukkit.util.BukkitUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.ScoreboardFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.LogUtil;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.*;


@Data
@NoArgsConstructor
/** Manges the teams. */
public class NodeTeam implements GameTeam, Validator {
    /** The name of the team. */
    @Since(1.0)
    private String name = null;

    /** The color of the team. */
    @Since(1.0)
    private ChatColor color = ChatColor.WHITE;

    /** The max size of the team. */
    @Since(1.0)
    private int size = 1;

    /** Are teammates save from each other. */
    @Since(1.0)
    @SerializedName("friendly_fire")
    private boolean allowFriendlyFire = false;

    /** Show each invisible teammates as ghosts. */
    @Since(1.0)
    @SerializedName("friendly_invisibles")
    private boolean canSeeFriendlyInvisibles = true;

    /** The kit this team will have. */
    @Since(1.0)
    private String kit = "default";

    /** Points to where the player can spawn. */
    //@Since(1.0)
    //private List<Points> spawns = new ArrayList<>();

    /** Should this team be tracked by the scoreboard. */
    @Since(1.0)
    @SerializedName("scoreboard")
    private boolean useScoreboard = true;

    @Override
    public void validate() throws InvalidJsonException {
        if (name == null || name.equals("")) {
            throw new InvalidJsonException(Msg.util("settings.team.name"));
        }

        /*if (spawns.size() == 0) {
            throw new InvalidJsonException(Msg.util("settings.team.spawns"));
        }*/
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient static final String TEAM_FORMAT = "%s%s &7(%s&8/&6%d&7)";
    private transient List<GamePlayer> players = new ArrayList<>();
    private transient Queue<GamePlayer> queue = new PriorityQueue<>();

    /** Join this team or add to queue */
    public void join(GamePlayer player, boolean display) {
        if (players.size() <= size || size == -1) {
            players.add(player);

            if (display) {
                player.sendMessage(Msg.locale(player, "team.join"), getDisplayName());
            }

            LogUtil.debug(player.getPlayer().getName() + " join " + name);
        }
        else {
            queue.add(player);

            if (display) {
                player.sendMessage(Msg.locale(player, "team.queue"), getDisplayName());
            }

            LogUtil.debug(player.getPlayer().getName() + " queue " + name);
        }
    }

    /** Leave the team */
    public void leave(GamePlayer player) {
        ScoreboardFactory.updateTeams();

        // leave team
        if (players.remove(player) || queue.remove(player)) {
            LogUtil.debug(player.getPlayer().getName() + " left " + name);
        }

        // add queue players to team
        if (queue.size() > 0) {
            join(queue.poll(), true);
        }
    }

    /** Start the team for the player */
    public void start(GamePlayer player) {
        //NodeKit.reset(player.getPlayer());
        ScoreboardFactory.updateTeams();


        /*if (players.contains(player)) {
            ScoreboardFactory.updateTeams();
        }
        else {
            LogUtil.debug(player.getPlayer().getName() + " not starting!");
        }*/
    }

    /** Get the number of players */
    public int getPlaying() {
        return players == null ? 0 : players.size();
    }

    public String getDisplayName() {
        return MessageUtil.message(color + name);
    }

    /** Pretty print the team general info */
    public String prettyPrint() {
        return MessageUtil.message(
            TEAM_FORMAT,
            color.toString(),
            name,
            Common.colorCapacity(getPlaying(), size),
            size
        );
    }

    /** Get the kit that is with this team */
    public GameKit getKit() {
        return MapNodes.getCurrentGame().getKits().get(kit);
    }

    /** Get the color of this team */
    public Color getRawColor() {
        return BukkitUtil.dyeColorToColor(BukkitUtil.chatColorToDyeColor(color));
    }

    /** Get the book page for this map */
    public static List<String> getBookPage(Player player) {
        List<String> lines = new ArrayList<>();
        Collection<NodeTeam> teams = MapNodes.getCurrentGame().getTeams().values();

        lines.add(MessageUtil.message("&b&l%s&7:\n", Msg.locale(player, "map.teams")));
        teams.stream().filter(NodeTeam::isUseScoreboard).forEach(t -> lines.add(t.prettyPrint()));

        return lines;
    }
}
