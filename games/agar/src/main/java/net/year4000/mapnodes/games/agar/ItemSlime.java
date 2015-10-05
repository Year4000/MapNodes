/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.games.agar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class represents an items that PlayerSlime wil eat to grow bigger.
 * It is an instance of Editable so it can eat other editable, but it will
 * only handle PlayerSlimes at the moment.
 */
public class ItemSlime implements Editable {
    public static final int STEP = 256;
    private static final Material HEAD = Material.SLIME_BLOCK;
    public Collection<ItemSlime> items;
    public Map<Vector2D, ItemSlime> vectors;
    private ArmorStand armorStand;
    private double step = 0;
    private BlockVector vector;
    private Vector2D vector2D;

    /** Create a new slime item at the given position */
    public ItemSlime(Collection<ItemSlime> items, Map<Vector2D, ItemSlime> vectors, World world, int x, int y, int z) {
        this.items = items;
        this.vectors = vectors;
        Location location = new Location(world, x + 0.5, y, z + 0.5);
        vector = location.toVector().toBlockVector();
        vector2D = new Vector2D(vector.getBlockX(), vector.getBlockZ());

        checkArgument(!vectors.containsKey(vector2D), "Only contain one in 2d vector location");

        armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setHelmet(new ItemStack(HEAD));
        armorStand.setVisible(false);
        armorStand.setHeadPose(new EulerAngle(0, 0, 0));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        vectors.put(vector2D, this);
        items.add(this);
    }

    /** Rotate the head */
    public void rotate() {
        step = step >= 360 ? 0 : step + 0.15;
        armorStand.setHeadPose(new EulerAngle(0, step, 0));
    }

    @Override
    public Vector getLocation() {
        return vector;
    }

    @Override
    public void eat(Editable editable) {
        if (editable instanceof PlayerSlime) {
            items.remove(this);
            vectors.remove(vector2D);
            armorStand.remove();

            // todo play ItemPop sound to PlayerSlime's player
        }
    }
}
