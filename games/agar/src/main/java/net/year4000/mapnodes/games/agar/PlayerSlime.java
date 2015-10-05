/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.agar;

import com.google.common.base.Objects;
import lombok.Getter;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class is a GamePlayer data that is the instance of the agar slime.
 *
 */
public class PlayerSlime implements Editable {
    private static final PotionEffect GONE = new PotionEffect(PotionEffectType.INVISIBILITY, 0, Integer.MAX_VALUE, true);
    private static int MAX_SIZE = 10;
    private static int EAT_SIZE_MIN = 5;
    private static double EAT_DISTANCE_MODIFIER = 0.75;
    private GamePlayer gamePlayer;
    private Slime slime;
    private Horse horse;
    private int size = 2;
    @Getter
    private int score = 10;

    public PlayerSlime(Collection<PlayerSlime> collection, GamePlayer gamePlayer) {
        checkNotNull(collection, "PlayerSlime collection must exists");
        checkNotNull(gamePlayer, "Must have a player to create PlayerSlime with");

        this.gamePlayer = gamePlayer;

        checkArgument(!collection.contains(this), "The collection must not have this instance");

        this.gamePlayer.addPlayerData(PlayerSlime.class, this);
        collection.add(this);

        Location location = gamePlayer.getPlayer().getLocation();
        // Set up the horse the player needs to ride
        horse = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.setAgeLock(true);
        //horse.setAge(distanceOfHorseAge());
        horse.setAge(50);
        horse.getInventory().setSaddle(ItemUtil.makeItem(Material.SADDLE));
        horse.setTamed(true);
        horse.setOwner(gamePlayer.getPlayer());

        // Set up the slime
        slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
        slime.setSize(size);

        slime.setPassenger(horse);
        horse.setPassenger(gamePlayer.getPlayer());
    }

    @Override
    public Vector getLocation() {
        return slime == null ? Vector.getRandom().zero() : slime.getLocation().toVector();
    }


    @Override
    public void eat(Editable editable) {
        if (editable instanceof PlayerSlime) {
            PlayerSlime playerSlime = (PlayerSlime) editable;
            boolean distance = playerSlime.getLocation().distanceSquared(getLocation()) < distanceToEat();

            // The player is within score size and distance
            if (playerSlime.score - score < EAT_SIZE_MIN && distance) {
                // todo kill slime and add their score
            }
        }
        else {
            // todo eat the scorer
        }
    }

    /** Calculate and give the velocity to the payer */
    public void giveVelocity() {
        double theta = (gamePlayer.getPlayer().getEyeLocation().getYaw() + 90) * Math.PI / 180;
        double speed = 0.05 * (32 - size);
        double x = speed * Math.cos(theta);
        double z = speed * Math.sin(theta);
        Vector vector = new Vector(x, 0, z);

        slime.setVelocity(vector);
        slime.setSize(size);
    }

    /** Calculate the distance needed to eat */
    public double distanceToEat() {
        // todo real math
        return size * EAT_DISTANCE_MODIFIER;
    }

    public int distanceOfHorseAge() {
        // todo real math
        return (int) -(size * EAT_DISTANCE_MODIFIER);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        PlayerSlime that = (PlayerSlime) other;
        return Objects.equal(that.getLocation(), getLocation());
    }

    @Override
    public int hashCode() {
        return getLocation().hashCode();
    }
}
