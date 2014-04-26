package net.year4000.mapnodes.game;


import com.ewized.utilities.bukkit.util.BukkitUtil;
import com.ewized.utilities.bukkit.util.ItemUtil;
import com.ewized.utilities.bukkit.util.LocationUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Points;
import net.year4000.mapnodes.configs.map.Teams;
import net.year4000.mapnodes.clocks.DelayJoin;
import net.year4000.mapnodes.utils.PlayerBadges;
import net.year4000.mapnodes.utils.TeamException;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.year4000.mapnodes.game.GameManager.createListLocation;

@Data
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class GameTeam {
    /** The team this team is. */
    private Team team;
    /** Get the display name of the team. */
    private String displayName;
    /** The name of the team. */
    private String name;
    /** The color of the team. */
    private ChatColor chatColor;
    /** The color of the team. */
    private Color color;
    /** The name of the color. */
    private String colorName;
    /** The max size of the team. */
    private int maxSize;
    /** The kit this team will have. */
    private GameKit kit;
    /** Points to where the player can spawn. */
    private List<Location> spawns = new ArrayList<>();
    /** Gets the players that are on this team. */
    private List<GamePlayer> players = new ArrayList<>();
    /** The inventory to select teams from. */
    private static Inventory teamsGUI = null;

    public GameTeam(Teams team, GameManager gameManager) throws NullPointerException {
        // Team name
        setName(checkNotNull(team.getName(), Messages.get("error-json-team-name")));

        // Team color
        String teamColor = checkNotNull(team.getColor().toUpperCase(), Messages.get("error-json-team-color"));
        setColorName(teamColor);
        setChatColor(ChatColor.valueOf(teamColor));
        setColor(BukkitUtil.dyeColorToColor(BukkitUtil.chatColorToDyeColor(getChatColor())));
        setDisplayName(MessageUtil.replaceColors(getChatColor() + getName() + "&r"));

        // Team max size
        setMaxSize(checkNotNull(team.getSize(), Messages.get("error-json-team-size")));

        // Set up the scoreboard team for this player.
        Team sbTeam = gameManager.getScoreboard().getScoreboard().registerNewTeam(getName());
        sbTeam.setAllowFriendlyFire(team.isAllowFriendlyFire());
        sbTeam.setCanSeeFriendlyInvisibles(team.isCanSeeFriendlyInvisibles());
        sbTeam.setDisplayName(getName());
        sbTeam.setPrefix(getChatColor().toString());
        sbTeam.setSuffix(MessageUtil.replaceColors("&r"));
        setTeam(sbTeam);

        if (team.isUseScoreboard()) {
            Objective o = sbTeam.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
            o.getScore(getDisplayName()).setScore(0);
        }

        // Set up the kits
        setKit(checkNotNull(
            gameManager.getKits().get(team.getKit().toUpperCase()),
            Messages.get("error-json-team-kit")
        ));

        // Set up the spawns.
        for (Points point : checkNotNull(team.getSpawns(), Messages.get("error-json-spawn"))) {
                getSpawns().addAll(createListLocation(gameManager.getWorld(), point));
        }
    }

    /** Get a random spawn. */
    private Location getRandomSpawn() {
        Random random = new Random(System.currentTimeMillis());
        int spawn = Math.abs(random.nextInt()) % getSpawns().size();

        return LocationUtil.center(getSpawns().get(spawn));
    }

    /** Get a safe random spawn. */
    public Location getSafeRandomSpawn() {
        Location spawn = getRandomSpawn();

        // If loop crashes because we cant find a slot use the next position even if its bad.
        try {
            // Only check if there is more than one slot
            if (getSpawns().size() > 1) {
                // Check floor
                if (spawn.getBlock().getRelative(0,-1,0).isEmpty())
                    spawn = getSafeRandomSpawn();
                // Check head
                else if (!spawn.getBlock().getRelative(0,1,0).isEmpty())
                    spawn = getSafeRandomSpawn();
                // Check feet
                else if (!spawn.getBlock().isEmpty())
                    spawn = getSafeRandomSpawn();
            }
        } catch (StackOverflowError e) {
            spawn = getRandomSpawn();
        }

        return spawn;
    }

    /** Joins the team. */
    public void join(GamePlayer player, boolean force) throws TeamException {
        boolean sizeFit = getCurrentSize() >= getMaxSize();
        boolean hasPerms = player.getPlayer().hasPermission(Messages.get("team-gui-perm"));
        boolean unlimited = getMaxSize() == -1;

        // Prevent players from entering when the game ended!
        if (GameStage.isEndGame() && !getName().equals("SPECTATOR"))
            throw new TeamException(Messages.get(player.getPlayer().getLocale(), "team-join-error"));

        // Don't allow player from entering the match if elimination mode is on
        GameManager gm = WorldManager.get().getCurrentGame();
        if (!GameStage.isPreGame() && gm.getMap().isElimination() && !getName().equals("SPECTATOR"))
            throw new TeamException(Messages.get(player.getPlayer().getLocale(), "team-join-error"));

        // Checks if player can join the team.
        if (!GameManager.isMapMaker(player)) {
            if (sizeFit && !hasPerms && !unlimited)
                throw new TeamException(Messages.get(player.getPlayer().getLocale(), "team-full"));

            if (!hasPerms && !unlimited && !force)
                throw new TeamException(Messages.get(player.getPlayer().getLocale(), "team-gui-perm-message"));
        }

        // Add the player to various sub tasks
        addPlayer(player);

        // Message
        if (!getName().equals("SPECTATOR")) {
            player.getPlayer().sendMessage(MessageUtil.replaceColors(String.format(
                Messages.get(player.getPlayer().getLocale(), "team-join"),
                getDisplayName()
            )));

            // If game started use delay joiner
            if (GameStage.isPlaying())
                new DelayJoin(player, player.getPlayer().hasPermission(Messages.get("team-gui-perm")) ? 5 : 15);
        }

        // Various team tasks
        updateTeamGUI();
        hideSpectator();
    }

    /** Joins the team. */
    public void join(GamePlayer player) throws TeamException {
        join(player, false);
    }

    /** Leaves the current team. */
    public void leave(GamePlayer player) {
        GameManager gm = WorldManager.get().getCurrentGame();
        getTeam().removePlayer(player.getPlayer()); // Scoreboard team
        getPlayers().remove(player); // This object team
        updateTeamGUI();
    }

    /** Add the player to this team and scoreboard. */
    private void addPlayer(GamePlayer player) {
        getPlayers().add(player);
        getTeam().addPlayer(player.getPlayer());
        player.setTeam(this);
        setPlayerColor(player);
    }

    /** Get the teams current size */
    public int getCurrentSize() {
        return getPlayers().size();
    }

    /** Get the current size of the team with colors. */
    public String getColorSize() {
        int size = getCurrentSize();

         String color;
        // Colors red yellow green
        if (size >= getMaxSize())
            color = "&c";
        else if (size + 2 >= getMaxSize())
            color = "&e";
        else
            color = "&a";

        return MessageUtil.replaceColors(String.format(
            "%s%s&7/&6%s",
            color,
            size,
            getMaxSize()
        ));
    }

    /** Get the score of this team. */
    public int getScore() {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        return gs.getSidebarScore(getDisplayName()).getScore();
    }

    /** Add a score to the team. */
    public void addScore() {
        addScore(1);
    }

    /** Add a score to the team. */
    public void addScore(int amount) {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        int last = gs.getSidebarScore(getDisplayName()).getScore();
        gs.getSidebarScore(getDisplayName()).setScore(last+amount);
    }

    /** Remove a score to the team. */
    public void removeScore() {
        removeScore(1);
    }

    /** Remove a score to the team. */
    public void removeScore(int amount) {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        int last = gs.getSidebarScore(getDisplayName()).getScore();
        gs.getSidebarScore(getDisplayName()).setScore(last-amount);
    }

    /** Get the Inventory to pick teams. */
    public static Inventory getTeamsGUI() {
        if (teamsGUI == null) {
            teamsGUI = Bukkit.createInventory(
                null,
                BukkitUtil.invBase(WorldManager.get().getCurrentGame().getTeams().size() + 1),
                Messages.get("team-gui-title")
            );
            updateTeamGUI();
        }

        return teamsGUI;
    }

    /** Update the items for the team gui. */
    public static void updateTeamGUI() {
        GameManager gm = WorldManager.get().getCurrentGame();
        ItemStack[] items = new ItemStack[gm.getTeams().size()];
        ItemStack rand = new ItemStack(Material.NETHER_STAR);
        int teams = 1;

        rand.setItemMeta(ItemUtil.addMeta(rand, String.format(
            "{display:{name:\"%s\",lore:[\"%s%s&7/&6%s\",\"%s\"]}}",
            "Random",
            gm.getGameSize() >= gm.getGameMaxSize() ? "&c" : "&a",
            gm.getGameSize(),
            gm.getGameMaxSize(),
            Messages.get("team-gui-join-random")
        )));
        items[0] = rand;
        for (GameTeam team : gm.getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            ItemStack i = new ItemStack(Material.LEATHER_CHESTPLATE);
            i.setItemMeta(ItemUtil.addMeta(i, String.format(
                "{display:{name:\"%s\",color:\"%s\",lore:[\"%s\",\"%s\"]}}",
                team.getName(),
                team.getColorName(),
                team.getColorSize(),
                Messages.get("team-gui-join")
            )));
            items[teams] = i;
            teams++;
        }

        Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> getTeamsGUI().setContents(items));
    }

    /** Set the display name of the current player. */
    private void setPlayerColor(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();
        String colorName = MessageUtil.replaceColors(String.format(
            "%s%s",
            getChatColor(),
            player.getName()
        ));

        player.setPlayerListName(PlayerBadges.getBadge(player) + " " + (colorName.length() > 12 ? colorName.substring(0,11) : colorName));
        player.setDisplayName(MessageUtil.replaceColors(colorName + "&r"));
    }

    /** Manage how the players see each other. */
    public static void hideSpectator() {
        for (GamePlayer gPlayer : WorldManager.get().getCurrentGame().getPlayers().values()) {
            for (GamePlayer player : WorldManager.get().getCurrentGame().getPlayers().values()) {
                if ((player.isSpecatator() || !player.isHasPlayed()) && !gPlayer.isSpecatator())
                    gPlayer.getPlayer().hidePlayer(player.getPlayer());
                else
                    gPlayer.getPlayer().showPlayer(player.getPlayer());
            }
        }
    }
}
