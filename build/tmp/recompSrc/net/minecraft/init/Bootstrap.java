package net.minecraft.init;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Bootstrap
{
    /** Whether the blocks, items, etc have already been registered */
    private static boolean alreadyRegistered = false;
    private static final String __OBFID = "CL_00001397";

    static void registerDispenserBehaviors()
    {
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense()
        {
            private static final String __OBFID = "CL_00001398";
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                EntityArrow entityarrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
                entityarrow.canBePickedUp = 1;
                return entityarrow;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense()
        {
            private static final String __OBFID = "CL_00001404";
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense()
        {
            private static final String __OBFID = "CL_00001405";
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense()
        {
            private static final String __OBFID = "CL_00001406";
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position)
            {
                return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
            }
            protected float func_82498_a()
            {
                return super.func_82498_a() * 0.5F;
            }
            protected float func_82500_b()
            {
                return super.func_82500_b() * 1.25F;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();
            private static final String __OBFID = "CL_00001407";
            /**
             * Dispenses the specified ItemStack from a dispenser.
             */
            public ItemStack dispense(IBlockSource source, final ItemStack stack)
            {
                return ItemPotion.isSplash(stack.getMetadata()) ? (new BehaviorProjectileDispense()
                {
                    private static final String __OBFID = "CL_00001408";
                    /**
                     * Return the projectile entity spawned by this dispense behavior.
                     */
                    protected IProjectile getProjectileEntity(World worldIn, IPosition position)
                    {
                        return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
                    }
                    protected float func_82498_a()
                    {
                        return super.func_82498_a() * 0.5F;
                    }
                    protected float func_82500_b()
                    {
                        return super.func_82500_b() * 1.25F;
                    }
                }).dispense(source, stack): this.field_150843_b.dispense(source, stack);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem()
        {
            private static final String __OBFID = "CL_00001410";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                double d0 = source.getX() + (double)enumfacing.getFrontOffsetX();
                double d1 = (double)((float)source.getYInt() + 0.2F);
                double d2 = source.getZ() + (double)enumfacing.getFrontOffsetZ();
                Entity entity = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), d0, d1, d2);

                if (entity instanceof EntityLivingBase && stack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
                }

                stack.splitStack(1);
                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem()
        {
            private static final String __OBFID = "CL_00001411";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                double d0 = source.getX() + (double)enumfacing.getFrontOffsetX();
                double d1 = (double)((float)source.getYInt() + 0.2F);
                double d2 = source.getZ() + (double)enumfacing.getFrontOffsetZ();
                EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(source.getWorld(), d0, d1, d2, stack);
                source.getWorld().spawnEntityInWorld(entityfireworkrocket);
                stack.splitStack(1);
                return stack;
            }
            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1002, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem()
        {
            private static final String __OBFID = "CL_00001412";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                IPosition iposition = BlockDispenser.getIPositionFromBlockSource(source);
                double d0 = iposition.getX() + (double)((float)enumfacing.getFrontOffsetX() * 0.3F);
                double d1 = iposition.getY() + (double)((float)enumfacing.getFrontOffsetX() * 0.3F);
                double d2 = iposition.getZ() + (double)((float)enumfacing.getFrontOffsetZ() * 0.3F);
                World world = source.getWorld();
                Random random = world.rand;
                double d3 = random.nextGaussian() * 0.05D + (double)enumfacing.getFrontOffsetX();
                double d4 = random.nextGaussian() * 0.05D + (double)enumfacing.getFrontOffsetY();
                double d5 = random.nextGaussian() * 0.05D + (double)enumfacing.getFrontOffsetZ();
                world.spawnEntityInWorld(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
                stack.splitStack(1);
                return stack;
            }
            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1009, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();
            private static final String __OBFID = "CL_00001413";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                double d0 = source.getX() + (double)((float)enumfacing.getFrontOffsetX() * 1.125F);
                double d1 = source.getY() + (double)((float)enumfacing.getFrontOffsetY() * 1.125F);
                double d2 = source.getZ() + (double)((float)enumfacing.getFrontOffsetZ() * 1.125F);
                int i = source.getXInt() + enumfacing.getFrontOffsetX();
                int j = source.getYInt() + enumfacing.getFrontOffsetY();
                int k = source.getZInt() + enumfacing.getFrontOffsetZ();
                Material material = world.getBlock(i, j, k).getMaterial();
                double d3;

                if (Material.water.equals(material))
                {
                    d3 = 1.0D;
                }
                else
                {
                    if (!Material.air.equals(material) || !Material.water.equals(world.getBlock(i, j - 1, k).getMaterial()))
                    {
                        return this.field_150842_b.dispense(source, stack);
                    }

                    d3 = 0.0D;
                }

                EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
                world.spawnEntityInWorld(entityboat);
                stack.splitStack(1);
                return stack;
            }
            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source)
            {
                source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
            }
        });
        BehaviorDefaultDispenseItem behaviordefaultdispenseitem = new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();
            private static final String __OBFID = "CL_00001399";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                ItemBucket itembucket = (ItemBucket)stack.getItem();
                int i = source.getXInt();
                int j = source.getYInt();
                int k = source.getZInt();
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());

                if (itembucket.tryPlaceContainedLiquid(source.getWorld(), i + enumfacing.getFrontOffsetX(), j + enumfacing.getFrontOffsetY(), k + enumfacing.getFrontOffsetZ()))
                {
                    stack.setItem(Items.bucket);
                    stack.stackSize = 1;
                    return stack;
                }
                else
                {
                    return this.field_150841_b.dispense(source, stack);
                }
            }
        };
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, behaviordefaultdispenseitem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, behaviordefaultdispenseitem);
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem()
        {
            private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();
            private static final String __OBFID = "CL_00001400";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int i = source.getXInt() + enumfacing.getFrontOffsetX();
                int j = source.getYInt() + enumfacing.getFrontOffsetY();
                int k = source.getZInt() + enumfacing.getFrontOffsetZ();
                Material material = world.getBlock(i, j, k).getMaterial();
                int l = world.getBlockMetadata(i, j, k);
                Item item;

                if (Material.water.equals(material) && l == 0)
                {
                    item = Items.water_bucket;
                }
                else
                {
                    if (!Material.lava.equals(material) || l != 0)
                    {
                        return super.dispenseStack(source, stack);
                    }

                    item = Items.lava_bucket;
                }

                world.setBlockToAir(i, j, k);

                if (--stack.stackSize == 0)
                {
                    stack.setItem(item);
                    stack.stackSize = 1;
                }
                else if (((TileEntityDispenser)source.getBlockTileEntity()).func_146019_a(new ItemStack(item)) < 0)
                {
                    this.field_150840_b.dispense(source, new ItemStack(item));
                }

                return stack;
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem()
        {
            private boolean field_150839_b = true;
            private static final String __OBFID = "CL_00001401";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int i = source.getXInt() + enumfacing.getFrontOffsetX();
                int j = source.getYInt() + enumfacing.getFrontOffsetY();
                int k = source.getZInt() + enumfacing.getFrontOffsetZ();

                if (world.isAirBlock(i, j, k))
                {
                    world.setBlock(i, j, k, Blocks.fire);

                    if (stack.attemptDamageItem(1, world.rand))
                    {
                        stack.stackSize = 0;
                    }
                }
                else if (world.getBlock(i, j, k) == Blocks.tnt)
                {
                    Blocks.tnt.onBlockDestroyedByPlayer(world, i, j, k, 1);
                    world.setBlockToAir(i, j, k);
                }
                else
                {
                    this.field_150839_b = false;
                }

                return stack;
            }
            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_150839_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem()
        {
            private boolean field_150838_b = true;
            private static final String __OBFID = "CL_00001402";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                if (stack.getMetadata() == 15)
                {
                    EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                    World world = source.getWorld();
                    int i = source.getXInt() + enumfacing.getFrontOffsetX();
                    int j = source.getYInt() + enumfacing.getFrontOffsetY();
                    int k = source.getZInt() + enumfacing.getFrontOffsetZ();

                    if (ItemDye.func_150919_a(stack, world, i, j, k))
                    {
                        if (!world.isRemote)
                        {
                            world.playAuxSFX(2005, i, j, k, 0);
                        }
                    }
                    else
                    {
                        this.field_150838_b = false;
                    }

                    return stack;
                }
                else
                {
                    return super.dispenseStack(source, stack);
                }
            }
            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source)
            {
                if (this.field_150838_b)
                {
                    source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
                else
                {
                    source.getWorld().playAuxSFX(1001, source.getXInt(), source.getYInt(), source.getZInt(), 0);
                }
            }
        });
        BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem()
        {
            private static final String __OBFID = "CL_00001403";
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                EnumFacing enumfacing = BlockDispenser.getFacingDirection(source.getBlockMetadata());
                World world = source.getWorld();
                int i = source.getXInt() + enumfacing.getFrontOffsetX();
                int j = source.getYInt() + enumfacing.getFrontOffsetY();
                int k = source.getZInt() + enumfacing.getFrontOffsetZ();
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), (EntityLivingBase)null);
                world.spawnEntityInWorld(entitytntprimed);
                --stack.stackSize;
                return stack;
            }
        });
    }

    /**
     * Registers blocks, items, stats, etc.
     */
    public static void register()
    {
        if (!alreadyRegistered)
        {
            alreadyRegistered = true;
            Block.registerBlocks();
            BlockFire.func_149843_e();
            Item.registerItems();
            StatList.func_151178_a();
            registerDispenserBehaviors();
        }
    }
}