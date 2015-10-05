/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.agar;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.year4000.mapnodes.GameModeTemplate;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerStartEvent;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.utils.SchedulerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@GameModeInfo(
    name = "Agar",
    version = "1.0.0",
    config = Agar.class
)
@GameModeConfigName("agar")
public class Agar extends GameModeTemplate implements GameModeConfig, GameMode, Listener {
    private transient static final Set<EntityType> ENTITIES = ImmutableSet.of(EntityType.ARMOR_STAND, EntityType.SLIME, EntityType.HORSE);

    // Game state variables
    private transient final Set<PlayerSlime> players = Sets.newHashSet();

    // For ItemSlime
    public transient final List<ItemSlime> items = Lists.newCopyOnWriteArrayList();
    public transient final Map<Vector2D, ItemSlime> vectors = Maps.newConcurrentMap();


    @Override
    public void validate() throws InvalidJsonException {
        // validated
    }

    @EventHandler
    public void join(GamePlayerStartEvent event) {
        // todo join the player to the specific game
        new PlayerSlime(players, event.getPlayer());
    }

    @EventHandler
    public void quit(GamePlayerJoinSpectatorEvent event) {
        // todo if the player was playing make them leave the game
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void load(GameLoadEvent event) {
        BukkitTask rotate = SchedulerUtil.runSync(this::rotateItems, ItemSlime.STEP);
        event.getGame().addTask(rotate);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void mob(EntitySpawnEvent event) {

        if (ENTITIES.contains(event.getEntityType())) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        GamePlayer gamePlayer = MapNodes.getCurrentGame().getPlayer(event.getPlayer());

        if (event.getFrom().toVector().toBlockVector().equals(event.getTo().toVector().toBlockVector())) return;
        if (!gamePlayer.isPlaying()) return;

        Optional<PlayerSlime> playerSlime = Optional.ofNullable(gamePlayer.getPlayerData(PlayerSlime.class));
        playerSlime.ifPresent(PlayerSlime::giveVelocity);
    }

    /** Rotate the armor stands heads */
    public void rotateItems() {
        items.forEach(ItemSlime::rotate);
    }
}
