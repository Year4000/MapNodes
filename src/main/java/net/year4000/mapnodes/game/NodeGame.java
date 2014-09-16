package net.year4000.mapnodes.game;

import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.events.game.GameClockEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.clocks.NextNode;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.regions.RegionEvents;
import net.year4000.mapnodes.messages.Message;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.GameSet;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Data
@NoArgsConstructor
public final class NodeGame implements GameManager, Validator {
    /** Details about the current map. */
    @Since(1.0)
    private NodeMap map = null;

    /** General game settings. */
    @Since(1.0)
    @SerializedName("world")
    private NodeConfig config = null;

    /** The mini locale system built right into the map.json */
    @Since(1.0)
    private Map<String, Map<String, String>> locales = new ConcurrentHashMap<>();

    @Since(1.0)
    @SerializedName("game")
    private GameSet<GameMode> gameModes = new GameSet<>();

    /** Manage the items and effects that are given to the player. */
    @Since(1.0)
    private Map<String, NodeKit> kits = new ConcurrentHashMap<>();

    /** Manage the regions and zones that can apply effects. */
    @Since(1.0)
    private Map<String, NodeRegion> regions = new ConcurrentHashMap<>();

    /** Manages the teams. */
    @Since(1.0)
    private Map<String, NodeTeam> teams = new ConcurrentHashMap<>();

    /** Classes to pick from on top of your team. */
    @Since(1.0)
    private Map<String, NodeClass> classes = new ConcurrentHashMap<>();

    @Override
    public void validate() throws InvalidJsonException {
        // Required component
        checkNotNull(map != null, Msg.util("settings.map"));

        // Validate component
        map.validate();

        // Required component
        checkNotNull(config != null, Msg.util("settings.game"));

        // Validate component
        config.validate();

        // Required component
        checkNotNull(gameModes != null, Msg.util("settings.modes"));

        // Validate components
        for (GameModeConfig mode : gameModes.stream().map(GameMode::getConfig).collect(Collectors.toList())) {
            mode.validate();
        }

        // Required component
        checkArgument(teams.size() != 0, Msg.util("settings.team"));

        // Validate components
        for (NodeTeam team : teams.values()) {
            team.validate();
        }

        // Validate components
        for (NodeClass clazz : classes.values()) {
            clazz.validate();
        }
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient static final Joiner pageJoiner = Joiner.on('\n');
    private transient Map<UUID, GamePlayer> players = new ConcurrentHashMap<>();
    private transient NodeStage stage = NodeStage.WAITING;
    private transient BukkitTask gameClock;

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String defaultLocale(String key) {
        return locale(Message.DEFAULT_LOCALE, key);
    }

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String locale(Locale locale, String key) {
        return locale(locale.toString(), key);
    }

    /** Get the locale wanted or try to default to en_US or use any locale */
    public String locale(String code, String key) {
        if (locales.size() > 0) {
            if (locales.containsKey(code)) {
                return locales.get(code).getOrDefault(key, key);
            }
            else if (locales.containsKey(Message.DEFAULT_LOCALE)) {
                return locales.get(Message.DEFAULT_LOCALE).getOrDefault(key, key);
            }
            else {
                return locales.values().stream().collect(Collectors.toList()).get(0).getOrDefault(key, key);
            }
        }
        else {
            return key;
        }
    }

    public Stream<GamePlayer> getPlayers() {
        return players.values().stream();
    }

    public Stream<GamePlayer> getPlaying() {
        return getPlayers().filter(GamePlayer::isPlaying);
    }

    public Stream<GamePlayer> getSpectating() {
        return getPlayers().filter(GamePlayer::isSpectator);
    }

    public Stream<GamePlayer> getEntering() {
        return getPlayers().filter(GamePlayer::isEntering);
    }

    public GamePlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void join(Player player) {
        players.put(player.getUniqueId(), new NodePlayer(player));
        ((NodePlayer) getPlayer(player)).join();
    }

    public void quit(Player player) {
        ((NodePlayer) getPlayer(player)).leave();
        players.remove(player.getUniqueId());
    }

    public int getMaxPlayers() {
        int count = 0;

        for (GameTeam team : teams.values()) {
            count += team.getSize();
        }

        return count;
    }

    /** Start the game */
    public void start() {
        stage = NodeStage.PLAYING;

        // Register game mode listeners
        gameModes.forEach(m -> NodeModeFactory.get().registerListeners(m));
        // Register region events
        regions.values().stream()
            .map(NodeRegion::getEvents)
            .filter(e -> e != null)
            .forEach(RegionEvents::registerEvents);

        GameStartEvent start = new GameStartEvent(this);
        start.call();

        // Close spectator inventories
        start.getGame().getSpectating().forEach(player -> player.getPlayer().closeInventory());
        start.getGame().getEntering().forEach(player -> ((NodePlayer) player).start());

        gameClock = SchedulerUtil.repeatAsync(() -> new GameClockEvent(this).call(), 20);
    }

    /** Stop the game and cycle to the next with default time */
    public void stop() {
        stop(null);
    }

    /** Stop the game anc cycle to the next with a time */
    public void stop(@Nullable Integer time) {
        stage = NodeStage.ENDING;

        if (gameClock != null) {
            gameClock.cancel();
        }

        GameStopEvent stop = new GameStopEvent(this);
        stop.call();

        stop.getGame().getPlaying().forEach(p -> ((NodePlayer) p).joinTeam(null));

        // Unregister game mode listeners
        gameModes.forEach(m -> NodeModeFactory.get().unregisterListeners(m));
        // Unregister region events
        regions.values().stream()
            .map(NodeRegion::getEvents)
            .filter(e -> e != null)
            .forEach(RegionEvents::unregisterEvents);

        if (NodeFactory.get().isQueuedGames()) {
            if (time != null) {
                new NextNode(time).run();
            }
            else {
                new NextNode().run();
            }
        }
        else {
            if (time != null) {
                new RestartServer(time).run();
            }
            else {
                new RestartServer().run();
            }
        }
    }

    /** Get the pages for a book with the map's info */
    public List<String> getBookPages(Player player) {
        List<String> pages = new ArrayList<>();

        pages.add(pageJoiner.join(map.getBookPage(player)));
        pages.add(pageJoiner.join(NodeTeam.getBookPage(player)));

        return pages;
    }
}