package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase
{
    /** The entity owner of this AITask */
    private EntityLiving grassEaterEntity;
    /** The world the grass eater entity is eating from */
    private World entityWorld;
    /** Number of ticks since the entity started to eat grass */
    int eatingGrassTimer;
    private static final String __OBFID = "CL_00001582";

    public EntityAIEatGrass(EntityLiving p_i45314_1_)
    {
        this.grassEaterEntity = p_i45314_1_;
        this.entityWorld = p_i45314_1_.worldObj;
        this.setMutexBits(7);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.grassEaterEntity.getRNG().nextInt(this.grassEaterEntity.isChild() ? 50 : 1000) != 0)
        {
            return false;
        }
        else
        {
            int i = MathHelper.floor_double(this.grassEaterEntity.posX);
            int j = MathHelper.floor_double(this.grassEaterEntity.posY);
            int k = MathHelper.floor_double(this.grassEaterEntity.posZ);
            return this.entityWorld.getBlock(i, j, k) == Blocks.tallgrass && this.entityWorld.getBlockMetadata(i, j, k) == 1 ? true : this.entityWorld.getBlock(i, j - 1, k) == Blocks.grass;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.eatingGrassTimer = 40;
        this.entityWorld.setEntityState(this.grassEaterEntity, (byte)10);
        this.grassEaterEntity.getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.eatingGrassTimer = 0;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.eatingGrassTimer > 0;
    }

    /**
     * Number of ticks since the entity started to eat grass
     */
    public int getEatingGrassTimer()
    {
        return this.eatingGrassTimer;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);

        if (this.eatingGrassTimer == 4)
        {
            int i = MathHelper.floor_double(this.grassEaterEntity.posX);
            int j = MathHelper.floor_double(this.grassEaterEntity.posY);
            int k = MathHelper.floor_double(this.grassEaterEntity.posZ);

            if (this.entityWorld.getBlock(i, j, k) == Blocks.tallgrass)
            {
                if (this.entityWorld.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                {
                    this.entityWorld.breakBlock(i, j, k, false);
                }

                this.grassEaterEntity.eatGrassBonus();
            }
            else if (this.entityWorld.getBlock(i, j - 1, k) == Blocks.grass)
            {
                if (this.entityWorld.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                {
                    this.entityWorld.playAuxSFX(2001, i, j - 1, k, Block.getIdFromBlock(Blocks.grass));
                    this.entityWorld.setBlock(i, j - 1, k, Blocks.dirt, 0, 2);
                }

                this.grassEaterEntity.eatGrassBonus();
            }
        }
    }
}