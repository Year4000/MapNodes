package net.year4000.mapnodes.map.components;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED)
@SuppressWarnings("unused")
/** Classes to pick from on top of your team. */
public class Classes {
    /** The name of the class. */
    private String name = null;
    /** The icon item for the class. */
    private String icon = null;
    /** The description of the class. */
    private String description = null;
    /** The kit name to use with this class. */
    private String kit = null;
    /** The permission to use this class. */
    private String permission = "";
    /** The permission message to display. */
    private String permMessage = "This kit requires a vip account.";
}
