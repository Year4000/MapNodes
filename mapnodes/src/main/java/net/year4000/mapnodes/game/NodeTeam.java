/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.*;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.clocks.Clocker;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.MathUtil;
import net.year4000.mapnodes.utils.PacketHacks;
import net.year4000.mapnodes.utils.TimeUtil;
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
import java.util.stream.Collectors;

import static net.year4000.mapnodes.utils.MathUtil.percent;
import static net.year4000.mapnodes.utils.MathUtil.ticks;

public class NodeTeam implements GameTeam {
    /** The name of the team. */
    @Since(1.0)
    protected String name = null;
    /** The color of the team. */
    @Since(1.0)
    protected ChatColor color = ChatColor.WHITE;
    /** The max size of the team. */
    @Since(1.0)
    protected int size = 1;
    /** Are teammates save from each other. */
    @Since(2.0)
    @SerializedName("friendly_fire")
    protected boolean allowFriendlyFire = false;
    /** Show each invisible teammates as ghosts. */
    @Since(2.0)
    @SerializedName("friendly_invisibles")
    protected boolean canSeeFriendlyInvisibles = true;
    /** The kit this team will have. */
    @Since(1.0)
    protected String kit = "default"; // todo make a list to allow for multiple kits

    public NodeTeam() {
    }

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

    public static final transient String SPECTATOR = "spectator";
    private transient static final String TEAM_FORMAT = "%s%s &7(%s&8/&6%d&7)";
    private final transient String id = id();
    /** Points to where the player can spawn. */
    @Since(1.0)
    protected LocationList<Location> spawns = new LocationList<>();
    /** Should this team be tracked by the scoreboard. */
    @Until(1.0)
    @SerializedName("scoreboard")
    protected boolean useScoreboard = true;
    private transient GameManager game;
    private transient List<GamePlayer> players = new ArrayList<>();
    private transient Queue<GamePlayer> queue = new PriorityQueue<>();

    public NodeTeam(NodeGame game, String name, LocationList<Location> spawns) {
        assignNodeGame(game);
        this.name = name;
        this.spawns = spawns;
    }

    /** Get the book page for this map */
    public static List<String> getBookPage(Player player) {
        List<String> lines = new ArrayList<>();
        Collection<GameTeam> teams = MapNodes.getCurrentGame().getPlayingTeams().collect(Collectors.toList());

        lines.add(MessageUtil.message("&b&l%s&7:\n", Msg.locale(player, "map.teams")));
        teams.stream().forEach(t -> lines.add(t.prettyPrint()));

        return lines;
    }

    /** Assign the game to this region */
    public void assignNodeGame(GameManager game) {
        this.game = game;
    }

    /** Get the id of this class and cache it */
    private String id() {
        NodeTeam thisObject = this;

        for (Map.Entry<String, GameTeam> entry : game.getTeams().entrySet()) {
            if (thisObject.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Can not find the id of " + this.toString());
    }

    public Location getSafeRandomSpawn() {
        return spawns.getSafeRandomSpawn();
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

            if (game.getStage().isPlaying()) {
                start(nextPlayer);
            }
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

                    PacketHacks.countTitle(player.getPlayer(), Msg.locale(player, "clocks.join.tock.new"), time, percent(getTime(), position));
                }

                public void runLast(int position) {
                    FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

                    PacketHacks.setTitle(player.getPlayer(), Msg.locale(player, "clocks.join.last.new"), "");

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

        return i;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public int getSize() {
        return this.size;
    }

    public boolean isAllowFriendlyFire() {
        return this.allowFriendlyFire;
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return this.canSeeFriendlyInvisibles;
    }

    public LocationList<Location> getSpawns() {
        return this.spawns;
    }

    public boolean isUseScoreboard() {
        return this.useScoreboard;
    }

    public GameManager getGame() {
        return this.game;
    }

    public List<GamePlayer> getPlayers() {
        return this.players;
    }

    public Queue<GamePlayer> getQueue() {
        return this.queue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        this.allowFriendlyFire = allowFriendlyFire;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public void setSpawns(LocationList<Location> spawns) {
        this.spawns = spawns;
    }

    public void setUseScoreboard(boolean useScoreboard) {
        this.useScoreboard = useScoreboard;
    }

    public void setGame(GameManager game) {
        this.game = game;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }

    public void setQueue(Queue<GamePlayer> queue) {
        this.queue = queue;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NodeTeam)) return false;
        final NodeTeam other = (NodeTeam) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$color = this.getColor();
        final Object other$color = other.getColor();
        if (this$color == null ? other$color != null : !this$color.equals(other$color)) return false;
        if (this.getSize() != other.getSize()) return false;
        if (this.isAllowFriendlyFire() != other.isAllowFriendlyFire()) return false;
        if (this.isCanSeeFriendlyInvisibles() != other.isCanSeeFriendlyInvisibles()) return false;
        final Object this$kit = this.getKit();
        final Object other$kit = other.getKit();
        if (this$kit == null ? other$kit != null : !this$kit.equals(other$kit)) return false;
        final Object this$spawns = this.getSpawns();
        final Object other$spawns = other.getSpawns();
        if (this$spawns == null ? other$spawns != null : !this$spawns.equals(other$spawns)) return false;
        if (this.isUseScoreboard() != other.isUseScoreboard()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $color = this.getColor();
        result = result * PRIME + ($color == null ? 43 : $color.hashCode());
        result = result * PRIME + this.getSize();
        result = result * PRIME + (this.isAllowFriendlyFire() ? 79 : 97);
        result = result * PRIME + (this.isCanSeeFriendlyInvisibles() ? 79 : 97);
        final Object $kit = this.getKit();
        result = result * PRIME + ($kit == null ? 43 : $kit.hashCode());
        final Object $spawns = this.getSpawns();
        result = result * PRIME + ($spawns == null ? 43 : $spawns.hashCode());
        result = result * PRIME + (this.isUseScoreboard() ? 79 : 97);
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof NodeTeam;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.NodeTeam(name=" + this.getName() + ", color=" + this.getColor() + ", size=" + this.getSize() + ", allowFriendlyFire=" + this.isAllowFriendlyFire() + ", canSeeFriendlyInvisibles=" + this.isCanSeeFriendlyInvisibles() + ", kit=" + this.getKit() + ", id=" + this.getId() + ", spawns=" + this.getSpawns() + ", useScoreboard=" + this.isUseScoreboard() + ", game=" + this.getGame() + ", players=" + this.getPlayers() + ", queue=" + this.getQueue() + ")";
    }

    public String getId() {
        return this.id;
    }
}
