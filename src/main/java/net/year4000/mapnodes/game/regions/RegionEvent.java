package net.year4000.mapnodes.game.regions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.typewrappers.ItemStackList;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class RegionEvent {
    /** The region the event is assigned to */
    @Getter
    @Setter
    protected transient NodeRegion region;

    @Since(1.0)
    private boolean allow = false;

    @Since(1.0)
    @SerializedName("drop_items")
    private ItemStackList<ItemStack> dropItem = new ItemStackList<>();

    @Since(1.0)
    @SerializedName("play_sound")
    private Sound playSound;

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

    /** Does the region apply to the current player */
    public boolean applyToPlayer(GamePlayer player) {
        return apply.size() == 0 || apply.contains(player.getTeam().getName().toLowerCase());
    }

    /** Run global events tasks */
    public void runGlobalEventTasks(GamePlayer player) {
        teleportPlayer(player);
        sendMessage(player);
    }

    /** Teleport the player to a random safe location */
    public void teleportPlayer(GamePlayer player) {
        if (teleport.size() > 0) {
            player.getPlayer().teleport(teleport.getSafeRandomSpawn());
        }
    }

    /** Send the message to the player */
    public void sendMessage(GamePlayer player) {
        // Translate by map first
        String translatedMessage = ((NodePlayer) player).getGame().locale(player.getPlayer().getLocale(), message);
        // Translate by MapNodes
        translatedMessage = Msg.locale(player, translatedMessage);
        player.sendMessage(translatedMessage);
    }
}
