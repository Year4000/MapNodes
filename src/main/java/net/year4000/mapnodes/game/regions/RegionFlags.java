package net.year4000.mapnodes.game.regions;

import com.google.common.annotations.Beta;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.NodeRegion;
import net.year4000.mapnodes.game.regions.flags.Bow;
import net.year4000.mapnodes.game.regions.flags.Chest;
import net.year4000.mapnodes.game.regions.flags.TNT;
import net.year4000.mapnodes.api.util.Validator;
import net.year4000.mapnodes.utils.typewrappers.DamageCauseList;
import net.year4000.mapnodes.utils.typewrappers.EntityTypeList;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class RegionFlags implements Validator {
    // todo make this work for the next version to allow for advance building
    /** A list of allow to place blocks */
    @Since(1.1)
    @SerializedName("block_place")
    @Beta
    private MaterialList<Material> blockPlace = new MaterialList<>();

    // todo make this work for the next version to allow for advance destroying
    /** A list of allow blocks can break */
    @Since(1.1)
    @SerializedName("block_break")
    @Beta
    private MaterialList<Material> blockBreak = new MaterialList<>();

    @Since(1.0)
    private Boolean build = null;

    @Since(1.0)
    private Boolean destroy = null;

    @Since(1.0)
    private TNT tnt = null;

    @Since(1.0)
    private Chest chests = null;

    @Since(1.0)
    private Bow bows = null;

    /** Can players enter this region */
    @Since(1.0)
    private Boolean enter = null;

    /** Can players exit this region */
    @Since(1.0)
    private Boolean exit = null;

    /** Can players pvp in this zone */
    @Since(1.0)
    private Boolean pvp = null;

    /** Can ice melt in this region */
    @Since(1.0)
    @SerializedName("block_fade")
    private Boolean blockFade = null;

    /** Can liquid flow in this region */
    @Since(1.0)
    @SerializedName("liquid_flow")
    private Boolean liquidFlow = null;

    /** What damage should be ignore from the player. */
    @Since(1.0)
    @SerializedName("no_damage")
    private DamageCauseList<EntityDamageEvent.DamageCause> noDamage = null;

    /** What mobs should be allowed in the map. */
    @Since(1.0)
    @SerializedName("enabled_mobs")
    private EntityTypeList<EntityType> enabledMobs = null;

    /** What items should be dropped from the player. */
    @Since(1.0)
    @SerializedName("player_drops")
    private MaterialList<Material> playerDrops = null;

    /** What items should be dropped from blocks. */
    @Since(1.1)
    @SerializedName("block_drops")
    // todo make this work for the next version to allow for advance block drops
    private MaterialList<Material> blockDrops = new MaterialList<>(Arrays.asList(Material.values()));

    /** The message to show if the action is deny in the instance of a player */
    @Since(1.0)
    private String message = null;

    @Override
    public void validate() throws InvalidJsonException {
        //boolean hasApply = region.getApply() != null;

        // todo validate must have apply from node region if exit, enter, blockBreak, or blockPlace exists
        //checkArgument(hasApply && blockBreak.size() > 0);
        //checkArgument(hasApply && blockPlace.size() > 0);
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    private transient NodeRegion region;

    public void validate(NodeRegion region) throws InvalidJsonException {
        this.region = region;
        validate();
    }

    // todo create a set of materials that is allowed to be broken from other regions

    // todo create a set of materials that is allowed to be placed from other regions
}
