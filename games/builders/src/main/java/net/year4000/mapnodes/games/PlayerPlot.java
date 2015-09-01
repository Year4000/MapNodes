/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games;

import com.comphenix.packetwrapper.WrapperPlayServerWorldBorder;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.utilities.bukkit.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
public class PlayerPlot implements Comparable<PlayerPlot> {
    @Getter
    private final GamePlayer player;
    @Getter
    private final BuildersConfig.Plot plot;
    @Getter
    private final int y;
    @Setter
    @Getter
    private boolean forfeited = false;
    @Getter
    private Map<GamePlayer, VoteType> votes = Maps.newHashMap();

    // Plot effects
    private Material floor = Material.GRASS;
    private Biome biome = Biome.PLAINS;
    // Player effects
    private WeatherType weather = WeatherType.CLEAR;
    private TimeState time = TimeState.NOON;

    @AllArgsConstructor
    public enum TimeState {
        DAWN(0),
        DAY(1000),
        NOON(6000),
        NIGHT(14000),
        ;

        public int time;
    }

    public PlayerPlot(GamePlayer player, BuildersConfig.Plot plot) {
        this.player = checkNotNull(player, "player");
        this.plot = checkNotNull(plot, "plot");

        // Get the floor's base level
        int x = plot.getMin().getBlockX();
        int z = plot.getMin().getBlockZ();
        y = MapNodes.getCurrentWorld().getHighestBlockYAt(x, z) - 1;

        player.addPlayerData(PlayerPlot.class, this);
    }

    /** Teleport the owner to this plot */
    public Location teleportToPlot() {
        return teleportToPlot(player);
    }

    /** Teleport the player to this plot */
    public Location teleportToPlot(GamePlayer player) {
        Location location = teleportPlotLocation();
        addPlotEffects(player);
        player.getPlayer().setAllowFlight(true);
        player.getPlayer().setFlying(true);
        player.getPlayer().teleport(location);

        return location;
    }

    /** Teleport the player to this plot */
    public Location teleportPlotLocation() {
        World world = MapNodes.getCurrentWorld();
        Vector midpoint = plot.getInnerMin().midpoint(plot.getInnerMax());
        double distance = Math.sqrt(midpoint.clone().setY(0).distance(plot.getMax().clone().setY(0)));
        double delta = new Random().nextInt(360) * Math.PI / 180;

        double x = midpoint.getX() + distance * Math.cos(delta);
        double z = midpoint.getZ() + distance * Math.sin(delta);
        int y = world.getHighestBlockYAt((int) x, (int) z) + 10;

        return LocationUtil.center(new Location(world, x, y, z));
    }

    /** Set the plot effects for the given player */
    public void addPlotEffects(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

        player.setPlayerWeather(weather);
        player.setPlayerTime(time.time, false);
    }

    /** Set the floor of the plot to the specific block type */
    public void setFloor(Material floor) {
        if (this.floor == floor) return;

        this.floor = checkNotNull(floor);
        processPlot(block -> block.setType(floor));
    }

    /** Set the biome of the plot to the specific biome */
    public void setBiome(Biome biome) {
        if (this.biome == biome) return;

        this.biome = checkNotNull(biome);
        processPlot(block -> block.setBiome(biome));
    }

    /** Set the weather for the plot owner */
    public void setWeather(WeatherType weather) {
        if (this.weather == weather) return;

        this.weather = checkNotNull(weather);
        player.getPlayer().setPlayerWeather(weather);
    }

    /** Set the time for the plot owner */
    public void setTime(TimeState time) {
        if (this.time == time) return;

        this.time = checkNotNull(time);
        player.getPlayer().setPlayerTime(time.time, false);
    }

    /** Process data for each block in the plot */
    public void processPlot(Consumer<Block> block) {
        for (int x = plot.getInnerMin().getBlockX(); x <= plot.getInnerMax().getBlockX(); x++) {
            for (int z = plot.getInnerMin().getBlockZ(); z <= plot.getInnerMax().getBlockZ(); z++) {
                Block pos = MapNodes.getCurrentWorld().getBlockAt(x, y, z);
                block.accept(pos);
            }
        }
    }

    public void fireworks() {

    }

    /** Get the owner of the plot */
    public String getOwner() {
        return player.getPlayerColor();
    }

    /** Calculate the score of this plot */
    public int calculateScore() {
        return forfeited ? -1 : votes.values().stream().mapToInt(VoteType::getScore).sum();
    }

    @Override
    public int compareTo(PlayerPlot playerPlot) {
        checkNotNull(playerPlot, "playerPlot");
        return calculateScore() < playerPlot.calculateScore() ? 1 : -1;
    }
}
