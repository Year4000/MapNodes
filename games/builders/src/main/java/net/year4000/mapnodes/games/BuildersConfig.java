package net.year4000.mapnodes.games;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import org.bukkit.util.BlockVector;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

@ToString
@Getter
@GameModeConfigName("builders")
public class BuildersConfig implements GameModeConfig {
    private int height = Byte.MAX_VALUE;
    private List<Plot> plots = Lists.newArrayList();

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(height > 0, "height is not > 0");
    }

    /**
     * The object that makes up a plot point with min and max
     */
    public class Plot {
        private String min;
        private String max;

        /** Translate a string seperated by commands into int array */
        private int[] transcribe(String input) {
            checkNotNull(input, "input");
            String[] parts = input.split(",");
            checkArgument(parts.length == 2);

            int x = Integer.parseInt(parts[0].replaceAll(" ", ""));
            int z = Integer.parseInt(parts[1].replaceAll(" ", ""));

            return new int[] {x, z};
        }

        /** Grab the BlockVector of min plot */
        public BlockVector getMin() {
            int[] xz = transcribe(min);

            return new BlockVector(xz[0], 0, xz[1]);
        }

        /** Grab the BlockVector of max plot */
        public BlockVector getMax() {
            int[] xz = transcribe(min);

            return new BlockVector(xz[0], 256, xz[1]);
        }
    }
}
