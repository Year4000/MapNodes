/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.destory;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameRegion;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.typewrappers.MaterialList;
import net.year4000.utilities.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class DestroyTarget implements Validator {
    /** The team id this object belongs to */
    private String owner;

    /** The team id that needs to break this to win */
    private String challenger;

    /** The name of the goal */
    private String name;

    /** The region id */
    private String region;

    /** The blocks that are the objective */
    private MaterialList<Material> blocks = new MaterialList<>(Arrays.asList(Material.values()));

    /** Show the destroys as percents not as active non active */
    @SerializedName("show_percent")
    private boolean showPercent = false;

    /** The percent to destroy to win */
    private int percent = 100;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(name != null, Msg.util("destroy.name"));
        checkArgument(region != null, Msg.util("destroy.region"));
        checkArgument(percent >= 0 && percent <= 100, Msg.util("destroy.percent"));
    }

    private transient Stage stage = Stage.START;
    private transient int maxSize;
    private transient int currentSize;
    private transient List<Block> blockList;
    private transient GameRegion nodeRegion;
    private transient String ownerName;

    public void init(GameManager game) {
        World world = MapNodes.getCurrentWorld();
        ownerName = game.getTeams().get(owner).getDisplayName();
        nodeRegion = game.getRegions().get(region);
        Set<Region> regionObject = nodeRegion.getZones();
        ImmutableList.Builder<Block> builder = ImmutableList.<Block>builder();

        // Get a list of all blocks
        for (Region region : regionObject) {
            for (Location loc : region.getLocations(world)) {
                Block block = world.getBlockAt(loc);

                if (blocks.contains(block.getType())) {
                    builder.add(block);
                }
            }
        }

        blockList = builder.build();
        maxSize = blockList.size();
        currentSize = maxSize;
        updateProgress();
    }

    public void updateProgress(Block... blocks) {
        // Filter the list of blocks
        for (Block block : blocks) {
            if (this.blocks.contains(block.getType())) {
                --currentSize;
            }
        }

        // Update the stage
        if (currentSize == maxSize) {
            stage = Stage.START;
        }
        else if (currentSize <= 0 || getRawPercent() >= percent) {
            stage = Stage.END;
        }
        else {
            stage = Stage.PROGRESS;
        }
    }

    public String getDisplayStage() {
        if (stage == Stage.START) {
            return ChatColor.RED + "\u2715"; // ✕
        }
        else if (stage == Stage.END) {
            return ChatColor.GREEN + "\u2714"; // ✔
        }
        else {
            return ChatColor.YELLOW + "\u2715"; // ✕
        }
    }

    public ChatColor getFStage() {
        if (stage == Stage.END) {
            return ChatColor.STRIKETHROUGH;
        }
        else if (stage == Stage.PROGRESS) {
            return ChatColor.ITALIC;
        }

        return ChatColor.RESET;
    }

    public String getPercent() {
        float per = getRawPercent();
        per = per < 0 ? 0 : per;
        return Common.colorNumber((int) per, 100) + "%%";
    }

    public float getRawPercent() {
        return ((currentSize / maxSize) * (float) -0.1);
    }

    public String getDisplay() {
        return " " + (showPercent ? getPercent() : getDisplayStage()) + " &f" + getFStage() + name;
    }

    public String getId() {
        return owner + "-" + region;
    }

    public enum Stage {START, PROGRESS, END}
}
