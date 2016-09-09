package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.Random;
import com.google.common.collect.Maps;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraftforge.common.util.ForgeDirection;
import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockFire extends Block
{
    @Deprecated
    private int[] field_149849_a = new int[4096];
    @Deprecated
    private int[] field_149848_b = new int[4096];
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149850_M;
    private static final String __OBFID = "CL_00000245";

    protected BlockFire()
    {
        super(Material.fire);
        this.setTickRandomly(true);
    }

    public static void func_149843_e()
    {
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.planks), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.double_wooden_slab), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.wooden_slab), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.fence), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.oak_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.birch_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.spruce_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.jungle_stairs), 5, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.log), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.log2), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.leaves), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.leaves2), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.bookshelf), 30, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.tnt), 15, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.tallgrass), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.double_plant), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.yellow_flower), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.red_flower), 60, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.wool), 30, 60);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.vine), 15, 100);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.coal_block), 5, 5);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.hay_block), 60, 20);
        Blocks.fire.func_149842_a(getIdFromBlock(Blocks.carpet), 60, 20);
    }

    @Deprecated // Use setFireInfo
    public void func_149842_a(int p_149842_1_, int p_149842_2_, int p_149842_3_)
    {
        this.setFireInfo((Block)Block.blockRegistry.getObjectById(p_149842_1_), p_149842_2_, p_149842_3_);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 30;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World worldIn, int x, int y, int z, Random random)
    {
        if (worldIn.getGameRules().getGameRuleBooleanValue("doFireTick"))
        {
            boolean flag = worldIn.getBlock(x, y - 1, z).isFireSource(worldIn, x, y - 1, z, UP);

            if (!this.canPlaceBlockAt(worldIn, x, y, z))
            {
                worldIn.setBlockToAir(x, y, z);
            }

            if (!flag && worldIn.isRaining() && (worldIn.isRainingAt(x, y, z) || worldIn.isRainingAt(x - 1, y, z) || worldIn.isRainingAt(x + 1, y, z) || worldIn.isRainingAt(x, y, z - 1) || worldIn.isRainingAt(x, y, z + 1)))
            {
                worldIn.setBlockToAir(x, y, z);
            }
            else
            {
                int l = worldIn.getBlockMetadata(x, y, z);

                if (l < 15)
                {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l + random.nextInt(3) / 2, 4);
                }

                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn) + random.nextInt(10));

                if (!flag && !this.canNeighborBurn(worldIn, x, y, z))
                {
                    if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || l > 3)
                    {
                        worldIn.setBlockToAir(x, y, z);
                    }
                }
                else if (!flag && !this.canCatchFire(worldIn, x, y - 1, z, UP) && l == 15 && random.nextInt(4) == 0)
                {
                    worldIn.setBlockToAir(x, y, z);
                }
                else
                {
                    boolean flag1 = worldIn.isBlockHighHumidity(x, y, z);
                    byte b0 = 0;

                    if (flag1)
                    {
                        b0 = -50;
                    }

                    this.tryCatchFire(worldIn, x + 1, y, z, 300 + b0, random, l, WEST );
                    this.tryCatchFire(worldIn, x - 1, y, z, 300 + b0, random, l, EAST );
                    this.tryCatchFire(worldIn, x, y - 1, z, 250 + b0, random, l, UP   );
                    this.tryCatchFire(worldIn, x, y + 1, z, 250 + b0, random, l, DOWN );
                    this.tryCatchFire(worldIn, x, y, z - 1, 300 + b0, random, l, SOUTH);
                    this.tryCatchFire(worldIn, x, y, z + 1, 300 + b0, random, l, NORTH);

                    for (int i1 = x - 1; i1 <= x + 1; ++i1)
                    {
                        for (int j1 = z - 1; j1 <= z + 1; ++j1)
                        {
                            for (int k1 = y - 1; k1 <= y + 4; ++k1)
                            {
                                if (i1 != x || k1 != y || j1 != z)
                                {
                                    int l1 = 100;

                                    if (k1 > y + 1)
                                    {
                                        l1 += (k1 - (y + 1)) * 100;
                                    }

                                    int i2 = this.getChanceOfNeighborsEncouragingFire(worldIn, i1, k1, j1);

                                    if (i2 > 0)
                                    {
                                        int j2 = (i2 + 40 + worldIn.difficultySetting.getDifficultyId() * 7) / (l + 30);

                                        if (flag1)
                                        {
                                            j2 /= 2;
                                        }

                                        if (j2 > 0 && random.nextInt(l1) <= j2 && (!worldIn.isRaining() || !worldIn.isRainingAt(i1, k1, j1)) && !worldIn.isRainingAt(i1 - 1, k1, z) && !worldIn.isRainingAt(i1 + 1, k1, j1) && !worldIn.isRainingAt(i1, k1, j1 - 1) && !worldIn.isRainingAt(i1, k1, j1 + 1))
                                        {
                                            int k2 = l + random.nextInt(5) / 4;

                                            if (k2 > 15)
                                            {
                                                k2 = 15;
                                            }

                                            worldIn.setBlock(i1, k1, j1, this, k2, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean requiresUpdates()
    {
        return false;
    }

    @Deprecated
    private void tryCatchFire(World p_149841_1_, int p_149841_2_, int p_149841_3_, int p_149841_4_, int p_149841_5_, Random p_149841_6_, int p_149841_7_)
    {
        this.tryCatchFire(p_149841_1_, p_149841_2_, p_149841_3_, p_149841_4_, p_149841_5_, p_149841_6_, p_149841_7_, UP);
    }

    private void tryCatchFire(World p_149841_1_, int p_149841_2_, int p_149841_3_, int p_149841_4_, int p_149841_5_, Random p_149841_6_, int p_149841_7_, ForgeDirection face)
    {
        int j1 = p_149841_1_.getBlock(p_149841_2_, p_149841_3_, p_149841_4_).getFlammability(p_149841_1_, p_149841_2_, p_149841_3_, p_149841_4_, face);

        if (p_149841_6_.nextInt(p_149841_5_) < j1)
        {
            boolean flag = p_149841_1_.getBlock(p_149841_2_, p_149841_3_, p_149841_4_) == Blocks.tnt;

            if (p_149841_6_.nextInt(p_149841_7_ + 10) < 5 && !p_149841_1_.isRainingAt(p_149841_2_, p_149841_3_, p_149841_4_))
            {
                int k1 = p_149841_7_ + p_149841_6_.nextInt(5) / 4;

                if (k1 > 15)
                {
                    k1 = 15;
                }

                p_149841_1_.setBlock(p_149841_2_, p_149841_3_, p_149841_4_, this, k1, 3);
            }
            else
            {
                p_149841_1_.setBlockToAir(p_149841_2_, p_149841_3_, p_149841_4_);
            }

            if (flag)
            {
                Blocks.tnt.onBlockDestroyedByPlayer(p_149841_1_, p_149841_2_, p_149841_3_, p_149841_4_, 1);
            }
        }
    }

    /**
     * Returns true if at least one block next to this one can burn.
     */
    private boolean canNeighborBurn(World p_149847_1_, int p_149847_2_, int p_149847_3_, int p_149847_4_)
    {
        return this.canCatchFire(p_149847_1_, p_149847_2_ + 1, p_149847_3_, p_149847_4_, WEST ) ||
               this.canCatchFire(p_149847_1_, p_149847_2_ - 1, p_149847_3_, p_149847_4_, EAST ) ||
               this.canCatchFire(p_149847_1_, p_149847_2_, p_149847_3_ - 1, p_149847_4_, UP   ) ||
               this.canCatchFire(p_149847_1_, p_149847_2_, p_149847_3_ + 1, p_149847_4_, DOWN ) ||
               this.canCatchFire(p_149847_1_, p_149847_2_, p_149847_3_, p_149847_4_ - 1, SOUTH) ||
               this.canCatchFire(p_149847_1_, p_149847_2_, p_149847_3_, p_149847_4_ + 1, NORTH);
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World p_149845_1_, int p_149845_2_, int p_149845_3_, int p_149845_4_)
    {
        byte b0 = 0;

        if (!p_149845_1_.isAirBlock(p_149845_2_, p_149845_3_, p_149845_4_))
        {
            return 0;
        }
        else
        {
            int l = b0;
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_ + 1, p_149845_3_, p_149845_4_, l, WEST );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_ - 1, p_149845_3_, p_149845_4_, l, EAST );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_ - 1, p_149845_4_, l, UP   );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_ + 1, p_149845_4_, l, DOWN );
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_, p_149845_4_ - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(p_149845_1_, p_149845_2_, p_149845_3_, p_149845_4_ + 1, l, NORTH);
            return l;
        }
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return false;
    }

    /**
     * Checks the specified block coordinate to see if it can catch fire.  Args: blockAccess, x, y, z
     */
    @Deprecated
    public boolean canBlockCatchFire(IBlockAccess p_149844_1_, int p_149844_2_, int p_149844_3_, int p_149844_4_)
    {
        return canCatchFire(p_149844_1_, p_149844_2_, p_149844_3_, p_149844_4_, UP);
    }

    @Deprecated
    public int func_149846_a(World p_149846_1_, int p_149846_2_, int p_149846_3_, int p_149846_4_, int p_149846_5_)
    {
        return getChanceToEncourageFire(p_149846_1_, p_149846_2_, p_149846_3_, p_149846_4_, p_149846_5_, UP);
    }

    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || this.canNeighborBurn(worldIn, x, y, z);
    }

    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !this.canNeighborBurn(worldIn, x, y, z))
        {
            worldIn.setBlockToAir(x, y, z);
        }
    }

    public void onBlockAdded(World worldIn, int x, int y, int z)
    {
        if (worldIn.provider.dimensionId > 0 || !Blocks.portal.tryToCreatePortal(worldIn, x, y, z))
        {
            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !this.canNeighborBurn(worldIn, x, y, z))
            {
                worldIn.setBlockToAir(x, y, z);
            }
            else
            {
                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
            }
        }
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random)
    {
        if (random.nextInt(24) == 0)
        {
            worldIn.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        int l;
        float f;
        float f1;
        float f2;

        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !Blocks.fire.canCatchFire(worldIn, x, y - 1, z, UP))
        {
            if (Blocks.fire.canCatchFire(worldIn, x - 1, y, z, EAST))
            {
                for (l = 0; l < 2; ++l)
                {
                    f = (float)x + random.nextFloat() * 0.1F;
                    f1 = (float)y + random.nextFloat();
                    f2 = (float)z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, x + 1, y, z, WEST))
            {
                for (l = 0; l < 2; ++l)
                {
                    f = (float)(x + 1) - random.nextFloat() * 0.1F;
                    f1 = (float)y + random.nextFloat();
                    f2 = (float)z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, x, y, z - 1, SOUTH))
            {
                for (l = 0; l < 2; ++l)
                {
                    f = (float)x + random.nextFloat();
                    f1 = (float)y + random.nextFloat();
                    f2 = (float)z + random.nextFloat() * 0.1F;
                    worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, x, y, z + 1, NORTH))
            {
                for (l = 0; l < 2; ++l)
                {
                    f = (float)x + random.nextFloat();
                    f1 = (float)y + random.nextFloat();
                    f2 = (float)(z + 1) - random.nextFloat() * 0.1F;
                    worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }

            if (Blocks.fire.canCatchFire(worldIn, x, y + 1, z, DOWN))
            {
                for (l = 0; l < 2; ++l)
                {
                    f = (float)x + random.nextFloat();
                    f1 = (float)(y + 1) - random.nextFloat() * 0.1F;
                    f2 = (float)z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        else
        {
            for (l = 0; l < 3; ++l)
            {
                f = (float)x + random.nextFloat();
                f1 = (float)y + random.nextFloat() * 0.5F + 0.5F;
                f2 = (float)z + random.nextFloat();
                worldIn.spawnParticle("largesmoke", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.field_149850_M = new IIcon[] {reg.registerIcon(this.getTextureName() + "_layer_0"), reg.registerIcon(this.getTextureName() + "_layer_1")};
    }

    @SideOnly(Side.CLIENT)
    public IIcon getFireIcon(int p_149840_1_)
    {
        return this.field_149850_M[p_149840_1_];
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.field_149850_M[0];
    }

    public MapColor getMapColor(int meta)
    {
        return MapColor.tntColor;
    }

    /*================================= Forge Start ======================================*/
    private static class FireInfo
    {
        private int encouragement = 0;
        private int flammibility = 0;
    }
    private IdentityHashMap<Block, FireInfo> blockInfo = Maps.newIdentityHashMap();

    public void setFireInfo(Block block, int encouragement, int flammibility)
    {
        if (block == Blocks.air) throw new IllegalArgumentException("Tried to set air on fire... This is bad.");
        int id = Block.getIdFromBlock(block);
        this.field_149849_a[id] = encouragement;
        this.field_149848_b[id] = flammibility;

        FireInfo info = getInfo(block, true);
        info.encouragement = encouragement;
        info.flammibility = flammibility;
    }

    private FireInfo getInfo(Block block, boolean garentee)
    {
        FireInfo ret = blockInfo.get(block);
        if (ret == null && garentee)
        {
            ret = new FireInfo();
            blockInfo.put(block, ret);
        }
        return ret;
    }

    public void rebuildFireInfo()
    {
        for (int x = 0; x < 4096; x++)
        {
            //If we care.. we could detect changes in here and make sure we keep them, however 
            //it's my thinking that anyone who hacks into the private variables should DIAF and we don't care about them.
            field_149849_a[x] = 0;
            field_149848_b[x] = 0;
        }

        for (Entry<Block, FireInfo> e : blockInfo.entrySet())
        {
            int id = Block.getIdFromBlock(e.getKey());
            if (id >= 0 && id < 4096)
            {
                field_149849_a[id] = e.getValue().encouragement;
                field_149848_b[id] = e.getValue().flammibility;
            }
        }
    }

    public int getFlammability(Block block)
    {
        int id = Block.getIdFromBlock(block);
        return id >= 0 && id < 4096 ? field_149848_b[id] : 0;
    }

    public int getEncouragement(Block block)
    {
        int id = Block.getIdFromBlock(block);
        return id >= 0 && id < 4096 ? field_149849_a[id] : 0;
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canCatchFire(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return world.getBlock(x, y, z).isFlammable(world, x, y, z, face);
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    public int getChanceToEncourageFire(IBlockAccess world, int x, int y, int z, int oldChance, ForgeDirection face)
    {
        int newChance = world.getBlock(x, y, z).getFireSpreadSpeed(world, x, y, z, face);
        return (newChance > oldChance ? newChance : oldChance);
    }
    /*================================= Forge Start ======================================*/
}