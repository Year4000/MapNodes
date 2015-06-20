/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.gamemodes.capture;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("capture")
public class CaptureConfig implements GameModeConfig {
    @SerializedName("block_captures")
    private List<BlockCapture> blockCaptures = null;

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(blockCaptures != null, Msg.util("capture.captures"));

        blockCaptures.forEach(BlockCapture::validate);
    }

    @Data
    public class BlockCapture implements Validator {
        private transient ChatColor prefix;
        private transient boolean customColor = false;

        // Normal blocks
        private String owner;
        private String challenger;
        private String region;
        private String name;
        private Material block;
        private Integer data;

        // Auto Fields when set it will populate some of the above vars
        private ChatColor wool = null;
        private ChatColor clay = null;
        private transient boolean done = false;
        private transient List<GamePlayer> grabbed = new ArrayList<>();

        @Override
        public void validate() throws InvalidJsonException {
            checkArgument(owner != null, Msg.util("capture.block.owner"));
            checkArgument(challenger != null, Msg.util("capture.block.challenger"));
            checkArgument(region != null, Msg.util("capture.block.region"));

            // Only change if wool is set and not clay
            if (wool != null && clay == null) {
                block = Material.WOOL;
                prefix = wool;
                name = Common.toUpperSpacedCamel(wool.name());
                data = Capture.CHAT_COLOR_DATA_MAP.get(wool);
            }

            // Only change if clay is set and not wool
            if (wool == null && clay != null) {
                block = Material.STAINED_CLAY;
                prefix = clay;
                name = Common.toUpperSpacedCamel(clay.name());
                data = Capture.CHAT_COLOR_DATA_MAP.get(clay);
            }

            if (prefix == null) {
                prefix = ChatColor.GREEN;
                customColor = true;
            }

            checkArgument(name != null, Msg.util("capture.block.name"));
            checkArgument(block != null, Msg.util("capture.block.block"));
            checkArgument(data != null, Msg.util("capture.block.block"));
        }

        public void setDone(boolean done) {
            this.done = done;

            if (customColor) {
                prefix = done ? ChatColor.GREEN : ChatColor.RED;
            }
        }
    }
}
