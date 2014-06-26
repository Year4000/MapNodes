package net.year4000.mapnodes.configs.map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("all")
public class Zones {
    /** The teams that is zone should apply to. */
    private String[] apply;
    private String[] deny;
    private Points[] areas;
}
