package net.year4000.mapnodes.game;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import com.ewized.utilities.bukkit.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

@Data
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class GamePlayer {
    private final GameManager game = WorldManager.get().getCurrentGame();
    /** The player that this represents. */
    private Player player;
    /** The number of lives this player has. */
    @Setter(AccessLevel.PRIVATE)
    private int lives;
    /** The string of the team the player belongs to. */
    private GameTeam team;
    /** The string of the teamClass this player has. */
    private GameClass teamClass;
    /** The number of kill streak this player has. */
    @Setter(AccessLevel.PUBLIC)
    private int killStreak = 0;
    /** The time since last kill. */
    @Setter(AccessLevel.PUBLIC)
    private long lastKill = System.currentTimeMillis();
    /** The number of kill streak this player has. */
    @Setter(AccessLevel.PUBLIC)
    private int quickKills = 0;
    /** Has this player played in the game before. */
    private boolean hasPlayed = false;

    /** Is the player a spectator. */
    public boolean isSpecatator() {
        return getTeam() == null || getTeam().getName().equals("SPECTATOR");
    }

    /** Respawn the player. */
    public void respawn() {
        GameTeam.hideSpectator();

        getTeam().getKit().giveKit(this);

        if (getTeamClass() != null)
            getTeamClass().getKit().giveKit(this);
    }

    /** Player joins the current game. */
    public static void join(final Player player) {
        final GameManager gm = WorldManager.get().getCurrentGame();

        // Set up gamePlayer
        GamePlayer gPlayer = new GamePlayer();
        gPlayer.setPlayer(player);
        gPlayer.setLives(gm.getMap().getLives());
        gm.getPlayers().put(player.getUniqueId(), gPlayer);

        // Tp the player to the current map's spawn.
        try {
            gm.getTeam("SPECTATOR").join(gm.getPlayer(player));
            gPlayer.start();
        } catch (Exception e) {/*Left Blank*/}


        if (!GameStage.isEndGame()) {
            if (!gm.getMap().isElimination())
                player.openInventory(GameTeam.getTeamsGUI());
        }

        player.setScoreboard(gm.getScoreboard().getScoreboard());
        player.setResourcePack(gm.getMap().getResourcepack());

        // Show message of the current map, later to allow time for choosing a team.
        Bukkit.getScheduler().runTaskLater(MapNodes.getInst(), () ->
            player.sendMessage(MessageUtil.replaceColors(String.format(
                Messages.get(player.getLocale(), "game-login"),
                gm.getMap().getName(),
                gm.getMap().getVersion(),
                gm.getMap().getAuthors().get(0)
            ))), 5 * 20);
    }

    /** Add x life's */
    public void addLife(int amount) {
        setLives(getLives() + amount);
    }

    /** Add a one life */
    public void addLife() {
        addLife(1);
    }

    /** Remove x life's */
    public void removeLife(int amount) {
        setLives(getLives() - amount);
    }

    /** Remove a one life */
    public void removeLife() {
        removeLife(1);
    }

    /** Player leaves the current game. */
    public void leave() {
        GameManager gm = WorldManager.get().getCurrentGame();
        getTeam().getPlayers().remove(this);
        gm.getPlayers().remove(getPlayer().getUniqueId());
    }

    /** Start the game for the player. */
    public void start() {
        if (!getTeam().getName().equals("SPECTATOR"))
            hasPlayed = true;

        getPlayer().teleport(getTeam().getSafeRandomSpawn());
        FunEffectsUtil.playEffect(getPlayer(), Effect.SMOKE);
        GameHelper.startMessage(this);

        // Give out the fresh kit.
        respawn();
    }


    /** Get the score of this player. */
    public int getScore() {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        return gs.getListScore(getPlayer().getName()).getScore();
    }

    /** Add a score to the player. */
    public void addScore() {
        addScore(1);
    }

    /** Add a score to the player. */
    public void addScore(int amount) {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        int last = gs.getListScore(getPlayer().getName()).getScore();
        gs.getListScore(getPlayer().getName()).setScore(last+amount);
    }

    /** Remove a score to the player. */
    public void removeScore() {
        removeScore(1);
    }

    /** Remove a score to the player. */
    public void removeScore(int amount) {
        GameScoreboard gs = WorldManager.get().getCurrentGame().getScoreboard();
        int last = gs.getListScore(getPlayer().getName()).getScore();
        gs.getListScore(getPlayer().getName()).setScore(last-amount);
    }

    /** Get the display name of the current player. */
    public String getPlayerColor() {
        Player player = getPlayer();

        return MessageUtil.replaceColors(String.format(
            "%s%s&r",
            getTeam().getChatColor(),
            player.getName()
        ));
    }
}