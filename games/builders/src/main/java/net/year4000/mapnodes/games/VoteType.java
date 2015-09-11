/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games;

import lombok.Getter;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.messages.Msg;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public enum VoteType {
    ONE(Material.STAINED_GLASS, (short) 14, 1, Note.Tone.A),
    TWO(Material.STAINED_GLASS, (short) 1, 2, Note.Tone.B),
    THREE(Material.STAINED_GLASS, (short) 4, 3, Note.Tone.C),
    FOUR(Material.STAINED_GLASS, (short) 5, 4, Note.Tone.D),
    FIVE(Material.STAINED_GLASS, (short) 13, 5, Note.Tone.E),
    INVALID(Material.BEDROCK, (short) 0, 0, Note.Tone.G),
    NO_VOTE(Material.BEDROCK, (short) 0, 0, Note.Tone.G),
    ;

    private Material material;
    private short data;
    @Getter
    private int score;
    private Note.Tone tone;

    VoteType(Material material, short data, int score, Note.Tone tone) {
        this.material = material;
        this.data = data;
        this.score = score;
        this.tone = tone;
    }

    /** Create the items stack */
    public ItemStack makeItem(GamePlayer player) {
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(voteName(player));

        item.setItemMeta(meta);
        return item;
    }

    /** Set the inventory of the vote type */
    public static void setInventory(GamePlayer gamePlayer) {
        PlayerInventory inventory = gamePlayer.getPlayer().getInventory();

        inventory.clear();

        for (int i = 0; i < values().length; i++) {
            VoteType type = values()[i];

            if (type.material != Material.BEDROCK) {
                inventory.setItem(i, type.makeItem(gamePlayer));
            }
        }
    }

    /** Play the selected sound */
    public void playSound(GamePlayer gamePlayer) {
        Location location = gamePlayer.getPlayer().getEyeLocation();
        gamePlayer.getPlayer().playNote(location, Instrument.PIANO, Note.sharp(1, tone));
    }

    /** The locale code for the VoteType */
    private String localeCode() {
        return "builders.vote." + name().toLowerCase();
    }

    /** Grab the translation of the vote type */
    public String voteName(GamePlayer gamePlayer) {
        return Msg.locale(gamePlayer, localeCode());
    }

    /** Grab the VoteType that matches the item stack */
    public static VoteType getVoteType(ItemStack item, GamePlayer gamePlayer) {
        for (VoteType voteType : VoteType.values()) {
            String translatedLocale = Msg.locale(gamePlayer, voteType.localeCode());

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();

                if (translatedLocale.equals(displayName)) {
                    return voteType;
                }
            }
        }

        throw new IllegalArgumentException("No vote for selected item");
    }
}
