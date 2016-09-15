/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.kits;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.utils.Validator;
import net.year4000.mapnodes.messages.Msg;

import static com.google.common.base.Preconditions.checkArgument;

public class SlotItem extends Item implements Validator {
    /** The slot number to appear in the inventory. */
    private int slot = -1;

    public SlotItem() {
    }

    @Override
    public void validate() throws InvalidJsonException {
        super.validate();

        checkArgument(slot >= -1 && slot <= 35, Msg.util("settings.kit.item.slot"));
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.kits.SlotItem(slot=" + this.getSlot() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SlotItem)) return false;
        final SlotItem other = (SlotItem) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getSlot() != other.getSlot()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSlot();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof SlotItem;
    }
}
