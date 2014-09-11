package net.year4000.mapnodes.game.components;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.GameConfig;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.configs.Bow;
import net.year4000.mapnodes.game.components.configs.Chest;
import net.year4000.mapnodes.game.components.configs.TNT;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.*;
import net.year4000.mapnodes.utils.typewrappers.DamageCauseList;
import net.year4000.mapnodes.utils.typewrappers.EntityTypeList;
import net.year4000.mapnodes.utils.typewrappers.LocationList;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.net.URL;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@NoArgsConstructor
/** General game settings. */
public final class NodeConfig implements GameConfig, Validator {
    /** The map's difficulty level. */
    @Since(1.0)
    private int difficulty = 3; // todo allow support for names easy, normal, hard

    /** The time that the map should be locked to. */
    @Since(1.0)
    @SerializedName("time_lock")
    private long timeLock = -1; // todo allow for name's ex dawn, dusk, day, night, midnight, midday

    /** Should the weather be forced on. */
    @Since(1.0)
    private boolean weather = false;

    /** Can the map be destroyed. */
    @Since(1.0)
    private boolean destructible = true; // todo make an enum for fast settings of all, foliage, natural...

    /** The Environment of the world. */
    @Since(1.0)
    private World.Environment environment = World.Environment.NORMAL;

    /** The height of the world. */
    @Since(1.0)
    @SerializedName("world_height")
    private int worldHeight = 256;

    /** The resource pack url for this map. */
    @Since(1.0)
    @SerializedName("resource_pack")
    private URL resourcePack = null;

    /** What damage should be ignore from the player. */
    @Since(1.0)
    @SerializedName("no_damage")
    private DamageCauseList<EntityDamageEvent.DamageCause> noDamage = new DamageCauseList<>();

    /** What mobs should be allowed in the map. */
    @Since(1.0)
    @SerializedName("enabled_mobs")
    private EntityTypeList<EntityType> enabledMobs = new EntityTypeList<>();

    /** What items should be dropped from the player. */
    @Since(1.0)
    @SerializedName("player_drops")
    private MaterialList<Material> playerDrops = new MaterialList<>();

    /** What items should be dropped from blocks. */
    @Since(1.0)
    @SerializedName("block_drops")
    private MaterialList<Material> blockDrops = new MaterialList<>(Arrays.asList(Material.values()));

    /** All settings for tnt. */
    @Since(1.0)
    private TNT tnt = new TNT();

    /** All settings for bows. */
    @Since(1.0)
    private Bow bow = new Bow();

    /** The area for the spawn. */
    @Since(1.0)
    private LocationList<Location> spawn = new LocationList<>();

    /** The settings for chest items. */
    @Since(1.0)
    private Chest chests = new Chest();

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(0 <= difficulty && difficulty <= 3, Msg.util("settings.game.difficulty"));

        checkArgument(-1 <= timeLock && timeLock <= 23000, Msg.util("settings.game.timeLock"));

        checkArgument(0 <= worldHeight && worldHeight <= 256, Msg.util("settings.game.worldHeight"));

        checkArgument(spawn.size() > 0, Msg.util("settings.game.spawn"));
    }
}