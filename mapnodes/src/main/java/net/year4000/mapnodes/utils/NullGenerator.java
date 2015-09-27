/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.utils;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class NullGenerator extends ChunkGenerator {

    @Override
    @SuppressWarnings("deprecation")
    public byte[] generate(World world, Random random, int x, int z) {
        world.setBiome(x, z, Biome.SKY);
        return new byte[16 * 16 * world.getMaxHeight()];
    }
}
