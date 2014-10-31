package net.year4000.mapnodes.game;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameKit;
import net.year4000.mapnodes.api.game.GameMap;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.system.Spectator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import net.year4000.utilities.bukkit.BukkitUtil;
import net.year4000.utilities.bukkit.FunEffectsUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

@Data
@NoArgsConstructor
/** Manges the teams. */
public class NodeTeam implements GameTeam, Validator, AssignNodeGame {
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
    @Since(2.0)
    @SerializedName("friendly_fire")
    private boolean allowFriendlyFire = false;

    /** Show each invisible teammates as ghosts. */
    @Since(2.0)
    @SerializedName("friendly_invisibles")
    private boolean canSeeFriendlyInvisibles = true;

    /** The kit this team will have. */
    @Since(1.0)
    private String kit = "default"; // todo make a list to allow for multiple kits

    /** Points to where the player can spawn. */
    @Since(1.0)
    private LocationList<Location> spawns = new LocationList<>();

    /** Should this team be tracked by the scoreboard. */
    @Until(1.0)
    @SerializedName("scoreboard")
    private boolean useScoreboard = true;

    @Override
    public void validate() throws InvalidJsonException {
        if (name == null || name.equals("")) {
            throw new InvalidJsonException(Msg.util("settings.team.name"));
        }

        if (spawns.size() == 0) {
            throw new InvalidJsonException(Msg.util("settings.team.spawns"));
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient NodeGame game;
    @Setter(AccessLevel.NONE)
    private transient String id;
    private transient static final String TEAM_FORMAT = "%s%s &7(%s&8/&6%d&7)";
    private transient List<GamePlayer> players = new ArrayList<>();
    private transient Queue<GamePlayer> queue = new PriorityQueue<>();

    /** Assign the game to this region */
    public void assignNodeGame(NodeGame game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    public String getId() {
        if (id == null) {
            NodeTeam thisObject = this;

            game.getTeams().forEach((string, object) -> {
                if (object.equals(thisObject)) {
                    id = string;
                }
            });
        }

        return id;
    }

    /** Join this team or add to queue */
    public void join(GamePlayer player, boolean display) {
        if (players.size() + 1 <= size || size == -1) {
            players.add(player);

            if (display) {
                player.sendMessage(Msg.locale(player, "team.join", getDisplayName()));
            }

            MapNodesPlugin.debug(player.getPlayer().getName() + " join " + name);
        }
        else {
            queue.add(player);

            if (display) {
                player.sendMessage(Msg.locale(player, "team.queue", getDisplayName()));
            }

            MapNodesPlugin.debug(player.getPlayer().getName() + " queue " + name);
        }
    }

    /** Leave the team */
    public void leave(GamePlayer player) {
        // leave team
        if (players.remove(player) || queue.remove(player)) {
            MapNodesPlugin.debug(player.getPlayer().getName() + " left " + name);
        }

        // add queue players to team
        if (queue.size() > 0) {
            GamePlayer nextPlayer = queue.poll();
            join(nextPlayer, true);
            start(nextPlayer);
        }
    }

    /** Start the team for the player */
    public void start(GamePlayer player) {
        if (players.contains(player)) {
            Clocker join = new Clocker(MathUtil.ticks(10)) {
                private Integer[] ticks = {
                    ticks(5),
                    ticks(4),
                    ticks(3),
                    ticks(2),
                    ticks(1)
                };

                public void runFirst(int position) {
                    FunEffectsUtil.playSound(player.getPlayer(), Sound.ORB_PICKUP);
                }

                public void runTock(int position) {
                    GameMap map = game.getMap();

                    if (Arrays.asList(ticks).contains(position)) {
                        FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);
                    }

                    int currentTime = sec(position);
                    String color = Common.chatColorNumber(currentTime, sec(getTime()));
                    String time = color + (new TimeUtil(currentTime, TimeUnit.SECONDS)).prettyOutput("&7:" + color);

                    if (PacketHacks.isTitleAble(player.getPlayer())) {
                        PacketHacks.countTitle(player.getPlayer(), Msg.locale(player, "clocks.join.tock.new"), time, percent(getTime(), position));
                    }
                    else {
                        PacketHacks.title(
                            player.getPlayer(),
                            Msg.locale(player, "clocks.join.tock", map.getName(), time),
                            percent(getTime(), position)
                        );
                    }
                }

                public void runLast(int position) {
                    FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

                    if (PacketHacks.isTitleAble(player.getPlayer())) {
                        PacketHacks.setTitle(player.getPlayer(), Msg.locale(player, "clocks.join.last.new"), "");
                    }
                    else {
                        PacketHacks.title(player.getPlayer(), Msg.locale(player, "clocks.join.last"), 1);
                    }

                    BossBar.removeBar(player.getPlayer());
                    ((NodePlayer) player).start();
                }
            };
            player.getPlayerTasks().add(join.run());
        }
        else {
            MapNodesPlugin.debug(player.getPlayer().getName() + " not starting!");
        }
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

    /** Get the icon for this team */
    public ItemStack getTeamIcon(Locale locale) {
        ItemStack i = new ItemStack(Material.LEATHER_CHESTPLATE);

        if (this instanceof Spectator) {
            i.setItemMeta(ItemUtil.addMeta(i, String.format(
                "{display: {name: '%s', color: '%s', lore: ['%s']}}",
                getDisplayName(),
                getColor().name().toLowerCase(),
                Msg.locale(locale.toString(), "team.menu.join")
            )));
        }
        else {
            i.setItemMeta(ItemUtil.addMeta(i, String.format(
                "{display: {name: '%s', color: '%s', lore: ['%s&7/&6%s', '%s']}}",
                getDisplayName(),
                getColor().name().toLowerCase(),
                Common.colorCapacity(players.size(), size),
                size,
                Msg.locale(locale.toString(), "team.menu.join")
            )));
        }

        return  i;
    }
}
