/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes.listeners;

import com.google.inject.Inject;
import net.year4000.mapnodes.MapNodesPlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.text.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class GameListener {

  @Inject private MapNodesPlugin mapnodes;
  @Inject private Game game;

  @Listener
  public void onClientPing(ClientPingServerEvent event) throws IOException {
    if (mapnodes.currentNode() == null) return;
    // Resize to 64 x 64
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(mapnodes.currentNode().map().image().array()));
    BufferedImage bufferedIcon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
    Graphics graphic = bufferedIcon.createGraphics();
    graphic.drawImage(bufferedImage, 0, 0, 64, 64, null);
    graphic.dispose();
    Favicon favicon = game.getRegistry().loadFavicon(bufferedIcon);
    event.getResponse().setFavicon(favicon);
    event.getResponse().setDescription(Text.of(mapnodes.currentNode().name() + " version " + mapnodes.currentNode().version()));
  }

  @Listener
  public void join(ClientConnectionEvent.Login event) {
    event.setToTransform(mapnodes.currentNode().worldTransformer());
  }

  @Listener
  public void join(ClientConnectionEvent.Join event) {
    event.getTargetEntity().gameMode().set(GameModes.SPECTATOR);
  }
}
