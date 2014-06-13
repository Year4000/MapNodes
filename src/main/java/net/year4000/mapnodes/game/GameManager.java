package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.LocationUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.addons.SpectatorKit;
import net.year4000.mapnodes.addons.SpectatorTeam;
import net.year4000.mapnodes.configs.MapConfig;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.configs.map.Classes;
import net.year4000.mapnodes.configs.map.Kits;
import net.year4000.mapnodes.configs.map.Points;
import net.year4000.mapnodes.configs.map.Teams;
import net.year4000.mapnodes.clocks.NextClock;
import net.year4000.mapnodes.clocks.RestartClock;
import net.year4000.mapnodes.clocks.StartClock;
import net.year4000.mapnodes.utils.Minify;
import net.year4000.mapnodes.utils.MissingJsonElement;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@Setter(AccessLevel.PRIVATE)
@SuppressWarnings("all")
public class GameManager {
    /** The world of this game. */
    private World world;
    /** The scoreboard for this game. */
    private GameScoreboard scoreboard;
    /** The current map for the game. */
    private GameMap map;
    /** The teams of the game. */
    private HashMap<String, GameTeam> teams;
    /** The classes of the game. */
    private HashMap<String, GameClass> teamClasses;
    /** The game kits. */
    private HashMap<String, GameKit> kits;
    /** The Current Stage of the game. */
    @Setter(AccessLevel.PUBLIC)
    private GameStage stage;
    /** The players that are in the game. */
    @Setter(AccessLevel.PUBLIC)
    private HashMap<UUID, GamePlayer> players = new HashMap<>();
    /** The start time of the game. */
    private long startTime;
    /** The stop time of the game. */
    private long stopTime;
    /** The icon for this game. */
    CachedServerIcon icon;
    /** Is stated manualy dont stop auto. */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private boolean manStart = false;

    /** When creating this instance set all the feilds accoriding to the config. */
    public GameManager(World worldMap) throws FileNotFoundException, MissingJsonElement {
        // Load the map.json
        Gson gson = new Gson();
        Scanner in = new Scanner(new File(worldMap.getWorldFolder(), "map.json"));
        String json = "";
        while (in.hasNext()) json += in.nextLine() + '\n';

        // Load the server icon.
        try {
            setIcon(Bukkit.loadServerIcon(new File(worldMap.getWorldFolder(), "map.png")));
        } catch (Exception e) {/*Left Blank*/}

        // Set up the game.
        try {
            // If json fails to load due to invalid json catch and dont load map.
            MapConfig mapConfig = gson.fromJson(Minify.minify(json), MapConfig.class);

            // Set the world must be done first.
            setWorld(worldMap);
            setStage(GameStage.WAITING);

            // Set the map part must be done before kits, teams, classes.
            setMap(new GameMap(mapConfig, getWorld()));

            // Registering the scoreboard must be done be for teams.
            setScoreboard(new GameScoreboard(getMap()));

            // Set the kits must be done before teams and team classes.
            HashMap<String, GameKit> kits = new HashMap<>();
            kits.put("SPECTATOR", new SpectatorKit()); // Built in kit
            for (Map.Entry<String, Kits> kit : mapConfig.getKits().entrySet()) {
                kits.put(kit.getKey().toUpperCase(), new GameKit(kit.getKey(), mapConfig));
            }
            setKits(kits);

            // Set the teams, must be done before team classes.
            HashMap<String, GameTeam> teams = new HashMap<>();
            teams.put("SPECTATOR", new GameTeam(new SpectatorTeam(this), this)); // Built in team

            checkArgument(mapConfig.getTeams().entrySet().size() > 1, Messages.get("error-json-team-one"));
            for (Map.Entry<String, Teams> team : mapConfig.getTeams().entrySet()) {
                //String kit = team.getValue().getKit();
                teams.put(
                    team.getKey().toUpperCase(),
                    new GameTeam(team.getValue(), this)
                );
            }

            setTeams(teams);

            // Set up the classes this does not need to be loaded
            HashMap<String, GameClass> teamClasses = new HashMap<>();

            if (mapConfig.getClasses() != null) {
                for (Map.Entry<String, Classes> gameClass : mapConfig.getClasses().entrySet()) {
                    teamClasses.put(
                        gameClass.getKey().toUpperCase(),
                        new GameClass(gameClass.getValue(), this)
                    );
                }
            }

            setTeamClasses(teamClasses);

        } catch (Exception e) {
            //e.printStackTrace();
            throw new MissingJsonElement(e, MessageUtil.stripColors(e.getMessage()));
        }
    }

    /** Get the player by its name. */
    public GamePlayer getPlayer(Player player) {
        return getPlayers().get(player.getUniqueId());
    }

