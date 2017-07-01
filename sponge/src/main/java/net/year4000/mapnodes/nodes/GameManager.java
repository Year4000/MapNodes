/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.SpongeBindings;
import net.year4000.mapnodes.events.DeleteWorldEvent;
import net.year4000.mapnodes.events.GameCycleEvent;
import net.year4000.mapnodes.events.MapNodesEvent;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.*;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;

/** This handles all the game logic to and from the v8 engine */
public class GameManager {
  /** The current state of the game */
  enum GameState {WAITING, RUNNING, ENDED}
  /** The colors, index matches the ordinal of the game state */
  private final TextColor[] GAME_STATE_COLORS = {TextColors.YELLOW, TextColors.GREEN, TextColors.RED};
  /** Predict the game state the game is in, this is to avoid unneeded access to the v8 engine */
  private GameState predictGameState = GameState.WAITING;
  /** The score board for the game, todo make it a score bord for each player */
  private Team spectator = Team.builder().name("spectator").color(TextColors.GRAY).allowFriendlyFire(false).prefix(Text.of(TextColors.GRAY)).suffix(Text.of(TextColors.RESET)).build();
  private Scoreboard scoreboard = Scoreboard.builder().teams(Collections.singletonList(spectator)).build();

  @Inject private Game game;
  @Inject private SpongeNode node;
  @Inject private Logger logger;
  @Inject private EventManager eventManager;
  @Inject private NodeManager nodeManager;
  @Inject private SpongeBindings $;

  /** This will cycle to the next game */
  public void cycle() throws Exception {
    if (gameState() != GameState.ENDED) {
      stop(); // Make sure we stop the game
    }
    SpongeNode nextNode = (SpongeNode) nodeManager.loadNextNode();
    logger.info("Cycling to Map " + nextNode.name() + " version " + nextNode.version());
    node.world().ifPresent(world -> { // World never loaded
      world.getPlayers().forEach(player ->  player.transferToWorld(nextNode.worldTransformer().getExtent()));
    });
    eventManager.post(new DeleteWorldEvent(node));
    eventManager.post(new GameCycleEvent());
  }

  /** Get the current state the v8 instance has with the game */
  public GameState gameState() {
    return GameState.valueOf($.js.gameState());
  }

  /** Tell the v8 instance that we want to start the game */
  public void start() {
    if (gameState() == GameState.WAITING) {
      predictGameState = GameState.RUNNING;
      $.js.start();
    } else {
      logger.warn("Can not start a game that is all ready running");
    }
  }

  /** Tell the v8 instance that we want to start the game */
  public void stop() {
    if (gameState() != GameState.ENDED) {
      predictGameState = GameState.ENDED;
      $.js.stop();
    } else {
      logger.warn("Can not stop a game that has been stopped");
    }
  }

  /** Construct the game object within the v8 engine */
  @Listener
  public void onGameCycle(GameCycleEvent event) {
    $.js.swapGame(node.id(), node.v8Object);
  }

  @Listener
  public void onClientPing(ClientPingServerEvent event) throws IOException {
    // Resize to 64 x 64
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(node.map().image().array()));
    BufferedImage bufferedIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
    Graphics graphic = bufferedIcon.createGraphics();
    graphic.drawImage(bufferedImage, 0, 0, 64, 64, null);
    graphic.dispose();
    Favicon favicon = game.getRegistry().loadFavicon(bufferedIcon);
    event.getResponse().setFavicon(favicon);
    event.getResponse().setDescription(Text.of(GAME_STATE_COLORS[predictGameState.ordinal()], predictGameState.name(), TextColors.GRAY, " | ", TextColors.DARK_PURPLE, TextStyles.ITALIC, node.name(), TextColors.LIGHT_PURPLE, " v", node.version()));
  }

  @Listener
  public void join(ClientConnectionEvent.Login event) {
    $.js.onEvent(event);
    event.setToTransform(node.worldTransformer());
  }

  @Listener
  public void join(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
    player.offer(Keys.CAN_FLY, true);
    player.offer(Keys.IS_FLYING, true);
    player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
    $.js.onEvent(event);
    $.js.joinGame(player);
    // scoreboard things
    scoreboard.getTeam("spectator").get().addMember(Text.of(player.getName()));
    player.setScoreboard(scoreboard);
  }

  @Listener
  public void leave(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
    $.js.onEvent(event);
    $.js.leaveGame(player);
    // scoreboard things
    scoreboard.getTeam("spectator").get().removeMember(Text.of(player.getName()));
  }

  /** Listen to events to pass into the game */
  @Listener(order = Order.LAST)
  @Include({ChangeBlockEvent.class, MapNodesEvent.class, InteractInventoryEvent.class, MoveEntityEvent.class})
  public void event(Event event) {
    $.js.onEvent(event);
  }
}
