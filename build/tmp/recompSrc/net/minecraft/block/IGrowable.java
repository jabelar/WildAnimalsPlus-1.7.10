package net.minecraft.block;

import java.util.Random;
import net.minecraft.world.World;

public interface IGrowable
{
    boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient);

    boolean shouldFertilize(World worldIn, Random random, int x, int y, int z);

    void fertilize(World worldIn, Random random, int x, int y, int z);
}