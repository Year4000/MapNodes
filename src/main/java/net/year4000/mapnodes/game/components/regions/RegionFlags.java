package net.year4000.mapnodes.game.components.regions;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.game.components.NodeRegion;
import net.year4000.mapnodes.utils.Validator;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import org.bukkit.Material;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class RegionFlags implements Validator {
    /** A list of allow to place blocks */
    @Since(1.0)
    @SerializedName("block_place")
    private MaterialList<Material> blockPlace = new MaterialList<>();

    /** A list of allow blocks can break */
    @Since(1.0)
    @SerializedName("block_break")
    private MaterialList<Material> blockBreak = new MaterialList<>();

    /** Can players enter this region */
    @Since(1.0)
    private boolean enter = true;

    /** Can players exit this region */
    @Since(1.0)
    private boolean exit = true;

    /** Can ice melt in this region */
    @Since(1.0)
    @SerializedName("ice_melt")
    private boolean iceMelt = true;

    /** Can liquid flow in this region */
    @Since(1.0)
    @SerializedName("liquid_flow")
    private boolean liquidFlow = true;

    /** Can explosion happen in this region */
    @Since(1.0)
    private boolean explosion = true;

    /** The message to show if the action is deny in the instance of a player */
    @Since(1.0)
    private String message = null;

    private transient NodeRegion region;

    public void validate(NodeRegion region) throws InvalidJsonException {
        this.region = region;
        validate();
    }

    @Override
    public void validate() throws InvalidJsonException {
        boolean hasApply = region.getApply() != null;

        // todo validate must have apply from node region if exit, enter, blockBreak, or blockPlace exists
        //checkArgument(hasApply && blockBreak.size() > 0);
        //checkArgument(hasApply && blockPlace.size() > 0);
    }

    /*//--------------------------------------------//
         Upper Json Settings / Bellow Instance Code
    *///--------------------------------------------//

    // todo create a set of materials that is allowed to be broken from other regions

    // todo create a set of materials that is allowed to be placed from other regions
}
