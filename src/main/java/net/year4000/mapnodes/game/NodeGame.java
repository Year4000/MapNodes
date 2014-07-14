package net.year4000.mapnodes.game;

import com.google.common.base.Joiner;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.game.GameStopEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.clocks.NextNode;
import net.year4000.mapnodes.clocks.RestartServer;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.*;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
    @SerializedName("game")
    private NodeConfig config = null;

    /** Manage the items and effects that are given to the player. */
    @Since(1.0)
    private Map<String, NodeKit> kits = new ConcurrentHashMap<>();

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

        GameStartEvent start = new GameStartEvent(this);
        start.call();

        // Close spectator inventories
        start.getGame().getSpectating().forEach(player -> player.getPlayer().closeInventory());
        start.getGame().getEntering().forEach(player -> ((NodePlayer) player).start());

        // nodeclock

    }

    /** Stop the game and cycle to the next with default time */
    public void stop() {
        stop(null);
    }

    /** Stop the game anc cycle to the next with a time */
    public void stop(@Nullable Integer time) {
        stage = NodeStage.ENDING;

        GameStopEvent stop = new GameStopEvent(this);
        stop.call();

        stop.getGame().getPlaying().forEach(p -> ((NodePlayer) p).joinTeam(null));

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