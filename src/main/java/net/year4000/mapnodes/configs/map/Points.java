package net.year4000.mapnodes.configs.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("all")
/** The places where the player can spawn. */
public class Points {
    /** The min cordent of the spawn area. */
    private Cordient min = new Cordient();
    /** The max cordent of the spawn area. */
    private Cordient max = new Cordient();
    /** The spawn point. */
    private Cordient point = null;

    @Data
    @NoArgsConstructor
    /** The cordients controler. */
    public class Cordient {
        /** The x cordent of the spawn area. */
        private double x = Double.MIN_VALUE;
        /** The y cordent of the spawn area. */
        private double y = Double.MIN_VALUE;
        /** The z cordent of the spawn area. */
        private double z = Double.MIN_VALUE;

        @Override
        public String toString() {
            return String.format("%s %s %s", x, y, z);
        }
    }
}