/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.game.kits;

import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.utils.Validator;
import org.bukkit.Material;

public class Armor implements Validator {
    /** The slot number to appear in the inventory. */
    private Item helmet = air();
    private Item chestplate = air();
    private Item leggings = air();
    private Item boots = air();

    public Armor() {
    }

    /** Create a blank air item */
    private Item air() {
        Item air = new Item();

        air.setItem(Material.AIR);
        air.setAmount(1);

        return air;
    }

    @Override
    public void validate() throws InvalidJsonException {
        helmet.validate();
        chestplate.validate();
        leggings.validate();
        boots.validate();
    }

    public Item getHelmet() {
        return this.helmet;
    }

    public Item getChestplate() {
        return this.chestplate;
    }

    public Item getLeggings() {
        return this.leggings;
    }

    public Item getBoots() {
        return this.boots;
    }

    public void setHelmet(Item helmet) {
        this.helmet = helmet;
    }

    public void setChestplate(Item chestplate) {
        this.chestplate = chestplate;
    }

    public void setLeggings(Item leggings) {
        this.leggings = leggings;
    }

    public void setBoots(Item boots) {
        this.boots = boots;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Armor)) return false;
        final Armor other = (Armor) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$helmet = this.getHelmet();
        final Object other$helmet = other.getHelmet();
        if (this$helmet == null ? other$helmet != null : !this$helmet.equals(other$helmet)) return false;
        final Object this$chestplate = this.getChestplate();
        final Object other$chestplate = other.getChestplate();
        if (this$chestplate == null ? other$chestplate != null : !this$chestplate.equals(other$chestplate))
            return false;
        final Object this$leggings = this.getLeggings();
        final Object other$leggings = other.getLeggings();
        if (this$leggings == null ? other$leggings != null : !this$leggings.equals(other$leggings)) return false;
        final Object this$boots = this.getBoots();
        final Object other$boots = other.getBoots();
        if (this$boots == null ? other$boots != null : !this$boots.equals(other$boots)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $helmet = this.getHelmet();
        result = result * PRIME + ($helmet == null ? 43 : $helmet.hashCode());
        final Object $chestplate = this.getChestplate();
        result = result * PRIME + ($chestplate == null ? 43 : $chestplate.hashCode());
        final Object $leggings = this.getLeggings();
        result = result * PRIME + ($leggings == null ? 43 : $leggings.hashCode());
        final Object $boots = this.getBoots();
        result = result * PRIME + ($boots == null ? 43 : $boots.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Armor;
    }

    public String toString() {
        return "net.year4000.mapnodes.game.kits.Armor(helmet=" + this.getHelmet() + ", chestplate=" + this.getChestplate() + ", leggings=" + this.getLeggings() + ", boots=" + this.getBoots() + ")";
    }
}
