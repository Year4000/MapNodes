/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.nodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.SpongeBindings;
import net.year4000.mapnodes.events.DeleteWorldEvent;
import net.year4000.mapnodes.events.GameCycleEvent;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/** This handles all the game logic to and from the v8 engine */
public class GameManager {
  /** The current state of the game */
  enum GameState {WAITING, RUNNING, ENDED}

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
      world.getPlayers().forEach(player -> {
        Transform<World> transform = nextNode.worldTransformer();
        player.transferToWorld(transform.getExtent(), transform.getPosition());
      });
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
      $.js.start();
    } else {
      logger.warn("Can not start a game that is all ready running");
    }
  }

  /** Tell the v8 instance that we want to start the game */
  public void stop() {
    if (gameState() != GameState.ENDED) {
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
    $.js.onEvent(event);
    // Resize to 64 x 64
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(node.map().image().array()));
    BufferedImage bufferedIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
    Graphics graphic = bufferedIcon.createGraphics();
    graphic.drawImage(bufferedImage, 0, 0, 64, 64, null);
    graphic.dispose();
    Favicon favicon = game.getRegistry().loadFavicon(bufferedIcon);
    event.getResponse().setFavicon(favicon);
    event.getResponse().setDescription(Text.of(node.name() + " version " + node.version()));
  }

  @Listener
  public void join(ClientConnectionEvent.Login event) {
    $.js.onEvent(event);
    event.setToTransform(node.worldTransformer());
  }

  @Listener
  public void join(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
    $.js.onEvent(event);
    player.gameMode().set(GameModes.SPECTATOR);
  }

}