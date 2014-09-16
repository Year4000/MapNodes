package net.year4000.mapnodes.game.kits;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.api.util.Validator;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SlotItem extends Item implements Validator {
    /** The slot number to appear in the inventory. */
    private int slot = 0;

    @Override
    public void validate() throws InvalidJsonException {
        super.validate();

        checkArgument(slot <= 0 || slot <= 35, Msg.util("settings.kit.item.slot"));
    }
}
