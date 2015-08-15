package net.year4000.mapnodes.games;

import com.comphenix.packetwrapper.WrapperPlayServerWorldBorder;
import com.comphenix.protocol.wrappers.EnumWrappers;
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
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

@ToString
public class PlayerPlot {
    private final GamePlayer player;
    @Getter
    private final BuildersConfig.Plot plot;
    @Getter
    private final int y;
    @Setter
    @Getter
    private boolean forfeited = false;

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

        int x = midpoint.getBlockX();
        int z = midpoint.getBlockZ();
        int y = world.getHighestBlockYAt(x, z) + 10;

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
        for (int x = plot.getInnerMin().getBlockX(); x < plot.getInnerMax().getBlockX(); x++) {
            for (int z = plot.getInnerMin().getBlockZ(); z < plot.getInnerMax().getBlockZ(); z++) {
                Block pos = MapNodes.getCurrentWorld().getBlockAt(x, y, z);
                block.accept(pos);
            }
        }
    }

    public void setBorder() {
        try {
            Vector center = plot.getMax().midpoint(plot.getMin());
            WrapperPlayServerWorldBorder border = new WrapperPlayServerWorldBorder();
            border.setCenterX(center.getBlockX());
            border.setCenterZ(center.getBlockZ());
            border.setWarningDistance(0);
            border.setRadius(center.distance(plot.getMax()));
            border.setAction(EnumWrappers.WorldBorderAction.INITIALIZE);

            MapNodes.getProtocolManager().sendServerPacket(player.getPlayer(), border.getHandle());
        }
        catch (InvocationTargetException error) {}
    }
}
