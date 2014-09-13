package net.year4000.mapnodes.game.components.regions.flags;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.api.game.configs.GameTNT;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Validator;

@Data
@NoArgsConstructor
public class TNT implements GameTNT, Validator {
    /** Should tnt be activated when its placed. */
    @Since(1.0)
    private boolean instant = false;

    /** Should the tnt destroy blocks. */

    @Since(1.0)
    @SerializedName("block_damage")
    private boolean blockDamage = true;

    /** The percent of blocks should appear. */
    @Since(1.0)
    private int drops = 100;

    @Override
    public void validate() throws InvalidJsonException {
        if (drops < 0 || drops > 100) {
            throw new InvalidJsonException(Msg.util("settings.game.tnt.drops"));
        }
    }

}
