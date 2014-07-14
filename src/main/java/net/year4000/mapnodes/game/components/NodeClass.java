package net.year4000.mapnodes.game.components;

import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.GameClass;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;
import org.bukkit.Material;

@Data
@NoArgsConstructor
/** Classes to pick from on top of your team. */
public final class NodeClass implements GameClass, Validator {
    /** The name of the class. */
    @Since(1.0)
    private String name = null;

    /** The icon item for the class. */
    @Since(1.0)
    private Material icon = null;

    /** The description of the class. */
    @Since(1.0)
    private String description = null;

    /** The kit name to use with this class. */
    @Since(1.0)
    private String kit = null;

    @Override
    public void validate() throws InvalidJsonException {
        if (name == null) {
            throw new InvalidJsonException(Msg.util("settings.class.name"));
        }

        if (icon == null) {
            throw new InvalidJsonException(Msg.util("settings.class.icon"));
        }

        if (description == null) {
            throw new InvalidJsonException(Msg.util("settings.class.description"));
        }

        if (kit == null) {
            throw new InvalidJsonException(Msg.util("settings.class.kit"));
        }
    }
}
