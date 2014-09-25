package net.year4000.mapnodes.game.regions;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.kits.Item;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.List;

@Data
public abstract class RegionEvent {
    /** The region the event is assigned to */
    protected NodeRegion region;

    private Integer weight = null;

    private boolean allow = false;

    @SerializedName("drop_items")
    private List<Item> dropItem;

    @SerializedName("play_sound")
    private Sound playSound;

    private String message;

    private LocationList<Location> teleport;

    @SerializedName("give_kit")
    private String giveKit;

    @SerializedName("add_kit")
    private String addKit;
}
