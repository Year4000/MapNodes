/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.builders;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

@ToString
@Getter
@GameModeConfigName("builders")
public class BuildersConfig implements GameModeConfig {
    private static final int HEIGHT = 256;
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
            int[] xzMin = transcribe(min);
            int[] xzMax = transcribe(max);

            return new BlockVector(Math.min(xzMin[0], xzMax[0]), 0, Math.min(xzMin[1], xzMax[1]));
        }

        /** Grab the BlockVector of max plot */
        public BlockVector getMax() {
            int[] xzMin = transcribe(min);
            int[] xzMax = transcribe(max);

            return new BlockVector(Math.max(xzMin[0], xzMax[0]), HEIGHT, Math.max(xzMin[1], xzMax[1]));
        }

        /** Grab the BlockVector of min plot */
        public BlockVector getInnerMin() {
            BlockVector xz = getMin();

            return new BlockVector(xz.getX() + 1, 0, xz.getZ() + 1);
        }

        /** Grab the BlockVector of max plot */
        public BlockVector getInnerMax() {
            BlockVector xz = getMax();

            return new BlockVector(xz.getX() - 1, HEIGHT, xz.getZ() - 1);
        }

        /** Grab the BlockVector of min plot */
        public BlockVector getOuterMin() {
            BlockVector xz = getMin();

            return new BlockVector(xz.getX() - 1, 0, xz.getZ() - 1);
        }

        /** Grab the BlockVector of max plot */
        public BlockVector getOuterMax() {
            BlockVector xz = getMax();

            return new BlockVector(xz.getX() + 1, HEIGHT, xz.getZ() + 1);
        }

        /** Is the specific vector inside region */
        public boolean isInPlot(Vector vector, int minY, int maxY) {
            Vector min = getOuterMin().setY(minY);
            Vector max = getOuterMax().setY(maxY);

            return isInPlot(min, max, vector);
        }

        /** Is the specific vector inside region */
        public boolean isInInnerPlot(Vector vector, int minY, int maxY) {
            Vector min = getInnerMin().setY(minY);
            Vector max = getInnerMax().setY(maxY);

            return isInPlot(min, max, vector);
        }

        /** Is the specific vector inside region */
        private boolean isInPlot(Vector min, Vector max, Vector vector) {
            return vector.isInAABB(min, max);
        }
    }
}