    /** Get the team by its name. */
    public GameTeam getTeam(String team) throws NullPointerException {
        return getTeams().get(team.toUpperCase());
    }

    /** Create a HashMap from two points. */
    protected static List<Location> createListLocation(
        final World world,
        final Points points
    ) {
        List<Location> locations = new ArrayList<>();
        // Single Point
        if (points.getPoint() != null) {
            Location loc = LocationUtil.parseLocation(
                world,
                points.getPoint().toString(),
                " "
            );
            locations.add(loc);
        }
        // Multi Points
        else {
            for (double y = points.getMin().getY(); y <= points.getMax().getY(); y++) {
                for (double x = points.getMin().getX(); x <= points.getMax().getX(); x++) {
                    for (double z = points.getMin().getZ(); z <= points.getMax().getZ(); z++) {
                        //Block block = world.getBlockAt(x, y, z);
                        //if (!block.getChunk().isLoaded()) block.getChunk().load(true);
                        locations.add(LocationUtil.create(world, x, y, z));
                    }
                }
            }
        }

        return locations;
    }

    /** Gets the smallest team. */
    public String getSmallestTeam() {
        String smallest = "";
        int size = -1;
        boolean first = true;
        for (GameTeam team : getTeams().values()) {
            if (first) {
                size = team.getCurrentSize();
                smallest = team.getName();
                first = false;
            }
            if (team.getCurrentSize() < size) {
                size = team.getCurrentSize();
                smallest = team.getName();
            }
        }
        return smallest.toUpperCase();
    }

    /** Set the start and end time. */
    public void setStartTime(long time) {
        startTime = time;
        int endTime = getMap().getTimeLimit() > 60 ? 60 : getMap().getTimeLimit();
        stopTime = startTime + (60000 * endTime-1);
    }

    /** Get the size of each team together. */
    public int getGameSize() {
        int size = 0;

        // Do the caculations.
        for (GameTeam team : getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            size += team.getCurrentSize();
        }

        return size;
    }

    /** Get the max size of each team together. */
    public int getGameMaxSize() {
        int size = 0;

        // Do the caculations.
        for (GameTeam team : getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            size += team.getMaxSize();
        }

        return size;
    }

    /** Should we end the game for not having the required players. */
    public boolean shouldEnd() {
        // If the game was started with the command dont end the game.
        //if (manStart) return false;

        // Do the caculations.
        for (GameTeam team : getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            if (team.getCurrentSize() == 0) return true;
        }

        return false;
    }

    /** Should we end the game for the last team standing */
    public boolean shouldEndLastTeam() {
        // If the game was started with the command dont end the game.
        //if (manStart) return false;
        int liveTeams = 0;

        // Do the caculations.
        for (GameTeam team : getTeams().values()) {
            if (team.getName().equals("SPECTATOR")) continue;
            if (team.getCurrentSize() > 0) liveTeams++;
        }

        return liveTeams <= 1;
    }

    /** Is the current player a map maker to the current map. */
    public static boolean isMapMaker(GamePlayer player) {
        for (String author : WorldManager.get().getCurrentGame().getMap().getAuthors()) {
            if (player.getPlayer().getName().equalsIgnoreCase(author))
                return true;
        }

        return false;
    }

    /** Start the match. */
    public void startMatch() {
        new StartClock();
        setStage(GameStage.STARTING);
    }

    /** Stop the match. */
    public void stopMatch() {
        setStage(GameStage.ENDING);
        setManStart(false);

        for (GamePlayer player : getPlayers().values()) {
            if (!player.isSpecatator()) {
                // TODO MOVE FIREWORK TO FUN EFFECT UTIL
                Firework firework = (Firework)player.getPlayer().getWorld().spawn(player.getPlayer().getLocation(), Firework.class);
                FireworkEffect effect = FireworkEffect.builder()
                        .flicker(true)
                        .trail(true)
                        .withColor(Color.RED)
                        .withFade(Color.ORANGE)
                        .with(FireworkEffect.Type.STAR).build();
                FireworkMeta meta = firework.getFireworkMeta();
                meta.clearEffects();
                meta.addEffect(effect);
                meta.setPower(1);
                firework.setFireworkMeta(meta);
                FunEffectsUtil.playSound(player.getPlayer(), Sound.FIREWORK_LARGE_BLAST2);

                GameHelper.endMessage(player);
            }

            try {
                getTeam("SPECTATOR").join(getPlayer(player.getPlayer()));
                getPlayer(player.getPlayer()).getTeam().getKit().giveKit(player);
            } catch (Exception e) {/*Left Blank*/}
        }

        if (!(WorldManager.get().getCurrentIndex() + 1 >= WorldManager.get().getGames().size()))
            new NextClock(); // Load the next map.
        else
            new RestartClock(); // Restart the server.
    }
}
