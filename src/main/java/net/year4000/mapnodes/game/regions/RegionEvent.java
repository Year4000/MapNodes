package net.year4000.mapnodes.game.regions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.game.regions.types.Point;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.typewrappers.ItemStackList;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class RegionEvent {
    /** The region the event is assigned to */
    @Getter
    @Setter
    protected transient NodeRegion region;

    @Since(1.0)
    private Boolean allow;

    @Since(1.0)
    @SerializedName("drop_items")
    private ItemStackList<ItemStack> dropItems = new ItemStackList<>();

    @Since(1.0)
    @SerializedName("play_sound")
    private Sound playSound;

    @Since(1.0)
    private Point velocity;

    @Since(1.0)
    private String message;

    @Since(1.0)
    private LocationList<Location> teleport = new LocationList<>();

    @Since(1.0)
    @SerializedName("give_kit")
    private String giveKit;

    @Since(1.0)
    @SerializedName("add_kit")
    private String addKit;

    /** The teams this region apply to option if just used for zones */
    @Since(1.0)
    private List<String> apply = new ArrayList<>();

    /** Should this event run based on the internal weight system */
    public boolean shouldRunEvent(Point point) {
        List<GameRegion> regions = region.getGame().getRegions().values().stream()
            .filter(r -> r.inZone(point))
            .sorted((r, l) -> r.getWeight() < l.getWeight() ? 1 : -1)
            .collect(Collectors.toList());

        return regions.size() != 0 && regions.get(0).equals(region);
    }

    public boolean isAllowSet() {
        return allow != null;
    }

    public boolean isAllow() {
        return allow;
    }

    /** Does the region apply to the current player */
    public boolean applyToPlayer(GamePlayer player) {
        return !(player.getTeam() instanceof Spectator) && (apply.size() == 0 || apply.contains(((NodeTeam) player.getTeam()).getId().toLowerCase()));
    }

    /** Run global events tasks */
    public void runGlobalEventTasks(GamePlayer player) {
        teleportPlayer(player);
        sendMessage(player);

        if (velocity != null) {
            player.getPlayer().setVelocity(player.getPlayer().getVelocity().add(Common.pointToVector(velocity)));
        }
    }

    /** Run global events tasks */
    public void runGlobalEventTasks(Location location) {
        dropItems(location);
    }

    public void dropItems(Location location) {
        if (dropItems.size() > 0) {
            dropItems.forEach(item -> location.getWorld().dropItemNaturally(location, item));
        }
    }

    /** Teleport the player to a random safe location */
    public void teleportPlayer(GamePlayer player) {
        if (teleport.size() == 0) return;

        player.getPlayer().teleport(teleport.getSafeRandomSpawn());
    }

    /** Send the message to the player */
    public void sendMessage(GamePlayer player) {
        if (message == null || message.equals("")) return;

        // Translate by map first
        String translatedMessage = ((NodePlayer) player).getGame().locale(player.getRawLocale(), message);
        // Translate by MapNodes
        translatedMessage = Msg.locale(player, translatedMessage);
        player.sendMessage(Msg.NOTICE + translatedMessage);
    }
}
