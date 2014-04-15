package net.year4000.mapnodes.world;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;
@SuppressWarnings("deprecation")
public class NullGenerator extends ChunkGenerator {
    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return new byte[16 * 16 * 256];
    }
}
